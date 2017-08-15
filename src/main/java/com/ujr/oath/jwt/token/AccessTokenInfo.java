package com.ujr.oath.jwt.token;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "azp", "aud", "scope", "exp", "expires_in", "access_type" })
public class AccessTokenInfo {

	@JsonProperty("azp")
	private String azp;
	@JsonProperty("aud")
	private String aud;
	@JsonProperty("scope")
	private String scope;
	@JsonProperty("exp")
	private String exp;
	@JsonProperty("expires_in")
	private String expiresIn;
	@JsonProperty("access_type")
	private String accessType;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("azp")
	public String getAzp() {
		return azp;
	}

	@JsonProperty("azp")
	public void setAzp(String azp) {
		this.azp = azp;
	}

	@JsonProperty("aud")
	public String getAud() {
		return aud;
	}

	@JsonProperty("aud")
	public void setAud(String aud) {
		this.aud = aud;
	}

	@JsonProperty("scope")
	public String getScope() {
		return scope;
	}

	@JsonProperty("scope")
	public void setScope(String scope) {
		this.scope = scope;
	}

	@JsonProperty("exp")
	public String getExp() {
		return exp;
	}

	@JsonProperty("exp")
	public void setExp(String exp) {
		this.exp = exp;
	}

	@JsonProperty("expires_in")
	public String getExpiresIn() {
		return expiresIn;
	}

	@JsonProperty("expires_in")
	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}

	@JsonProperty("access_type")
	public String getAccessType() {
		return accessType;
	}

	@JsonProperty("access_type")
	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@Override
	public String toString() {
		return "AccessTokenInfo [azp=" + azp + ", aud=" + aud + ", scope=" + scope + ", exp=" + exp + ", expiresIn="
				+ expiresIn + ", accessType=" + accessType + "]";
	}
	
	
	
	

}