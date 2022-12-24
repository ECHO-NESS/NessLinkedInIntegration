package com.linkedin.oauth;

import static com.linkedin.oauth.Constants.LI_ME_ENDPOINT;
import static com.linkedin.oauth.Constants.TOKEN_INTROSPECTION_ERROR_MESSAGE;
import static com.linkedin.oauth.Constants.USER_AGENT_OAUTH_VALUE;
import static com.linkedin.oauth.util.Constants.REDIRECT_URI;
import static com.linkedin.oauth.util.Constants.REQUEST_TOKEN_URL;
import static com.linkedin.oauth.util.Constants.TOKEN_INTROSPECTION_URL;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import com.linkedin.oauth.builder.ScopeBuilder;
import com.linkedin.oauth.pojo.AccessToken;
import com.linkedin.oauth.service.LinkedInOAuthService;

/*
 * Getting Started with LinkedIn's OAuth APIs ,
 * Documentation: https://docs.microsoft.com/en-us/linkedin/shared/authentication/authentication?context=linkedin/context
 */

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = { "http://localhost:4200", "www.linkedin.com",
		"http://localhost:8080" }, allowedHeaders = "Requestor-Type")
public final class LinkedInOAuthController {

	@Bean
	public RestTemplate restTemplate(final RestTemplateBuilder builder) {
		return builder.build();
	}

	@Autowired
	private RestTemplate restTemplate;

	// Define all inputs in the property file
	private Properties prop = new Properties();
	private String propFileName = "config.properties";
	public static String token = null;
	public String refresh_token = null;
	public LinkedInOAuthService service;

	private Logger logger = Logger.getLogger(LinkedInOAuthController.class.getName());

	/**
	 * Make a Login request with LinkedIN Oauth API
	 *
	 * @param code optional Authorization code
	 * @return Redirects to the client UI after successful token creation
	 */

	public String clientId = "77m71340tgxr9i";
	public String clientSecret = "RfWUDDi3qulTBA4H";
	public String redirectUrl = "http://localhost:8080/login";

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
	public String oauthToken(@RequestParam(name = "code", required = false) final String code)
			throws Exception {
		String response =null;
		loadProperty();
		service = new LinkedInOAuthService.LinkedInOAuthServiceBuilder().apiKey(prop.getProperty("clientId"))
				.apiSecret(prop.getProperty("clientSecret"))
				.defaultScope(new ScopeBuilder(prop.getProperty("scope").split(",")).build()) // replace with desired
																								// scope
				.callback(prop.getProperty("redirectUri")).build();

		
        
		if (code != null && !code.isEmpty()) {

			logger.log(Level.INFO, "Authorization code not empty, trying to generate a 3-legged OAuth token.");

			final AccessToken[] accessToken = { new AccessToken() };
			HttpEntity request = service.getAccessToken3Legged(code);
			response = restTemplate.postForObject(REQUEST_TOKEN_URL, request, String.class);
			
			/*
			 * accessToken[0] = service.convertJsonTokenToPojo(response);
			 * 
			 * prop.setProperty("token", accessToken[0].getAccessToken()); token =
			 * accessToken[0].getAccessToken(); refresh_token =
			 * accessToken[0].getRefreshToken();
			 */

			logger.log(Level.INFO, "Generated Access token and Refresh Token.");

			//redirectView.setUrl(prop.getProperty("client_url"));
		}

		// redirectView.setAttributesMap("token")

		System.out.println(response.toString());
		return response;
	}

	public HttpEntity getAuthCode() throws IOException {

		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add("response_type", "code");
		parameters.add("client_id", "77m71340tgxr9i");
		parameters.add(REDIRECT_URI, "http://localhost:4200/");
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

		final AccessToken[] accessToken = { new AccessToken() };

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
	 *         token
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
}
