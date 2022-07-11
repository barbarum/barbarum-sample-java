package com.barbarum.sample.utils;

import com.barbarum.sample.loaders.models.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * Role related utilities.
 */
public class RoleUtils {

    private RoleUtils() { }
    /**
     * Transform a list of roles from Policies into role hierarchy string. 
     *
     * @param roles the given role list.
     * @return 
     * @see {@link org.springframework.security.access.hierarchicalroles.RoleHierarchy}
     */
    public static String toRoleHierarchy(List<Role> roles) {
        List<List<String>> roleLeaves = buildRoleLeaves(roles);
        List<String> roleLeafPaths = roleLeaves.stream()
            .map(e -> StringUtils.join(e, " > "))
            .collect(Collectors.toList());
        return StringUtils.join(roleLeafPaths, "\n");
    }

    private static List<List<String>> buildRoleLeaves(List<Role> roles) {
        Set<String> parents = roles.stream()
            .filter(e -> e.getParent() != null)
            .map(Role::getParent)
            .map(Role::getName)
            .collect(Collectors.toSet());

        return roles.stream()
            .filter(e -> !parents.contains(e.getName()))
            .map(RoleUtils::toRoleChain)
            .collect(Collectors.toList());
    }

    /**
     * Explore a role's chain. 
     *
     * @param role the given role.
     * @return
     */
    public static List<String> toRoleChain(Role role) { 
        return RoleUtils.toRoleChain(role, Objects::nonNull);
    }

    private static List<String> toRoleChain(Role role, Predicate<Role> predicate) {
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

}
