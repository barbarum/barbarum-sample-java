package com.barbarum.sample.service;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
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
        this.enforcer.addPolicy("Ming", "domain", "read");
        this.enforcer.savePolicy();
        
        log.info("Ming to read domain ({}) and write domain ({})."
            , this.enforcer.enforce("Ming", "domain", "read")
            , this.enforcer.enforce("Ming", "domain", "write"));
    }
}
