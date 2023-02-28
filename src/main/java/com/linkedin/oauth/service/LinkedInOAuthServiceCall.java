package com.linkedin.oauth.service;

import com.linkedin.oauth.dto.TokenIntrospectionResponseDTO;
import com.linkedin.oauth.pojo.AccessToken;
import com.linkedin.oauth.pojo.LinkedInAuthDetails;

import java.io.IOException;

public interface LinkedInOAuthServiceCall {
    public TokenIntrospectionResponseDTO tokenIntrospection(String token) throws Exception;

    AccessToken refreshToken(String token) throws IOException;

}
