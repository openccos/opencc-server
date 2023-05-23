package com.openccos.framework.core.db.jdbc;

/**
 * JDBC事务处理类，可以在try中使用自动结束事务
 * @author xkliu
 */
public abstract class JdbcTrans implements AutoCloseable {
  /**
   * 回滚事务
   */
  public abstract void rollback();

  /**
   * 提交事务
   */
  public abstract boolean commit();

  /**
   * 实现自动关闭，回滚方式结束事务
   */
  @Override
  public void close() {
    this.rollback();
  }

  /**
   * 是否子事务
   */
  public abstract boolean isSpringTrans();
}
