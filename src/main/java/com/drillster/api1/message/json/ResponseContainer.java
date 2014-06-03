package com.drillster.api1.message.json;

import com.drillster.api1.message.Response;


/**
 *	Contains a response from the Drillster API.
 *
 */
public class ResponseContainer {

	private Response response;

	public void setResponse(Response response) { this.response = response; }

	public Response getResponse() {	return this.response; }

}
