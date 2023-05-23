package com.openccos.framework.core.spring;

import lombok.Data;

/**
 * 数据库名字替代规则，目前支持前缀替代
 * @author xkilu
 */
@Data
public class RuleProperties {
  /** 数据库需要替代的前缀 */
  private String prefix;
  /** 数据库替代前缀匹配后的替代值 */
  private String replace;
}
