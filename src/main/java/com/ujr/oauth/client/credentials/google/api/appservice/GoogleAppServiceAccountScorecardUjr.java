package com.ujr.oauth.client.credentials.google.api.appservice;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ujr.oauth.client.credentials.google.api.GoogleJwtApiHandler;

public class GoogleAppServiceAccountScorecardUjr implements GoogleAppServiceAccount {
	
	Map<String, String> properties = new HashMap<>();
	
	public GoogleAppServiceAccountScorecardUjr() {
		init();
	}
	
	private void init() {
		try {
			URL url = GoogleJwtApiHandler.class.getResource("/com/ujr/oath/client/credentials/google/api/appservice/scorecard-ujr-2a310402735b.json");
			Path pathFile = Paths.get(url.toURI());
			String jsonString = new String(Files.readAllBytes(pathFile));
			ObjectMapper mapper = new ObjectMapper();
			properties = mapper.readValue(jsonString, new TypeReference<Map<String, String>>() {});
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String getPrivateKey() {
		return properties.get("private_key");
	}
	
	@Override
	public String getIssuer() {
		return properties.get("client_email");
	}
	
	@Override
	public String getAudience() {
		return properties.get("token_uri");
	}
	
	@Override
	public String getGrantType() {
		return "urn:ietf:params:oauth:grant-type:jwt-bearer";
	}
	
	@Override
	public String getTokenUri() {
		return getAudience();
	}

	@Override
	public String getProjectId() {
		return properties.get("project_id");
	}
	
	
	
}
