package com.openccos.framework.core.db.jdbc;

import com.openccos.framework.core.SwMap;
import com.openccos.framework.core.spring.RuleProperties;
import com.openccos.framework.core.exception.DbException;
import com.openccos.framework.core.util.JsonUtil;
import com.openccos.framework.core.util.SwConsumer;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * JDBC访问类，包装了spring jdbcTemplate对象
 */
public class JdbcEngine implements IIdGenerator {
  private JdbcTemplate jdbcTemplate;
  private DataSourceTransactionManager dataSourceTransactionManager;
  @Getter
  private IdGenerator idGenerator;
  @Getter @Setter
  protected RuleProperties rule;
//  private JdbcSpringTrans jdbcTrans;
  // 线程存储处理多线程SQL事务
  private static ThreadLocal<JdbcSpringTrans> jdbcSpringTrans = new ThreadLocal<>();

  public JdbcEngine(JdbcTemplate jdbcTemplate, IdGenerator idGenerator) {
    this.jdbcTemplate = jdbcTemplate;
    this.dataSourceTransactionManager = new DataSourceTransactionManager(jdbcTemplate.getDataSource());
    this.idGenerator = idGenerator;
  }

  /**
   * 获取数据库唯一id
   * @return 返回ID值
   */
  @Override
  public long nextId() {
    return this.idGenerator.nextId();
  }

  /**
   * 查询单行数据
   * @param sql 查询SQL
   * @param rowHandler ResultSet处理器
   * @param <T> 返回得对象类型
   * @return 返回单个对象数据
   */
  public <T> T queryEntity(String sql, final ResultSetExtractor<T> rowHandler) {
      return jdbcTemplate.query(transQuerySql(sql), rowHandler);
  }

  /**
   * 查询单行数据
   * @param sql 查询SQL
   * @param rowHandler ResultSet处理器
   * @param params SQL参数
   * @param <T> 返回得对象类型
   * @return 返回单个对象数据
   */
  public <T> T queryEntity(String sql, final ResultSetExtractor<T> rowHandler, Object... params) {
      return jdbcTemplate.query(transQuerySql(sql), rowHandler, params);
  }

  /** 通过回调函数查询SQL，返回列表或者对象数据 */
  public <T> T query(String sql, ResultSetExtractor<T> rse, Object... params) {
      return jdbcTemplate.query(transQuerySql(sql), rse, params);
  }

  /** 查询SQL，返回列表数据 */
  public <T> List<T> query(String sql, RowMapper<T> rowMapper) {
      return jdbcTemplate.query(transQuerySql(sql), rowMapper);
  }

