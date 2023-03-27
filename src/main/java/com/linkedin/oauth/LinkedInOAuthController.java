package com.linkedin.oauth;

import com.linkedin.oauth.builder.ScopeBuilder;
import com.linkedin.oauth.dto.TokenIntrospectionResponseDTO;
import com.linkedin.oauth.pojo.AccessToken;
import com.linkedin.oauth.pojo.LinkedInAuthDetails;
import com.linkedin.oauth.pojo.ProfileDetails;
import com.linkedin.oauth.repository.LinkedInAuthDetailsDAO;
import com.linkedin.oauth.service.LinkedInOAuthService;
import com.linkedin.oauth.service.LinkedInOAuthServiceCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.linkedin.oauth.Constants.*;
import static com.linkedin.oauth.util.Constants.*;

/*
 * Getting Started with LinkedIn's OAuth APIs ,
 * Documentation: https://docs.microsoft.com/en-us/linkedin/shared/authentication/authentication?context=linkedin/context
 */

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://amplify.ness.com", "www.linkedin.com",
        "http://amplify.ness.com:8080"}, allowedHeaders = "Requestor-Type")
public final class LinkedInOAuthController {

/*	@Bean
	public RestTemplate restTemplate(final RestTemplateBuilder builder) {
		return builder.build();
	}*/

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    LinkedInAuthDetailsDAO linkedInAuthDetailsDAO;

    // Define all inputs in the property file
    private Properties prop = new Properties();
    private String propFileName = "config.properties";
    public static String token = null;
    public String refresh_token = null;
    public LinkedInOAuthService service;

    @Autowired
    LinkedInOAuthServiceCall linkedInOAuthServiceCall;

    private Logger logger = Logger.getLogger(LinkedInOAuthController.class.getName());

    /**
     * Make a Login request with LinkedIN Oauth API
     *
     * @param code optional Authorization code
     * @return Redirects to the client UI after successful token creation
     */

    public String clientId = "77m71340tgxr9i";
    public String clientSecret = "RfWUDDi3qulTBA4H";
    public String redirectUrl = "http://amplify.ness.com:8080/login";

    // create button on your page and hit this get request
    // @CrossOrigin(origins="*")
    @GetMapping(value = "/login")
    @ResponseBody
    public RedirectView oauthAuthCode(@RequestParam(name = "code", required = false) final String code)
            throws Exception {

        loadProperty();

        // Construct the LinkedInOAuthService instance for use
        service = new LinkedInOAuthService.LinkedInOAuthServiceBuilder().apiKey(prop.getProperty("clientId"))
                .apiSecret(prop.getProperty("clientSecret"))
                .defaultScope(new ScopeBuilder(prop.getProperty("scope").split(",")).build()) // replace with desired
                // scope
                .callback(prop.getProperty("redirectUri")).build();

        final String secretState = "secret" + new Random().nextInt(999_999);
        final String authorizationUrl = service.createAuthorizationUrlBuilder().state(secretState).build();

        HttpEntity ent = getAuthCode();
        // restTemplate.getForObject(authorizationUrl, ent, String.class);
        ResponseEntity<String> response = restTemplate.exchange(authorizationUrl, HttpMethod.GET, ent, String.class);
        // System.out.println(response.toString());
        // System.out.println(response.getHeaders());

        System.out.println(response.unprocessableEntity());

        RedirectView redirectView = new RedirectView("redirect:/users");
        redirectView.setPropagateQueryParams(true);
        // return redirectView;

        redirectView.setUrl(authorizationUrl);

        // redirectView.setAttributesMap("token")

        System.out.println(redirectView.getUrl());
        return redirectView;
    }

