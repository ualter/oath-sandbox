package com.ujr.oath.client.credentials.google.api.pubsub.domain;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "data", "attributes", "messageId", "publishTime" })
public class Message {

	@JsonProperty("data")
	private String data;
	@JsonProperty("attributes")
	private Map<String,String> attributes = null;
	@JsonProperty("messageId")
	private String messageId;
	@JsonProperty("publishTime")
	private String publishTime;

	@JsonProperty("data")
	public String getData() {
		return data;
	}

	@JsonProperty("data")
	public void setData(String data) {
		this.data = data;
	}

	@JsonProperty("attributes")
	public Map<String,String> getAttributes() {
		return attributes;
	}

	@JsonProperty("attributes")
	public void setAttributes(Map<String,String> attributes) {
		this.attributes = attributes;
	}

	@JsonProperty("messageId")
	public String getMessageId() {
		return messageId;
	}

	@JsonProperty("messageId")
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	@JsonProperty("publishTime")
	public String getPublishTime() {
		return publishTime;
	}

	@JsonProperty("publishTime")
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
	
	public void addAttribute(String key, String value) {
		if ( this.attributes == null ) {
			this.attributes = new HashMap<String,String>();
		}
		this.attributes.put(key, value);
	}
	
	@JsonIgnore(true)
	public String getDataDecoded() {
		return new String(Base64.getDecoder().decode(this.getData()));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Message [data=");
		builder.append(data);
		builder.append(", attributes=");
		if ( attributes != null ) {
			builder.append(attributes.entrySet().stream().map(s -> s.getKey() + ":" + s.getValue()).collect(Collectors.joining(";","[","]")));
		}
		builder.append(", messageId=");
		builder.append(messageId);
		builder.append(", publishTime=");
		builder.append(publishTime);
		builder.append("]");
		return builder.toString();
	}
	
	

}