package com.drillster.api1.message.json.jackson;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.drillster.api1.message.Proficiency;

public interface ProficienciesMixin {

	@JsonProperty("proficiency")
	List<Proficiency> getProficiencies();
}
