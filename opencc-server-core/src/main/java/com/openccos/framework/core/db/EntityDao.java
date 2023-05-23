package com.openccos.framework.core.db;

import com.openccos.framework.core.annotation.CcColumn;
import com.openccos.framework.core.db.dao.AbstractEntityDao;
import com.openccos.framework.core.db.dao.EntityColumn;
import com.openccos.framework.core.db.jdbc.JdbcEngine;
import com.openccos.framework.core.util.NumberTimeUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 提供数据对象Dao操作
 * @author xkliu
 */
public class EntityDao<T> extends AbstractEntityDao<T> {
    @Getter
    protected JdbcEngine jdbcEngine;

    public EntityDao(Class<T> type, JdbcEngine jdbcEngine) {
        super(type);
        this.jdbcEngine = jdbcEngine;
    }

    /**
     * 获取数据库唯一id
     * @return 返回ID值
     */
    public long nextId() {
        return this.jdbcEngine.nextId();
    }

    /**
     * 根据PO对象的ID值更新其余所有字段
     * @param entity PO对象
     * @return 更新数量
     */
    public int updateEntity(T entity) {
        return updateEntity(entity, null,null);
    }

    /**
     * 使用ID字段更新单行数据
     * @param entity PO值对象，对象属性是需要更新的值
     * @param fields 需要更新额字段列表，逗号分隔
     * @return 更新数量
     */
    public int updateEntity(T entity, String fields) {
        return updateEntity(entity, fields, null);
    }

    /**
     * 指定自定义条件更新对象
     * @param entity PO值对象，对象属性是需要更新的值和更新条件值
     * @param fields 需要更新额字段列表，逗号分隔
     * @param whereFields 更新条件字段列表，逗号分隔
     * @return 更新数量
     */
    public int updateEntity(T entity, String fields, String whereFields) {
        StringBuilder sb = new StringBuilder();
        Object[] params = this.handleUpdate(entity, sb, fields, whereFields);

        return jdbcEngine.update(sb.toString(), params);
    }

    /**
     * 批量更新数据
     * @param entities PO对象列表
     * @param fields 逗号分隔的字段列表
     * @return 更新数量
     */
    public int[] batchUpdateEntity(List<T> entities, String fields) {
        StringBuilder sql = new StringBuilder();
        EntityColumn idColumn = findIdColumn();

        EntityColumn lastTimeColumn = typeColumns.get(CcColumn.Type.LAST_TIME);
        Long now = NumberTimeUtil.now();

        sql.append("update ").append(tableName).append(" set ");
        List<EntityColumn> fieldColumns = new ArrayList<>();

        if (fields == null) {
            for (EntityColumn column: this.columns.values()) {
                if (idColumn != column) {
                    fieldColumns.add(column);
                    sql.append(column.getFieldName()).append("=?,");
                }
            }
            sql.setCharAt(sql.length() - 1, ' ');

            // 默认使用Id字段条件
            fieldColumns.add(idColumn);

            sql.append("where ").append(idColumn.getFieldName()).append("=?");
        } else {
            String[] fieldNames = fields.split(",");

            for (String name: fieldNames) {
                name = name.trim();
                EntityColumn beanColumn = getBeanColumn(name);
                fieldColumns.add(beanColumn);
                sql.append(name).append("=?,");
            }

            if (lastTimeColumn != null) {
                fieldColumns.add(lastTimeColumn);
                sql.append(lastTimeColumn.getFieldName()).append("=?,");
            }

            sql.setCharAt(sql.length() - 1, ' ');

             // 默认使用Id字段条件
            fieldColumns.add(idColumn);

            sql.append("where ").append(idColumn.getFieldName()).append("=?");
        }

        List<Object[]> paramsList = new ArrayList<>(entities.size());
        for (Object obj: entities) {
            Object[] params = new Object[fieldColumns.size()];

            for (int i = 0; i < params.length; i++) {
                EntityColumn column = fieldColumns.get(i);

                Object value = column.readValue(obj);
                if (lastTimeColumn == column) {
                    if (value == null) {
                        value = now;
                        // 自动更新对象的值
                        column.writeValue(obj, now);
                    }
                }

                params[i] = value;
            }

            paramsList.add(params);
        }

        return jdbcEngine.batchUpdate(sql.toString(), paramsList);
    }

    /**
     * 用PO对象所有字段入单行数据
     * @param type PO对象字段范围的类，是entity的父类
     * @param entity PO对象
     * @return 更新数量
     */
    public int insertEntity(Class<? super T> type, T entity) {
        return insertEntity(type, entity, null);
    }
    /**
     * 用PO对象所有字段入单行数据
     * @param entity PO对象
     * @return 更新数量
     */
    public int insertEntity(T entity) {
        return insertEntity(entity.getClass(), entity, null);
    }
    /**
     * 插入单行数据
     * @param entity PO对象
     * @param fields 逗号分隔的字段列表
     * @return 更新数量
     */
    public int insertEntity(T entity, String fields) {
        return insertEntity(entity.getClass(), entity, fields);
    }

