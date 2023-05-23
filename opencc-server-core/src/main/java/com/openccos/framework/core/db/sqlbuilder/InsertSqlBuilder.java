package com.openccos.framework.core.db.sqlbuilder;

import com.openccos.framework.core.db.jdbc.JdbcEngine;

public class InsertSqlBuilder extends AbstractUpdateSqlBuilder<InsertSqlBuilder> {
  private String tableName;

  InsertSqlBuilder(String tableName) {
    this.tableName = tableName;
  }

  @Override
  public int update(JdbcEngine dbEngine) {
    int fieldSize = fields.size();
    Object[] params = new Object[fieldSize];

    StringBuilder sb = new StringBuilder("insert into ");
    sb.append(tableName).append('(');

    for (int i = 0; i < fieldSize; i++) {
      SqlFieldValue field = fields.get(i);
      sb.append(field.getName()).append(',');
      params[i] = field.getValue();
    }

    sb.setCharAt(sb.length() - 1, ')');

    sb.append(" values(");

    for (int i = 0; i < fieldSize; i++) {
      sb.append("?,");
    }

    sb.setCharAt(sb.length() - 1, ')');

    return dbEngine.update(sb.toString(), params);
  }
}
