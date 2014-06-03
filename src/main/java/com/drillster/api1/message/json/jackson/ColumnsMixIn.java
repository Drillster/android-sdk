package com.drillster.api1.message.json.jackson;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.drillster.api1.message.Columns;
import com.drillster.api1.message.UnknownColumn;


public interface ColumnsMixIn {

	@JsonProperty("unknownColumn")
	List<UnknownColumn> getUnknownColumns();
	@JsonProperty("unknownColumn")
	Columns setUnknownColumns(List<UnknownColumn> unknownColumns);

}
