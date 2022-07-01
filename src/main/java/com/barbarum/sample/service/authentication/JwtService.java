package com.barbarum.sample.service.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class JwtService {
    
    private final RSAPublicKey publicKey; 

    private final RSAPrivateKey privateKey;

    /**
     * Issue a jwt token for current authenticated user.
     *
     * @param duration represents how long the token is valid.
     * @param claims claims
     * @return generated jwt token
     */
    public DecodedJWT issue(String subject, Duration duration, Map<String, String> claims) {
        JWTCreator.Builder builder = JWT.create();
        claims.forEach(builder::withClaim);
        Instant now = Instant.now();
        String token = builder
                .withSubject(subject)
                .withNotBefore(Date.from(now.minusSeconds(30))) // set clock skew to prevent client-server time mismatch.
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusMillis(duration.toMillis())))
                .sign(Algorithm.RSA256(this.publicKey, this.privateKey));
        return JWT.decode(token);
    }

    /**
     * Decode the given jwt token into detailed information.
     *
     * @param token encoded token.
     * @return
     */
    public DecodedJWT decode(String token) {
        return JWT.decode(token);
    }
}
