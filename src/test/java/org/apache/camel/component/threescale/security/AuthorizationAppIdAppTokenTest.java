package org.apache.camel.component.threescale.security;

import javax.cache.Cache;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import org.apache.camel.CamelAuthorizationException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.threescale.ServiceApiDriverMock;
import org.apache.camel.component.threescale.ThreeScaleConstants;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationAppIdAppTokenTest extends CamelTestSupport {

	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationAppIdAppTokenTest.class);
	
	private static final String APP_ID = "0123456";
	private static final String APP_TOKEN = "789";
	private static final String SERVICE_ID = "01234";
	private static final String SERVICE_TOKEN = "56789";

	private ServiceApiDriverMock serviceApi;
	
	private CachingProvider cachingProvider;

	@Produce(uri = "direct:startSuccess")
	ProducerTemplate producerTemplateSuccess;

	@EndpointInject(uri = "mock:success")
	protected MockEndpoint successEndpoint;

	@EndpointInject(uri = "mock:authorizationException")
	protected MockEndpoint failureEndpoint;
	
	@Override
	@Before
	public void setUp() throws Exception {
		serviceApi = new ServiceApiDriverMock();
		super.setUp();
	}

	@Override
	protected void doPreSetup() throws Exception {
		cachingProvider = Caching.getCachingProvider("com.hazelcast.cache.HazelcastCachingProvider");
	}

	@Test
	public void testThreeScaleValidAuthorizationTest() throws Exception {
		
		// Ensure that Mock Service returns a success (authorized) response.
		ServiceApiDriverMock.return200 = true;
		
		successEndpoint.expectedMessageCount(2);
		failureEndpoint.expectedMessageCount(0);

		LOG.info("3ScaleAuthorizationCacheTest invoking route");
		template.send("direct:threeScaleCache", new ApiKeyProcessor());

		Cache<String, String> cache = cachingProvider.getCacheManager().getCache("threeScaleCache", String.class,
				String.class);
		cache.put(APP_ID, APP_TOKEN);

		LOG.info("3ScaleAuthorizationCacheTest invoking route second time");

		template.send("direct:threeScaleCache", new ApiKeyProcessor());
		successEndpoint.assertIsSatisfied();
		failureEndpoint.assertIsSatisfied();
	}

	@Override
	protected JndiRegistry createRegistry() throws Exception {
		LOG.info("Adding mock to registry");
		JndiRegistry registry = super.createRegistry();
		registry.bind("serviceApi", serviceApi);
		return registry;
	}

	@Override
	protected RouteBuilder[] createRouteBuilders() throws Exception {

		return new RouteBuilder[] {

				new RouteBuilder() {

					public void configure() {

						final ThreeScaleAuthPolicy threeScalePolicy = new ThreeScaleAuthPolicy("custom.ip.net", 443,
								cachingProvider, serviceApi);

						onException(CamelAuthorizationException.class).to("mock:authorizationException");

						from("direct:threeScaleCache").policy(threeScalePolicy).log("log:incoming payload")
								.to("mock:success");

					}

				}, new RouteBuilder() {

					public void configure() {

						final ThreeScaleAuthPolicy threeScalePolicy = new ThreeScaleAuthPolicy("custom.ip.net", 443,
								null, serviceApi);

						onException(CamelAuthorizationException.class).to("mock:authorizationException");

						from("direct:threeScaleNoCache").policy(threeScalePolicy).log("log:incoming payload")
								.to("mock:success");

					}

				} };

	}

	private static class ApiKeyProcessor implements Processor {
		public void process(Exchange exchange) throws Exception {
			exchange.getIn().setHeader(ThreeScaleConstants.THREE_SCALE_SERVICE_ID, SERVICE_ID);
			exchange.getIn().setHeader(ThreeScaleConstants.THREE_SCALE_SERVICE_TOKEN, SERVICE_TOKEN);
			exchange.getIn().setHeader(ThreeScaleConstants.THREE_SCALE_APP_ID, APP_ID);
			exchange.getIn().setHeader(ThreeScaleConstants.THREE_SCALE_APP_TOKEN, APP_TOKEN);
			exchange.getIn().setBody("This is the Payload");
		}
	}
	
}
