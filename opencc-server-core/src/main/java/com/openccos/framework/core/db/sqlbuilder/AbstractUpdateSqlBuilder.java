package com.openccos.framework.core.db.sqlbuilder;

import com.openccos.framework.core.db.jdbc.JdbcEngine;

public abstract class AbstractUpdateSqlBuilder<T extends AbstractUpdateSqlBuilder> extends SqlBuilder<T> {
  // 无效的更新值，用以占位表示不组装值到params对象里面
  public static final Object VALUE_INVALID = new SqlFieldValue("", "");

  public boolean isEmpty() {
    return fields.isEmpty();
  }

  public abstract int update(JdbcEngine dbEngine);

  public void updateMap(java.util.Map<String, Object> map) {
    for (SqlFieldValue field: fields) {
      map.put(field.getName(), field.getValue());
    }
  }
}
