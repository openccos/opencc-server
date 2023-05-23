package com.openccos.framework.core.db.sqlbuilder;

import com.openccos.framework.core.db.jdbc.JdbcEngine;

import java.util.ArrayList;
import java.util.List;

public class UpdateSqlBuilder extends AbstractUpdateSqlBuilder<UpdateSqlBuilder> {
  private String tableName;

  UpdateSqlBuilder(String tableName) {
    this.tableName = tableName;
  }

  @Override
  public int update(JdbcEngine dbEngine) {
    int fieldSize = fields.size() + wheres.size();
    List<Object> params = new ArrayList<>(fieldSize);
//    Object[] params = new Object[fieldSize];

    StringBuilder sb = new StringBuilder("update ");

    sb.append(tableName).append(" set ");

//    int index = 0;
    for (SqlFieldValue field: fields) {
      if (field.getValue() == VALUE_INVALID) {
        sb.append(field.getName());
      } else {
        sb.append(field.getName()).append("=?,");
        params.add(field.getValue());
      }
//      params[index] = field.getValue();
//      index++;
    }

    sb.setCharAt(sb.length() - 1, ' ');
    sb.append("where ");

    boolean first = true;
    for (SqlWhereValue whereValue: wheres) {
      if (first) {
        first = false;
      } else {
        sb.append(" and ");
      }
      sb.append(whereValue.getName()).append("=?");
      params.add(whereValue.getValue());
//      params[index]
//      index++;
    }

    return dbEngine.update(sb.toString(), params.toArray());
  }
}
