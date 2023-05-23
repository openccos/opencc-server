package com.openccos.framework.core.spring;


import com.openccos.framework.core.redis.RedisManager;
import io.lettuce.core.RedisURI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 〈redis配置〉
 *
 * @author kevin
 * @since 1.0.0
 */
@Configuration
public class RedisConfig {
    @Value("${cc.redis.host:127.0.0.1}")
    private String redisHost;

    @Value("${cc.redis.port:6379}")
    private int redisPort;

    @Value("${cc.redis.username:}")
    private String redisUserName;

    @Value("${cc.redis.password:}")
    private String redisPassword;

    @Value("${cc.redis.database:}")
    private String redisDb;

    @Value("${cc.redis.serializeType:}")
    private String serializeType;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean(destroyMethod = "shutdown")
    public RedisManager redisManager() {
        RedisURI redisUri = RedisURI.create (redisHost, redisPort);
        if (StringUtils.isNotBlank(redisDb)) {
            redisUri.setDatabase(Integer.parseInt(redisDb));
        }
        if (StringUtils.isNotBlank(redisUserName)) {
            redisUri.setUsername(redisUserName.trim());
        }
        if (StringUtils.isNotBlank(redisPassword)) {
            redisUri.setPassword((CharSequence)redisPassword.trim());
        }

        return new RedisManager(applicationContext, redisUri, serializeType);
    }


}
