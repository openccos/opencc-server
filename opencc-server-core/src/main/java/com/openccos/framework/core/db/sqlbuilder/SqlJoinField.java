package com.openccos.framework.core.db.sqlbuilder;

import lombok.Data;
import lombok.Getter;

@Getter
public class SqlJoinField {
  private String keyField;
  private String valueField;

  public SqlJoinField(String keyField, String valueField) {
    this.keyField = keyField;
    this.valueField = valueField;
  }
}
