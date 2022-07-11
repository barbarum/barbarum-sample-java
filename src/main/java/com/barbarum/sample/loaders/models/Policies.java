package com.barbarum.sample.loaders.models;

import com.barbarum.sample.loaders.models.Policy.Effect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

@Slf4j
public class Policies {
    
    private static final String ROLE_PREFIX = "ROLE_";

    private final Map<String, Role> roles = new HashMap<>();

    private final List<Policy> rules = new ArrayList<>();
    
    @Setter
    @Getter
    private String rolePrefix = ROLE_PREFIX;
    
    public Role addRole(String name) {
        return this.addRole(name, null);
    }

    public Role addRole(String name, String parent) {
        Assert.hasText(name, "Role name must not be empty.");
        Role parentRole = StringUtils.isNotBlank(parent) ? this.createRoleIfNotExist(parent) : null;
        Role role = this.createRoleIfNotExist(name);
        role.setParent(parentRole);

        if (hasCycle(role)) {
            String cycleRoleChain = StringUtils.join(this.getCycleRoleChain(role), " -> ");
            throw new IllegalArgumentException("Cycle role chain deteteced " + cycleRoleChain + "!");
        }
        logRoleChainIfTraceEnabled(role);
        return role;
    }
    
    public Optional<Role> getRole(String name) {
        if (StringUtils.isBlank(name)) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.roles.get(toInternalRoleName(name)));
    }

    public List<Role> getAllRoles() {
        return this.roles.values().stream().collect(Collectors.toList());
    }

    public Policy addPolicy(String role, String resource, String operation, String effectValue) {
        if (StringUtils.isAnyBlank(resource)) {
			throw new IllegalArgumentException("Resource must exist while adding policy.");
        }
        Optional<Role> optionalRole = this.getRole(role);
        if (optionalRole.isEmpty()) {
			throw new IllegalArgumentException("Role must exist in the role definitions while adding policy, but it's '" + role + "' now.");
        }
        HttpMethod method = this.toHttpMethod(operation);
        Effect effect = this.toEffect(effectValue, Effect.GRANT);
        Policy policy = new Policy(resource, method, optionalRole.orElse(null), effect);
        this.rules.add(policy);
        return policy;
    }

    public List<Policy> getAllPolicies() {
        return this.rules;
    }

    public boolean isEmpty() {
        return this.roles.isEmpty() && this.rules.isEmpty();
    }

    private void logRoleChainIfTraceEnabled(Role role) {
        if (!log.isTraceEnabled()) {
            return;
        }
        log.trace("Role chain while adding role ({}): {}", 
            role.getName(),
            StringUtils.join(this.toRoleChain(role), " -> "));
    }

    private Role createRoleIfNotExist(String name) {
        return this.roles.computeIfAbsent(toInternalRoleName(name), key -> new Role(key, null));
    }

    private String toInternalRoleName(String name) {
        String key = this.toUpperCase(name);
        return StringUtils.startsWith(key, this.getRolePrefix()) ? key : this.getRolePrefix() + key;
    }

    private Effect toEffect(String effect, Effect defaultEffect) {
        if (StringUtils.isBlank(effect)) {
            return defaultEffect;
        }
        Effect result = Effect.valueOf(this.toUpperCase(effect));
        return result != null ? result : defaultEffect;
    }

    private HttpMethod toHttpMethod(String operation) {
        if (StringUtils.isBlank(operation)) {
            return null;
        }
        return HttpMethod.resolve(this.toUpperCase(operation));
    }

    private String toUpperCase(String value) {
        return StringUtils.toRootUpperCase(StringUtils.trimToEmpty(value));
    }

    private List<String> toRoleChain(Role role) {
        return this.toRoleChain(role, Objects::nonNull);
    }
    
    private List<String> getCycleRoleChain(Role role) {
        List<String> result = this.toRoleChain(role, e -> e != null && e != role);
        if (role != null) {
            result.add(role.getName());   
        }
        return result;
    }

    private List<String> toRoleChain(Role role, Predicate<Role> predicate) {
        if (role == null) {
            return new ArrayList<>(0);
        }
        Role node = role;
        List<String> result = new ArrayList<>();
        do {
            result.add(node.getName()); 
            node = node.getParent();
        } while (predicate.test(node));
        return result;
    }

    private boolean hasCycle(Role node) {
        Role fast = node; 
        Role slow = node; 
        while (fast != null && fast.getParent() != null) {
            fast = fast.getParent().getParent();
            slow = slow.getParent();
            if(fast == slow) {
                return true;
            }
        }
        return false;
    }
}
