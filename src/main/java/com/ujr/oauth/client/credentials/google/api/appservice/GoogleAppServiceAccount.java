package com.ujr.oauth.client.credentials.google.api.appservice;

public interface GoogleAppServiceAccount {

	String getPrivateKey();

	String getIssuer();

	String getAudience();

	String getGrantType();

	String getTokenUri();
	
	String getProjectId();

}