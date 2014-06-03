package com.drillster.api1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.junit.BeforeClass;
import org.junit.Test;

import com.drillster.api1.Api.ContentType;
import com.drillster.api1.message.Attachment;
import com.drillster.api1.message.Chart;
import com.drillster.api1.message.Course;
import com.drillster.api1.message.Drill;
import com.drillster.api1.message.Group;
import com.drillster.api1.message.GroupSummary;
import com.drillster.api1.message.Question;
import com.drillster.api1.message.Request;
import com.drillster.api1.message.Response;
import com.drillster.api1.message.TokenRequest;
import com.drillster.api1.message.TokenResponse;
import com.drillster.api1.message.User;
import com.drillster.api1.message.json.ResponseContainer;

public class ApiTest {

	private static String oAuthToken = "9a9a8d3d4bf640f9acf76db8cfed8be5";	// tom.vandenberge@gmail.com	// Vlt
	//private static String oAuthToken = "6181807f675844058d00153eb68b07ba";	// tom.vandenberge@gmail.com	// PROD
	//private static String oAuthToken = "8dbb284ca24a4918a74449e82d351cb7";	// tom.vandenberge@gmail.com		// Wdx
	//private static String oAuthToken = "f800fc6e26d548db86ccdc96099961e7"; // harry@drillster.com
	//private static String oAuthToken = "f3abdfbc0fd14b02b76156afe40df756"; // dogbert@drillster.com
	//private static String oAuthToken = "7dbadd2b99324f489d5f4f43db08feb8"; // harry123@drillster.com
	
	private static String hostName = "localhost";
	private static Integer port = 8080;
	private static String scheme = "http";

//	private static String hostName = "www.drillster.com";
//	private static Integer port = 443;
//	private static String scheme = "https";

	
	
	private static Api api = new Api();

	@BeforeClass
	public static void setUp() {
		api.setHostName(hostName);
		api.setPort(port);
		api.setScheme(scheme);
		api.setOAuthToken(oAuthToken);
		api.setContentType(ContentType.XML);
	}

	@Test
	public void printGroups() throws ApiException {
		Response response = api.sendGetRequest("/api/groups");
		System.out.println("Adminstrator: "+response.getGroups().getAdministrator());
		for (GroupSummary group : response.getGroups().getGroups()) {
			System.out.println(group.getName());
		}
	}

	String groupCode = "_Mc4cB0feU3JQkksmGCqD1xqhoHTsQ9no9nlCP73VyQ"; // Dogbert's group.
	
	@Test
	public void printGroup() throws ApiException {
		Response response = api.sendGetRequest("/api/group/"+groupCode);
		System.out.println(response.getGroup().getDescription());
		for (User member : response.getGroup().getMembers()) {
			System.out.println(member.getId() + ", " + member.getEmailAddress() + ", status=" + member.getStatus());
		}
	}

	@Test
	public void printGroupMembers() throws ApiException {
		Response response = api.sendGetRequest("/api/group/" + groupCode + "/members");
		System.out.println(response.getGroup().getDescription());
	}
	
	@Test
	public void addGroupMember() throws ApiException {
		//api.sendPutRequest("/api/group/" + groupCode + "/members/tNI2jV88S_i1ie0UZHIfLw"); // piet@drillster.com
		//api.sendPutRequest("/api/group/" + groupCode + "/members/qYffq1mPTCKI7KT2x08ukA"); //tom.vandenberge@drillster.com
		api.sendPutRequest("/api/group/" + groupCode + "/members/LUdWRfzgSYWdS9hCu2k-Lw"); //demo-tg@drillster.com
		//api.sendPutRequest("/api/group/b-WC4KA4yynTzcGt2C7L46B-0wzeHAJzM6vlL1te6wI/members/9L0gaDiBRK6tYc6GsSHvIQ"); // George Baker
		//Request request = new Request().setUser(new User().setRealName("Tommie"));	// RhBb00H2TxibtznXAmqCpw = "Test Course User 1"
		//api.sendPutRequest("/api/group/b-WC4KA4yynTzcGt2C7L46B-0wzeHAJzM6vlL1te6wI/members/george@drillster.com", request);
	}

