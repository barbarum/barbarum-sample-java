package com.barbarum.sample.service.acl;

import java.util.Collection;

import javax.persistence.EntityManager;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class AclPersistenceService {
    
    @Autowired
    private EntityManager entityManager; 

    @AfterReturning(value = "@annotation(PostAclEntityPersist)", returning = "returnObject")
    public void persistAclEntity(JoinPoint joinPoint, Object returnObject) {
        validateReturnObjectClass(returnObject);
    }

    private void validateReturnObjectClass(Object returnObject) {
        if (returnObject == null) {
            log.error("Return object must be a non-null entity.");
            throw new InvalidAclEntityException((Class<Object>) null);
        }
        if (Collection.class.isAssignableFrom(returnObject.getClass())) {
            Collection<?> c = (Collection<?>) returnObject;
            c.forEach(this::validateReturnObjectClass);
            return;
        }
        if (isEntity(returnObject)) {
            log.info("Return object class: {}, isEntity: {}, Id: {}", 
                returnObject.getClass(), 
                isEntity(returnObject), 
                getEntityIdentifier(returnObject));
            return;
        }
        throw new InvalidAclEntityException(returnObject.getClass());
    }

    private boolean isEntity(Object returnObject) {
        return this.entityManager.contains(returnObject);
    }

    private Object getEntityIdentifier(Object returnObject) {
        return this.entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(returnObject);
    }
}
