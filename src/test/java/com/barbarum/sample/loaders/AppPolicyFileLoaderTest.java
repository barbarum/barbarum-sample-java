package com.barbarum.sample.loaders;

import static org.assertj.core.api.Assertions.assertThat;

import com.barbarum.sample.loaders.models.Policies;
import com.barbarum.sample.loaders.models.Role;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

@Slf4j
@SpringBootTest
public class AppPolicyFileLoaderTest {
    
    @Test
    public void testLoad() throws IOException {
        String path = "classpath:security/rbac/policies.csv";
        Resource resource = new FileSystemResource(ResourceUtils.getFile(path));
        AppPolicyFileLoader loader = new AppPolicyFileLoader(resource);
        Policies policies = loader.load();

        log.info("Policies load complete (roles: {}, rules: {}).", policies.getAllRoles().size(), policies.getAllPolicies().size());

        assertThat(policies.getAllRoles())
            .extracting(Role::getName)
            .contains("ROLE_USER");
    }

    @Test
    public void testEmptyLoad() throws IOException {
        AppPolicyFileLoader loader = new AppPolicyFileLoader(null);
        Policies policies = loader.load();
        assertThat(policies.getAllRoles()).isEmpty();
        assertThat(policies.getAllPolicies()).isEmpty();
    }
}
