package com.drillster.api1.message.json.jackson;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.drillster.api1.message.Entries;
import com.drillster.api1.message.Entry;


public interface EntriesMixIn {

	@JsonProperty("entry")
	List<Entry> getEntries();
	@JsonProperty("entry")
	Entries setEntries(List<Entry> entries);

}
