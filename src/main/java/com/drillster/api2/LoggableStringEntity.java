package com.drillster.api2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import ch.boye.httpclientandroidlib.entity.AbstractHttpEntity;
import ch.boye.httpclientandroidlib.entity.StringEntity;

/**
 * Utility wrapper class for {@link StringEntity}, providing a
 * {@link #toString()} implementation which allows it to be logged decently.
 * Apart from toString, the behavior is identical to StringEntity.
 * 
 * @author Tom van den Berge, Drillster BV.
 */
public class LoggableStringEntity extends AbstractHttpEntity implements Cloneable {

	private final StringEntity stringEntity;
	private final String content;

	public LoggableStringEntity(final String string, String mimeType, String charset) throws UnsupportedEncodingException {
		this.stringEntity = new StringEntity(string, mimeType, charset);
		this.content = string;
	}

	@Override
	public boolean isRepeatable() {
		return stringEntity.isRepeatable();
	}

	@Override
	public long getContentLength() {
		return stringEntity.getContentLength();
	}

	@Override
	public InputStream getContent() throws IOException, IllegalStateException {
		return stringEntity.getContent();
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		stringEntity.writeTo(outstream);
	}

	@Override
	public boolean isStreaming() {
		return stringEntity.isStreaming();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Returns the string content.
	 */
	@Override
	public String toString() {
		return content;
	}
}
