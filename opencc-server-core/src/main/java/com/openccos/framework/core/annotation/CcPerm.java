package com.openccos.framework.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD,ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CcPerm {
    // 忽略权限检测
    public static final String NONE = "*";
    // 会话权限
    public static final String SESSION = "";
    /**
     * 权限校验编码
     * @return 校验编码
     */
    String value() default SESSION;
}
