package com.openccos.framework.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 被该注释修饰的方法对应了字段名和类型
 * @author kevin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface CcColumn {
  String value() default "";

  /** 字段作用，前端用RefType表示 */
  Type[] type() default {};

  public static enum Type {
    // 主键
    ID,
    // 上级ID，树结构需要
    PARENT_ID,
    // 排序字段，树结构需要
    ORDER,
    // 编码字段
    CODE,
    // 名词字段
    NAME,
    // 主表ID，MapToOne
    MASTER_ID,
    // 创建时间
    CREATE_TIME,
    // 更新时间
    LAST_TIME,
  }
}
