package com.barbarum.sample.config;

import javax.sql.DataSource;

import org.casbin.annotation.CasbinDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

@Configuration
public class PersistenceConfig {
    
    @Bean(name = "primaryDataSource")
    @Primary
    // @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource configPrimaryDataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.HSQL)
            .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
            .addScript("createAclSchema.sql")
            .build();
    }

    @Bean(name = "casbinDataSource")
    @CasbinDataSource
    // @ConfigurationProperties(prefix = "spring.datasource.casbin")
    public DataSource configCasbinDataSource() {
        return new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.H2)
        .build();
    }
}
