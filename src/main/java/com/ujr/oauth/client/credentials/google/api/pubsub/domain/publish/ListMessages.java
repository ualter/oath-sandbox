package com.ujr.oauth.client.credentials.google.api.pubsub.domain.publish;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.ujr.oauth.client.credentials.google.api.pubsub.domain.Message;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "messages" })
public class ListMessages {

	@JsonProperty("messages")
	private List<Message> messages = null;

	@JsonProperty("messages")
	public List<Message> getMessages() {
		return messages;
	}

	@JsonProperty("messages")
	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
	
	public void addMessage(String msg) {
		this.addMessage(msg,null);
	}
	public void addMessage(String msg, Map<String,String> attributes) {
		if (this.messages == null) {
			this.messages = new ArrayList<Message>();
		}
		
		Message message = new Message();
		message.setData(Base64.getEncoder().encodeToString(msg.getBytes()));
		message.setMessageId("");
		message.setAttributes(attributes);
		addPublishTime(message);
		this.messages.add(message);
	}
	
	private void addPublishTime(Message message) {
		//message.setPublishTime("2014-10-02T15:01:23.045123456Z");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'KK:mm:ss.n'Z'");
		LocalDateTime localDateTime = LocalDateTime.now();
		message.setPublishTime(localDateTime.format(formatter));
	}

}