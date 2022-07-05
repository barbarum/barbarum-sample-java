package com.barbarum.sample.config.security;

import static com.barbarum.sample.api.PathConstants.ADMIN;
import static com.barbarum.sample.api.PathConstants.HOME;
import static com.barbarum.sample.api.PathConstants.LOGIN;
import static com.barbarum.sample.api.PathConstants.LOGIN_FORM;
import static com.barbarum.sample.api.PathConstants.ME;
import static com.barbarum.sample.api.PathConstants.ROOT;
import static com.barbarum.sample.api.PathConstants.SYS_MANAGER;
import static com.barbarum.sample.api.PathConstants.WELCOME;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true)
@Slf4j
public class SecurityConfig {

    @Value("${app.authentication.state.storage.type:SESSION}")
    private AuthenticationStateStorageType storageType = AuthenticationStateStorageType.SESSION;

    /**
     * Configure security policy for http request.
     */
    @Bean
    protected SecurityFilterChain configHttpSecurity(HttpSecurity security) throws Exception {
        log.info("Configure http request security with authentication state storage type: {}", this.storageType);
        security
            .csrf().disable()
            .cors()
                .and()
            .httpBasic().disable()
            .authorizeRequests()
                .antMatchers(ant(ADMIN), ant(SYS_MANAGER)).hasRole("ADMIN")
                .antMatchers(HOME, ROOT, WELCOME, LOGIN, LOGIN_FORM).permitAll()
                .anyRequest().authenticated()
                .and(); 

        if (storageType == AuthenticationStateStorageType.JWT) {
            this.configOauth2AndJwtAuthentication(security);
        } else {
            this.configFormLogin(security);
        }
        return security.build();
    }

    /**
     * Configure security policy for login form style.
     */
    protected void configFormLogin(HttpSecurity security) throws Exception {
        security
            .formLogin()
                .loginProcessingUrl(LOGIN) // Redefine the login processing url for formLogin, use AuthenticationSuccessHandler for login logic control instead.
                .defaultSuccessUrl(ME)
                .permitAll();
    }

    /**
     * Configure security policy for JWT token by leveraging Oauth2 resource server.
     */
    protected void configOauth2AndJwtAuthentication(HttpSecurity security) throws Exception {
        security
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
    }

    private String ant(String path) {
        return StringUtils.joinWith("*", path);
    }

    /**
     * Configure in-memory user detail service for demo purpose. Ignore this setup for now.
     */
    @SuppressWarnings("deprecation")
    @Bean
    public UserDetailsService userDetailsService(DataSource source, PasswordEncoder encoder) {
        UserDetailsManager manager =  new JdbcUserDetailsManager(source);
        
        UserDetails admin = User.withDefaultPasswordEncoder()
            .username("admin")
            .passwordEncoder(encoder::encode)
            .password("demo")
            .roles("ADMIN")
            .build();
        this.createUserIfNotExists(manager, admin);
        
        UserDetails user = User.withDefaultPasswordEncoder()
            .username("user")
            .passwordEncoder(encoder::encode)
            .password("demo")
            .roles("USER")
            .build();
        this.createUserIfNotExists(manager, user);
        
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void createUserIfNotExists(UserDetailsManager manager, UserDetails user) {
        if (manager.userExists(user.getUsername())) {
            return;
        }
        manager.createUser(user);
    }

    public enum AuthenticationStateStorageType {
        SESSION, 
        JWT
    }
}
