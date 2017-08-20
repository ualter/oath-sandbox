package com.ujr.oath.client.credentials.google.api;


import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ujr.oath.HttpCommunicationHandler;
import com.ujr.oath.HttpResponse;
import com.ujr.oath.client.credentials.google.api.appservice.GoogleAppServiceAccount;
import com.ujr.oath.client.credentials.google.api.appservice.GoogleAppServiceAccountScorecardUjr;
import com.ujr.oath.client.credentials.google.api.pubsub.GooglePubSubApiUtils;
import com.ujr.oath.jwt.token.AccessToken;
import com.ujr.oath.jwt.token.AccessTokenInfo;
import com.ujr.security.utils.PublicPrivateKeysUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;



/**
 * 
 * 
 *  Anatomy of a JWT ("pronounced jot")
 *  A JWT is composed of three parts: a header, a claim set, and a signature.
 *  {Base64url encoded header}.{Base64url encoded claim set}.{Base64url encoded signature}
 *  
 *  Header of a JWT 
 *  {"alg":"RS256","typ":"JWT"}
 *  
 *  
 *  Required Claims by Google OAuth Server
 *  --------------------------------------
 *  iss........:The email address of the service account.
 *  scope......:A space-delimited list of the permissions that the application requests.
 *  aud........:A descriptor of the intended target of the assertion. When making an access token request this value is always https://www.googleapis.com/oauth2/v4/token.
 *  exp........:The expiration time of the assertion, specified as seconds since 00:00:00 UTC, January 1, 1970. This value has a maximum of 1 hour after the issued time.
 *  iat........:The time the assertion was issued, specified as seconds since 00:00:00 UTC, January 1, 1970.
 *  
 *  Example:
 *  {
 *    "iss":"app@appspot.gserviceaccount.com",
 *    "scope":"https://www.googleapis.com/auth/gmail.readonly",
 *    "aud":"https://accounts.google.com/o/oauth2/auth",
 *    "exp":1328554385,
 *    "iat":1328550785
 *  }
 * 
 * 
 * @author Ualter
 *
 */
public class GoogleJwtApiHandler extends HttpCommunicationHandler {
	
	static final Logger LOG = LoggerFactory.getLogger(GoogleJwtApiHandler.class);
	
	private GoogleAppServiceAccount appServiceAccount = null;
	public static final String URL_ACCESS_TOKEN_INFO = "https://www.googleapis.com/oauth2/v3/tokeninfo";
	
	public static void main(String[] args) {
		testJwtGoogleSubPubApiHandler();
	}
	
	public GoogleJwtApiHandler(GoogleAppServiceAccount appServiceAccount) {
		this.appServiceAccount = appServiceAccount;
	}

	private static void testJwtGoogleSubPubApiHandler() {
		GoogleJwtApiHandler jwtGmailApiHandler = new GoogleJwtApiHandler(new GoogleAppServiceAccountScorecardUjr());
		
		// Create the Application JWS Token (A Signed JWT) to access the Google PubSub API
		String jws = jwtGmailApiHandler.createTokenJWT(GooglePubSubApiUtils.URL_API_PUB_SUB_SCOPES);
		
		// Retrieve the Access Token to communicate with the Google PubSub API
		AccessToken accessToken = jwtGmailApiHandler.retrieveAccessTokenToAPI(jws);
		System.out.println("Token for Access...:" + accessToken.toString());
		
		// Retrieve Access Token Info/Validation of the Access Token retrieved
		AccessTokenInfo accessTokenInfo = jwtGmailApiHandler.retrieveAccessTokenInfo(accessToken);
		System.out.println("Info Token for Access...:" + accessTokenInfo.toString());
	}
	
	
	public AccessToken retrieveAccessTokenToAPI(String jws) {
		StringBuffer postData = new StringBuffer();
		postData
		  .append("grant_type=").append(appServiceAccount.getGrantType())
		  .append("&assertion=").append(jws);
		
		
		HttpResponse response    = doHttpBasicPost(appServiceAccount.getTokenUri(), postData.toString());
		if ( response.getCode().isOk() ) {
			StringBuilder      json        = response.getContent();
			ObjectMapper       mapper      = new ObjectMapper();
			AccessToken        accessToken = null;
			try {
				accessToken = mapper.readValue(json.toString(), AccessToken.class);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return accessToken;
		} else {
			LOG.error("Error {}-{} attemping retrieving the Access Token", response.getCode().getCode(), response.getCode().name());
			throw new RuntimeException("Error " + response.getCode().getCode() + "-" +  response.getCode().name());
		}
		
	}
	
	public AccessTokenInfo retrieveAccessTokenInfo(AccessToken accessToken) {
		StringBuffer postData = new StringBuffer();
		postData
		  .append("access_token=").append(accessToken.getAccessToken());
		
		HttpResponse response     = doHttpBasicPost(URL_ACCESS_TOKEN_INFO, postData.toString());
		if ( response.getCode().isOk() ) {
			StringBuilder   json            = response.getContent();
			ObjectMapper    mapper          = new ObjectMapper();
			AccessTokenInfo accessTokenInfo = null;
			try {
				accessTokenInfo = mapper.readValue(json.toString(), AccessTokenInfo.class);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return accessTokenInfo;
		} else {
			LOG.error("Error {}-{} attemping retrieving the Access Token", response.getCode().getCode(), response.getCode().name());
			throw new RuntimeException("Error " + response.getCode().getCode() + "-" +  response.getCode().name());
		}
	}
	

	public String createTokenJWT(String apiScope) {
		RSAPrivateKey rsaPrivateKey = PublicPrivateKeysUtils.extractPrivateKeyFromString(appServiceAccount.getPrivateKey());
		
		ZonedDateTime timeBarcelona = ZonedDateTime.now(ZoneId.of("Europe/Paris"));
		Date expiryDateTime         = Date.from(timeBarcelona.plusHours(1).toInstant());
		Date currentDateTime        = Date.from(timeBarcelona.toInstant());
		
		Claims claims = Jwts.claims();
		claims.put(Claims.ISSUER,    appServiceAccount.getIssuer());
		claims.put("scope",          apiScope);
		claims.put(Claims.AUDIENCE,  appServiceAccount.getAudience());
		claims.put(Claims.EXPIRATION, expiryDateTime.getTime() / 1000l);
		claims.put(Claims.ISSUED_AT,  currentDateTime.getTime() / 1000l);
		
		JwtBuilder jwtBuilder = Jwts.builder()
								.setClaims(claims)
								.signWith(SignatureAlgorithm.RS256, rsaPrivateKey);
		
		String compactJws = jwtBuilder.compact();
		return compactJws;
	}
	
	public JwtBuilder createInvalidTokenJWT(String apiScope) {
		ZonedDateTime timeBarcelona = ZonedDateTime.now(ZoneId.of("Europe/Paris"));
		Date expiryDateTime         = Date.from(timeBarcelona.plusHours(1).toInstant());
		Date currentDateTime        = Date.from(timeBarcelona.toInstant());
		
		Claims claims = Jwts.claims();
		claims.put(Claims.ISSUER,    appServiceAccount.getIssuer());
		claims.put("scope",          apiScope);
		claims.put(Claims.AUDIENCE,  appServiceAccount.getAudience());
		claims.put(Claims.EXPIRATION, expiryDateTime.getTime() / 1000l);
		claims.put(Claims.ISSUED_AT,  currentDateTime.getTime() / 1000l);
		
		JwtBuilder jwtBuilder = Jwts.builder()
								.setClaims(claims)
								.signWith(SignatureAlgorithm.HS256, "secretPhrase");
		return jwtBuilder;
	}
	

	

}
