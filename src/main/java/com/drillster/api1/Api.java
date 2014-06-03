package com.drillster.api1;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpDelete;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.methods.HttpPut;
import ch.boye.httpclientandroidlib.entity.StringEntity;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
import ch.boye.httpclientandroidlib.protocol.BasicHttpContext;
import ch.boye.httpclientandroidlib.util.EntityUtils;

import com.drillster.api1.message.Request;
import com.drillster.api1.message.Response;
import com.drillster.api1.message.TokenRequest;
import com.drillster.api1.message.TokenResponse;
import com.drillster.api1.message.json.jackson.JacksonMarshaller;
import com.drillster.api1.message.xml.xstream.CollectionTweaker;
import com.thoughtworks.xstream.XStream;


/**
 *	Provides the base class for the Drillster API SDK.  See http://drill.st/api
 *	for more information about the API.  Go to http://www.drillster.com for
 *	more information about Drillster.
 *
 *	@author Tom van den Berge
 *	@author Thomas Goossens
 *	Provided by Drillster BV, dual licensed under MIT and GPL.
 */
public class Api {

	public enum ContentType {
		JSON("application/json", ".json"), XML("text/xml", ".xml");

		private String contentTypeString;
		private String extension;

		private ContentType(String contentTypeString, String extension) {
			this.contentTypeString = contentTypeString;
			this.extension = extension;
		}
	}

	public enum HttpMethod {
		GET, POST, PUT, DELETE;
	}

	private String oAuthToken;
	private ContentType contentType = ContentType.JSON;
	private String hostName = "www.drillster.com";
	private Integer port = 443;
	private String scheme = "https";
	private DefaultHttpClient httpClient;
	private BasicHttpContext localcontext;
	private HttpHost targetHost;
	private XStream xstream;
	private CollectionTweaker collectionTweaker;
	private JacksonMarshaller jsonMarshaller;

	public String getHostName() {
		return this.hostName;
	}

	public Integer getPort() {
		return this.port;
	}

	public String getScheme() {
		return this.scheme;
	}

