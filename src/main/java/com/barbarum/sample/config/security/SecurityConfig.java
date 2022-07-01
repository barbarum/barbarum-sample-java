package com.barbarum.sample.config.security;

import static com.barbarum.sample.api.PathConstants.ADMIN;
import static com.barbarum.sample.api.PathConstants.HOME;
import static com.barbarum.sample.api.PathConstants.LOGIN;
import static com.barbarum.sample.api.PathConstants.LOGIN_FORM;
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
public class SecurityConfig {

    /**
     * Configure security policy for login form style.
     */
    // @Bean
    protected SecurityFilterChain configFormLogin(HttpSecurity security) throws Exception {
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
     * Configure security policy for JWT token by leveraging Oauth2 resource server.
     */
    @Bean
    protected SecurityFilterChain configOauth2AndJwtAuthentication(HttpSecurity security) throws Exception {
        return security
            .csrf().disable()
            .cors()
                .and()
            .httpBasic().disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .authorizeRequests()
                .antMatchers(ant(ADMIN), ant(SYS_MANAGER)).hasRole("ADMIN")
                .antMatchers(HOME, ROOT, WELCOME, LOGIN, LOGIN_FORM).permitAll()
                .anyRequest().authenticated()
                .and()
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
            .build();
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
}
