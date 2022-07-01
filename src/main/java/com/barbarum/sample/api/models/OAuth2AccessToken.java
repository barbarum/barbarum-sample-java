package com.barbarum.sample.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * https://datatracker.ietf.org/doc/html/rfc6749#section-5.1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OAuth2AccessToken {
    
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private long expiresIn;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("refresh_token")
    private String refreshToken;
}
