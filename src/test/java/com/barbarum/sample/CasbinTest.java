package com.barbarum.sample;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.barbarum.sample.config.CasbinConfig;

import java.io.FileNotFoundException;

import lombok.extern.slf4j.Slf4j;

import org.casbin.jcasbin.main.Enforcer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("it")
@Slf4j
public class CasbinTest {
    
    @Autowired
    private CasbinConfig config;

    @Test
    public void testAcl() throws FileNotFoundException {

        CasbinConfig.Config acl = this.config.getAcl(); 
        Enforcer enforcer = new Enforcer(acl.getModelPath(), acl.getPolicyPath(), false);

        assertTrue(enforcer.enforce("Ming", "domain", "read"));
        assertFalse(enforcer.enforce("Ming", "domain", "write"));
        assertFalse(enforcer.enforce("Meimei", "domain", "read"));
        assertTrue(enforcer.enforce("Meimei", "data-source", "write"));
    }

    @Test
    public void testRbac() throws FileNotFoundException {
        String user = "Ming";

        // rbac
        CasbinConfig.Config rbac = this.config.getRbac(); 
        Enforcer enforcer = new Enforcer(rbac.getModelPath(), rbac.getPolicyPath(), false);  
        assertTrue(enforcer.enforce(user, "domain", "read"));
        assertTrue(enforcer.enforce(user, "domain", "write"));

        // rbac with role hierarchy
        rbac.setPolicyPath("classpath:casbin/rbac/policy_with_role_hierarchy.csv");
        enforcer = new Enforcer(rbac.getModelPath(), rbac.getPolicyPath(), false);

        log.info("{}: {}", user, enforcer.getRolesForUser(user));
        assertTrue(enforcer.enforce(user, "management", "read"));
        assertTrue(enforcer.enforce(user, "management", "write"));          
        assertTrue(enforcer.enforce(user, "domain", "read"));
        assertTrue(enforcer.enforce(user, "domain", "write"));

        // rbac with resource hierarchy
        rbac.setModelPath("classpath:casbin/rbac/model_with_resource_hierarchy.conf");
        rbac.setPolicyPath("classpath:casbin/rbac/policy_with_resource_hierarchy.csv");
        enforcer = new Enforcer(rbac.getModelPath(), rbac.getPolicyPath(), false);

        log.info("{}: {}", user, enforcer.getRolesForUser(user));
        assertTrue(enforcer.enforce(user, "management", "read"));
        assertTrue(enforcer.enforce(user, "management", "write"));          
        assertTrue(enforcer.enforce(user, "domain", "read"));
        assertTrue(enforcer.enforce(user, "domain", "write"));
        assertTrue(enforcer.enforce(user, "metrics", "read"));
        assertTrue(enforcer.enforce(user, "atomic_metrics", "write"));
    }
}
