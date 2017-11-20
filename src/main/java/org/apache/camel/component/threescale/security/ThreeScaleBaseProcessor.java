package org.apache.camel.component.threescale.security;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.threescale.ThreeScaleConfiguration;
import org.apache.camel.component.threescale.ThreeScaleConstants;
import org.apache.camel.processor.DelegateAsyncProcessor;

public class ThreeScaleBaseProcessor extends DelegateAsyncProcessor {

	public ThreeScaleBaseProcessor(Processor processor) {
		super(processor);
	}
	
	protected void parseServiceHeaders(Exchange exchange, ThreeScaleConfiguration threeScaleConfiguration) {

		final String serviceId = exchange.getIn().getHeader(ThreeScaleConstants.THREE_SCALE_SERVICE_ID, String.class);
		final String serviceToken = exchange.getIn().getHeader(ThreeScaleConstants.THREE_SCALE_SERVICE_TOKEN,
				String.class);

		if ((serviceId != null) && (serviceToken != null)) {

			threeScaleConfiguration.setServiceId(serviceId);
			threeScaleConfiguration.setServiceToken(serviceToken);
			threeScaleConfiguration.getParameterMap().add("service_id", serviceId);
			threeScaleConfiguration.getParameterMap().add("service_token", serviceToken);
			exchange.getIn().removeHeader(ThreeScaleConstants.THREE_SCALE_SERVICE_ID);
			exchange.getIn().removeHeader(ThreeScaleConstants.THREE_SCALE_SERVICE_TOKEN);

		}

	}

	protected void parseApiKeyHeader(Exchange exchange, ThreeScaleConfiguration threeScaleConfiguration) {

		final String apiKey = exchange.getIn().getHeader(ThreeScaleConstants.THREE_SCALE_API_KEY, String.class);

		if (apiKey != null) {

			threeScaleConfiguration.setApiKey(apiKey);
			threeScaleConfiguration.getParameterMap().add("user_key", apiKey);
			exchange.getIn().removeHeader(ThreeScaleConstants.THREE_SCALE_API_KEY);

		}

	}

	protected void parseAppHeaders(Exchange exchange, ThreeScaleConfiguration threeScaleConfiguration) {

		final String appId = exchange.getIn().getHeader(ThreeScaleConstants.THREE_SCALE_APP_ID, String.class);
		final String appKey = exchange.getIn().getHeader(ThreeScaleConstants.THREE_SCALE_APP_TOKEN, String.class);

		if ((appId != null) && (appKey != null)) {

			threeScaleConfiguration.setAppId(appId);
			threeScaleConfiguration.setAppKey(appKey);
			exchange.getIn().removeHeader(ThreeScaleConstants.THREE_SCALE_APP_ID);
			exchange.getIn().removeHeader(ThreeScaleConstants.THREE_SCALE_APP_TOKEN);
			threeScaleConfiguration.getParameterMap().add("app_id", appId);
			threeScaleConfiguration.getParameterMap().add("app_key", appKey);

		}

	}
}
