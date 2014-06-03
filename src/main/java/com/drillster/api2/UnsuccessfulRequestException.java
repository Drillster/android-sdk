package com.drillster.api2;

import com.drillster.api2.general.Error;

@SuppressWarnings("serial")
public class UnsuccessfulRequestException extends ApiException {

	private Error error;
	
	public UnsuccessfulRequestException(Error error) {
		super(error.getDescription());
		this.error = error;
	}
	
	public Error getError() {
		return this.error;
	}
}
