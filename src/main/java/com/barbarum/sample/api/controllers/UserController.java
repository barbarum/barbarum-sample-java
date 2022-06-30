package com.barbarum.sample.api.controllers;

import com.barbarum.sample.api.models.User;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    public org.springframework.security.core.userdetails.User getMyInfo(Principal principal) {
        return (org.springframework.security.core.userdetails.User) ((Authentication)principal).getPrincipal();
    }
}
