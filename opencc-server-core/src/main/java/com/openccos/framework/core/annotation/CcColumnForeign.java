package com.openccos.framework.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 被该注释修饰的方法对应了外键表名和字段名，名称可选
 * @author kevin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface CcColumnForeign {
  // 外键表名
  String table() default "";
  // ID字段名
  String id() default "";
  // 唯一名称字段名
  String code() default "";
}