  /** 查询SQL，返回列表数据 */
  public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... params) {
      return jdbcTemplate.query(transQuerySql(sql), rowMapper, params);
  }

  /** 翻页查询 */
  public <T> List<T> pagedQuery(String sql, RowMapper<T> rowMapper, int start, int limit, Object... params) {
      return jdbcTemplate.query(transQuerySql(sql) + " LIMIT " + start + "," + limit, rowMapper, params);
  }

  /** 执行更新SQL */
  public int update(String sql) {
      return jdbcTemplate.update(transUpdateSql(sql));
  }

  /** 执行更新SQL */
  public int update(String sql, Object... params) {
      return jdbcTemplate.update(transUpdateSql(sql), params);
  }

  /** 执行新增SQL，并返回自动增长key */
  public Number updateAutoKey(String sql, Object... params) {
    final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    int ret = jdbcTemplate.update(connection -> {
      //这里要注意5.1.7版本之后要加入Statement.RETURN_GENERATED_KEYS才可获取自增长的主键id
      PreparedStatement prepareStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

      if (params != null && params.length > 0) {
        for (int i = 0; i < params.length; i++) {
          Object param = params[i];
          prepareStatement.setObject(i + 1, param);
        }
      }

      return prepareStatement;
    }, keyHolder);

    if (ret <= 0) {
      return null;
    }

    return keyHolder.getKey();
  }

  /**
   * 执行更新语句直到无更新数据或者达到最大修改记录数
   * @param sql 需要执行的SQL语句
   * @param limit 每次更新的条数
   * @param count 最大更新次数
   * @param params 执行参数
   * @return 返回更新的数据数量
   */
  public int pagedUpdate(String sql, int limit, int count, Object... params) {
    int result = 0;
    for (int i = 0, ret = 1; i < count && ret > 0; i++) {
      ret = jdbcTemplate.update(transUpdateSql(sql) + " LIMIT " + limit, params);
      if (ret > 0) {
        result += ret;
      }
    }

    return result;
  }

  /**
   * 查询字符list
   * @param sql 查询SQL
   * @param params 查询SQL参数
   * @return 字符list
   */
  public List<String> queryStringList(String sql, Object... params) {
      return jdbcTemplate.query(transQuerySql(sql), (resultSet, i) -> resultSet.getString(1), params);
  }

  /**
   * 查询字符set
   * @param sql 查询SQL
   * @param params 查询SQL参数
   * @return 字符set
   */
  public Set<String> queryStringSet(String sql, Object... params) {
      return jdbcTemplate.query(transQuerySql(sql), (resultSet) -> {
          Set<String> result = new HashSet<>();
          while (resultSet.next()) {
              String value = resultSet.getString(1);
              if (value != null) {
                  result.add(value);
              }
          }

          return result;
      }, params);
  }

  /**
   * 查询字符
   * @param sql 查询SQL
   * @param params 查询SQL参数
   * @return 字符
   */
  public String queryString(String sql, Object... params) {
    List<String> list = jdbcTemplate.query(transQuerySql(sql), (resultSet, i) -> resultSet.getString(1), params);
    if (list != null && !list.isEmpty()) {
      return list.get(0);
    }

    return null;
  }

  /**
   * 查询浮点值
   * @param sql 查询SQL
   * @param params 查询SQL参数
   * @return 字符
   */
  public Double queryDouble(String sql, Object... params) {
    List<Double> list = jdbcTemplate.query(transQuerySql(sql), (resultSet, i) -> resultSet.getDouble(1), params);
    if (list != null && !list.isEmpty()) {
      return list.get(0);
    }

    return null;
  }

  /**
   * 查询值为JSON格式，转换为对象返回
   * @param sql 查询SQL
   * @param clazz 对象类
   * @param params 查询SQL参数
   * @param <T> 对象类型
   * @return JSON格式，转换为对象返回
   */
  public <T> T queryJson(String sql, Class<T> clazz, Object... params) {
    List<String> list = jdbcTemplate.query(transQuerySql(sql), (resultSet, i) -> resultSet.getString(1), params);
    if (list != null && !list.isEmpty()) {
      String s = list.get(0);
      if (StringUtils.isNotBlank(s)) {
        return JsonUtil.parse(s, clazz);
      } else {
        try {
          return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
          throw new DbException(e);
        }
      }
    }

    return null;
  }

  /**
   * 查询Long对象
   * @param sql 查询SQL
   * @param params 查询SQL参数
   * @return Long对象
   */
  public Long queryLong(String sql, Object... params) {
      List<Long> list = jdbcTemplate.query(transQuerySql(sql), (resultSet, i) -> resultSet.getLong(1), params);
      if (list != null && !list.isEmpty()) {
          return list.get(0);
      }

      return null;
  }

  /**
   * 查询List<Long>对象
   * @param sql 查询SQL
   * @param params 查询SQL参数
   * @return List<Long>对象
   */
  public List<Long> queryLongList(String sql, Object... params) {
      return jdbcTemplate.query(transQuerySql(sql), (resultSet, i) -> resultSet.getLong(1), params);
  }

  /**
   * 查询Set<Long>对象
   * @param sql 查询SQL
   * @param params 查询SQL参数
   * @return Set<Long>对象
   */
  public Set<Long> queryLongSet(String sql, Object... params) {
      return jdbcTemplate.query(transQuerySql(sql), (resultSet) -> {
          Set<Long> result = new HashSet<>();
          while (resultSet.next()) {
              long value = resultSet.getLong(1);
              if (!resultSet.wasNull()) {
                  result.add(value);
              }
          }

          return result;
      }, params);
  }

  /**
   * 查询Integer对象
   * @param sql 查询SQL
   * @param params 查询SQL参数
   * @return Integer对象
   */
  public Integer queryInt(String sql, Object... params) {
      List<Integer> list = jdbcTemplate.query(transQuerySql(sql), (resultSet, i) -> resultSet.getInt(1), params);
      if (list != null && !list.isEmpty()) {
          return list.get(0);
      }

      return null;
  }

  /**
   * 执行批量更新语句
   * @param sql SQL语句
   * @param params 数组参数列表
   * @return 执行结果数组
   */
  public int[] batchUpdate(String sql, List<Object[]> params) {
      return jdbcTemplate.batchUpdate(transQuerySql(sql), params);
  }

  private String transQuerySql(String sql) {
      return TransSql.transSelect(rule, sql);
  }

  private String transUpdateSql(String sql) {
      return TransSql.transUpdate(rule, sql);
  }

  /**
   * 启动事务,需要用try(DbTrans dbTrans=dbEngine.openTrans())来处理异常后自动回滚事务，如果手工提交事务DbTaans.commit()
   * @return 事务对象
   */
  private JdbcTrans openTrans() {
    JdbcSpringTrans jdbcTrans = jdbcSpringTrans.get();
    if (jdbcTrans == null) {
      jdbcTrans = new JdbcSpringTrans(this.dataSourceTransactionManager);
      jdbcSpringTrans.set(jdbcTrans);
      return jdbcTrans;
    } else {
      return new JdbcChildTrans(jdbcTrans);
    }
  }

  /**
   * 在回调函数中执行事务，返回的true就提交事务，否则回滚事务
   * @param extractor 回调方法
   * @return 事务是否成功，如果异常会也会事务回滚后再抛出异常
   */
  public boolean doTrans(Supplier<Boolean> extractor) {
      boolean clean = false;
      try(JdbcTrans trans = openTrans()) {
          if (trans.isSpringTrans()) {
            clean = true;
          }

          boolean result = extractor.get();
          if (result) {
              trans.commit();
              return true;
          }
      } finally {
        if (clean) {
          jdbcSpringTrans.remove();
        }
      }

      return false;
  }

  /**
   * 执行事务SQL，产生异常事务回滚，手动也可以实现事务回滚
   * @param run 执行事务SQL
   */
  public void doTrans(Consumer<JdbcTrans> run) {
    doTrans(run, null, null);
  }

  /**
   * 执行事务SQL，成功可回调函数
   * @param run 执行事务SQL
   * @param success 成功回调函数
   */
  public void doTrans(Consumer<JdbcTrans> run, SwConsumer success) {
    doTrans(run, success, null);
  }

  /**
   * 执行事务SQL，成功和失败可回调函数
   * @param run 执行事务SQL
   * @param success 成功回调函数
   * @param failed 失败回调函数
   */
  public void doTrans(Consumer<JdbcTrans> run, SwConsumer success, SwConsumer failed) {
    boolean commit = false;
    boolean clean = false;
    try(JdbcTrans trans = openTrans()) {
      if (trans.isSpringTrans()) {
        clean = true;
      }

      run.accept(trans);
      commit = trans.commit();
    } finally {
      // 最外层事务需要清空事务变量
      if (clean) {
        jdbcSpringTrans.remove();
      }
      // 事务成功/失败调用
      if (commit) {
        if (success != null) {
          success.accept();
        }
      } else if (failed != null) {
        failed.accept();
      }
    }

  }

  /**
   * 获取原始数据库连接执行命令
   * @return 数据库连接
   */
  public <T> T doConn(Function<Connection, T> extractor) {
      DataSource ds = Objects.requireNonNull(this.jdbcTemplate.getDataSource());

      try(Connection conn = ds.getConnection()) {
          return extractor.apply(conn);
      } catch (SQLException e) {
          throw new DbException(e);
      }
  }

  /** 查询单行数据，返回bean */
  public <T> T queryEntity(String sql, Class<T> type) {
    List<T> list = jdbcTemplate.query(transQuerySql(sql), createRowMapper(type));

    if (list != null && !list.isEmpty()) {
      return list.get(0);
    }

    return null;
  }

  /** 查询单行数据，返回bean */
  public <T> T queryEntity(String sql, Class<T> type, Object... params) {
    List<T> list = query(sql, createRowMapper(type), params);

    if (list != null && !list.isEmpty()) {
      return list.get(0);
    }

    return null;
  }

  /** 通过回调函数查询SQL，返回列表或者对象数据 */
  public <T> T query(String sql, ResultSetExtractor<T> rse) {
    return jdbcTemplate.query(sql, rse);
  }

  /** 查询SQL，返回列表数据 */
  public <T> List<T> query(String sql, Class<T> type) {
    return jdbcTemplate.query(transQuerySql(sql), createRowMapper(type));
  }

  /** 查询SQL，返回列表数据 */
  public <T> List<T> query(String sql, Class<T> type, Object... params) {
    return jdbcTemplate.query(transQuerySql(sql), createRowMapper(type), params);
  }

  /** 翻页查询 */
  public <T> List<T> pagedQuery(String sql, Class<T> type, int start, int limit) {
    return jdbcTemplate.query(transQuerySql(sql) + " LIMIT " + start + "," + limit, createRowMapper(type));
  }

  private <T> RowMapper<T> createRowMapper(Class<T> type) {
    RowMapper<T> rowMapper;
    if (java.util.Map.class.isAssignableFrom(type)) {
      if (SwMap.class.equals(type)) {
        rowMapper = new SwMapPropertyRowMapper<>(type);
      } else {
        rowMapper = new MapPropertyRowMapper<>(type);
      }
    } else {
      rowMapper = new BeanPropertyRowMapper<>(type);
    }
    return rowMapper;
  }

  /** 翻页查询 */
  public <T> List<T> pagedQuery(String sql, Class<T> type, int start, int limit, Object... params) {
    return jdbcTemplate.query(transQuerySql(sql) + " LIMIT " + start + "," + limit, createRowMapper(type), params);
  }
}
