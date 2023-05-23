package com.openccos.framework.core.spring;

import com.openccos.framework.core.db.DbEngine;
import com.openccos.framework.core.db.jdbc.IdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author kevin
 */
@Configuration
@ComponentScan
@EnableScheduling
public class CoreAutoConfiguration {
  /**
   *  ID生成器的分步式机器码（1-1023）
   */
  @Value("${cc.machine-id:1}")
  private int machineId;

  @Bean
  public IdGenerator idGenerator() {
    return new IdGenerator(machineId);
  }

  /**
   * 产生数据库数据库访问对象 dbEngine
   * @param jdbcTemplate Spring框架Jdbc，通过 spring.datasource 配置
   * @param idGenerator ID生成器对象，思想数据库ID生成
   * @return dbEngine对象
   */
  @Bean
  public DbEngine dbEngine(JdbcTemplate jdbcTemplate, IdGenerator idGenerator) {
    return new DbEngine(jdbcTemplate, idGenerator);
  }
}
