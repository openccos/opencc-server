package com.openccos.framework.core.db.jdbc;

/**
 * 子事务，处理嵌套事务执行
 * @author xkliu
 */
public class JdbcChildTrans extends JdbcTrans {
  private JdbcSpringTrans jdbcSpringTrans;

  public JdbcChildTrans(JdbcSpringTrans jdbcSpringTrans) {
    this.jdbcSpringTrans = jdbcSpringTrans;
  }

  @Override
  public void rollback() {
    jdbcSpringTrans.rollback();
  }

  @Override
  public boolean commit() {
    return jdbcSpringTrans.needCommit();
  }

  @Override
  public boolean isSpringTrans() {
    return false;
  }
}
