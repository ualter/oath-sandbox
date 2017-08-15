package com.ujr.oath.client.credentials.google.api.pubsub.domain.list;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "subscriptions" })
public class ListSubscription {

	@JsonProperty("subscriptions")
	private List<String> subscriptions = null;

	@JsonProperty("subscriptions")
	public List<String> getSubscriptions() {
		return subscriptions;
	}

	@JsonProperty("subscriptions")
	public void setSubscriptions(List<String> subscriptions) {
		this.subscriptions = subscriptions;
	}

}