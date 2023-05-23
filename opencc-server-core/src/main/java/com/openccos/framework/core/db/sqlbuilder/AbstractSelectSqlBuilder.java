package com.openccos.framework.core.db.sqlbuilder;

import com.openccos.framework.core.db.DbEngine;
import com.openccos.framework.core.db.jdbc.JdbcEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class AbstractSelectSqlBuilder extends SqlBuilder<AbstractSelectSqlBuilder> {
  private List<String> orderBy;

  protected void makeFields(StringBuilder sb) {
  }

  protected Object[] makeParams(StringBuilder sb) {
    makeFields(sb);

    if (wheres != null) {
      List<Object> params = new ArrayList<>(wheres.size());

      if (wheres.size() > 0) {
        String logicOp = " AND ";
        boolean addLoginOp = false;

        for (int i = 0; i < wheres.size(); i++) {
          SqlWhereValue whereValue = wheres.get(i);

          if (i == 0) {
            sb.append(" WHERE ");
          }

          if (whereValue.getName() == null) {
            Object value = whereValue.getValue();

            if ("(".equals(value)) {
              if (i > 0) {
                sb.append(logicOp);
              }
              addLoginOp = false;
            }

            sb.append(value);
            logicOp = whereValue.getOp();
          } else {
            if (addLoginOp) {
              sb.append(logicOp);
            } else {
              addLoginOp = true;
            }

            sb.append(whereValue.getName()).append(whereValue.getOp()).append('?');
            params.add(whereValue.getValue());
          }
        }
      }

      if (orderBy != null) {
        for (int i = 0; i < orderBy.size(); i++) {
          if (i == 0) {
            sb.append(" ORDER BY ");
          } else {
            sb.append(",");
          }

          sb.append(orderBy.get(i));
        }
      }

      return params.toArray(new Object[params.size()]);
    }

    return null;
  }

  @Override
  public AbstractSelectSqlBuilder addOrderBy(String orderByField) {
    if (this.orderBy == null) {
      this.orderBy = new ArrayList<>();
    }

    this.orderBy.add(orderByField);

    return this;
  }

  public <T> List<T> query(DbEngine dbEngine, Class<T> clazz) {
    return exec((sql, params) -> dbEngine.query(sql, clazz, params));
  }

  public <T> T queryEntity(DbEngine dbEngine, Class<T> clazz) {
    return exec((sql, params) -> dbEngine.queryEntity(sql, clazz, params));

//    StringBuilder sb = new StringBuilder("select ");
//
//    Object[] params = makeParams(sb);
//
//    return dbEngine.queryEntity(sb.toString(), clazz, params);
  }

  public <T> T exec(BiFunction<String, Object[], T> execute) {
    StringBuilder sb = new StringBuilder("select ");

    Object[] params = makeParams(sb);

    return execute.apply(sb.toString(), params);
  }

  public <T> List<T> pagedQuery(DbEngine dbEngine, Class<T> clazz, int start, int limit) {
    return exec((sql, params) -> dbEngine.pagedQuery(sql, clazz, start, limit, params));
//    StringBuilder sb = new StringBuilder("select ");
//
//    Object[] params = makeParams(sb);
//
//    return dbEngine.pagedQuery(sb.toString(), clazz, start, limit, params);
  }

  public int queryInt(JdbcEngine dbEngine) {
    return exec(dbEngine::queryInt);
  }

  public SqlJoinTable addJoinTable(String dbName, String tableName, String tableAlias) {
    return null;
  }

  public SqlJoinTable findJoinTable(String dbName, String tableName) {
    return null;
  }
}
