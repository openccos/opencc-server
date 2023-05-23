package com.openccos.framework.core.db.jdbc;

import com.openccos.framework.core.SwMap;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * ORM映射处理器，实现spring jdbcTemplate的行集映射器，对下划线进行小驼峰命名转化
 * @author xkliu
 */
public class SwMapPropertyRowMapper<T> implements RowMapper<T> {
  public SwMapPropertyRowMapper(Class<T> mappedClass) {
  }

  @Override
  public T mapRow(ResultSet resultSet, int i) throws SQLException {
    SwMap map = new SwMap();

    ResultSetMetaData rsmd = resultSet.getMetaData();
    int columnCount = rsmd.getColumnCount();

    for(int index = 1; index <= columnCount; ++index) {
      Object value = resultSet.getObject(index);

      if (value != null) {
        String columnLabel = rsmd.getColumnLabel(index);
        map.put(toCamelCase(columnLabel), value);
      }
    }

    return (T)map;
  }

  private String toCamelCase(String columnLabel) {
    int len = columnLabel.length();
    StringBuilder sb = new StringBuilder(len);
    int lowCase = 1;
    for (int i = 0; i < len; i++) {
      char ch = columnLabel.charAt(i);
      if (ch == '_') {
        lowCase = 2;
      } else {
        if (lowCase == 1) {
          ch = Character.toLowerCase(ch);
          lowCase = 0;
        } else if (lowCase == 2) {
          ch = Character.toUpperCase(ch);
          lowCase = 0;
        }
        sb.append(ch);
      }
    }

    return sb.toString();
  }
}
