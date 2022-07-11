package com.barbarum.sample.loaders.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Role implements PolicyEntry {

    private String name; 

    private Role parent;

    @Override
    public EntryType getType() {
        return EntryType.ROLE;
    }

}
