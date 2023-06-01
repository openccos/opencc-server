package com.openccos.framework.core.spring;

import com.openccos.framework.core.cache.CacheManager;
import com.openccos.framework.core.db.DbEngine;
import com.openccos.framework.core.permiss.PermInterceptor;
import com.openccos.framework.core.redis.RedisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Autowired
    private RedisManager redisManager;

    @Autowired
    private DbEngine dbEngine;

    public void addInterceptors(InterceptorRegistry registry) {
        // 权限校验拦截器,判断登录状态
        registry.addInterceptor(new PermInterceptor(redisManager, dbEngine));
    }
}
