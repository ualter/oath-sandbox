package com.ujr.oath.client.credentials.google.api.pubsub;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ujr.oath.ApiHandler;
import com.ujr.oath.ResponseApiHandler;
import com.ujr.oath.client.credentials.google.api.GoogleJwtApiHandler;
import com.ujr.oath.client.credentials.google.api.appservice.GoogleAppServiceAccount;
import com.ujr.oath.client.credentials.google.api.appservice.GoogleAppServiceAccountScorecardUjr;
import com.ujr.oath.client.credentials.google.api.domain.error.HttpConnectionError;
import com.ujr.oath.client.credentials.google.api.pubsub.domain.list.ListSubscription;
import com.ujr.oath.client.credentials.google.api.pubsub.domain.list.ListTopics;
import com.ujr.oath.client.credentials.google.api.pubsub.domain.list.Topic;
import com.ujr.oath.client.credentials.google.api.pubsub.domain.publish.ListMessages;
import com.ujr.oath.client.credentials.google.api.pubsub.domain.publish.ResponsePublishTopic;
import com.ujr.oath.client.credentials.google.api.pubsub.domain.pull.ListAckIds;
import com.ujr.oath.client.credentials.google.api.pubsub.domain.pull.RequestPullMessagesSubscription;
import com.ujr.oath.client.credentials.google.api.pubsub.domain.pull.ResponsePullMessagesSubscription;
import com.ujr.oath.jwt.token.AccessToken;

public class GooglePubSubApiHandler extends ApiHandler {
	
	static final Logger LOG = LoggerFactory.getLogger(GooglePubSubApiHandler.class);
	
	private GoogleAppServiceAccount appServiceAccount;
	
	
	public static final String URL_PULL_MSGS_SUBSCRIPTION   = "https://pubsub.googleapis.com/v1/projects/#project_id#/subscriptions/#subscription#:pull";
	public static final String URL_ACKNOWLEDGE              = "https://pubsub.googleapis.com/v1/projects/#project_id#/subscriptions/#subscription#:acknowledge";
	public static final String URL_PUBLISH_TOPIC            = "https://pubsub.googleapis.com/v1/projects/#project_id#/topics/#topic#:publish";
	public static final String URL_LIST_TOPIC_SUBSCRIPTIONS = "https://pubsub.googleapis.com/v1/projects/#project_id#/topics/#topic#/subscriptions";
	public static final String URL_LIST_TOPICS              = "https://pubsub.googleapis.com/v1/projects/#project_id#/topics";
	public static final String URL_API_PUB_SUB              = "https://pubsub.googleapis.com/v1/";
	public static final String URL_API_PUB_SUB_SCOPES       = "https://www.googleapis.com/auth/pubsub";
	
	public static void main(String[] args) {
		GoogleAppServiceAccount appServiceAccount = new GoogleAppServiceAccountScorecardUjr();
		GooglePubSubApiHandler pubSubApiHandler = new GooglePubSubApiHandler(appServiceAccount);
		
		//testPullMessagesForSubscription("ualter",pubSubApiHandler);
		//testListTopicsAndSubscriptions(pubSubApiHandler);
		testPublishMessages(pubSubApiHandler);
	}

	private static void testPullMessagesForSubscription(String subscription, GooglePubSubApiHandler pubSubApiHandler) {
		RequestPullMessagesSubscription request = new RequestPullMessagesSubscription();
		request.setSubscription(subscription);
		request.setMaxMessages(1000);
		request.setReturnImmediately(true);
		pubSubApiHandler.pullMessagesForSubscription(request,true);
	}

	private static void testListTopicsAndSubscriptions(GooglePubSubApiHandler pubSubApiHandler) {
		// List Topics
		List<Topic> listTopics = pubSubApiHandler.listTopics();
		// List Subscriptions
		for(Topic topic : listTopics) {
			pubSubApiHandler.listSubscriptions(topic.getShortName());
		}
	}

