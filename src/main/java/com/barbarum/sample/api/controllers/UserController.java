package com.barbarum.sample.api.controllers;

import com.barbarum.sample.api.models.User;

import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    
    @GetMapping("/users")
    public List<User> getUsers() {
        return Collections.emptyList();
    }
}
