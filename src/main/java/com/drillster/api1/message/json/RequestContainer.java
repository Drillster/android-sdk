package com.drillster.api1.message.json;

import com.drillster.api1.message.Request;


/**
 *	Contains a request to the Drillster API.
 *
 */
public class RequestContainer {

	private final Request request;

	public RequestContainer(Request request) {
		this.request = request;
	}
	
	public Request getRequest() {
		return this.request;
	}
}
