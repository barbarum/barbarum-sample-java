package com.barbarum.sample.config;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

import org.casbin.annotation.CasbinDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceConfig {
    
    @Bean(name = "casbinDataSource")
    @CasbinDataSource
    @ConfigurationProperties(prefix = "spring.datasource.casbin")
    public DataSource configCasbinDataSource() {
        return DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .build();
    }
}
