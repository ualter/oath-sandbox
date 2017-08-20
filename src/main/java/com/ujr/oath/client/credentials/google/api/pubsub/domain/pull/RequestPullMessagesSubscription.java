package com.ujr.oath.client.credentials.google.api.pubsub.domain.pull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "returnImmediately", "maxMessages" })
public class RequestPullMessagesSubscription {

	@JsonProperty("returnImmediately")
	private Boolean returnImmediately = new Boolean(false);
	@JsonProperty("maxMessages")
	@JsonIgnore(true)
	private Integer maxMessages;
	
	private String subscription;

	@JsonProperty("returnImmediately")
	public Boolean getReturnImmediately() {
		return returnImmediately;
	}

	@JsonProperty("returnImmediately")
	public void setReturnImmediately(Boolean returnImmediately) {
		this.returnImmediately = returnImmediately;
	}

	@JsonProperty("maxMessages")
	public Integer getMaxMessages() {
		return maxMessages;
	}

	@JsonProperty("maxMessages")
	public void setMaxMessages(Integer maxMessages) {
		this.maxMessages = maxMessages;
	}

	@JsonIgnore(true)
	public String getSubscription() {
		return subscription;
	}

	@JsonIgnore(true)
	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}
	
	

}