	private static void testPublishMessages(GooglePubSubApiHandler pubSubApiHandler) {
		ListMessages listMessages = new ListMessages();
		listMessages.addMessage("hello topic n.1");
		listMessages.addMessage("hello topic n.2");
		pubSubApiHandler.publishMessageOnTopic("saques",listMessages);
	}
	
	
	public GooglePubSubApiHandler(GoogleAppServiceAccount serviceAccount) {
		this.appServiceAccount = serviceAccount;
	}
	
	public ResponsePullMessagesSubscription pullMessagesForSubscription(RequestPullMessagesSubscription request, boolean acknowledge) {
		ResponsePullMessagesSubscription responsePullMessagesSubscription = null;
		
		LOG.info("Pull Messages for Subscription {}...",request.getSubscription());
		
		StringBuilder url = new StringBuilder(prepareUrl(URL_PULL_MSGS_SUBSCRIPTION).replaceAll("#subscription#", request.getSubscription()));
		url.append("?access_token=")
		   .append(generateAccessToken().getAccessToken());
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonPostData;
		try {
			jsonPostData = mapper.writeValueAsString(request);
		} catch (JsonProcessingException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		
		ResponseApiHandler response = doHttpPost(url.toString(), jsonPostData.toString(), ContentType.ApplicationJSON);
		if ( response.getCode().isOk() ) { 
			responsePullMessagesSubscription = response.convertResponseTo(ResponsePullMessagesSubscription.class);
			if ( responsePullMessagesSubscription.getReceivedMessages() != null ) {
				LOG.info("Total of {} Messages pulled for the Subscription: {}",responsePullMessagesSubscription.getReceivedMessages().size(),request.getSubscription());
				final AtomicInteger countResponse = new AtomicInteger();
				responsePullMessagesSubscription.getReceivedMessages().forEach(m -> LOG.info("  ===> Messaged #{} Received:\"{}\" [{}]",countResponse.incrementAndGet(),m.getMessage().getDataDecoded(),m.getMessage()));
				if (acknowledge) {
					List<String> ackIds = responsePullMessagesSubscription.getReceivedMessages().stream().map(m -> m.getAckId()).collect(Collectors.toList());
					ListAckIds listAckIds = new ListAckIds();
					listAckIds.setAckIds(ackIds);
					acknowledgeReceivedMessages(listAckIds, request.getSubscription());
				}
			} else {
				LOG.info("No messages were found for the Subscription: {}",request.getSubscription());
			}
		} else {
			logError(response);
		}
		return responsePullMessagesSubscription;
	}
	
	public void acknowledgeReceivedMessages(ListAckIds listAckIds, String subscription) {
		LOG.info("Acknowledge Received Messages for Subscription {}, total of {} messages...",subscription, listAckIds.getAckIds().size());
		
		StringBuilder url = new StringBuilder(prepareUrl(URL_ACKNOWLEDGE).replaceAll("#subscription#", subscription));
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
		
		ResponseApiHandler response = doHttpPost(url.toString(), jsonPostData.toString(), ContentType.ApplicationJSON);
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
		
		StringBuilder url = new StringBuilder(prepareUrl(URL_PUBLISH_TOPIC).replaceAll("#topic#", topic));
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
		
		ResponseApiHandler response = doHttpPost(url.toString(), jsonPostData.toString(), ContentType.ApplicationJSON);
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

	private void logError(ResponseApiHandler response) {
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
		StringBuilder url = new StringBuilder(prepareUrl(URL_LIST_TOPIC_SUBSCRIPTIONS).replaceAll("#topic#", topic));
		url.append("?access_token=")
		   .append(generateAccessToken().getAccessToken());
		
		ResponseApiHandler response = doHttpGet(url.toString()); 
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
		StringBuilder url = new StringBuilder(this.prepareUrl(URL_LIST_TOPICS));
		url.append("?access_token=")
		   .append(generateAccessToken().getAccessToken());
		
		ResponseApiHandler response = doHttpGet(url.toString());
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
		AccessToken accessToken = jwtGmailApiHandler.retrieveAccessTokenToAPI(jwtGmailApiHandler.createTokenJWT(URL_API_PUB_SUB_SCOPES));
		return accessToken;
	}
	
	private String prepareUrl(String url) {
		return url.replaceAll("#project_id#", appServiceAccount.getProjectId());
	}
	

}
