package com.drillster.api1.message.json.jackson;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.drillster.api1.message.Entry;
import com.drillster.api1.message.Unknown;


public interface EntryMixIn {

	@JsonProperty("unknown")
	List<Unknown> getUnknowns();
	@JsonProperty("unknown")
	Entry setUnknowns(List<Unknown> unknowns);

}
