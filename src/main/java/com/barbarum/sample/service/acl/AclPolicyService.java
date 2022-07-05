package com.barbarum.sample.service.acl;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.EntityManager;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class AclPolicyService {
    
    @Autowired
    private EntityManager entityManager; 

    @Autowired
    private JdbcMutableAclService aclService;

    @AfterReturning(value = "@annotation(PostPersistAclPolicy)", returning = "returnObject")
    public void persistAclEntity(JoinPoint joinPoint, Object returnObject) {
        validateReturnObjectClass(returnObject);
        if (isCollection(returnObject)) {
            Collection<?> elements = (Collection<?>) returnObject;
            elements.forEach(this::doPersistAclEntity);
            return;
        }
        this.doPersistAclEntity(returnObject);
    }

    private void doPersistAclEntity(Object returnObject) {
        log.info("Persist acl entity: class ({}), id ({})", returnObject.getClass(), this.getEntityIdentifier(returnObject));

        MutableAcl acl = createAclIfNotExists(returnObject.getClass(), this.getEntityIdentifier(returnObject));
        Sid sid = new PrincipalSid(this.getAuthenticationName());

        CumulativePermission permission = new CumulativePermission();
        permission.set(BasePermission.READ);
        permission.set(BasePermission.WRITE);
        permission.set(BasePermission.DELETE);
        permission.set(BasePermission.ADMINISTRATION);

        acl.insertAce(acl.getEntries().size(), permission, sid, true);
        this.aclService.updateAcl(acl);
    }

    private MutableAcl createAclIfNotExists(Class<?> clazz, Serializable identifier) {
        ObjectIdentity oi = new ObjectIdentityImpl(clazz, identifier);
        
        MutableAcl acl = null; 
        try {
            acl = (MutableAcl) this.aclService.readAclById(oi);
        } catch (NotFoundException e) {
            acl = this.aclService.createAcl(oi);
        }
        return acl;
    }
    
    /**
     * Validate if the return object class is an JPA entity with a proper identifier, otherwise, {@link InvalidAclEntityException} is thrown.
     *
     * @param returnObject return object after the annotated (@PostPersistAclPolicy) method invocation.
     */
    private void validateReturnObjectClass(Object returnObject) {
        if (returnObject == null) {
            log.error("Return object must be a non-null entity.");
            throw new InvalidAclEntityException((Class<Object>) null);
        }
        if (isCollection(returnObject)) {
            Collection<?> c = (Collection<?>) returnObject;
            c.forEach(this::validateReturnObjectClass);
            return;
        }
        if (isEntity(returnObject)) {
            return;
        }
        throw new InvalidAclEntityException(returnObject.getClass());
    }

    private boolean isCollection(Object returnObject) {
        return Collection.class.isAssignableFrom(returnObject.getClass());
    }

    private boolean isEntity(Object returnObject) {
        try {
            return this.entityManager.contains(returnObject) && this.getEntityIdentifier(returnObject) != null;
        } catch (Exception e) {
            log.debug("Object {} is not a entity.", returnObject);
        }
        return false;
    }

    private Serializable getEntityIdentifier(Object returnObject) {
        return (Serializable) this.entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(returnObject);
    }

    private String getAuthenticationName() {
        return SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();
    }
}
