package com.ujr.oath;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ujr.oath.client.credentials.google.api.domain.error.HttpConnectionError;

public class ResponseApiHandler {
	
	final static Logger LOG = LoggerFactory.getLogger(ResponseApiHandler.class);
	
	private HttpStatus code;
	private String message;
	private StringBuilder content;
	private HttpConnectionError error;
	
	public ResponseApiHandler(int code, String message) {
		this.code    = HttpStatus.convert(code);
		this.message = message;
	}
	
	public HttpStatus getCode() {
		return code;
	}
	public void setCode(HttpStatus code) {
		this.code = code;
	}
	public StringBuilder getContent() {
		return content;
	}
	public void setContent(StringBuilder content) {
		this.content = content;
		
		if ( !this.getCode().isOk() ) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				this.error = mapper.readValue(this.getContent().toString(), HttpConnectionError.class);
			} catch (IOException e) {
				LOG.warn("It was not understable the JSON message error: (" + this.content + ") " + e.getMessage(),e);
				this.error = null;
			}
		}
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Optional<HttpConnectionError> getError() {
		return Optional.ofNullable(error);
	}
	public void setError(HttpConnectionError error) {
		this.error = error;
	}
	
	public <T> T convertResponseTo(Class<T> type) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(this.getContent().toString(), type);
		} catch (IOException e) {
			LOG.error(e.getMessage(),e);
			throw new RuntimeException(e);
		}
	}

	public static enum HttpStatus {
		OK(200), Created(201), Accepted(202), NoContent(204), 
		BadRequest(400), Unauthorized(401), Forbidden(403), NotFound(404), ProxyAuthenticationRequired(407),
		InternalServerError(500),
		Unknown(0);
		
		private int code;
		
		HttpStatus(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}
		
		public boolean isOk() {
			if ( this.code >= 200 && this.code <= 204 ) {
				return true;
			}
			return false;
		}
		
		public static HttpStatus convert(int code) {
			HttpStatus result = HttpStatus.Unknown;
			switch (code) {
				case 200: 
					result = HttpStatus.OK;
					break;
				case 201: 
					result = HttpStatus.Created;
					break;
				case 202: 
					result = HttpStatus.Accepted;
					break;
				case 204: 
					result = HttpStatus.NoContent;
					break;
				case 400: 
					result = HttpStatus.BadRequest;
					break;	
				case 401: 
					result = HttpStatus.Unauthorized;
					break;	
				case 403: 
					result = HttpStatus.Forbidden;
					break;	
				case 404: 
					result = HttpStatus.NotFound;
					break;
				case 407: 
					result = HttpStatus.ProxyAuthenticationRequired;
					break;	
				case 500:
					result = HttpStatus.InternalServerError;
					break;
				default: 
					result = HttpStatus.Unknown;
					break;
			}
			return result;
		}
	}


}
