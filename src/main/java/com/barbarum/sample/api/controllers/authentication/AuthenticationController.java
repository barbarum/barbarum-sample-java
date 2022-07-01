package com.barbarum.sample.api.controllers.authentication;

import static com.barbarum.sample.api.PathConstants.LOGIN;

import com.barbarum.sample.api.PathConstants;
import com.barbarum.sample.api.models.AuthenticationForm;
import com.barbarum.sample.api.models.OAuth2AccessToken;
import com.barbarum.sample.service.authentication.AuthService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
public class AuthenticationController {
    
    @Autowired
    private AuthService authService;

    @PostMapping(path = LOGIN, 
        consumes = MediaType.APPLICATION_JSON_VALUE, 
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OAuth2AccessToken> authenticate(@RequestBody AuthenticationForm auth) {
        if (auth == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request body");    
        }
        OAuth2AccessToken content = this.authService.authenticate(auth.getUsername(), auth.getPassword());
        return ResponseEntity.ok()
            .cacheControl(CacheControl.noStore()) // disable token to be stored by browsers.
            .header("Pragma", CacheControl.noCache().getHeaderValue()) // compaitable for old browsers.
            .body(content);
    }

    @GetMapping(LOGIN)
    public ResponseEntity<String> loginPage() {
        log.debug("GET {} is mistakenly called, redirect to login form page.", LOGIN);
        return ResponseEntity
            .status(HttpStatus.MOVED_PERMANENTLY)
            .header(HttpHeaders.LOCATION, PathConstants.LOGIN_FORM)
            .build();
    }
}
