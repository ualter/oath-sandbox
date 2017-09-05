package com.ujr.oauth.client.credentials.google.api.pubsub.domain.list;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "topics" })
public class ListTopics {

	@JsonProperty("topics")
	private List<Topic> topics = null;

	@JsonProperty("topics")
	public List<Topic> getTopics() {
		return topics;
	}

	@JsonProperty("topics")
	public void setTopics(List<Topic> topics) {
		this.topics = topics;
	}

}
