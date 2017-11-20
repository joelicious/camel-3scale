package org.apache.camel.component.threescale.security;

import javax.cache.Cache;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import org.apache.camel.CamelAuthorizationException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.threescale.ThreeScaleConstants;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreeScaleAuthRepTest extends CamelTestSupport {

	private static final Logger LOG = LoggerFactory.getLogger(ThreeScaleAuthRepTest.class);

	private static final String API_KEY = "0bcf317e88e71915442d10c37986f040";
	private static final String SERVICE_ID = "2555417749097";
	private static final String SERVICE_TOKEN = "0f4fe5d82dc61c57f7f02b602bd9b42e670748097c3bb8ea4f644fa4c3be19a2";

	private CachingProvider cachingProvider;

	@Override
	protected void doPreSetup() throws Exception {
		cachingProvider = Caching.getCachingProvider("com.hazelcast.cache.HazelcastCachingProvider");
	}

	@EndpointInject(uri = "mock:success")
	protected MockEndpoint successEndpoint;

	@EndpointInject(uri = "mock:authorizationException")
	protected MockEndpoint failureEndpoint;

	@Test
	public void testThreeScaleAuthRepCacheTest() throws Exception {
		
		successEndpoint.expectedMessageCount(2);
		failureEndpoint.expectedMessageCount(0);

		template.send("direct:threeScaleCache", new TestProcessor());

		Cache<String, String> cache = cachingProvider.getCacheManager().getCache("threeScaleCache", String.class, String.class);
		cache.put(API_KEY, "TRUE");
		
		template.send("direct:threeScaleCache", new TestProcessor());

		successEndpoint.assertIsSatisfied();
		failureEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void testThreeScaleAuthRepNoCacheTest() throws Exception {
		
		successEndpoint.expectedMessageCount(2);
		failureEndpoint.expectedMessageCount(0);

		template.send("direct:threeScaleNoCache", new TestProcessor());

		Cache<String, String> cache = cachingProvider.getCacheManager().getCache("threeScaleCache", String.class, String.class);
		cache.put(API_KEY, "TRUE");
		
		template.send("direct:threeScaleNoCache", new TestProcessor());

		successEndpoint.assertIsSatisfied();
		failureEndpoint.assertIsSatisfied();
		
	}

	@Override
	protected RouteBuilder[] createRouteBuilders() throws Exception {

		return new RouteBuilder[] {

				new RouteBuilder() {

					public void configure() {

						final ThreeScaleAuthRepPolicy threeScaleAuthRepPolicy = new ThreeScaleAuthRepPolicy(
								"su1.3scale.net", 443, cachingProvider);

						onException(CamelAuthorizationException.class).to("mock:authorizationException");

						from("direct:threeScaleCache").policy(threeScaleAuthRepPolicy).log("log:incoming payload")
								.to("mock:success");

					}

				}, new RouteBuilder() {

					public void configure() {

						final ThreeScaleAuthRepPolicy threeScaleAuthRepPolicy = new ThreeScaleAuthRepPolicy(
								"su1.3scale.net", 443, null);

						onException(CamelAuthorizationException.class).to("mock:authorizationException");

						from("direct:threeScaleNoCache").policy(threeScaleAuthRepPolicy).log("log:incoming payload")
								.to("mock:success");

					}

				} };

	}

	private static class TestProcessor implements Processor {
		public void process(Exchange exchange) throws Exception {
			exchange.getIn().setHeader(ThreeScaleConstants.THREE_SCALE_SERVICE_ID, SERVICE_ID);
			exchange.getIn().setHeader(ThreeScaleConstants.THREE_SCALE_SERVICE_TOKEN, SERVICE_TOKEN);
			exchange.getIn().setHeader(ThreeScaleConstants.THREE_SCALE_API_KEY, API_KEY);
			exchange.getIn().setBody("This is the Payload");
		}
	}

}