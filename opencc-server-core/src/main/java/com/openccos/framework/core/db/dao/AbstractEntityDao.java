package com.openccos.framework.core.db.dao;

import com.openccos.framework.core.annotation.CcColumn;
import com.openccos.framework.core.annotation.CcColumnForeign;
import com.openccos.framework.core.annotation.CcTable;
import com.openccos.framework.core.exception.DbException;
import com.openccos.framework.core.util.NumberTimeUtil;
import com.openccos.framework.core.util.VariableUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ClassUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抽象的值对象数据库访问
 * @param <T> 数据库值对象类型
 */
public abstract class AbstractEntityDao<T> {
	protected String tableName;
	protected Map<String, EntityColumn> columns = new HashMap<>();
	protected Map<CcColumn.Type, EntityColumn> typeColumns = new HashMap<>();
	protected Class<T> type;

//	public AbstractEntityDao(String tableName) {
//		this.tableName = tableName;
//	}

	/**
	 * 通过值对象类型构造值对象数据库访问
	 * @param type 值对象类型
	 */
	public AbstractEntityDao(Class<T> type) {
		this.type = type;

		// type.isAnnotationPresent(Table.class);
		CcTable table = type.getAnnotation(CcTable.class);

		Class<? super T> superclass = type.getSuperclass();

		if (table == null && superclass != null) {
			table = superclass.getAnnotation(CcTable.class);
		}

		if (table == null) {
			throw new IllegalAccessError("not find annotation @SwTable");
		}

		List<Field> fields;
		if (superclass == null) {
			fields = appendField(type.getFields(), type.getDeclaredFields());
		} else {
			fields = appendField(type.getFields(), type.getDeclaredFields(), superclass.getFields(),
							superclass.getDeclaredFields());
		}

//						Map<String, String> columnToPropertyOverrides = new HashMap<>();
		tableName = table.value();

		for (Field field : fields) {
			CcColumn column = field.getAnnotation(CcColumn.class);
			// 获取name
			String columnName;
			if (column != null && StringUtils.isNotBlank(column.value())) {
				columnName = column.value();
			} else {
				columnName = VariableUtil.humpToUnderline(field.getName());
			}

			// 处理get method
			try {
				PropertyDescriptor pd = new PropertyDescriptor((String)field.getName(), type);
				// 获得get方法
				Method readMethod = pd.getReadMethod();
				Method writeMethod = pd.getWriteMethod();

				if (readMethod != null && writeMethod != null) {
					Class<?>[] parameterTypes = writeMethod.getParameterTypes();
					if (parameterTypes.length == 1 && ClassUtils.isAssignable(parameterTypes[0], readMethod.getReturnType())) {
						add(columnName, readMethod, writeMethod, column, field.getAnnotation(CcColumnForeign.class));
					}
				}
			} catch (IntrospectionException ignore) {
			}

			// TODO 判断是否Public
		}
	}

	private List<Field> appendField(Field[]... fields) {
		List<Field> list = new ArrayList<>();
		if (fields == null) {
			return list;
		}

		for (Field[] field : fields) {
			if (field == null) {
				continue;
			}

			for (Field temp : field) {
				list.add(temp);
			}
		}
		return list;
	}

	/**
	 * 添加值对象的属性访问方法何注解值
	 * @param fieldName 字段名
	 * @param readMethod 读值对象属性方法
	 * @param writeMethod 写值值对象属性方法
	 * @param column 字段注解
	 * @param mayToOne 外键注解
	 */
	public void add(String fieldName, Method readMethod, Method writeMethod, CcColumn column, CcColumnForeign mayToOne) {
		EntityColumnForeign foreign = null;
		if (mayToOne != null) {
			foreign = new EntityColumnForeign(mayToOne.table(), mayToOne.id(), mayToOne.code());
		}

		EntityColumn beanColumn = new EntityColumn(fieldName, readMethod, writeMethod, foreign);
		columns.put(fieldName, beanColumn);

		if (column != null) {
			for (CcColumn.Type type : column.type()) {
				typeColumns.put(type, beanColumn);
			}
		}
	}

