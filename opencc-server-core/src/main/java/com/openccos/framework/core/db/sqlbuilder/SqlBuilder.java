package com.openccos.framework.core.db.sqlbuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * SQL语句建造器
 * @author admin
 * @param <T> 建造器类型
 */
public abstract class SqlBuilder<T extends SqlBuilder> {
  protected List<SqlFieldValue> fields = new ArrayList<>();
  protected List<SqlWhereValue> wheres;

  SqlBuilder() {
  }

  public static InsertSqlBuilder createInsert(String tableName) {
    return new InsertSqlBuilder(tableName);
  }
  public static UpdateSqlBuilder createUpdate(String tableName) {
    return new UpdateSqlBuilder(tableName);
  }
  public static SelectSqlBuilder createSelect(String tableName) {
    return new SelectSqlBuilder(tableName);
  }
  public static DeleteSqlBuilder createDelete(String tableName) {
    return new DeleteSqlBuilder(tableName);
  }

  public static InsertSqlBuilder createInsert(String dbName, String tableName) {
    return new InsertSqlBuilder(dbName + "." + tableName);
  }

  public static UpdateSqlBuilder createUpdate(String dbName, String tableName) {
    return new UpdateSqlBuilder(dbName + "." + tableName);
  }

  public static AbstractSelectSqlBuilder createSelect(String dbName, String tableName) {
    return new SelectSqlBuilder(dbName + "." + tableName);
  }

  public static AbstractSelectSqlBuilder createDirectSelect(String sql) {
    return new DirectSelectSqlBuilder(sql);
  }

  public static DeleteSqlBuilder createDelete(String dbName, String tableName) {
    return new DeleteSqlBuilder(dbName + "." + tableName);
  }

  public T add(String fieldName) {
    fields.add(new SqlFieldValue(fieldName, null));
    return (T)this;
  }

  public T add(String fieldName, Object fieldValue) {
    fields.add(new SqlFieldValue(fieldName, fieldValue));
    return (T)this;
  }

  public T addWhere(String fieldName, Object fieldValue, String op) {
    if (wheres == null) {
      wheres = new ArrayList<>();
    }
    wheres.add(new SqlWhereValue(fieldName, fieldValue, op));
    return (T)this;
  }

  public T addWhere(String fieldName, Object fieldValue) {
//    if (fieldValue != null) {
      return addWhere(fieldName, fieldValue, "=");
//    } else {
//      return addWhere(fieldName, null, " is NULL ");
//    }
  }

  public T addWhereOrBegin() {
    return addWhere(null, "(", " or ");
  }

  public T addWhereOrEnd() {
    return addWhere(null, ")", " and ");
  }

  public T addOrderBy(String orderBy) {
    return (T)this;
  }
}
