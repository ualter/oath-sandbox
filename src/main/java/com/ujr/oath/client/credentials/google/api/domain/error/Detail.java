package com.ujr.oath.client.credentials.google.api.domain.error;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "@type", "fieldViolations" })
public class Detail {

	@JsonProperty("@type")
	private String type;
	@JsonProperty("fieldViolations")
	private List<FieldViolation> fieldViolations = null;

	@JsonProperty("@type")
	public String getType() {
		return type;
	}

	@JsonProperty("@type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("fieldViolations")
	public List<FieldViolation> getFieldViolations() {
		return fieldViolations;
	}

	@JsonProperty("fieldViolations")
	public void setFieldViolations(List<FieldViolation> fieldViolations) {
		this.fieldViolations = fieldViolations;
	}

}