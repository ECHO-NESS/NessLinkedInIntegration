package com.linkedin.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;

@Data
public class TokenIntrospectionResponseDTO {
    @JsonProperty(value = "active")
    private Boolean active;
    @JsonProperty(value = "client_id")
    private String clientId;
    @JsonProperty(value = "authorized_at")
    private long authorizedAt;
    @JsonProperty(value = "created_at")
    private long createdAt;
    @JsonProperty(value = "status")
    private String status;
    @JsonProperty(value = "expires_in")
    private String expiresIn;

    @JsonProperty(value = "scope")
    private String scope;
    @JsonProperty(value = "auth_type")
    private String authType;

}
