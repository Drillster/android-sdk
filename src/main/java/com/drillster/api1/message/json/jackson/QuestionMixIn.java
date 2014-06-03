package com.drillster.api1.message.json.jackson;

import java.util.Set;

import org.codehaus.jackson.annotate.JsonProperty;

import com.drillster.api1.message.Drill;
import com.drillster.api1.message.Question;


public interface QuestionMixIn {

	@JsonProperty("drill")
	Set<Drill> getDrills();
	@JsonProperty("drill")
	Question setDrills(Set<Drill> drill);

}
