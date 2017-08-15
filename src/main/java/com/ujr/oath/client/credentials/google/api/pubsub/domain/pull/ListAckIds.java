package com.ujr.oath.client.credentials.google.api.pubsub.domain.pull;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "ackIds" })
public class ListAckIds {

	@JsonProperty("ackIds")
	private List<String> ackIds = null;

	@JsonProperty("ackIds")
	public List<String> getAckIds() {
		return ackIds;
	}

	@JsonProperty("ackIds")
	public void setAckIds(List<String> ackIds) {
		this.ackIds = ackIds;
	}

}