	@Test
	public void deleteGroupMember() throws ApiException {
		//api.sendDeleteRequest("/api/group/b-WC4KA4yynTzcGt2C7L46B-0wzeHAJzM6vlL1te6wI/members/tNI2jV88S_i1ie0UZHIfLw"); // piet@drillster.com
		api.sendDeleteRequest("/api/group/" + groupCode + "/members/tNI2jV88S_i1ie0UZHIfLw");
	}
	
	@Test
	public void updateGroup() throws ApiException {
		Group group = api.sendGetRequest("/api/group/b-WC4KA4yynTzcGt2C7L46B-0wzeHAJzM6vlL1te6wI").getGroup();
		
		
		List<User> members = group.getMembers();
		//members.add(new User().setId("tNI2jV88S_i1ie0UZHIfLw")); // pietje@drillster.com (Pietje Puk)
		//members.add(new User().setId("RhBb00H2TxibtznXAmqCpw"));
		
		List<Drill> drills = group.getDrills();
//		drills.add(new Drill().setCode("NcM6X_5q-7fZ8lrgyl-GHQ"));	// Francais - Les couleurs
		drills.remove(1);
		
		Request request = new Request().setGroup(group.setMembers(members).setDrills(drills));
		
		api.sendPostRequest("/api/group/b-WC4KA4yynTzcGt2C7L46B-0wzeHAJzM6vlL1te6wI", request);
	}
	
	@Test
	public void createGroup() throws ApiException {
		
		Group group = new Group().setName("API group").setDescription("API group description");
		List<Drill> drills = new ArrayList<Drill>();
		//drills.add(new Drill().setCode("NcM6X_5q-7fZ8lrgyl-GHQ"));
		drills.add(new Drill().setCode("CoJkun-NTaW8c7G9porJMg"));
		group.setDrills(drills);
		
		List<User> users = new ArrayList<User>();
		users.add(new User().setId("tNI2jV88S_i1ie0UZHIfLw"));
		users.add(new User().setId("RhBb00H2TxibtznXAmqCpw"));
		group.setMembers(users);
		
		Response response = api.sendPutRequest("/api/group", new Request().setGroup(group));
	}
	
	@Test
	public void getUserDetails() throws ApiException {
		Response response = api.sendGetRequest("/api/user/george@drillster.com");
	}
	
	@Test
	public void createNewUser() throws ApiException {
	//	Response response = api.sendPutRequest("/api/user", new Request().setUser(new User().setRealName("George Baker").setEmailAddress("tom@drillster.com").setLanguage("nl").setBiography("George likes singing and sigars")));
		Response response = api.sendPutRequest("/api/user", new Request().setUser(new User().setRealName("Tom van den Berge").setEmailAddress("tom.vandenberge@drillster.com").setLanguage("nl").setBiography("George likes singing and sigars")));
	}
	
	@Test
	public void modifyUser() throws ApiException {
		api.sendPostRequest("/api/user", new Request().setUser(new User().setId("tom.vandenberge@gmail.com").setEmailAddress("tom@drillster.com")));
	}
	
	@Test
	public void printRepertoire() throws ApiException {
		api.setContentType(ContentType.JSON);
		Response response = api.sendGetRequest("/api/repertoire");

		for (Drill drill : response.getRepertoire().getDrills()) {
			System.out.print(drill.getCode() + " ");
			System.out.print(drill.getName());
			if (drill.getCourses() != null && !drill.getCourses().isEmpty()) {
				System.out.print(" (courses: ");
				for (Course course : drill.getCourses()) {
					System.out.print(course.getCode()+" ");
				}
				System.out.print(")");
			}
			System.out.println();
		}
		System.out.println(response.getRepertoire().getDrills().size() + " drills in repertoire.");
	}
	
	@Test
	public void printCourses() throws ApiException {
		//api.setContentType(ContentType.XML);
		api.getCourses();
	}
	
	@Test
	public void printCourse() throws ApiException {
		//api.setContentType(ContentType.XML);
		api.sendGetRequest("/api/course/58kOAUzuRQChPS-JGZvZbQ");
	}

	@Test
	public void getChart() throws ApiException {
		api.sendPostRequest("/api/chart/7Qthmnr4Q9OJ7x1r94E06Q", new Request().setChart(new Chart()));
	}
	
