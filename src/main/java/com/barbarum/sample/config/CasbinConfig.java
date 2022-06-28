package com.barbarum.sample.config;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Casbin configuration.
 */
@Configuration
@ConfigurationProperties(prefix = "framework.casbin")
@Data
public class CasbinConfig {
    
    @Value("${mode-path:classpath:/framework/casbin/model.conf}")
    private String modelPath; 

    @Value("${policy-path:classpath:/framework/casbin/policy.csv}")
    private String policyPath;
}
