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
package org.apache.camel.component.threescale.authrep;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.URISupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import threescale.v3.api.AuthorizeResponse;
import threescale.v3.api.ParameterMap;
import threescale.v3.api.ServerError;

public class AuthRepProducer extends DefaultProducer {

	private static final Logger LOG = LoggerFactory.getLogger(AuthRepProducer.class);

	private transient String authRepProducerToString;

	public AuthRepProducer(Endpoint endpoint) {
		super(endpoint);
	}

	@Override
	public void process(Exchange exchange) throws Exception {

		ParameterMap params;
		String serviceToken = null;
		String serviceId = null;

		// If no parameter map is defined, then use URI parameters
		if (null == getConfiguration().getParameterMap()) {
			params = new ParameterMap();
			params.add("user_key", getConfiguration().getUserKey());
			serviceToken = getConfiguration().getServiceToken();
			serviceId = getConfiguration().getServiceId();
		} else {
			params = getConfiguration().getParameterMap();
			serviceToken = getConfiguration().getParameterMap().getStringValue("service_token");
			serviceId = getConfiguration().getParameterMap().getStringValue("service_id");
		}

		ParameterMap usage = new ParameterMap();
		usage.add("hits", "1");
		params.add("usage", usage);

		AuthorizeResponse response = null;

		try {

			response = getEndpoint().getServiceApi().authrep(serviceToken, serviceId, params);

			if (response.success() == true) {

				exchange.getIn().setHeader("THREESCALE_AUTH", "VALID");
				LOG.info("Plan: " + response.getPlan());

			} else {
				
				exchange.getIn().setHeader("THREESCALE_AUTH", "INVALID");
				
				LOG.error("Error: " + response.getErrorCode());
				LOG.error("Reason: " + response.getReason());
			}

		} catch (ServerError serverError) {
			serverError.printStackTrace();
		}

	}

	protected AuthRepConfiguration getConfiguration() {
		return getEndpoint().getConfiguration();
	}

	@Override
	public String toString() {
		if (authRepProducerToString == null) {
			authRepProducerToString = "AuthRepProducer[" + URISupport.sanitizeUri(getEndpoint().getEndpointUri()) + "]";
		}
		return authRepProducerToString;
	}

	@Override
	public AuthRepEndpoint getEndpoint() {
		return (AuthRepEndpoint) super.getEndpoint();
	}

}
