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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.HttpRequestFutureTask;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Ualter
 *
 */
public class HttpCommunicationHandler {
	
	static final Logger LOG = LoggerFactory.getLogger(HttpCommunicationHandler.class);
	
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
	
	
	public HttpResponse doHttpBasicPost(String urlAddress, String postData) {
		return doHttpBasicPost(urlAddress, postData, ContentType.ApplicationFormUrlEncoded);
	}
	
	public HttpResponse doHttpBasicPost(String urlAddress, String postData, ContentType contentType) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		Matcher matcher = Pattern.compile(".*=.*?(?=&)|(?!&).*=.*?$").matcher(postData);
		while ( matcher.find() ) {
			String parameters[] = matcher.group().split("=");
			params.add(new BasicNameValuePair(parameters[0],parameters[1]));
		}
		
		HttpResponse response  = null;
		try ( CloseableHttpClient httpclient = HttpClients.createDefault() ) {
			HttpPost httpPost = new HttpPost(urlAddress);
		    httpPost.setEntity(new UrlEncodedFormEntity(params));
			httpPost.setHeader("Content-Type", contentType.descriptionType());
			try (CloseableHttpResponse httpResponse = httpclient.execute(httpPost) ) {
				// httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK
				HttpEntity httpEntity = httpResponse.getEntity();
				String responseContent = EntityUtils.toString(httpEntity);
				response = new HttpResponse(httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase());
				response.setContent(new StringBuilder(responseContent));
				response.setCode(httpResponse.getStatusLine().getStatusCode());
			}
		} catch (IOException e1) {
			LOG.error(e1.getMessage(),e1);
			throw new RuntimeException(e1);
		}
		return response;
	}
	
	public HttpResponse doHttpJSONPost(String urlAddress, String jsonData) {
		return doHttpJSONPost(urlAddress, jsonData, ContentType.ApplicationJSON);
	}
	
	public HttpResponse doHttpJSONPost(String urlAddress, String jsonData, ContentType contentType) {
		HttpResponse response  = null;
		try ( CloseableHttpClient httpclient = HttpClients.createDefault() ) {
			HttpPost httpPost = new HttpPost(urlAddress);
			StringEntity entityJson = new StringEntity(jsonData);
		    httpPost.setEntity(entityJson);
		    httpPost.setHeader("Accept", "application/json");
		    httpPost.setHeader("Content-type", contentType.descriptionType());
			try (CloseableHttpResponse httpResponse = httpclient.execute(httpPost) ) {
				// httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK
				HttpEntity httpEntity = httpResponse.getEntity();
				String responseContent = EntityUtils.toString(httpEntity);
				response = new HttpResponse(httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase());
				response.setContent(new StringBuilder(responseContent));
				response.setCode(httpResponse.getStatusLine().getStatusCode());
			}
		} catch (IOException e1) {
			LOG.error(e1.getMessage(),e1);
			throw new RuntimeException(e1);
		}
		return response;
	}
	
//	HttpURLConnection  connection  = null;
//	DataOutputStream   wr          = null;
//	BufferedReader     br          = null;
//	StringBuilder      sb          = null;
//	ResponseApiHandler response    = null;
//	try {
//		// HTTP Connection POST
//		LOG.trace(urlAddress);
//		URL url = new URL(urlAddress);
//		connection = (HttpURLConnection) url.openConnection();
//		connection.setDoOutput(true);
//		connection.setDoInput(true);
//		connection.setInstanceFollowRedirects(true);
//		connection.setRequestMethod("POST");
//		connection.setRequestProperty("Content-Type", contentType.descriptionType());
//		connection.setRequestProperty("Content-Length", "" + Integer.toString(postData.toString().getBytes().length));
//		connection.setUseCaches(false);
//		// Sending the POST data content
//		LOG.trace(postData.toString());
//	    wr = new DataOutputStream(connection.getOutputStream());
//	    wr.writeBytes(postData.toString());
//	    wr.flush();
//	    
//	    response = new ResponseApiHandler(connection.getResponseCode(), connection.getResponseMessage());
//		// Retrieving the response
//	    sb = readOutput(connection);
//	    response.setContent(sb);
//	    
//	} catch (MalformedURLException e) {
//		throw new RuntimeException(e);
//	} catch (ProtocolException e) {
//		throw new RuntimeException(e);
//	} catch (IOException e) {
//		throw new RuntimeException(e);
//	} finally {
//		try {
//			if (connection != null)connection.disconnect();
//			if ( wr != null) wr.close();
//			if ( br != null) br.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	return response;
	
	public HttpResponse doHttpGet(String urlAddress) {
		HttpURLConnection  connection  = null;
		DataOutputStream   wr          = null;
		BufferedReader     br          = null;
		StringBuilder      sb          = null;
		HttpResponse response    = null;
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
			
			response = new HttpResponse(connection.getResponseCode(), connection.getResponseMessage());
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
