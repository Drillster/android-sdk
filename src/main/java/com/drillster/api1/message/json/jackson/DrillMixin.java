package com.drillster.api1.message.json.jackson;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.drillster.api1.message.Course;
import com.drillster.api1.message.Drill;


public interface DrillMixin {

	@JsonProperty("course")
	public List<Course> getCourses();

	@JsonProperty("course")
	public Drill setCourses(List<Course> courses);

}
