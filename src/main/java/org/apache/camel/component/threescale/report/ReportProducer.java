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
package org.apache.camel.component.threescale.report;

import java.util.Set;

import org.apache.camel.CamelExchangeException;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.component.threescale.ThreeScaleConfiguration;
import org.apache.camel.component.threescale.ThreeScaleConstants;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.ExchangeHelper;
import org.apache.camel.util.ObjectHelper;
import org.apache.camel.util.URISupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import threescale.v3.api.ParameterMap;
import threescale.v3.api.ReportResponse;
import threescale.v3.api.ServerError;

public class ReportProducer extends DefaultProducer {

	private static final Logger LOG = LoggerFactory.getLogger(ReportProducer.class);

	private transient String reportProducerToString;

	public ReportProducer(Endpoint endpoint) {
		super(endpoint);
	}

	@Override
	public void process(Exchange exchange) throws Exception {

		boolean apiKey = true;
		boolean appIdToken = true;

		ThreeScaleConfiguration threeScaleConfiguration = new ThreeScaleConfiguration();

		parseServiceHeaders(exchange, threeScaleConfiguration);

		parseApiKeyHeader(exchange, threeScaleConfiguration);

		parseAppHeaders(exchange, threeScaleConfiguration);

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

		// TODO
		// Hits should be configurable
		ParameterMap usage = new ParameterMap();
		usage.add("hits", "1");
		threeScaleConfiguration.getParameterMap().add("usage", usage);

		if (invoke3scale(threeScaleConfiguration)) {

			// TODO
			// If invoking 3scale was successful; then update cache
			// The option of updating cache should be configurable?
			
		}

	}

	protected ThreeScaleConfiguration getConfiguration() {
		return getEndpoint().getConfiguration();
	}

	@Override
	public String toString() {
		if (reportProducerToString == null) {
			reportProducerToString = "ReportProducer[" + URISupport.sanitizeUri(getEndpoint().getEndpointUri()) + "]";
		}
		return reportProducerToString;
	}

	@Override
	public ReportEndpoint getEndpoint() {
		return (ReportEndpoint) super.getEndpoint();
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

	private boolean invoke3scale(ThreeScaleConfiguration threeScaleConfiguration) {
		
		ReportResponse response = null;

		try {

			response = getEndpoint().getServiceApi().report(
					threeScaleConfiguration.getParameterMap().getStringValue("service_token"),
					threeScaleConfiguration.getParameterMap().getStringValue("service_id"),
					threeScaleConfiguration.getParameterMap());

			if (response.success() == true) {
				LOG.info("Plan: " + response.toString());
				return true;
			} else {
				LOG.error("Error Code: " + response.getErrorCode());
				LOG.error("Error Message: " + response.getErrorMessage());
			}

		} catch (ServerError serverError) {
			serverError.printStackTrace();
		}

		return false;
	}
}