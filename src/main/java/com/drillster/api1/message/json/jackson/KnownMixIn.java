package com.drillster.api1.message.json.jackson;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.drillster.api1.message.Attachment;
import com.drillster.api1.message.Known;
import com.drillster.api1.message.Term;
import com.drillster.api1.message.Unknown;


public interface KnownMixIn {

	@JsonProperty("term")
	List<Term> getTerms();
	@JsonProperty("term")
	Unknown setTerms(List<Term> term);
	@JsonProperty("attachment")
	List<Attachment> getAttachments();
	@JsonProperty("attachment")
	Known setAttachments(List<Attachment> attachments);

}
