package com.openccos.framework.core.db.jdbc;

import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * ORM映射处理器，实现spring jdbcTemplate的行集映射器
 * @author xkliu
 */
public class MapPropertyRowMapper<T> implements RowMapper<T> {
  private Class<T> mappedClass;

  public MapPropertyRowMapper(Class<T> mappedClass) {
    this.mappedClass = mappedClass;
  }

  @Override
  public T mapRow(ResultSet resultSet, int i) throws SQLException {
    T mappedObject = BeanUtils.instantiateClass(this.mappedClass);
    java.util.Map map = (java.util.Map)mappedObject;

    ResultSetMetaData rsmd = resultSet.getMetaData();
    int columnCount = rsmd.getColumnCount();

    for(int index = 1; index <= columnCount; ++index) {
      Object value = resultSet.getObject(index);

      if (value != null) {
        map.put(rsmd.getColumnLabel(index), value);
      }
    }

    return mappedObject;
  }
}
