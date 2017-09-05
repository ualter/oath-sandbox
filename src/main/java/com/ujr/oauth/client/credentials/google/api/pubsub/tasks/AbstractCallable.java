package com.ujr.oauth.client.credentials.google.api.pubsub.tasks;

import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ujr.oauth.HttpCommunicationHandler;
import com.ujr.oauth.HttpResponse;
import com.ujr.oauth.client.credentials.google.api.GoogleJwtApiHandler;
import com.ujr.oauth.client.credentials.google.api.appservice.GoogleAppServiceAccount;
import com.ujr.oauth.client.credentials.google.api.domain.error.HttpConnectionError;
import com.ujr.oauth.client.credentials.google.api.pubsub.GooglePubSubApiUtils;
import com.ujr.oauth.jwt.token.AccessToken;

public class AbstractCallable<T> implements Callable<T> {
	
	static final Logger LOG = LoggerFactory.getLogger(AbstractCallable.class);
	
	protected HttpCommunicationHandler httpCommunicationHandler;
	protected GoogleAppServiceAccount appServiceAccount;
	protected boolean cancel;
	
	public AbstractCallable(GoogleAppServiceAccount appServiceAccount) {
		super();
		this.appServiceAccount        = appServiceAccount;
		this.httpCommunicationHandler = new HttpCommunicationHandler();
	}

	@Override
	public T call() throws Exception {
		return null;
	}
	
	protected void logError(HttpResponse response) {
		LOG.warn("{} {}",response.getCode().getCode(),response.getCode().name());
		if ( response.getError().isPresent() ) {
			HttpConnectionError httpStatusError = response.getError().get();
			LOG.warn("Error: {}",httpStatusError.getError().getStatus());
			LOG.warn("Error: {}",httpStatusError.getError().getMessage());
			if ( httpStatusError.getError().getDetails() != null ) {
				httpStatusError.getError().getDetails().forEach(d -> LOG.warn("{}", d.getFieldViolations().stream().map(de -> de.getDescription()).collect(Collectors.joining("  |  "))));
			}
			
		}
	}
	
	protected String prepareUrl(String url) {
		return url.replaceAll("#project_id#", appServiceAccount.getProjectId());
	}
	
	protected AccessToken generateAccessToken() {
		GoogleJwtApiHandler jwtGmailApiHandler = new GoogleJwtApiHandler(this.appServiceAccount);
		AccessToken accessToken = jwtGmailApiHandler.retrieveAccessTokenToAPI(jwtGmailApiHandler.createTokenJWT(GooglePubSubApiUtils.URL_API_PUB_SUB_SCOPES));
		return accessToken;
	}

}
