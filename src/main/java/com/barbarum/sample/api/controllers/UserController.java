package com.barbarum.sample.api.controllers;

import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getUsers() {
        return Collections.emptyList();
    }

    @GetMapping("/me")
    public ResponseEntity<Object> getMyInfo(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken) {
            return ResponseEntity.ok((JwtAuthenticationToken) authentication);
        }
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(user);
    }
}
