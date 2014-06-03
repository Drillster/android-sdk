package com.drillster.api1.message.json.jackson;

import org.codehaus.jackson.annotate.JsonProperty;

import com.drillster.api1.message.Chart;

public interface ChartMixin {

	@JsonProperty("color-average")
	public String getColorAverage();

	@JsonProperty("color-average")
	public Chart setColorAverage(String colorAverage);

	@JsonProperty("color-deviation")
	public String getColorDeviation();

	@JsonProperty("color-deviation")
	public Chart setColorDeviation(String colorDeviation);

	@JsonProperty("color-individual")
	public String getColorIndividual();

	@JsonProperty("color-individual")
	public Chart setColorIndividual(String colorIndividual);
}
