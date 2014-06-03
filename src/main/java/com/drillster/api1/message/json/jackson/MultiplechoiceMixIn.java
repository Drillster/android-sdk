package com.drillster.api1.message.json.jackson;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.drillster.api1.message.Attachment;
import com.drillster.api1.message.MultipleChoice;


public interface MultiplechoiceMixIn {

	@JsonProperty("attachment")
	List<Attachment> getAttachments();
	@JsonProperty("attachment")
	MultipleChoice setAttachments(List<Attachment> attachments);

}