	@Test
	public void getDrill() throws ApiException {
		//api.setContentType(ContentType.JSON);
		// 9L0gaDiBRK6tYc6GsSHvIQ George Baker
		Response response = api.sendGetRequest("/api/drill/gVya_ubkaM2Wn41KX2sp9w");
	}
	
	@Test
	public void getTest() throws ApiException {
		api.setContentType(ContentType.JSON);
		api.sendGetRequest("/api/test/RWm1lSF9Tz-toP5Qy6vqsA/9L0gaDiBRK6tYc6GsSHvIQ");
	}
	
	@Test
	public void getProficiency() throws ApiException {
		api.setContentType(ContentType.JSON);
		Response response = api.sendGetRequest("/api/proficiency/eGLMdj6rAir4Wo6JO-BnZA");
	}
	
	@Test
	public void getToken() throws ApiException {
		// ID and secret for Tom's Macbook db.
		TokenRequest tokenRequest = new TokenRequest().setClient_id("f298fbb5c91f44de95246550faef2c1a WEG").setClient_secret("cf43a6df90cd4cd79f5bc610a7deb6fa");
		tokenRequest.setGrant_type("password");
		tokenRequest.setScope("non-expiring");
		tokenRequest.setUsername("tom.vandenberge@gmail.com");
		tokenRequest.setPassword("*******");
		
		TokenResponse tokenResponse = api.sendTokenRequest("/api/token", tokenRequest);
		System.out.println(tokenResponse);
	}
	
	@Test
	public void practicePojo() throws ApiException, IOException {
		api.setContentType(ContentType.XML);
		Request request = new Request();
//		Question question = new Question().setMode("bidirectional").setType("multiplechoice");
		Question question = new Question().setMode("productive").setType("multiplechoice").setIntroduction("yes");
//		Question question = new Question().setMode("receptive").setType("multiplechoice");
		request.setQuestion(question);
		Set<Drill> drills = new HashSet<Drill>();
		Drill drill = new Drill();

		String bicycleParts = "gVya_ubkaM2Wn41KX2sp9w";	// LIBRARY-EXT-292
		String mountains = "jK4_BZWr89Y4MB7UGaDR0g";
		String sound = "rI9Ut3bA9jrWN87L5Kl_aW51pMWTFaM_oSxpq0qU_uI";
		String mixedContent = "1x7CYr2WKWPbZpJMcL7CZW51pMWTFaM_oSxpq0qU_uI";
		String wilhelmus = "D_wYBpQZTmq-JUl4_TxqvQ";
		String duits = "iLoWtJZONYViMGCnVLJfcg";
		String manyColumns = "bVMNOAc2SKud8oh-J4-SEA";
		String quotes = "R6Mw_qxyQQ-gKdVrNlZhfQ";
		String set = "8qeDRydFTUCR1RX-Wcbv3Q";
		String feedback = "0Ks3WK7vTQeX9Ut7ohuf3A";
		String empty = "HogsdaP9TKmP_v9WGNxT_w";
		
		drill.setCode(bicycleParts);
		drills.add(drill);
		question.setDrills(drills);

//		List<UnknownColumn> unknownColumns = new ArrayList<UnknownColumn>();
//		unknownColumns.add(new UnknownColumn().setName("Onbekende kant"));
//		unknownColumns.add(new UnknownColumn().setName("Fubar"));
//		question.setColumns(new Columns().setKnownColumn(new KnownColumn().setName("Bekende kant")).setUnknownColumns(unknownColumns));

		Response response = (Response) api.sendPostRequest("/api/question", request);

		Question returnedQuestion = response.getQuestion();

		System.out.println("Question: "
				+ (returnedQuestion.getLeft().getTerm() != null ? returnedQuestion.getLeft().getTerm() : returnedQuestion.getLeft().getAttachment()
						.getReference()));

		if (returnedQuestion.getRight().getIntro() != null) {
			System.out.println("The correct answer is: "
					+ (returnedQuestion.getRight().getIntro().getTerm() != null ? returnedQuestion.getRight().getIntro().getTerm() : returnedQuestion
							.getRight().getIntro().getAttachment().getReference()));
			System.out.println("Press enter to continue...");
			System.in.read();
			return;
		}

		System.out.println("Possible answers: ");
		List<String> possibleAnswers = new ArrayList<String>();
		if (returnedQuestion.getRight().getMultiplechoice().getTerm() != null && returnedQuestion.getRight().getMultiplechoice().getTerm().isEmpty()) {
			for (String possibleAnswer : returnedQuestion.getRight().getMultiplechoice().getTerm()) {
				possibleAnswers.add(possibleAnswer);
			}
		} else {
			for (Attachment attachment : returnedQuestion.getRight().getMultiplechoice().getAttachments()) {
				possibleAnswers.add(attachment.getReference());
			}
		}

		int i = 1;
		for (String possibleAnswer : possibleAnswers) {
			System.out.println((i++) + ":  " + possibleAnswer);
		}

		List<String> selectedTerms = new ArrayList<String>();
		// Manually enter a number:
		System.out.println("Enter the number(s) of the correct answer(s): ");
		int b = System.in.read();
		while (b != '\n') {
			selectedTerms.add(possibleAnswers.get(b - '0' - 1));
			b = System.in.read();
		}

		System.out.println("Answering " + selectedTerms);
		Request answer = new Request().setAnswer(selectedTerms.get(0));

		response = (Response) api.sendPostRequest("/api/answer/" + returnedQuestion.getReference(), answer);

		System.out.print("Your answer was " + response.getResult().getValue() + ". ");
		if ("incorrect".equals(response.getResult().getValue())) {
			System.out.print("It should have been " + response.getResult().getIntended().getAnswer());
		}
		System.out.println();

	}

