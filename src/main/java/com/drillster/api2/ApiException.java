package com.drillster.api2;


/**
 *	Indicates an exception in the communication with the Drillster API.
 */
@SuppressWarnings("serial")
public class ApiException extends Exception {

	public ApiException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApiException(String message) {
		super(message);
	}

	public ApiException(Throwable cause) {
		super(cause);
	}

}
