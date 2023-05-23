package com.openccos.framework.core.db.dao;

import lombok.Getter;

/**
 * 字段外键属性
 * @author admin
 */
@Getter
public class EntityColumnForeign {
	private final String table;
	private final String id;
	private final String name;

	/**
	 * 构造字段外键属性
	 * @param table 外键表名
	 * @param id 外键ID字段名
	 * @param name 外键名称字段名
	 */
	public EntityColumnForeign(String table, String id, String name) {
		this.table = table;
		this.id = id;
		this.name = name;
	}
}
