package com.openccos.framework.core.db.jdbc;

import com.openccos.framework.core.spring.RuleProperties;
import org.apache.commons.lang3.StringUtils;

/**
 * SQL转换器，用于处理数据库名称替换
 * @author xkliu
 */
public class TransSql {
  /**
   * 替换Select语句中的库名
   * @param rule 替换规则
   * @param sql SQL语句
   * @return 替换完成的SQL语句
   */
  public static String transSelect(RuleProperties rule, String sql) {
    if (rule == null || StringUtils.isBlank(rule.getPrefix())) {
      return sql;
    }

    int step = 0;
    int wordStart = 0;
    int lastPos = 0;
    StringBuilder sb = null;
    for (int i = 0; i < sql.length(); i++) {
      char ch = sql.charAt(i);
      switch (ch) {
        case ' ':
          if (step == 0) {
            step = 1;
          } else if (step == 2) {
            step = 1;
          }
          break;
        case '\'':
          if (step == 0) {
            step = 3;
          } else if (step == 3) {
            step = 0;
          }
          break;
        case '.':
          if (step == 2) {
            String word = sql.substring(wordStart, i);
            if (word.startsWith(rule.getPrefix())) {
              if (sb == null) {
                sb = new StringBuilder(sql.length() + 64);
              }

              sb.append(sql, lastPos, wordStart);
              sb.append(rule.getReplace());
              sb.append(sql, wordStart + rule.getPrefix().length(), i);
              lastPos = i;
            }
          }
          break;
        default:
          if (step == 1) {
            step = 2;
            wordStart = i;
          }
          break;
      }
    }

    if (sb != null) {
      sb.append(sql.substring(lastPos));
      return sb.toString();
    }

    return sql;
  }

  /**
   * 替换Insert/Update语句中的库名
   * @param rule 替换规则
   * @param sql SQL语句
   * @return 替换完成的SQL语句
   */
  public static String transUpdate(RuleProperties rule, String sql) {
    return transSelect(rule, sql);
  }
}