	/**
	 * 拼接插入SQL语句
	 * @param obj 值对象
	 * @param sql 记录sql的字符缓存
	 * @param fields 逗号分割字段列表，设置为null表示使用所有字段
	 * @return SQL参数列表
	 */
	protected Object[] handleInsert(T obj, StringBuilder sql, String fields) {
		List<Object> result;

		sql.append("insert into ").append(tableName).append("(");

		Long now = NumberTimeUtil.now();
		EntityColumn createTimeColumn = typeColumns.get(CcColumn.Type.CREATE_TIME);
		EntityColumn lastTimeColumn = typeColumns.get(CcColumn.Type.LAST_TIME);

		if (fields == null) {
//			len = this.columns.size();
			result = new ArrayList<>(this.columns.size());
//			int index = 0;

			for (EntityColumn column: this.columns.values()) {
				Object value = column.readValue(obj);

				if (column == createTimeColumn) {
					if (value == null) {
						column.writeValue(obj, now);
						value = now;
					}
					createTimeColumn = null;
				} else if (column == lastTimeColumn) {
					if (value == null) {
						column.writeValue(obj, now);
						value = now;
					}
					lastTimeColumn = null;
				}

				result.add(value);
				sql.append(column.getFieldName()).append(",");
			}
		} else {
			String[] fieldNames = fields.split(",");
			result = new ArrayList<>(fieldNames.length + 2);

			for (String name: fieldNames) {
				EntityColumn column = this.columns.get(name.trim());
				if (column == null) {
					throw new IllegalArgumentException("not define column: " + name);
				}

				Object value = column.readValue(obj);

				if (column == createTimeColumn) {
					if (value == null) {
						column.writeValue(obj, now);
						value = now;
					}
					createTimeColumn = null;
				} else if (column == lastTimeColumn) {
					if (value == null) {
						column.writeValue(obj, now);
						value = now;
					}
					lastTimeColumn = null;
				}

				result.add(value);
				sql.append(column.getFieldName()).append(",");
			}

			// 自动更新fields未设置 createTime
			if (createTimeColumn != null) {
				createTimeColumn.writeValue(obj, now);
				result.add(now);
				sql.append(createTimeColumn.getFieldName()).append(",");
			}

			// 自动更新fields未设置 createTime
			if (lastTimeColumn != null) {
				lastTimeColumn.writeValue(obj, now);
				result.add(now);
				sql.append(lastTimeColumn.getFieldName()).append(",");
			}
		}

		sql.setCharAt(sql.length() - 1, ')');

		// values(?,?)
		sql.append(" values(");
		for (int i = result.size(); i > 0; i--) {
			sql.append("?,");
		}
		sql.setCharAt(sql.length() - 1, ')');

		return result.toArray();
	}

	/**
	 * 拼接更新SQL语句
	 * @param obj 值对象
	 * @param sql 记录sql的字符缓存
	 * @param fields 逗号分割更新字段列表，设置为null表示使用所有字段
	 * @param whereFields 逗号分割查询字段列表，设置为null表示使用ID字段作为查询条件
	 * @return SQL参数列表
	 */
	protected Object[] handleUpdate(T obj, StringBuilder sql, String fields, String whereFields) {
		EntityColumn idColumn = findIdColumn();

		EntityColumn lastTimeColumn = typeColumns.get(CcColumn.Type.LAST_TIME);
		Long now = NumberTimeUtil.now();

		sql.append("update ").append(tableName).append(" set ");
		List<Object> result = new ArrayList<>();

		if (fields == null) {
			for (EntityColumn column: this.columns.values()) {
				if (idColumn != column) {
					Object value = column.readValue(obj);
					if (lastTimeColumn == column) {
						if (value == null) {
							value = now;
						} else {
							lastTimeColumn = null;
						}
					}

					result.add(value);
					sql.append(column.getFieldName()).append("=?,");
				}
			}
			sql.setCharAt(sql.length() - 1, ' ');

			// 默认使用Id字段条件
			result.add(idColumn.readValue(obj));

			sql.append("where ").append(idColumn.getFieldName()).append("=?");
		} else {
			String[] fieldNames = fields.split(",");

			for (String name: fieldNames) {
				name = name.trim();
				EntityColumn beanColumn = getBeanColumn(name);

				Object value = beanColumn.readValue(obj);
				if (lastTimeColumn == beanColumn) {
					if (value == null) {
						value = now;
					} else {
						lastTimeColumn = null;
					}
				}

				result.add(value);
				sql.append(name).append("=?,");
			}

			if (lastTimeColumn != null) {
				result.add(now);
				sql.append(lastTimeColumn.getFieldName()).append("=?,");
			}

			sql.setCharAt(sql.length() - 1, ' ');

			if (StringUtils.isNotBlank(whereFields)) {
				sql.append("where ");
				boolean first = true;
				for (String name: whereFields.split(",")) {
					name = name.trim();
					if (first) {
						first = false;
					} else {
						sql.append(" and ");
					}

					sql.append(name).append("=?");
					result.add(readValue(obj, name));
				}
			} else {
				// 默认使用Id字段条件
				result.add(idColumn.readValue(obj));

				sql.append("where ").append(idColumn.getFieldName()).append("=?");
			}
		}

		// 自动更新对象的值
		if (lastTimeColumn != null) {
			lastTimeColumn.writeValue(obj, now);
		}

		return result.toArray();
	}

