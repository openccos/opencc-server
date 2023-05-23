package com.openccos.framework.core.db.sqlbuilder;

import lombok.Getter;

@Getter
class SqlFieldValue {
  private final String name;
  private final Object value;

  public SqlFieldValue(String name, Object value) {
    this.name = name;
    this.value = value;
  }
}
