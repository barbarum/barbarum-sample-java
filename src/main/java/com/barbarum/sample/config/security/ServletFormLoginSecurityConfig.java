package com.barbarum.sample.config.security;

import static com.barbarum.sample.api.controllers.PathConstants.ADMIN;
import static com.barbarum.sample.api.controllers.PathConstants.SYS_MANAGER;
import static com.barbarum.sample.api.controllers.PathConstants.HOME;
import static com.barbarum.sample.api.controllers.PathConstants.LOGIN;
import static com.barbarum.sample.api.controllers.PathConstants.ROOT;
import static com.barbarum.sample.api.controllers.PathConstants.WELCOME;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
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
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("demo")
            .roles("ADMIN")
            .build();
        UserDetails user = User.withDefaultPasswordEncoder()
            .username("user")
            .password("demo")
            .roles("USER")
            .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    private String ant(String path) {
        return StringUtils.joinWith("*", path);
    }
}
