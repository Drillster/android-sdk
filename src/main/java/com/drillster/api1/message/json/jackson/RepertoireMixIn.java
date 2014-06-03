package com.drillster.api1.message.json.jackson;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.drillster.api1.message.Drill;
import com.drillster.api1.message.Repertoire;


public interface RepertoireMixIn {

	@JsonProperty("drill")
	List<Drill> getDrills();
	@JsonProperty("drill")
	Repertoire setDrills(List<Drill> drill);

}
