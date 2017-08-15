package com.ujr.oath.client.credentials.google.api.pubsub.domain.pull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.ujr.oath.client.credentials.google.api.pubsub.domain.Message;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "ackId", "message" })
public class ReceivedMessage {

	@JsonProperty("ackId")
	private String ackId;
	@JsonProperty("message")
	private Message message;

	@JsonProperty("ackId")
	public String getAckId() {
		return ackId;
	}

	@JsonProperty("ackId")
	public void setAckId(String ackId) {
		this.ackId = ackId;
	}

	@JsonProperty("message")
	public Message getMessage() {
		return message;
	}

	@JsonProperty("message")
	public void setMessage(Message message) {
		this.message = message;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReceivedMessage [ackId=");
		builder.append(ackId);
		builder.append(", message=");
		builder.append(message.toString());
		builder.append("]");
		return builder.toString();
	}
	
	

}