package com.openccos.framework.core.db.jdbc;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * JDBC事务处理类，可以在try中使用自动结束事务
 * @author xkliu
 */
public class JdbcSpringTrans extends JdbcTrans {
  private DataSourceTransactionManager transactionManager;
  private TransactionStatus status;

  /**
   * 构造事务执行
   * @param transactionManager spring事务管理对象
   */
  public JdbcSpringTrans(DataSourceTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
    // 事务定义类
    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    // 返回事务对象
    this.status = transactionManager.getTransaction(def);
  }

  /**
   * 回滚事务
   */
  @Override
  public void rollback() {
    if (status != null) {
      transactionManager.rollback(status);
      status = null;
    }
  }

  /**
   * 提交事务
   */
  @Override
  public boolean commit() {
    if (status != null) {
      transactionManager.commit(status);
      status = null;
      return true;
    }

    return false;
  }

  /**
   * 实现自动关闭，回滚方式结束事务
   */
  @Override
  public void close() {
    this.rollback();
  }

  public boolean needCommit() {
    return status != null;
  }

  @Override
  public boolean isSpringTrans() {
    return true;
  }
}
