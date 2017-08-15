package com.ujr.oath;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Ualter
 *
 */
public abstract class ApiHandler {
	
	static final Logger LOG = LoggerFactory.getLogger(ApiHandler.class);
	
	public static enum ContentType {
		ApplicationJSON("application/json"), ApplicationFormUrlEncoded("application/x-www-form-urlencoded");
		
		private String descriptionType;
		ContentType(String descriptionType) {
			this.descriptionType = descriptionType;
		}
		
		public String descriptionType() {
			return this.descriptionType;
		}
	}
	
	
	public ResponseApiHandler doHttpPost(String urlAddress, String postData) {
		return doHttpPost(urlAddress, postData, ContentType.ApplicationFormUrlEncoded);
	}
	
	public ResponseApiHandler doHttpPost(String urlAddress, String postData, ContentType contentType) {
		HttpURLConnection  connection  = null;
		DataOutputStream   wr          = null;
		BufferedReader     br          = null;
		StringBuilder      sb          = null;
		ResponseApiHandler response    = null;
		try {
			// HTTP Connection POST
			LOG.trace(urlAddress);
			URL url = new URL(urlAddress);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", contentType.descriptionType());
			connection.setRequestProperty("Content-Length", "" + Integer.toString(postData.toString().getBytes().length));
			connection.setUseCaches(false);
			// Sending the POST data content
			LOG.trace(postData.toString());
		    wr = new DataOutputStream(connection.getOutputStream());
		    wr.writeBytes(postData.toString());
		    wr.flush();
		    
		    response = new ResponseApiHandler(connection.getResponseCode(), connection.getResponseMessage());
			// Retrieving the response
		    sb = readOutput(connection);
		    response.setContent(sb);
		    
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (ProtocolException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (connection != null)connection.disconnect();
				if ( wr != null) wr.close();
				if ( br != null) br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return response;
	}
	
	public ResponseApiHandler doHttpGet(String urlAddress) {
		HttpURLConnection  connection  = null;
		DataOutputStream   wr          = null;
		BufferedReader     br          = null;
		StringBuilder      sb          = null;
		ResponseApiHandler response    = null;
		try {
			// HTTP Connection GET
			LOG.trace(urlAddress);
			URL url = new URL(urlAddress);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setUseCaches(false);
			
			response = new ResponseApiHandler(connection.getResponseCode(), connection.getResponseMessage());
			// Retrieving the response
		    sb = readOutput(connection);
		    response.setContent(sb);
		    
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (ProtocolException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (connection != null)connection.disconnect();
				if ( wr != null) wr.close();
				if ( br != null) br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return response;
	}
	
	protected StringBuilder readOutput(HttpURLConnection connection) throws IOException {
		InputStream in = null;
		if ( connection.getResponseCode() >= 200 && connection.getResponseCode() <= 200 ) {
			in = connection.getInputStream();
		} else {
			in = connection.getErrorStream();
		}
		
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
			LOG.trace(line);
		}
		return sb;
	}
	
}
