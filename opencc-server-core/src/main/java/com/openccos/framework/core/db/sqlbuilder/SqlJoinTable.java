package com.openccos.framework.core.db.sqlbuilder;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * SelectSqlBuilder使用的查询关联表
 * @author xkliu
 */
@Data
public class SqlJoinTable {
  public static final int LEFT_JOIN = 1;
  public static final int RIGHT_JOIN = 2;
  public static final int INNER_JOIN = 3;
  public static final int FULL_JOIN = 4;

  private int joinType = LEFT_JOIN;
  private String dbName;
  private String tableName;
  private String tableAlias;

  private List<SqlJoinField> fields = new ArrayList<>();

  public void add(String keyField, String valueField) {
    fields.add(new SqlJoinField(keyField, valueField));
  }

  public String joinSql() {
    switch (joinType) {
      default:
      case LEFT_JOIN: return " LEFT JOIN ";
      case RIGHT_JOIN: return " RIGHT JOIN ";
      case INNER_JOIN: return " INNER JOIN ";
      case FULL_JOIN: return " FULL JOIN ";
    }
  }
}
