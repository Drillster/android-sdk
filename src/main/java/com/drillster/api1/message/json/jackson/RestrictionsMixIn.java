package com.drillster.api1.message.json.jackson;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.drillster.api1.message.MultipleChoiceSet;
import com.drillster.api1.message.Restrictions;
import com.drillster.api1.message.Style;


public interface RestrictionsMixIn {

	@JsonProperty("style")
	List<Style> getStyles();
	@JsonProperty("style")
	Restrictions setStyles(List<Style> style);
	@JsonProperty("multiplechoice-set")
	MultipleChoiceSet getMultipleChoiceSet();
	@JsonProperty("multiplechoice-set")
	Restrictions setMultipleChoiceSet(MultipleChoiceSet multipleChoiceSet);
}
