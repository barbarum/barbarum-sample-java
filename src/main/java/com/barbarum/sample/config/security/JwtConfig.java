package com.barbarum.sample.config.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@Configuration
public class JwtConfig {
    
    @Value("${app.security.jwt.keystore.location}")
    private String keyStoreLocation;

    @Value("${app.security.jwt.keystore.password}")
    private String keyStorePassword;

    @Value("${app.security.jwt.key-alias}")
    private String keyAlias;

    @Value("${app.security.jwt.private-key-passphrase}")
    private String privateKeyPassphrase;

    @Bean
    public KeyStore keyStore(ResourceLoader resourceLoader) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream stream = resourceLoader.getResource(this.keyStoreLocation).getInputStream();
            keyStore.load(stream, this.keyStorePassword.toCharArray());
            return keyStore;
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new ApplicationContextException("Unable to load keystore from " + this.keyStoreLocation, e);
        }
    }

    @Bean
    public RSAPrivateKey jwtSigningKey(KeyStore keyStore) {
        try {
            Key key = keyStore.getKey(this.keyAlias, this.privateKeyPassphrase.toCharArray());
            if (key instanceof RSAPrivateKey) {
                return (RSAPrivateKey) key;
            }
            throw new ApplicationContextException("Unable to init jwt signing/private key, only RSA alogrithm support, but get" + key.getAlgorithm());
        } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
            throw new ApplicationContextException("Unable to generate jwt signing/private key.", e);
        }
    }

    @Bean
    public RSAPublicKey jwtValidationKey(KeyStore keyStore) {
       try {
            Certificate certificate = keyStore.getCertificate(this.keyAlias);
            PublicKey key = certificate.getPublicKey(); 
            if (key instanceof PublicKey) {
                return (RSAPublicKey) key;
            }
            throw new ApplicationContextException("Unable to init jwt validation/public key, only RSA alogrithm support, but get" + key.getAlgorithm());
        } catch (KeyStoreException e) {
            throw new ApplicationContextException("Unable to generate jwt validation/public key", e);
        }
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey key) {
        return NimbusJwtDecoder.withPublicKey(key).build();
    }


    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter) {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter(); 
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return converter;
    }

    /**
     * Customize {@linkplain JwtGrantedAuthoritiesConverter} to comply with Spring security builtin {@linkplain org.springframework.security.access.vote.RoleVoter} authorization.
     * @return
     */
    @Bean
    public JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter(); 
        converter.setAuthorityPrefix("");
        return converter;
    }
}