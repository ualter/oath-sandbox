package com.ujr.oauth.client.credentials.google.api.pubsub.taks.pullmessages;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ujr.oauth.HttpResponse;
import com.ujr.oauth.client.credentials.google.api.appservice.GoogleAppServiceAccount;
import com.ujr.oauth.client.credentials.google.api.pubsub.GooglePubSubApiUtils;
import com.ujr.oauth.client.credentials.google.api.pubsub.domain.pull.ListAckIds;
import com.ujr.oauth.client.credentials.google.api.pubsub.domain.pull.RequestPullMessagesSubscription;
import com.ujr.oauth.client.credentials.google.api.pubsub.domain.pull.ResponsePullMessagesSubscription;
import com.ujr.oauth.client.credentials.google.api.pubsub.tasks.AbstractCallable;

public class CallablePullMessagesSubscription extends AbstractCallable<ResponsePullMessagesSubscription> {

	static final Logger LOG = LoggerFactory.getLogger(CallablePullMessagesSubscription.class);
	
	private RequestPullMessagesSubscription request;
	private boolean acknowledge;
	
	public CallablePullMessagesSubscription(GoogleAppServiceAccount appServiceAccount, RequestPullMessagesSubscription request, boolean acknowledge) {
		super(appServiceAccount);
		this.request     = request;
		this.acknowledge = acknowledge;
	}
	
	
	@Override
	public ResponsePullMessagesSubscription call() throws Exception {
		ResponsePullMessagesSubscription responsePullMessagesSubscription = null;
		
		LOG.info("Pull Messages for Subscription {}...", request.getSubscription());
		
		StringBuilder url = new StringBuilder(prepareUrl(GooglePubSubApiUtils.URL_PULL_MSGS_SUBSCRIPTION).replaceAll("#subscription#", request.getSubscription()));
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
		
		HttpResponse httpResponse = this.httpCommunicationHandler.doHttpJSONPost(url.toString(), jsonPostData.toString());
		if ( httpResponse.getCode().isOk() ) { 
			responsePullMessagesSubscription = httpResponse.convertResponseTo(ResponsePullMessagesSubscription.class);
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
			logError(httpResponse);
		}
		return responsePullMessagesSubscription;
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
		
		HttpResponse response = this.httpCommunicationHandler.doHttpJSONPost(url.toString(), jsonPostData.toString());
		if ( response.getCode().isOk() ) { 
			LOG.info("Successfully Acknowledge!");
			listAckIds.getAckIds().forEach(s -> LOG.trace(" ==> AckId {}",s));
		} else {
			logError(response);
		}
	}

	
}
