package com.ujr.oauth.client.credentials.google.api.pubsub;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ujr.oauth.HttpCommunicationHandler;
import com.ujr.oauth.HttpResponse;
import com.ujr.oauth.client.credentials.google.api.GoogleJwtApiHandler;
import com.ujr.oauth.client.credentials.google.api.appservice.GoogleAppServiceAccount;
import com.ujr.oauth.client.credentials.google.api.domain.error.HttpConnectionError;
import com.ujr.oauth.client.credentials.google.api.pubsub.domain.list.ListSubscription;
import com.ujr.oauth.client.credentials.google.api.pubsub.domain.list.ListTopics;
import com.ujr.oauth.client.credentials.google.api.pubsub.domain.list.Topic;
import com.ujr.oauth.client.credentials.google.api.pubsub.domain.publish.ListMessages;
import com.ujr.oauth.client.credentials.google.api.pubsub.domain.publish.ResponsePublishTopic;
import com.ujr.oauth.client.credentials.google.api.pubsub.domain.pull.ListAckIds;
import com.ujr.oauth.client.credentials.google.api.pubsub.domain.pull.RequestPullMessagesSubscription;
import com.ujr.oauth.client.credentials.google.api.pubsub.domain.pull.ResponsePullMessagesSubscription;
import com.ujr.oauth.client.credentials.google.api.pubsub.taks.pullmessages.CallablePullMessagesSubscription;
import com.ujr.oauth.client.credentials.google.api.pubsub.taks.pullmessages.FutureTaskPullMessagesSubscription;
import com.ujr.oauth.jwt.token.AccessToken;

/**
 * 
 * @author Ualter
 *
 */
public class GooglePubSubApiHandler extends HttpCommunicationHandler {
	
	static final Logger LOG = LoggerFactory.getLogger(GooglePubSubApiHandler.class);
	
	private GoogleAppServiceAccount appServiceAccount;
	
	public GooglePubSubApiHandler(GoogleAppServiceAccount serviceAccount) {
		this.appServiceAccount = serviceAccount;
	}
	