    @PostMapping(value = "/getToken")
    public AccessToken oauthToken(@RequestParam(name = "code", required = false) final String code, @RequestParam(name = "loggedInUserId", required = false) final String loggedInUserId)
            throws Exception {
        String response = null;
        LinkedInAuthDetails linkedInAuthDetails = null;
        AccessToken[] accessToken = {new AccessToken()};
        loadProperty();
        if (!ObjectUtils.isEmpty(loggedInUserId)) {
            linkedInAuthDetails = linkedInAuthDetailsDAO.findByUserId(loggedInUserId);
            if (linkedInAuthDetails != null) {
                TokenIntrospectionResponseDTO introspectResponse = linkedInOAuthServiceCall.tokenIntrospection(linkedInAuthDetails.getRefreshToken());
                if (!ObjectUtils.isEmpty(introspectResponse) && introspectResponse.getActive() == Boolean.TRUE) {
                    AccessToken accessToken1 = new AccessToken();
                    accessToken1.setPersonId(loggedInUserId);
                    accessToken1.setRefreshToken(linkedInAuthDetails.getRefreshToken());
                    accessToken1.setAccessToken(linkedInAuthDetails.getAccessToken());
                    accessToken1.setRefreshTokenExpiresIn(linkedInAuthDetails.getRefreshTokenExpirein());
                    accessToken1.setExpiresIn(linkedInAuthDetails.getAccessTokenExpireIn());

                    prop.setProperty("token", accessToken1.getAccessToken());
                    token =
                            accessToken1.getAccessToken();
                    refresh_token =
                            accessToken1.getRefreshToken();

                    return accessToken1;
                }
            }
        }
        service = new LinkedInOAuthService.LinkedInOAuthServiceBuilder().apiKey(prop.getProperty("clientId"))
                .apiSecret(prop.getProperty("clientSecret"))
                .defaultScope(new ScopeBuilder(prop.getProperty("scope").split(",")).build()) // replace with desired
                // scope
                .callback(prop.getProperty("redirectUri")).build();

        if (code != null && !code.isEmpty()) {

            logger.log(Level.INFO, "Authorization code not empty, trying to generate a 3-legged OAuth token.");


            HttpEntity request = service.getAccessToken3Legged(code);
            response = restTemplate.postForObject(REQUEST_TOKEN_URL, request, String.class);


            //todo

            accessToken[0] = service.convertJsonTokenToPojo(response);

            prop.setProperty("token", accessToken[0].getAccessToken());
            token =
                    accessToken[0].getAccessToken();
            refresh_token =
                    accessToken[0].getRefreshToken();

            ProfileDetails profileDetails = new ProfileDetails();
            String profileInfo = profile();
            profileDetails = service.convertJsonToPojo(profileInfo);
            linkedInAuthDetails = linkedInAuthDetailsDAO.findByFirstNameAndLastName(profileDetails.getFirst_name(), profileDetails.getLast_name());
            if (linkedInAuthDetails == null) {
                linkedInAuthDetails = new LinkedInAuthDetails();
                linkedInAuthDetails.setUserId(profileDetails.getId());
                linkedInAuthDetails.setFirstName(profileDetails.getFirst_name());
                linkedInAuthDetails.setLastName(profileDetails.getLast_name());
                linkedInAuthDetails.setAccessToken(accessToken[0].getAccessToken());
                linkedInAuthDetails.setAccessTokenExpireIn(accessToken[0].getExpiresIn());
                linkedInAuthDetails.setRefreshToken(accessToken[0].getRefreshToken());
                linkedInAuthDetails.setRefreshTokenExpirein(accessToken[0].getRefreshTokenExpiresIn());
                linkedInAuthDetails.setScope(accessToken[0].getScope());
                linkedInAuthDetails.setCreatedAt(getCurrentDate());
            } else {
                linkedInAuthDetails.setUserId(profileDetails.getId());
                linkedInAuthDetails.setFirstName(profileDetails.getFirst_name());
                linkedInAuthDetails.setLastName(profileDetails.getLast_name());
                linkedInAuthDetails.setAccessToken(accessToken[0].getAccessToken());
                linkedInAuthDetails.setAccessTokenExpireIn(accessToken[0].getExpiresIn());
                linkedInAuthDetails.setRefreshToken(accessToken[0].getRefreshToken());
                linkedInAuthDetails.setRefreshTokenExpirein(accessToken[0].getRefreshTokenExpiresIn());
                linkedInAuthDetails.setScope(accessToken[0].getScope());
                linkedInAuthDetails.setCreatedAt(getCurrentDate());
            }
            linkedInAuthDetailsDAO.save(linkedInAuthDetails);
            accessToken[0].setPersonId(profileDetails.getId());

            logger.log(Level.INFO, "Generated Access token and Refresh Token.");

            //redirectView.setUrl(prop.getProperty("client_url"));
        }

        // redirectView.setAttributesMap("token")

        System.out.println(response.toString());
        return accessToken[0];
    }