	public ContentType getContentType() {
		return this.contentType;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	public Response getRepertoire() throws ApiException {
		return sendGetRequest("/api/repertoire");
	}

	public Response getCourses() throws ApiException {
		return sendGetRequest("/api/courses");
	}

	public void setOAuthToken(String oAuthToken) {
		this.oAuthToken = oAuthToken;
	}

	public boolean isAuthenticated() {
		return this.oAuthToken != null;
	}
	
	private synchronized void init() {
		if (this.httpClient != null) {
			return;
		}
		this.httpClient = new DefaultHttpClient();
		this.targetHost = new HttpHost(this.hostName, this.port, this.scheme);
		this.xstream = createXStream();
		this.collectionTweaker = new CollectionTweaker();
		this.jsonMarshaller = new JacksonMarshaller();
	}

	XStream createXStream() {
		XStream xs = new XStream();
		xs.alias("response", Response.class);	// root class
		xs.autodetectAnnotations(true);
		xs.setMode(XStream.NO_REFERENCES);
		return xs;
	}

	public Response sendPostRequest(String url, Request request) throws ApiException {
		return sendRequestWithContent(url, request, HttpMethod.POST, Response.class);
	}

	public TokenResponse sendTokenRequest(String url, TokenRequest request) throws ApiException {
		init();
		return deserializeFromJson(sendRequestInternal(url, convert(request), HttpMethod.POST, ContentType.JSON), TokenResponse.class);
	}

	private HttpEntity convert(TokenRequest request) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		addNonNullParam(params, "client_id", request.getClient_id());
		addNonNullParam(params, "client_secret", request.getClient_secret());
		addNonNullParam(params, "grant_type", request.getGrant_type());
		addNonNullParam(params, "scope", request.getScope());
		addNonNullParam(params, "username", request.getUsername());
		addNonNullParam(params, "password", request.getPassword());
		addNonNullParam(params, "code", request.getCode());
		addNonNullParam(params, "refresh_token", request.getRefresh_token());
		try {
			return new UrlEncodedFormEntity(params, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private static void addNonNullParam(List<NameValuePair> params, String paramName, String paramValue) {
		if (paramValue != null) {
			params.add(new BasicNameValuePair(paramName, paramValue));
		}
	}

	public Response sendPutRequest(String url, Request request) throws ApiException {
		return sendRequestWithContent(url, request, HttpMethod.PUT, Response.class);
	}

	public Response sendPutRequest(String url) throws ApiException {
		return sendRequest(url, HttpMethod.PUT);
	}

	public Response sendDeleteRequest(String url) throws ApiException {
		return sendRequest(url, HttpMethod.DELETE);
	}

	public Response sendGetRequest(String url) throws ApiException {
		init();
		String response = sendRequestInternal(url, (String)null, HttpMethod.GET);
		if (this.contentType == ContentType.JSON) {
			return deserializeFromJson(response);
		}
		return deserializeFromXml(response, Response.class);
	}

	private <T> T sendRequestWithContent(String url, Object request, HttpMethod method, Class<T> responseType) throws ApiException {
		init();
		try {
			if (this.contentType == ContentType.JSON) {
				StringEntity content = new StringEntity(
						serializeToJson(request),
						this.contentType.contentTypeString, "UTF-8");
				return deserializeFromJson(
						sendRequestInternal(url, content, method, this.contentType), responseType);
			}

			StringEntity content = new StringEntity(serializeToXml(request),
					this.contentType.contentTypeString, "UTF-8");
			return deserializeFromXml(
					sendRequestInternal(url, content, method, this.contentType), responseType);
		} catch (UnsupportedEncodingException e) {
			throw new ApiException(e);
		}
	}

	private Response sendRequest(String url, HttpMethod method) throws ApiException {
		init();
		if (this.contentType == ContentType.JSON) {
			return deserializeFromJson(sendRequestInternal(url, null, method));
		}
		return deserializeFromXml(sendRequestInternal(url, null, method), Response.class);
	}

	/**
	 * Serializes the provided object to XML. 
	 */
	public String serializeToXml(Object content) {
		init();
		if (content instanceof Request) {
			this.collectionTweaker.tweak((Request) content);
		}
		return this.xstream.toXML(content);
	}

	@SuppressWarnings("unchecked")
	public <T> T deserializeFromXml(String xml, Class<T> responseType) {
		return (T) this.xstream.fromXML(xml);
	}

	public String serializeToJson(Object content) throws ApiException {
		init();
		try {
			String json = this.jsonMarshaller.marshal(content);
			System.out.println(json);
			return json;
		} catch (JsonGenerationException jgx) {
			throw new ApiException(jgx);
		} catch (JsonMappingException jmx) {
			throw new ApiException(jmx);
		} catch (IOException iox) {
			throw new ApiException(iox);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T deserializeFromJson(String json, Class<T> responseType) throws ApiException {
		init();
		try {
			if (responseType.equals(Response.class)) {
				return (T) this.jsonMarshaller.unmarshal(json);
			}
			return this.jsonMarshaller.unmarshal(json, responseType);
		} catch (JsonParseException jpx) {
			throw new ApiException(jpx);
		} catch (JsonMappingException jmx) {
			throw new ApiException(jmx);
		} catch (IOException iox) {
			throw new ApiException(iox);
		}
	}

	private Response deserializeFromJson(String json) throws ApiException {
		try {
			return this.jsonMarshaller.unmarshal(json);
		} catch (JsonParseException jpx) {
			throw new ApiException(jpx);
		} catch (JsonMappingException jmx) {
			throw new ApiException(jmx);
		} catch (IOException iox) {
			throw new ApiException(iox);
		}
	}

	private String sendRequestInternal(String url, String content, HttpMethod method) throws ApiException {
		String target = url + this.contentType.extension;
		System.out.println("Sending request to " + target + ". Content:\n" + content);

		HttpRequest request;
		if (method == HttpMethod.GET) {
			request = new HttpGet(target);
		} else {
			try {
				if (method == HttpMethod.PUT) {
					HttpPut put = new HttpPut(target);
					if (content != null) {
						put.setEntity(new StringEntity(content, this.contentType.contentTypeString, "UTF-8"));
					}
					request = put;
				} else if (method == HttpMethod.POST) {
					HttpPost post = new HttpPost(target);
					if (content != null) {
						post.setEntity(new StringEntity(content, this.contentType.contentTypeString, "UTF-8"));
					}
					request = post;
				} else if (method == HttpMethod.DELETE) {
					request = new HttpDelete(target);
				} else {
					throw new IllegalArgumentException();
				}
			} catch (UnsupportedEncodingException e) {
				throw new ApiException(e);
			}
		}
		return sendRequestInternal(request);
	}

	private String sendRequestInternal(String url, HttpEntity content, HttpMethod method, ContentType ct) throws ApiException {
		String target = url + ct.extension;
		System.out.println("Sending request to " + target + ". Content:\n" + content);

		HttpRequest request;
		if (method == HttpMethod.GET) {
			request = new HttpGet(target);
		} else {
			if (method == HttpMethod.PUT) {
				HttpPut put = new HttpPut(target);
				put.setEntity(content);
				request = put;
			} else if (method == HttpMethod.POST) {
				HttpPost post = new HttpPost(target);
				post.setEntity(content);
				request = post;
			} else {
				throw new IllegalArgumentException();
			}
		}
		return sendRequestInternal(request);
	}

	private String sendRequestInternal(HttpRequest httpRequest) throws ApiException {
		httpRequest.addHeader("Authorization", "OAuth " + this.oAuthToken);
		try {
			HttpResponse response = this.httpClient.execute(this.targetHost, httpRequest, this.localcontext);
			String responseBody = EntityUtils.toString(response.getEntity());
			System.out.println("Received:\n" + responseBody);
			return responseBody;
		} catch (ClientProtocolException e) {
			throw new ApiException(e);
		} catch (IOException e) {
			throw new ApiException(e);
		}
	}

}
