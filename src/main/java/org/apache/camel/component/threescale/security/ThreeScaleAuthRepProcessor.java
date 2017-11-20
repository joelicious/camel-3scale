package org.apache.camel.component.threescale.security;

import java.util.Set;

import javax.cache.Cache;

import org.apache.camel.AsyncCallback;
import org.apache.camel.CamelExchangeException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.threescale.ThreeScaleConfiguration;
import org.apache.camel.component.threescale.ThreeScaleConstants;
import org.apache.camel.util.ExchangeHelper;
import org.apache.camel.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import threescale.v3.api.AuthorizeResponse;
import threescale.v3.api.ParameterMap;
import threescale.v3.api.ServerError;

public class ThreeScaleAuthRepProcessor extends ThreeScaleBaseProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(ThreeScaleAuthRepProcessor.class);
	private final ThreeScaleAuthRepPolicy policy;

	public ThreeScaleAuthRepProcessor(Processor processor, ThreeScaleAuthRepPolicy policy) {
		super(processor);
		this.policy = policy;
	}

	@Override
	public boolean process(Exchange exchange, AsyncCallback callback) {

		try {

			applySecurityPolicy(exchange);

		} catch (Exception e) {

			exchange.setException(e);
			callback.done(true);
			return true;
		}

		return super.process(exchange, callback);

	}

	private void applySecurityPolicy(Exchange exchange) throws Exception {

		boolean apiKey = true;
		boolean appIdToken = true;
		
		ThreeScaleConfiguration threeScaleConfiguration = new ThreeScaleConfiguration();

		parseServiceHeaders(exchange, threeScaleConfiguration);

		parseApiKeyHeader(exchange, threeScaleConfiguration);

		parseAppHeaders(exchange, threeScaleConfiguration);

		// TODO
		// The hits need to be configurable
		ParameterMap usage = new ParameterMap();
		usage.add("hits", "1");
		threeScaleConfiguration.getParameterMap().add("usage", usage);

		exchange.getIn().setHeader(ThreeScaleConstants.THREE_SCALE_CONFIGURATION, threeScaleConfiguration);

		// The 3scale Configuration can be set by Camel Route or
		// via the logic above.
		Object object = ExchangeHelper.getMandatoryHeader(exchange, ThreeScaleConstants.THREE_SCALE_CONFIGURATION,
				Object.class);

		if (object != null) {
			if (object instanceof ThreeScaleConfiguration) {

				exchange.getIn().removeHeader(ThreeScaleConstants.THREE_SCALE_CONFIGURATION);
				ThreeScaleConfiguration exchangeConfiguration = (ThreeScaleConfiguration) object;

				final Set<String> keySet = exchangeConfiguration.getParameterMap().getKeys();

				// Ensure the Service Id is set on the class or in the Map
				if ((null == exchangeConfiguration.getServiceId()) && (!keySet.contains("service_id"))) {
					throw new CamelExchangeException("3scale Configuration requires a Service Id", exchange);
				}

				// If parameter map "service_id" is null; copy from getServiceId
				if (null == exchangeConfiguration.getParameterMap().getStringValue("service_id")) {
					exchangeConfiguration.getParameterMap().add("service_id", exchangeConfiguration.getServiceId());
				}

				// Ensure the Service Token is set on the class or in the Map
				if ((null == exchangeConfiguration.getServiceToken()) && (!keySet.contains("service_token"))) {
					throw new CamelExchangeException("3scale Configuration requires a Service Token", exchange);
				}

				// If parameter map "service_token" is null; copy from
				// getServiceToken
				if (null == exchangeConfiguration.getParameterMap().getStringValue("service_token")) {
					exchangeConfiguration.getParameterMap().add("service_token",
							exchangeConfiguration.getServiceToken());
				}

				// Ensure API Key -OR- App Id + App Key Pair is supplied
				if ((null == exchangeConfiguration.getApiKey()) && (keySet.contains("user_key"))) {
					apiKey = false;
				}

				if ((null == exchangeConfiguration.getAppId()) && (keySet.contains(("app_id")))
						&& (null == exchangeConfiguration.getAppKey()) && (keySet.contains(("app_key")))) {
					appIdToken = false;
				}

				if (!apiKey && !appIdToken) {
					throw new CamelExchangeException("3scale Configuration requires Authorization Credentials",
							exchange);
				}

				// Set the configuration on the exchange to class scope
				// exchange.
				threeScaleConfiguration = exchangeConfiguration;

			} else {
				throw new CamelExchangeException("3scale header " + ThreeScaleConstants.THREE_SCALE_CONFIGURATION
						+ " is unsupported type: " + ObjectHelper.classCanonicalName(object), exchange);
			}
		}

		boolean invoke3scale = true;
		
		// If a CacheManager is available; check the cache.
		if (this.policy.getCacheManager() != null) {

			final Cache<String, String> cache = this.policy.getCacheManager().getCache("threeScaleCache", String.class,
					String.class);

			final Set<String> keySet = threeScaleConfiguration.getParameterMap().getKeys();

			if (keySet.contains("user_key")) {
				if (cache.containsKey(threeScaleConfiguration.getParameterMap().getStringValue("user_key"))) {
					LOG.info("API Key Found in Cache");
					exchange.getIn().setHeader(ThreeScaleConstants.THREE_SCALE_CACHE_USED, Boolean.TRUE);
					exchange.getIn().setHeader(ThreeScaleConstants.THREE_SCALE_SERVICE_ID,
							threeScaleConfiguration.getParameterMap().getStringValue("service_id"));
					exchange.getIn().setHeader(ThreeScaleConstants.THREE_SCALE_SERVICE_TOKEN,
							threeScaleConfiguration.getParameterMap().getStringValue("service_token"));
					exchange.getIn().setHeader(ThreeScaleConstants.THREE_SCALE_API_KEY,
							threeScaleConfiguration.getParameterMap().getStringValue("user_key"));
					invoke3scale = false;
				}
			} else if ((keySet.contains("app_id")) && (keySet.contains("app_key"))) {
				if (cache.containsKey(threeScaleConfiguration.getParameterMap().getStringValue("app_id"))) {
					String appToken = cache.get(threeScaleConfiguration.getParameterMap().getStringValue("app_id"));
					if (appToken
							.equalsIgnoreCase(threeScaleConfiguration.getParameterMap().getStringValue("app_key"))) {
						LOG.info("Service ID + Token Found in Cache");
						exchange.getIn().setHeader(ThreeScaleConstants.THREE_SCALE_CACHE_USED, Boolean.TRUE);
						exchange.getIn().setHeader(ThreeScaleConstants.THREE_SCALE_SERVICE_ID,
								threeScaleConfiguration.getParameterMap().getStringValue("service_id"));
						exchange.getIn().setHeader(ThreeScaleConstants.THREE_SCALE_SERVICE_TOKEN,
								threeScaleConfiguration.getParameterMap().getStringValue("service_token"));
						exchange.getIn().setHeader(ThreeScaleConstants.THREE_SCALE_APP_ID,
								threeScaleConfiguration.getParameterMap().getStringValue("app_id"));
						exchange.getIn().setHeader(ThreeScaleConstants.THREE_SCALE_APP_TOKEN,
								threeScaleConfiguration.getParameterMap().getStringValue("app_key"));
						invoke3scale = false;
					}
				}
			}

		}
		
		if (invoke3scale) {
			
			if (invoke3scale(threeScaleConfiguration)) {

				// TODO
				// If invoking 3scale was successful; then update cache
				// The option of updating cache should be configurable?
				if (this.policy.getCacheManager() != null) {

					final Cache<String, String> cache = this.policy.getCacheManager().getCache("threeScaleCache",
							String.class, String.class);

					if (apiKey) {
						cache.put(threeScaleConfiguration.getParameterMap().getStringValue("user_key"), "TRUE");
					} else if (appIdToken) {
						cache.put(threeScaleConfiguration.getParameterMap().getStringValue("app_id"),
								threeScaleConfiguration.getParameterMap().getStringValue("app_key"));
					}
				}
			}
		}
	}

	private boolean invoke3scale(ThreeScaleConfiguration threeScaleConfiguration) {
		LOG.info("Not Found in Cache; invoking 3scale Admin Portal");

		AuthorizeResponse response = null;

		try {

			response = this.policy.getServiceApi().authrep(threeScaleConfiguration.getParameterMap());

			if (response.success() == true) {
				LOG.info("Plan: " + response.getPlan());
				return true;
			} else {
				LOG.error("Error: " + response.getErrorCode());
				LOG.error("Reason: " + response.getReason());
			}

		} catch (ServerError serverError) {
			serverError.printStackTrace();
		}
		
		return false;
	}

}