    public HttpEntity getAuthCode() throws IOException {

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("response_type", "code");
        parameters.add("client_id", "77m71340tgxr9i");
        parameters.add(REDIRECT_URI, "http://amplify.ness.com/");
        parameters.add("state", "secret415359");
        parameters.add("scope", "r_liteprofile%20r_emailaddress%20w_member_social");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
        // headers.set(HttpHeaders.USER_AGENT, USER_AGENT_OAUTH_VALUE);
        headers.setAccessControlAllowOrigin("*");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(parameters,
                headers);
        return request;

    }

    /**
     * Create 2 legged auth access token
     *
     * @return Redirects to the client UI after successful token creation
     */
    @RequestMapping(value = "/twoLeggedAuth")
    public RedirectView two_legged_auth() throws Exception {
        loadProperty();

        RedirectView redirectView = new RedirectView();
        // Construct the LinkedInOAuthService instance for use
        service = new LinkedInOAuthService.LinkedInOAuthServiceBuilder().apiKey(prop.getProperty("clientId"))
                .apiSecret(prop.getProperty("clientSecret"))
                .defaultScope(new ScopeBuilder(prop.getProperty("scope").split(",")).build())
                .callback(prop.getProperty("redirectUri")).build();

        final AccessToken[] accessToken = {new AccessToken()};

        HttpEntity request = service.getAccessToken2Legged();
        String response = restTemplate.postForObject(REQUEST_TOKEN_URL, request, String.class);
        accessToken[0] = service.convertJsonTokenToPojo(response);
        prop.setProperty("token", accessToken[0].getAccessToken());
        token = accessToken[0].getAccessToken();

        logger.log(Level.INFO, "Generated Access token.");

        redirectView.setUrl(prop.getProperty("client_url"));
        return redirectView;
    }

    /**
     * Make a Token Introspection request with LinkedIN API
     *
     * @return check the Time to Live (TTL) and status (active/expired) for all
     * token
     */

    @RequestMapping(value = "/tokenIntrospection")
    public String token_introspection(final String token) throws Exception {
        loadProperty();
        service = new LinkedInOAuthService.LinkedInOAuthServiceBuilder().apiKey(prop.getProperty("clientId"))
                .apiSecret(prop.getProperty("clientSecret"))
                .defaultScope(new ScopeBuilder(prop.getProperty("scope").split(",")).build())
                .callback(prop.getProperty("redirectUri")).build();
        if (service != null) {
            HttpEntity request = service.introspectToken(token);
            String response = restTemplate.postForObject(TOKEN_INTROSPECTION_URL, request, String.class);
            logger.log(Level.INFO, "Token introspected. Details are {0}", response);

            return response;
        } else {
            return TOKEN_INTROSPECTION_ERROR_MESSAGE;
        }
    }

    /**
     * Make a Refresh Token request with LinkedIN API
     *
     * @return get a new access token when your current access token expire
     */

    @RequestMapping(value = "/refreshToken")
    public String refresh_token(final String token) throws IOException {
        HttpEntity request = service.getAccessTokenFromRefreshToken(token);
        String response = restTemplate.postForObject(REQUEST_TOKEN_URL, request, String.class);
        logger.log(Level.INFO, "Used Refresh Token to generate a new access token successfully.");
        return response;
    }

    /**
     * Make a Public profile request with LinkedIN API
     *
     * @return Public profile of user
     */

    @RequestMapping(value = "/profile")
    public String profile() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, USER_AGENT_OAUTH_VALUE);
        return restTemplate.exchange(LI_ME_ENDPOINT + token, HttpMethod.GET, new HttpEntity<>(headers), String.class)
                .getBody();
    }

    private void loadProperty() throws IOException {
        InputStream inputStream = LinkedInOAuthController.class.getClassLoader().getResourceAsStream(propFileName);
        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }
    }

    private Date getCurrentDate() throws ParseException {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String currentDate = formatter.format(date);
        return date = formatter.parse(currentDate);
    }
}
