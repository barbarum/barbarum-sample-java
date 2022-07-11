package com.barbarum.sample.loaders.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.lang.NonNull;

public interface PolicyEntry {
    
    /**
     * Get system policy entry type.
     * @return
     */
    @NonNull
    public EntryType getType();

    @AllArgsConstructor
    @Getter
    public enum EntryType {
        ROLE("role"),
        POLICY("policy");

        private final String value;
    }
}
