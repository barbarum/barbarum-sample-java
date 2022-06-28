package com.barbarum.sample.config;

import java.io.FileNotFoundException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

/**
 * Casbin configuration.
 */
@Configuration
@ConfigurationProperties(prefix = "framework.casbin")
@Data
public class CasbinConfig {
    
    private static final String DEFAULT_ACL_POLIFY_PATH = "classpath:framework/casbin/acl/policy.csv";

    private static final String DEFAULT_ACL_MODEL_PATH = "classpath:framework/casbin/acl/model.conf";
    
    private static final String DEFAULT_RBAC_MODEL_PATH = "classpath:framework/casbin/rbac/model.conf";

    private static final String DEFAULT_RBAC_POLIFY_PATH = "classpath:framework/casbin/rbac/policy.csv";

    private static final String DEFAULT_ABAC_MODEL_PATH = "classpath:framework/casbin/rbac/model.conf";

    private static final String DEFAULT_ABAC_POLIFY_PATH = "classpath:framework/casbin/rbac/policy.csv";

    private final Config acl = new Config(DEFAULT_ACL_MODEL_PATH, DEFAULT_ACL_POLIFY_PATH); 

    private final Config rbac = new Config(DEFAULT_RBAC_MODEL_PATH, DEFAULT_RBAC_POLIFY_PATH); 

    private final Config abac = new Config(DEFAULT_ABAC_MODEL_PATH, DEFAULT_ABAC_POLIFY_PATH);

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    public static class Config {
    
        private String modelPath; 

        private String policyPath;

        public String getModelPath() throws FileNotFoundException {
            return ResourceUtils.getFile(this.modelPath).getAbsolutePath();
        }
        
        public String getPolicyPath() throws FileNotFoundException {
            return ResourceUtils.getFile(this.policyPath).getAbsolutePath();
        }
    }
}
