package com.barbarum.sample.api.controllers.authentication;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LoginController {
    
    public static final String LOGIN_PATH = "/auth/login";

    @PostMapping(LOGIN_PATH)
    public String login() {
        log.info("Login Authentication is called!");
        return "login";
    }
}
