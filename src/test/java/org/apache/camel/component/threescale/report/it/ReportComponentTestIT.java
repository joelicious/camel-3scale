/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.threescale.report.it;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.threescale.ThreeScaleConstants;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReportComponentTestIT extends CamelTestSupport {

	private static final String API_KEY = "0bcf317e88e71915442d10c37986f040";
	private static final String SERVICE_ID = "2555417749097";
	private static final String SERVICE_TOKEN = "0f4fe5d82dc61c57f7f02b602bd9b42e670748097c3bb8ea4f644fa4c3be19a2";

	@Produce(uri = "direct:start")
	ProducerTemplate producerTemplate;
	
	private static java.util.Scanner scanner;

	@Test
	public void testThreeScaleAuthRepCacheTest() throws Exception {

		URL url = new URL(buildUri());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/json");
		InputStream json = connection.getInputStream();

		String str = convertStreamToString(json);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = mapper.readTree(str);
		int beforeHits = actualObj.get("total").asInt();
		connection.disconnect();

		Map<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put(ThreeScaleConstants.THREE_SCALE_SERVICE_ID, SERVICE_ID);
		headerMap.put(ThreeScaleConstants.THREE_SCALE_SERVICE_TOKEN, SERVICE_TOKEN);
		headerMap.put(ThreeScaleConstants.THREE_SCALE_API_KEY, API_KEY);

		producerTemplate.sendBodyAndHeaders(null, headerMap);

		url = new URL(buildUri());
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/json");
		json = connection.getInputStream();

		str = convertStreamToString(json);
		mapper = new ObjectMapper();
		actualObj = mapper.readTree(str);
		int afterHits = actualObj.get("total").asInt();
		connection.disconnect();
		
		assertEquals(afterHits, (beforeHits + 1));

	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("direct:start").to("threescale-report:saasAdmin?serverHost=su1.3scale.net&serverPort=443");
			}
		};
	}

	private String buildUri() {

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd+HH%3Amm%3Ass");
		String formatDateTime = now.format(formatter);

		String uri = "https://redhat-bcn-training-admin.3scale.net/stats/services/" + SERVICE_ID
				+ "/usage.json?access_token=" + "3bcda06e7ea632a484ad6bf633cbdd75314f09bb108cb5701ac84826966ddf2e"
				+ "&metric_name=hits&since=" + formatDateTime
				+ "&period=day&granularity=day&skip_change=true";

		return uri;
	}

	static String convertStreamToString(java.io.InputStream is) {
		scanner = new java.util.Scanner(is);
		java.util.Scanner s = scanner.useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

}
