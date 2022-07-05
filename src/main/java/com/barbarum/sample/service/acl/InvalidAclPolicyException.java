package com.barbarum.sample.service.acl;

import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.BeansException;

public class InvalidAclPolicyException extends BeansException {

    private static final String DEFAULT_MESSAGE_FORMAT_PATTERN = "Invalid return object class {}, @PostPersistAclPolicy expects either an entity with identifier, or a collection of entities.";

    public InvalidAclPolicyException(String msg) {
        super(msg);
    }

    public InvalidAclPolicyException(Class<?> clazz) {
        this(MessageFormatter.format(DEFAULT_MESSAGE_FORMAT_PATTERN, clazz).getMessage());
    }

}
