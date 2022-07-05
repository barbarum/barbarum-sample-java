package com.barbarum.sample.config.security;

import com.barbarum.sample.service.acl.CumulativePermissionGrantingStrategy;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.EhCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import net.sf.ehcache.CacheManager;

@SuppressWarnings("deprecation")
@Slf4j
@Configuration
@EnableAutoConfiguration
public class AclSecurityConfig {

    /**
     * Registered {@link PermissionEvaluator} bean will be automatically registered into {@linkplain GlobalMethodSecurityConfiguration} 
     *  for {@link hasPermission(...)} method interception inside {@link PreAuthorize}/{@link PostAuthorize} annotation.
     * 
     * @see {@link PermissionEvaluator}, {@link GlobalMethodSecurityConfiguration#afterSingletonsInstantiated()}, 
     */
    @Bean
    public AclPermissionEvaluator permissionEvaluator(JdbcMutableAclService aclService) {
        return new AclPermissionEvaluator(aclService);
    }

    @Bean
    public JdbcMutableAclService jdbcMutableAclService(DataSource dataSource, LookupStrategy lookupStrategy, AclCache aclCache) {
        JdbcMutableAclService service = new JdbcMutableAclService(dataSource, lookupStrategy, aclCache);
        customizeQueries(service, dataSource);
        return service;
    }

    private void customizeQueries(JdbcMutableAclService service, DataSource dataSource) {
        try(Connection connection = dataSource.getConnection()) {
            String driverClassName = connection.getClass().getName();

            if (StringUtils.containsAnyIgnoreCase(driverClassName, "mysql")) {
                service.setClassIdentityQuery("SELECT @@IDENTITY");
                service.setSidIdentityQuery("SELECT @@IDENTITY");
            }

        } catch (SQLException e) {
            log.error("Failed to customize JdbcMutableAclService query on DataSource: {}", dataSource);
        }
    }

    @Bean
    public LookupStrategy lookupStrategy(DataSource dataSource, AclCache aclCache, AclAuthorizationStrategy strategy) {
        return new BasicLookupStrategy(dataSource, aclCache, strategy, auditLogger());
    }

    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new CumulativePermissionGrantingStrategy(auditLogger());
    }

    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");
        return new AclAuthorizationStrategyImpl(authority);
    }

    @Bean
    public AuditLogger auditLogger() {
        return new ConsoleAuditLogger();
    }

    @Bean
    public AclCache aclCache(EhCacheFactoryBean ehCacheFactory, 
        PermissionGrantingStrategy permissionGrantingStrategy, 
        AclAuthorizationStrategy aclAuthorizationStrategy) {
        return new EhCacheBasedAclCache(ehCacheFactory.getObject(), permissionGrantingStrategy, aclAuthorizationStrategy);
    }

    @Bean
    public EhCacheFactoryBean ehCacheFactory(EhCacheManagerFactoryBean ehCacheManagerFactoryBean) {
        CacheManager cacheManager = ehCacheManagerFactoryBean.getObject();
        if (cacheManager == null) {
            throw new ApplicationContextException(
                    "Unable to initialize an cache manager from EhCacheManagerFactoryBean.");
        }
        EhCacheFactoryBean bean = new EhCacheFactoryBean();
        bean.setCacheManager(cacheManager);
        bean.setCacheName("barbarmAclCache");
        return bean;
    }

    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
        return new EhCacheManagerFactoryBean();
    }
}
