package com.barbarum.sample.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    
    @GetMapping(value = {"/", "/home", "/welcome"})
    public ResponseEntity<String> welcome() {
        return ResponseEntity.ok().build();
    }
    
}