	public FutureTask<ResponsePullMessagesSubscription> pullMessagesForSubscription(RequestPullMessagesSubscription request, boolean acknowledge) {
		CallablePullMessagesSubscription callable = new CallablePullMessagesSubscription(this.appServiceAccount,request, acknowledge);
		FutureTaskPullMessagesSubscription taskPullMessagesSubscription = new FutureTaskPullMessagesSubscription(callable);
		return taskPullMessagesSubscription;
	}
	
	
	public void acknowledgeReceivedMessages(ListAckIds listAckIds, String subscription) {
		LOG.info("Acknowledge Received Messages for Subscription {}, total of {} messages...",subscription, listAckIds.getAckIds().size());
		
		StringBuilder url = new StringBuilder(prepareUrl(GooglePubSubApiUtils.URL_ACKNOWLEDGE).replaceAll("#subscription#", subscription));
		url.append("?access_token=")
		   .append(generateAccessToken().getAccessToken());
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonPostData;
		try {
			jsonPostData = mapper.writeValueAsString(listAckIds);
		} catch (JsonProcessingException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		
		HttpResponse response = doHttpJSONPost(url.toString(), jsonPostData.toString());
		if ( response.getCode().isOk() ) { 
			LOG.info("Successfully Acknowledge!");
			listAckIds.getAckIds().forEach(s -> LOG.trace(" ==> AckId {}",s));
		} else {
			logError(response);
		}
	}
	
	public ResponsePublishTopic publishMessageOnTopic(String topic, ListMessages listMessages) {
		ResponsePublishTopic responsePublishTopic = null;
		
		final AtomicInteger countMessages = new AtomicInteger();
		listMessages.getMessages().forEach( m -> LOG.info("Publishing Message #{} - {} for Topic {}...",countMessages.incrementAndGet(),m.getDataDecoded(),topic));
		
		StringBuilder url = new StringBuilder(prepareUrl(GooglePubSubApiUtils.URL_PUBLISH_TOPIC).replaceAll("#topic#", topic));
		url.append("?access_token=")
		   .append(generateAccessToken().getAccessToken());
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonPostData;
		try {
			jsonPostData = mapper.writeValueAsString(listMessages);
		} catch (JsonProcessingException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		
		HttpResponse response = doHttpJSONPost(url.toString(), jsonPostData.toString());
		if ( response.getCode().isOk() ) { 
			LOG.info("Total of {} Messages published to the Topic: {}",listMessages.getMessages().size(),topic);
			responsePublishTopic = response.convertResponseTo(ResponsePublishTopic.class);
			final AtomicInteger countResponse = new AtomicInteger();
			responsePublishTopic.getMessageIds().forEach(s -> {
				int    index = countResponse.incrementAndGet();
				String msg   = listMessages.getMessages().get(index-1).getDataDecoded();
				LOG.info("   ===> Message Id of the published message #{} - {} is: {}",index,msg,s);
			});
			
		} else {
			logError(response);
		}
		return responsePublishTopic;
	}

	private void logError(HttpResponse response) {
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
	
	public List<String> listSubscriptions(String topic) {
		List<String> result = null;
		LOG.info("Retrieving Subscriptions for Topic {}...",topic);
		StringBuilder url = new StringBuilder(prepareUrl(GooglePubSubApiUtils.URL_LIST_TOPIC_SUBSCRIPTIONS).replaceAll("#topic#", topic));
		url.append("?access_token=")
		   .append(generateAccessToken().getAccessToken());
		
		HttpResponse response = doHttpGet(url.toString()); 
		if ( response.getCode().isOk() ) {
			StringBuilder json = response.getContent(); 
			
			LOG.trace(json.toString());
			
		    // Parse the JSON Token Response
		    ObjectMapper mapper = new ObjectMapper();
		    try {
		    	ListSubscription listSubscription = mapper.readValue(json.toString(),ListSubscription.class);
		    	Optional<List<String>> optionalListSubscription = Optional.ofNullable(listSubscription.getSubscriptions());
		    	if ( optionalListSubscription.isPresent() ) {
		    		result = optionalListSubscription.get();
		    		LOG.info("Found a total of {} Subscriptions for the Topic: {}",result.size(), topic);
		    		final AtomicInteger count = new AtomicInteger();
		    		result.forEach(s -> LOG.info("  ===> Subscription #{}: {}", count.incrementAndGet(), s));
		    	} else {
		    		LOG.info("No Subscriptions were found! For the Topic: {}",topic);
		    	}
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		} else {
			LOG.warn("{}-{}",response.getCode().getCode(),response.getCode().name());
		}
		return result;
	}
	
	public List<Topic> listTopics() {
		List<Topic> listTopic = null;
		LOG.info("Retrieving Topics...");
		StringBuilder url = new StringBuilder(this.prepareUrl(GooglePubSubApiUtils.URL_LIST_TOPICS));
		url.append("?access_token=")
		   .append(generateAccessToken().getAccessToken());
		
		HttpResponse response = doHttpGet(url.toString());
		if ( response.getCode().isOk() ) {
			StringBuilder json = response.getContent();
			LOG.trace(json.toString());
			
		    // Parse the JSON Token Response
		    ObjectMapper mapper = new ObjectMapper();
		    try {
				ListTopics listTopics = mapper.readValue(json.toString(), ListTopics.class);
				Optional<List<Topic>> optionalListTopics = Optional.ofNullable(listTopics.getTopics());
				if ( optionalListTopics.isPresent() ) {
					listTopic = optionalListTopics.get();
					LOG.info("Found a total of {} topics",listTopic.size());
					final AtomicInteger count = new AtomicInteger();
					listTopic.forEach(t -> LOG.info("  ===> Topic #{}: {}",count.incrementAndGet(), t.getName()));
				} else {
					LOG.info("No Topics were found!");
				}
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		} else {
			LOG.warn("{} {}",response.getCode().getCode(),response.getCode().name());
		}
		return listTopic;
	}
	
	private AccessToken generateAccessToken() {
		GoogleJwtApiHandler jwtGmailApiHandler = new GoogleJwtApiHandler(this.appServiceAccount);
		AccessToken accessToken = jwtGmailApiHandler.retrieveAccessTokenToAPI(jwtGmailApiHandler.createTokenJWT(GooglePubSubApiUtils.URL_API_PUB_SUB_SCOPES));
		return accessToken;
	}
	
	private String prepareUrl(String url) {
		return url.replaceAll("#project_id#", appServiceAccount.getProjectId());
	}
	

}