	@Test
	public void practicePojoLoop() throws ApiException, IOException {
		for (;;) {
			practicePojo();
		}
	}
	

	@Test
	public void term() throws JsonParseException, JsonMappingException, IOException {
		String json = "{\"response\": {\"question\": {" + "\"left\": {" + "\"name\": \"Actor / Actress\"," + "\"term\": \"Clint Eastwood\"" + "},"
				+ "\"reference\": \"8662-58e7-12ca-5310-8883\"," + "\"right\": {" + "\"multiplechoice\": {\"term\": ["
				+ "\"\\\"Mama always said life was like a box of chocolates. You never know what you're gonna get.\\\"\","
				+ "\"\\\"A martini. Shaken, not stirred.\\\"\"," + "\"\\\"Go ahead, make my day\\\"\"," + "\"\\\"You can't handle the truth!\\\"\"" + "]},"
				+ "\"name\": \"Quotation\"" + "}," + "\"title\": \"Famous movie quotes\"" + "}}}";

//		String j2 = "{\"response\": {\"question\": {" + "\"reference\": \"abc\"," + "\"left\": {" + "\"name\": \"Actor / Actress\","
//				+ "\"term\": \"Clint Eastwood\"" + "}," + "\"name\": \"Quotation\"," + "\"title\": \"Famous movie quotes\"" + "}}}";
		ObjectMapper mapper = new ObjectMapper();
		ResponseContainer rc = mapper.readValue(json, ResponseContainer.class);
		Assert.assertEquals("8662-58e7-12ca-5310-8883", rc.getResponse().getQuestion().getReference());
		Assert.assertEquals("Famous movie quotes", rc.getResponse().getQuestion().getTitle());
		Assert.assertEquals("Actor / Actress", rc.getResponse().getQuestion().getLeft().getName());
		Assert.assertEquals("Clint Eastwood", rc.getResponse().getQuestion().getLeft().getTerm());
		Assert.assertEquals("Quotation", rc.getResponse().getQuestion().getRight().getName());
		Assert.assertEquals(4, rc.getResponse().getQuestion().getRight().getMultiplechoice().getTerm().size());
	}

	
	@Test
	public void jackson() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, true);
		
		
		Foo bar = new Foo();
		System.out.println(mapper.writeValueAsString(bar));

		
	}

	public static class Foo {
		private List<String> content = Arrays.asList("aap");

		public List<String> getContent() {
			return content;
		}

		public void setContent(List<String> content) {
			this.content = content;
		}
		
		public String toString() {
			return content.toString();
		}
	}
}
