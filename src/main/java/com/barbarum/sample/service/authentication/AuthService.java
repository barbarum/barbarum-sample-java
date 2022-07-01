package com.barbarum.sample.service.authentication;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.barbarum.sample.api.models.OAuth2AccessToken;
import com.google.common.collect.ImmutableMap;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AuthService {
    
    @Autowired
    private UserDetailsService service; 

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;
    
    public OAuth2AccessToken authenticate(String username, String password) {
        if (StringUtils.isAnyBlank(username, password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");    
        }
        
        UserDetails userDetails = loadUser(username);
        if (!this.passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User " + username + " is not authenticated");
        }
        String authorities = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(" "));
        Map<String, String> claims = ImmutableMap.of("id", String.valueOf(1), "scope", authorities);

        Duration expiredIn = Duration.ofHours(4);
        DecodedJWT jwt = this.jwtService.issue(userDetails.getUsername(), expiredIn, claims);

        return OAuth2AccessToken.builder()
            .accessToken(jwt.getToken())
            .tokenType(TokenType.BEARER.getValue())
            .scope(authorities)
            .expiresIn(Duration.between(Instant.now(), jwt.getExpiresAt().toInstant()).toSeconds())
            .build();
    }

    private UserDetails loadUser(String username) {
        try {
            return this.service.loadUserByUsername(username);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User " + username + " is not found");
        }
    }
}
