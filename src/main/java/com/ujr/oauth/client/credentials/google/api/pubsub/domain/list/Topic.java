package com.ujr.oauth.client.credentials.google.api.pubsub.domain.list;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name" })
public class Topic {

	@JsonProperty("name")
	private String name;

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		if (this.getName().lastIndexOf("/") > 0) {
			return this.getName().substring(this.getName().lastIndexOf("/") + 1);
		} else {
			return this.getName();
		}
	}

}