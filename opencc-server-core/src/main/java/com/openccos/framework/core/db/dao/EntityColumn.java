package com.openccos.framework.core.db.dao;

import com.openccos.framework.core.exception.DbException;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 值对象字段处理类
 * @author xkliu
 */
@Getter
public class EntityColumn {
	private String fieldName;
	private final Method readMethod;
	private final Method writeMethod;
	private EntityColumnForeign foreign;

	/**
	 * 构建值对象字段
	 * @param fieldName 字段名
	 * @param readMethod 读值方法
	 * @param writeMethod 写值方法
	 * @param foreign 外键注解
	 */
	public EntityColumn(String fieldName, Method readMethod, Method writeMethod, EntityColumnForeign foreign) {
		this.fieldName = fieldName;
		this.readMethod = readMethod;
		this.writeMethod = writeMethod;
		this.foreign = foreign;
	}

	/**
	 * 从对象中读取字段对应的属性值
	 * @param obj 值对象
	 * @return 属性值
	 */
	public Object readValue(Object obj) {
		try {
			return readMethod.invoke(obj);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw new DbException(e);
		}
	}

	/**
	 * 写入值到对象字段对象属性
	 * @param obj 值对象
	 * @param value 属性值
	 */
  public void writeValue(Object obj, Object value) {
		try {
			writeMethod.invoke(obj, value);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw new DbException(e);
		}
  }
}
