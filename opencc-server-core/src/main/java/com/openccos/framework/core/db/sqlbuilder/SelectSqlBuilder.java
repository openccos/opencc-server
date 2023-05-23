package com.openccos.framework.core.db.sqlbuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class SelectSqlBuilder extends AbstractSelectSqlBuilder {
  private String tableName;
  private List<SqlJoinTable> joinTables;

  SelectSqlBuilder(String tableName) {
    this.tableName = tableName;
  }

//  @Override
//  public <T> List<T> query(DbEngine dbEngine, Class<T> clazz) {
//    StringBuilder sb = new StringBuilder("select ");
//
//    Object[] params = makeParams(sb);
//
//    if (params != null) {
//      return dbEngine.query(sb.toString(), clazz, params);
//    } else {
//      return dbEngine.query(sb.toString(), clazz);
//    }
//  }

  @Override
  protected void makeFields(StringBuilder sb) {
    for (SqlFieldValue field: fields) {
      sb.append(field.getName()).append(",");
    }

    sb.setCharAt(sb.length() - 1, ' ');
    sb.append("FROM ").append(tableName);

    if (joinTables != null) {
      for (SqlJoinTable joinTable: joinTables) {
        sb.append(joinTable.joinSql()).append(joinTable.getDbName()).append('.').append(joinTable.getTableName())
                .append(' ').append(joinTable.getTableAlias()).append(" ON(");

        boolean first = true;
        for (SqlJoinField joinField: joinTable.getFields()) {
          if (first) {
            first = false;
          } else {
            sb.append(" AND ");
          }

          sb.append(joinField.getKeyField()).append("=").append(joinField.getValueField());
        }

        sb.append(')');
      }
    }
  }

  @Override
  public <T> T exec(BiFunction<String, Object[], T> execute) {
    StringBuilder sb = new StringBuilder("select ");

    Object[] params = makeParams(sb);

    return execute.apply(sb.toString(), params);
  }

  @Override
  public SqlJoinTable addJoinTable(String dbName, String tableName, String tableAlias) {
    if (joinTables == null) {
      joinTables = new ArrayList<>();
    }

    SqlJoinTable joinTable = new SqlJoinTable();
    joinTable.setDbName(dbName);
    joinTable.setTableName(tableName);
    joinTable.setTableAlias(tableAlias);

    joinTables.add(joinTable);

    return joinTable;
  }

  @Override
  public SqlJoinTable findJoinTable(String dbName, String tableName) {
    if (joinTables != null) {
      for (SqlJoinTable table: joinTables) {
        if (dbName.equals(table.getDbName()) && tableName.equals(table.getTableName())) {
          return table;
        }
      }
    }

    return null;
  }
}
