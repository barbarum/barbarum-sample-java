package com.barbarum.sample.service.acl;

import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.BeansException;

public class InvalidAclEntityException extends BeansException {

    private static final String DEFAULT_MESSAGE_FORMAT_PATTERN = "Invalid return object class {}, @PostAclEntityPersist expects either an entity with identifier, or a collection of entities.";

    public InvalidAclEntityException(String msg) {
        super(msg);
    }

    public InvalidAclEntityException(Class<?> clazz) {
        this(MessageFormatter.format(DEFAULT_MESSAGE_FORMAT_PATTERN, clazz).getMessage());
    }

}
