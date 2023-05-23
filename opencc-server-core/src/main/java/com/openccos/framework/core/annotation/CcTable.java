package com.openccos.framework.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 被该注释修饰的类对应了数据库表名（库+表的形式，如 iot_user.sys_user）
 * @author kevin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CcTable {
  /** 库名+表名 */
  String value() default "";
}
