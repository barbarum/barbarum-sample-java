package com.barbarum.sample.api.controllers.authentication;

import com.barbarum.sample.api.controllers.PathConstants;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LoginController {
    
    @GetMapping(PathConstants.LOGIN)
    public ResponseEntity<String> login() {
        log.info("Login Authentication is called!");
        return ResponseEntity
            .status(HttpStatus.MOVED_PERMANENTLY)
            .header(HttpHeaders.LOCATION, PathConstants.LOGIN_FORM)
            .build();
    }
}
