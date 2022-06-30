package com.barbarum.sample.service.acl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.AuditableAccessControlEntry;
import org.springframework.util.Assert;

@Slf4j
public class Slf4jAuditLogger implements AuditLogger {

    @Override
    public void logIfNeeded(boolean granted, AccessControlEntry ace) {
		Assert.notNull(ace, "AccessControlEntry required");

        if (granted) {
            log.info("GRANTED due to ACE ({}): {}", ace.getClass(), ace);
        } else {
            log.info("DENIED due to ACE ({}): {}", ace.getClass(), ace);
        }

		if (ace instanceof AuditableAccessControlEntry) {
			AuditableAccessControlEntry auditableAce = (AuditableAccessControlEntry) ace;
			if (granted && auditableAce.isAuditSuccess()) {
				log.info("GRANTED due to ACE: {}", ace);
			}
			else if (!granted && auditableAce.isAuditFailure()) {
				log.info("DENIED due to ACE: {}", ace);
			}
		}
    }
    
}
