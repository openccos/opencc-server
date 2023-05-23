package com.openccos.framework.core.db.sqlbuilder;

import lombok.Getter;

class SqlWhereValue extends SqlFieldValue {
  @Getter
  private String op;

  public SqlWhereValue(String name, Object value, String op) {
    super(name, value);
    this.op = op;
  }
}
