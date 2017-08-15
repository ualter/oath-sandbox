package com.ujr.oath.client.credentials.google.api.pubsub.domain.pull;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "receivedMessages" })
public class ResponsePullMessagesSubscription {

	@JsonProperty("receivedMessages")
	private List<ReceivedMessage> receivedMessages = null;

	@JsonProperty("receivedMessages")
	public List<ReceivedMessage> getReceivedMessages() {
		return receivedMessages;
	}

	@JsonProperty("receivedMessages")
	public void setReceivedMessages(List<ReceivedMessage> receivedMessages) {
		this.receivedMessages = receivedMessages;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ResponsePullMessagesSubscription [receivedMessages=");
		builder.append(receivedMessages.stream().map(m -> m.getMessage().toString()).collect(Collectors.joining(" | ")));
		builder.append("]");
		return builder.toString();
	}
	
	
	
	

}