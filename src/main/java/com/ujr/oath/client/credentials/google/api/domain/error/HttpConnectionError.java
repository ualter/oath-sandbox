package com.ujr.oath.client.credentials.google.api.domain.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "error" })
public class HttpConnectionError {

	@JsonProperty("error")
	private Error error;

	@JsonProperty("error")
	public Error getError() {
		return error;
	}

	@JsonProperty("error")
	public void setError(Error error) {
		this.error = error;
	}

}