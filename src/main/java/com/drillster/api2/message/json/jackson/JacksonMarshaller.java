package com.drillster.api2.message.json.jackson;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.node.ObjectNode;

import com.drillster.api2.drill.Course;
import com.drillster.api2.drill.Drill;
import com.drillster.api2.drill.Drillable;
import com.drillster.api2.general.Request;
import com.drillster.api2.general.Response;


/**
 *	Provides a marshaller/unmarshaller for Drillster JSON API messages to and
 *	from Drillster domain objects, using Jackson.
 *	<p>
 *	Various tricks are pulled to conform to some less-obvious requirements of
 *	the JSON API.
 *
 *	@author Tom van den Berge, Drillster BV.
 */
public final class JacksonMarshaller {

	private final ObjectMapper jsonMapper;

	public JacksonMarshaller() {
		this.jsonMapper = createJsonMapper();
	}

	/**
	 *	Marshals a {@link Request} domain object to JSON which can be sent to
	 *	the Drillster JSON API.
	 * 
	 *	@param request the request to be marshalled
	 *	@return the JSON representation of the request
	 */
	public String marshal(Object request) throws JsonGenerationException, JsonMappingException, IOException {
		// TODO modify the api object structure so we no longer need to manipulate the tree! This method should not be necessary at all!
		if (request instanceof Request) {
			ObjectNode objectNode = manipulateTree(requestToTree((Request) request));
			return this.jsonMapper.writeValueAsString(objectNode);
		}
		return this.jsonMapper.writeValueAsString(request);
	}


	/**
	 *	Unmarshals a JSON response to a {@link Response} domain object.
	 *
	 *	@param jsonResponse the exact JSON as received from the Drillster JSON
	 *		API
	 *	@return The unmarshalled Response object
	 */
	public Response unmarshal(String jsonResponse) throws JsonParseException, JsonMappingException, IOException {
		JsonNode tree = this.jsonMapper.readTree(jsonResponse);
		JsonNode drillNode = tree.get("response").get("drill");
		if (drillNode != null) {
			JsonNode tagsNode = drillNode.get("tags");
			if (tagsNode != null) {
				JsonNode tagNode = ((ObjectNode) tagsNode).remove("tag");
				if (tagNode != null) {
					((ObjectNode) drillNode).put("tags", tagNode);
				}
			}
		}
		return this.jsonMapper.treeToValue(tree.get("response"), Response.class);
	}


	public <T> T unmarshal(String jsonResponse, Class<T> responseType) throws JsonParseException, JsonMappingException, IOException {
		return this.jsonMapper.readValue(jsonResponse, responseType);
	}

	/**
	 *	Returns the underlying Jackson object mapper.
	 */
	public ObjectMapper getObjectMapper() {
		return this.jsonMapper;
	}

	ObjectMapper createJsonMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
		mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		configureJacksonMixIns(mapper);
		return mapper;
	}


	/**
	 *	Configures mix-in annotations for (de)serialization with Jackson.  The
	 *	mix-ins configure various collection properties to be serialized to and
	 *	deserialized from JSON with non-standard names.  This is necessary since
	 *	the Drillster JSON API requires these property names.  In a future
	 *	version of the JSON API, the property names will be regular names
	 *	though.
	 */
	private static void configureJacksonMixIns(ObjectMapper jsonMapper) {
		jsonMapper.getDeserializationConfig().addMixInAnnotations(
				Drillable.class, DrillableMixIn.class);
	}

	/*
	 * Mixing for polymorphic Drillable derserialization.
	 */
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
	@JsonSubTypes({ @Type(value = Course.class, name = "COURSE"),
			@Type(value = Drill.class, name = "DRILL") })
	private abstract class DrillableMixIn {

	}
	
	private ObjectNode requestToTree(Request request) {
		return this.jsonMapper.valueToTree(request);
	}

	private ObjectNode manipulateTree(ObjectNode tree) {
		// If present, "drill/entries" becomes "drill/entries/entry"
		JsonNode drillNode = tree.get("drill");
		if (drillNode != null) {
			JsonNode entriesNode = drillNode.get("entries");
			if (entriesNode != null) {
				ObjectNode node = tree.objectNode();
				node.put("entry", entriesNode);
				tree.put("entries", node);
			}
		}
		return tree;
	}
}