	protected EntityColumn findIdColumn() {
		EntityColumn idColumn = typeColumns.get(CcColumn.Type.ID);

		if (idColumn == null) {
			throw new DbException(tableName + " not define id column");
		}

		return idColumn;
	}

	/**
	 * 拼接删除值对象语句
	 * @param obj 值对象
	 * @param sql 记录sql的字符缓存
	 * @return SQL参数列表
	 */
	protected Object[] handleDelete(T obj, StringBuilder sql) {
		EntityColumn idColumn = findIdColumn();

		sql.append("DELETE FROM ").append(tableName).append(" WHERE ").append(idColumn.getFieldName()).append("=?");

		return new Object[]{idColumn.readValue(obj)};
	}

	/**
	 * 拼接删除值对象语句，条件由外部设置
	 * @param sql 记录sql的字符缓存
	 */
	protected void handleDelete(StringBuilder sql) {
		EntityColumn idColumn = findIdColumn();

		sql.append("DELETE FROM ").append(tableName).append(" WHERE ").append(idColumn.getFieldName()).append("=?");
	}

	private Object readValue(T obj, String fieldName) {
		EntityColumn beanColumn = getBeanColumn(fieldName);

		return beanColumn.readValue(obj);
	}

	protected EntityColumn getBeanColumn(String fieldName) {
		EntityColumn beanColumn = this.columns.get(fieldName);

		if (beanColumn == null) {
			throw new DbException("not define column:" + fieldName);
		}

		return beanColumn;
	}

	/**
	 * 获取表名
	 * @return 表名
	 */
	protected String getTableName() {
		return tableName;
	}

	/**
	 * 拼接查询SQL语句
	 * @param sql SQL字符缓存
	 * @param fields 逗号分割的查询字段列表，传入null表示全部值对象字段
	 */
	public void handleSelect(StringBuilder sql, String fields) {
		sql.append("select ");

		if (fields != null) {
			sql.append(fields).append(' ');
		} else {
			for (String fieldName : columns.keySet()) {
				sql.append(fieldName).append(',');
			}
			sql.setCharAt(sql.length() - 1, ' ');
		}

		sql.append("from ").append(tableName);
	}

	/**
	 * 拼接SQL排序字段
	 * @param sql SQL字符缓存
	 */
	public void handleOrderBy(StringBuilder sql) {
		EntityColumn entityColumn = this.typeColumns.get(CcColumn.Type.ORDER);
		if (entityColumn != null) {
			sql.append(" order by ").append(entityColumn.getFieldName());
			EntityColumn idColumn = this.typeColumns.get(CcColumn.Type.ID);
			if (idColumn != null) {
				sql.append(", ").append(idColumn.getFieldName());
			}
		}
	}

	protected void handleSelectOne(StringBuilder sql, String fields) {
		EntityColumn idColumn = findIdColumn();

		sql.append("select ");

		if (fields != null) {
			sql.append(fields).append(' ');
		} else {
			for (EntityColumn field : columns.values()) {
				sql.append(field.getFieldName()).append(",");
			}
			sql.setCharAt(sql.length() - 1, ' ');
		}

		sql.append("from ").append(tableName).append(" where ").append(idColumn.getFieldName()).append("=?");
	}

	public EntityColumn getColumn(CcColumn.Type type) {
		return this.typeColumns.get(type);
	}

	/**
	 * 读取ID值
	 * @param entity
	 * @return
	 */
	public Long readId(T entity) {
		EntityColumn idColumn = findIdColumn();

		return (Long)idColumn.readValue(entity);
	}
}

