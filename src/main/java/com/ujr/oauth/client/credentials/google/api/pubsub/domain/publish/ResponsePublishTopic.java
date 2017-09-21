package com.ujr.oauth.client.credentials.google.api.pubsub.domain.publish;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 
 * see https://cloud.google.com/pubsub/docs/reference/rest/v1/projects.topics/publish
 * @author Ualter
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "messageIds" })
public class ResponsePublishTopic {

	@JsonProperty("messageIds")
	private List<String> messageIds = null;
	
	@JsonIgnore
	private List<String> messages = null;

	@JsonProperty("messageIds")
	public List<String> getMessageIds() {
		return messageIds;
	}

	@JsonProperty("messageIds")
	public void setMessageIds(List<String> messageIds) {
		this.messageIds = messageIds;
	}

	@JsonIgnore
	public List<String> getMessages() {
		return messages;
	}
	
	@JsonIgnore
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	
	

}