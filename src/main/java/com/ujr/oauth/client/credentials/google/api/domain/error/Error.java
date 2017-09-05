package com.ujr.oauth.client.credentials.google.api.domain.error;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "code", "message", "status", "details" })
public class Error {

	@JsonProperty("code")
	private Integer code;
	@JsonProperty("message")
	private String message;
	@JsonProperty("status")
	private String status;
	@JsonProperty("details")
	private List<Detail> details = null;

	@JsonProperty("code")
	public Integer getCode() {
		return code;
	}

	@JsonProperty("code")
	public void setCode(Integer code) {
		this.code = code;
	}

	@JsonProperty("message")
	public String getMessage() {
		return message;
	}

	@JsonProperty("message")
	public void setMessage(String message) {
		this.message = message;
	}

	@JsonProperty("status")
	public String getStatus() {
		return status;
	}

	@JsonProperty("status")
	public void setStatus(String status) {
		this.status = status;
	}

	@JsonProperty("details")
	public List<Detail> getDetails() {
		return details;
	}

	@JsonProperty("details")
	public void setDetails(List<Detail> details) {
		this.details = details;
	}

}