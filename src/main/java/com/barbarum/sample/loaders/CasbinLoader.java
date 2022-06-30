package com.barbarum.sample.loaders;

import lombok.extern.slf4j.Slf4j;

import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CasbinLoader {
    
    @Autowired
    private Enforcer enforcer; 

    @EventListener
    public void onAppLoaded(ContextRefreshedEvent event) {
        String subject = "Ming";
        String resource = "domain";
        String readAction = "read";
        String writeAction = "write";
        this.enforcer.addPolicy(subject, resource, readAction);
        this.enforcer.savePolicy();
        
        log.info("Ming to read domain ({}) and write domain ({})."
            , this.enforcer.enforce(subject, resource, readAction)
            , this.enforcer.enforce(subject, resource, writeAction));
    }
}
