package com.drillster.api2;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.conn.ssl.SSLConnectionSocketFactory;
import ch.boye.httpclientandroidlib.conn.ssl.SSLContextBuilder;
import ch.boye.httpclientandroidlib.conn.ssl.TrustSelfSignedStrategy;
import ch.boye.httpclientandroidlib.impl.client.CloseableHttpClient;
import ch.boye.httpclientandroidlib.impl.client.HttpClients;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.drillster.api2.drill.Drill;
import com.drillster.api2.drill.Drillable;
import com.drillster.api2.message.json.jackson.JacksonMarshaller;
import com.drillster.api2.practice.AnswerResponse;
import com.drillster.api2.practice.QuestionResponse;
import com.drillster.api2.practice.Tell;
import com.drillster.api2.practice.Term;
import com.drillster.api2.practice.TermEvaluation;

public class ApiTest {

	private static String oAuthToken = "17331de2d1da4de0bef1018888bd7bee";	// insert your OAuth token here
	private static String hostName = "www.drillster.com";
	private static Integer port = 443;
	private static String scheme = "https";

	private static Api api = new Api();

	@BeforeClass
	public static void setUp() {
		api.setHostName(hostName);
		api.setPort(port);
		api.setScheme(scheme);
		api.setOAuthToken(oAuthToken);
	}

	/**
	 * Tests the polymorphic deserialization of Drillable sub types. 
	 */
	@Test
	public void deserializeRepertoire() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new JacksonMarshaller().getObjectMapper();
		InputStream input = this.getClass().getResourceAsStream("/drillable.json");
		Drillable drill = mapper.readValue(input, Drillable.class);
		assertEquals("Winnaars Tour de France", drill.getName());
	}

	@Test
	public void justATest() throws ApiException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
		SSLContextBuilder builder = new SSLContextBuilder();
		builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				builder.build(), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(
				sslsf).build();

		api.setHttpClient(httpClient);
		api.sendGetRequest("/api/2/drill/rj7XZRD7TBSBcUz1FeTiEw", Drill.class);
	}

	@Test
	public void practicePojo() throws ApiException, IOException {

		String bicycleParts = "gVya_ubkaM2Wn41KX2sp9w";
		String mountains = "jK4_BZWr89Y4MB7UGaDR0g";
		String sound = "rI9Ut3bA9jrWN87L5Kl_aW51pMWTFaM_oSxpq0qU_uI";
		String mixedContent = "1x7CYr2WKWPbZpJMcL7CZW51pMWTFaM_oSxpq0qU_uI";
		String wilhelmus = "D_wYBpQZTmq-JUl4_TxqvQ";
		String duits = "iLoWtJZONYViMGCnVLJfcg";
		String manyColumns = "bVMNOAc2SKud8oh-J4-SEA";
		String quotes = "R6Mw_qxyQQ-gKdVrNlZhfQ";
		String set = "8qeDRydFTUCR1RX-Wcbv3Q";
		String sequence = "Izez87NkTdKQtZ0e0uFebw";

		QuestionResponse response = api.sendGetRequest("/2/question/" + duits, QuestionResponse.class);


		com.drillster.api2.practice.Question returnedQuestion = response.getQuestion();

		System.out.println("Question: " + returnedQuestion.getAsk().getTerm().getValue());

		if (returnedQuestion.getTell().getType() == Tell.Type.INTRODUCTION) {
			List<String> correctValues = new ArrayList<String>();
			for (Term term : returnedQuestion.getTell().getTerms()) {
				correctValues.add(term.getValue());
			}
			System.out.println("The correct answers is: " + correctValues);
			System.out.println("Press enter to continue...");
			System.in.read();
			return;
		}

		System.out.println("Possible answers: ");
		List<String> possibleAnswers = new ArrayList<String>();
		int i = 1;
		for (Term term : returnedQuestion.getTell().getTerms()) {
			possibleAnswers.add(term.getValue());
			System.out.println((i++) + ":  " + term.getValue());
		}

		List<NameValuePair> selectedTerms = new ArrayList<NameValuePair>();
		// Manually enter a number:
		System.out.println("Enter the number(s) of the correct answer(s): ");
		int b = System.in.read();
		while (b != '\n') {
			selectedTerms.add(new BasicNameValuePair("answer", possibleAnswers.get(b - '0' - 1)));
			b = System.in.read();
		}

		System.out.println("Answering " + selectedTerms);

		AnswerResponse answerResponse = api.sendPostRequest("/2/answer/" + returnedQuestion.getReference(), selectedTerms, AnswerResponse.class);

		System.out.print("Your answer was " + answerResponse.getEvaluation().getResult() + ". ");
		if ("INCORRECT".equals(answerResponse.getEvaluation().getResult())) {
			List<String> correct = new ArrayList<String>();
			for (TermEvaluation eval : answerResponse.getEvaluation().getTermEvaluations()) {
				if ("CORRECT".equals(eval.getResult()) || "MISSED".equals(eval.getResult())) {
					correct.add(eval.getValue());
				}
			}
			System.out.print("It should have been " + correct);
		}
		System.out.println();

	}

	@Test
	public void practicePojoLoop() throws ApiException, IOException {
		for (;;) {
			practicePojo();
		}
	}
}