    private int insertEntity(Class<?> type, T entity, String fields) {
        StringBuilder sb = new StringBuilder();

        Object[] params = handleInsert(entity, sb, fields);

        return jdbcEngine.update(sb.toString(), params);
    }

    /**
     * 批量插入单行数据
     * @param entities PO对象列表
     * @return 更新数量
     */
    public int[] batchInsertEntity(List<T> entities) {
        return batchInsertEntity(entities, null);
    }

    /**
     * 批量插入数据
     * @param entities PO对象列表
     * @param fields 逗号分隔的字段列表
     * @return 更新数量
     */
    public int[] batchInsertEntity(List<T> entities, String fields) {
        StringBuilder sql = new StringBuilder();

        sql.append("insert into ").append(tableName).append("(");

        Long now = NumberTimeUtil.now();
        EntityColumn createTimeColumn = typeColumns.get(CcColumn.Type.CREATE_TIME);
        EntityColumn lastTimeColumn = typeColumns.get(CcColumn.Type.LAST_TIME);
        boolean includeCreateTimeColumn = false;
        boolean includeLastTimeColumn = false;

        List<EntityColumn> insertColumns = new ArrayList<>(this.columns.size());

        if (fields == null) {
            for (EntityColumn column: this.columns.values()) {
                sql.append(column.getFieldName()).append(",");
                insertColumns.add(column);
            }
        } else {
            String[] fieldNames = fields.split(",");

            for (String name: fieldNames) {
                EntityColumn column = this.columns.get(name.trim());
                sql.append(column.getFieldName()).append(",");
                if (column == createTimeColumn) {
                    includeCreateTimeColumn = true;
                } else if (column == lastTimeColumn) {
                    includeLastTimeColumn = true;
                }
                insertColumns.add(column);
            }

            if (createTimeColumn != null && !includeCreateTimeColumn) {
                sql.append(createTimeColumn.getFieldName()).append(",");
            }

            if (lastTimeColumn != null && !includeLastTimeColumn) {
                sql.append(lastTimeColumn.getFieldName()).append(",");
            }
        }

        sql.setCharAt(sql.length() - 1, ')');

        // values(?,?)
        sql.append(" values(");
        for (int i = insertColumns.size(); i > 0; i--) {
            sql.append("?,");
        }
        sql.setCharAt(sql.length() - 1, ')');

        // 参数列表
        List<Object[]> listParams = new ArrayList<>(entities.size());
        for (T obj: entities) {
            List<Object> params = new ArrayList<>(insertColumns.size());

            for (EntityColumn column : insertColumns) {
                if (column == createTimeColumn) {
                    column.writeValue(obj, now);
                    params.add(now);
                } else if (column == lastTimeColumn) {
                    column.writeValue(obj, now);
                    params.add(now);
                } else {
                    params.add(column.readValue(obj));
                }
            }

            listParams.add(params.toArray());
        }

        return jdbcEngine.batchUpdate(sql.toString(), listParams);
    }


    /**
     * 根据ID值删除单行数据
     * @param entity PO对象
     * @return 删除数量
     */
    public int deleteEntity(T entity) {
        StringBuilder sb = new StringBuilder();

        Object[] params = handleDelete(entity, sb);

        return jdbcEngine.update(sb.toString(), params);
    }

    /**
     * 根据ID值删除单行数据
     * @param id 记录主建值
     * @return 删除数量
     */
    public int deleteEntity(Long id) {
        StringBuilder sb = new StringBuilder();
        handleDelete(sb);

        return jdbcEngine.update(sb.toString(), id);
    }

    /**
     * 根据ID值删除单行数据
     * @param whereSql Where条件SQL语句，以where开头
     * @param params 条件的值，可以多个
     * @return 删除数量
     */
    public int deleteEntity(String whereSql, Object...params) {
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(getTableName()).append(" ").append(whereSql);

        return jdbcEngine.update(sb.toString(), params);
    }

    /**
     * 读取实体对象ID值
     * @param entity
     * @return
     */
    public Long readEntityId(T entity) {
        return (Long)readId(entity);
    }

    /** 查询单行数据，返回bean */
    public T queryEntity(Long id) {
        return queryEntity(id, null);
    }

    public T queryEntity(Long id, String fields) {
        StringBuilder sb = new StringBuilder();
        handleSelectOne(sb, fields);

        List<T> list = jdbcEngine.query(sb.toString(), type, id);

        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }

        return null;
    }

    /** 查询对象所有数据，返回列表 */
    public List<T> query() {
        return query(null);
    }

    /** 查询对象所有数据，返回列表 */
    public List<T> query(String fields) {
        StringBuilder sb = new StringBuilder();
        handleSelect(sb, fields);

        return jdbcEngine.query(sb.toString(), type);
    }

    /** 查询对象所有数据，返回列表 */
    public List<T> queryWhere(String sqlWhere, Object...params) {
        StringBuilder sb = new StringBuilder();
        handleSelect(sb, null);
        sb.append(" where ").append(sqlWhere);

        return jdbcEngine.query(sb.toString(), type, params);
    }
}
