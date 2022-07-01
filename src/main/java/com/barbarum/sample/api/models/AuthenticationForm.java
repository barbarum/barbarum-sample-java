package com.barbarum.sample.api.models;

import lombok.Data;

@Data
public class AuthenticationForm {
    
    private String username; 

    private String password;
}
