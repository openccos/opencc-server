package com.openccos.framework.core.db.sqlbuilder;

import java.util.function.BiFunction;

public class DirectSelectSqlBuilder extends AbstractSelectSqlBuilder {
  private String sql;

  DirectSelectSqlBuilder(String sql) {
    this.sql = sql;
  }

  @Override
  public <T> T exec(BiFunction<String, Object[], T> execute) {
    StringBuilder sb = new StringBuilder(sql);

    Object[] params = makeParams(sb);

    return execute.apply(sb.toString(), params);
  }
}
