package com.barbarum.sample.config.security;

import com.barbarum.sample.api.controllers.authentication.LoginController;

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
                .antMatchers("/admin", "/admin/", "/admin/**").hasRole("ADMIN")
                .antMatchers( "/", "/home*", LoginController.LOGIN_PATH).permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginProcessingUrl(LoginController.LOGIN_PATH)
                .defaultSuccessUrl("/users")
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
}
