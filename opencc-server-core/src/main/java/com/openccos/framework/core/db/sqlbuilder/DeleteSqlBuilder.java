package com.openccos.framework.core.db.sqlbuilder;

import com.openccos.framework.core.db.jdbc.JdbcEngine;

public class DeleteSqlBuilder extends AbstractUpdateSqlBuilder<DeleteSqlBuilder> {
  private String tableName;

  DeleteSqlBuilder(String tableName) {
    this.tableName = tableName;
  }

  @Override
  public int update(JdbcEngine dbEngine) {
    int fieldSize = wheres.size();
    Object[] params = new Object[fieldSize];

    StringBuilder sb = new StringBuilder("delete from ");

    sb.append(tableName).append(" where ");

    int index = 0;
    for (SqlWhereValue whereValue: wheres) {
      if (index > 0){
        sb.append(" and ");
      }
      sb.append(whereValue.getName()).append("=?");
      params[index] = whereValue.getValue();
      index++;
    }

    return dbEngine.update(sb.toString(), params);
  }
}
