package com.barbarum.sample.config.security;

import static com.barbarum.sample.api.PathConstants.ADMIN;
import static com.barbarum.sample.api.PathConstants.HOME;
import static com.barbarum.sample.api.PathConstants.LOGIN;
import static com.barbarum.sample.api.PathConstants.ROOT;
import static com.barbarum.sample.api.PathConstants.SYS_MANAGER;
import static com.barbarum.sample.api.PathConstants.WELCOME;

import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true)
public class ServletFormLoginSecurityConfig {
    
    /**
     * Configure security policy for http request.
     */
    @Bean
    protected SecurityFilterChain config(HttpSecurity security) throws Exception {
        return security
            .csrf().disable()
            .cors()
                .and()
            .httpBasic().disable()
            .authorizeRequests()
                .antMatchers(ant(ADMIN), ant(SYS_MANAGER)).hasRole("ADMIN")
                .antMatchers(HOME, ROOT, WELCOME).permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginProcessingUrl(LOGIN) // Redefine the login processing url for formLogin, use AuthenticationSuccessHandler for login logic control instead.
                .defaultSuccessUrl(HOME)
                .permitAll()
                .and()
            .build();
    }

    /**
     * Configure in-memory user detail service for demo purpose. Ignore this setup for now.
     */
    @SuppressWarnings("deprecation")
    @Bean
    public UserDetailsService userDetailsService(DataSource source) {
        UserDetailsManager manager =  new JdbcUserDetailsManager(source);
        
        UserDetails admin = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("demo")
            .roles("ADMIN")
            .build();
        this.createUserIfNotExists(manager, admin);
        
        UserDetails user = User.withDefaultPasswordEncoder()
            .username("user")
            .password("demo")
            .roles("USER")
            .build();
        this.createUserIfNotExists(manager, user);
        
        return manager;
    }

    private String ant(String path) {
        return StringUtils.joinWith("*", path);
    }

    private void createUserIfNotExists(UserDetailsManager manager, UserDetails user) {
        if (manager.userExists(user.getUsername())) {
            return;
        }
        manager.createUser(user);
    }
}
