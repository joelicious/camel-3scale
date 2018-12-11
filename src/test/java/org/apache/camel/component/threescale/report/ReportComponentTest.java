package org.apache.camel.component.threescale.report;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.threescale.ServiceApiDriverMock;
import org.apache.camel.component.threescale.ThreeScaleConstants;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportComponentTest extends CamelTestSupport {

	private static final Logger LOG = LoggerFactory.getLogger(ReportComponentTest.class);

	private ServiceApiDriverMock serviceApi;

	private static final String SERVICE_ID = "01234";
	private static final String SERVICE_TOKEN = "56789";

	private static final String API_KEY = "0123456789";
	private static final String APP_ID = "0123456";
	private static final String APP_TOKEN = "789";

	@Produce(uri = "direct:startSuccess")
	ProducerTemplate producerTemplateSuccess;
	
	@Produce(uri = "direct:startMissingParameters")
	ProducerTemplate producerTemplateMissing;

	@Override
	@Before
	public void setUp() throws Exception {
		serviceApi = new ServiceApiDriverMock();
		super.setUp();
	}

	@Test
	public void sendWithAppKeyTest() throws Exception {
		LOG.info("ReportComponentTest::sendWithAppKeyTest");

		Map<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put(ThreeScaleConstants.THREE_SCALE_SERVICE_ID, SERVICE_ID);
		headerMap.put(ThreeScaleConstants.THREE_SCALE_SERVICE_TOKEN, SERVICE_TOKEN);
		headerMap.put(ThreeScaleConstants.THREE_SCALE_API_KEY, API_KEY);
		
		ServiceApiDriverMock.return200 = true;

		producerTemplateSuccess.sendBodyAndHeaders("Body", headerMap);

		ServiceApiDriverMock.return200 = false;

		producerTemplateSuccess.sendBodyAndHeaders("Body", headerMap);

	}

	@Test
	public void sendWithAppIdAndTokenTest() throws Exception {
		LOG.info("ReportComponentTest::sendWithAppIdAndTokenTest");

		Map<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put(ThreeScaleConstants.THREE_SCALE_SERVICE_ID, SERVICE_ID);
		headerMap.put(ThreeScaleConstants.THREE_SCALE_SERVICE_TOKEN, SERVICE_TOKEN);
		headerMap.put(ThreeScaleConstants.THREE_SCALE_APP_ID, APP_ID);
		headerMap.put(ThreeScaleConstants.THREE_SCALE_APP_TOKEN, APP_TOKEN);
		
		ServiceApiDriverMock.return200 = true;

		producerTemplateSuccess.requestBodyAndHeaders("Body", headerMap);

		ServiceApiDriverMock.return200 = false;
		
		producerTemplateSuccess.requestBodyAndHeaders("Body", headerMap);

	}
	
	@Test
	public void sendWithAppKeyMissingParametersTest() throws Exception {
		LOG.info("ReportComponentTest::sendWithAppKeyMissingParametersTest");

		Map<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put(ThreeScaleConstants.THREE_SCALE_SERVICE_ID, SERVICE_ID);
		headerMap.put(ThreeScaleConstants.THREE_SCALE_SERVICE_TOKEN, SERVICE_TOKEN);
		
		ServiceApiDriverMock.return200 = true;

		producerTemplateMissing.sendBodyAndHeaders("Body", headerMap);

		ServiceApiDriverMock.return200 = false;

		producerTemplateMissing.sendBodyAndHeaders("Body", headerMap);

	}

	@Override
	protected JndiRegistry createRegistry() throws Exception {
		LOG.info("Adding mock to registry");
		JndiRegistry registry = super.createRegistry();
		registry.bind("serviceApi", serviceApi);
		return registry;
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("direct:startSuccess").to(
						"threescale-report:saasAdmin?serverHost=custom.ip.net&serverPort=443&serviceApi=#serviceApi");
				from("direct:startMissingParameters").to(
						"threescale-report:saasAdmin?serverHost=custom.ip.net&serviceApi=#serviceApi");
			}
		};
	}

}