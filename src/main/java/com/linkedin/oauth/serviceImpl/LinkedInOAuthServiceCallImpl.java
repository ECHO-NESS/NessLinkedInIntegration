package com.linkedin.oauth.serviceImpl;

import com.linkedin.oauth.LinkedInOAuthController;
import com.linkedin.oauth.builder.ScopeBuilder;
import com.linkedin.oauth.dto.TokenIntrospectionResponseDTO;
import com.linkedin.oauth.pojo.AccessToken;
import com.linkedin.oauth.service.LinkedInOAuthService;
import com.linkedin.oauth.service.LinkedInOAuthServiceCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.linkedin.oauth.util.Constants.REQUEST_TOKEN_URL;
import static com.linkedin.oauth.util.Constants.TOKEN_INTROSPECTION_URL;

@Service
public class LinkedInOAuthServiceCallImpl implements LinkedInOAuthServiceCall {

    private Logger logger = Logger.getLogger(LinkedInOAuthServiceCallImpl.class.getName());

    public LinkedInOAuthService service;
    private Properties prop = new Properties();
    private String propFileName = "config.properties";

    /* @Bean
     public RestTemplate restTemplate(final RestTemplateBuilder builder) {
         return builder.build();
     }
 */
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public TokenIntrospectionResponseDTO tokenIntrospection(final String token) throws Exception {
        loadProperty();
        service = new LinkedInOAuthService.LinkedInOAuthServiceBuilder().apiKey(prop.getProperty("clientId"))
                .apiSecret(prop.getProperty("clientSecret"))
                .defaultScope(new ScopeBuilder(prop.getProperty("scope").split(",")).build())
                .callback(prop.getProperty("redirectUri")).build();
        if (service != null) {
            HttpEntity request = service.introspectToken(token);
            TokenIntrospectionResponseDTO response = restTemplate.postForObject(TOKEN_INTROSPECTION_URL, request, TokenIntrospectionResponseDTO.class);
            logger.log(Level.INFO, "Token introspected. Details are {0}", response);
            return response;
        } else {
            return null;
        }
    }

    private void loadProperty() throws IOException {
        InputStream inputStream = LinkedInOAuthController.class.getClassLoader().getResourceAsStream(propFileName);
        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }
    }

    @Override
    public AccessToken refreshToken(final String token) throws IOException {
        HttpEntity request = service.getAccessTokenFromRefreshToken(token);
        AccessToken response = restTemplate.postForObject(REQUEST_TOKEN_URL, request, AccessToken.class);
        logger.log(Level.INFO, "Used Refresh Token to generate a new access token successfully.");
        return response;
    }

}
