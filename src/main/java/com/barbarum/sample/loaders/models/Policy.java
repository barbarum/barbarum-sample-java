package com.barbarum.sample.loaders.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.http.HttpMethod;

@Data
@AllArgsConstructor
public class Policy implements PolicyEntry {

    private String resource; 

    private HttpMethod operation; 

    private Role role; 

    private Effect effect;

    @Override
    public EntryType getType() {
        return EntryType.POLICY;
    }
    
    public enum Effect {
        GRANT, 
        DENY
    }
}
