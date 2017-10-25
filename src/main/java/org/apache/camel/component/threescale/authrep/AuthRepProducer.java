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
		System.out.println("Constructor");
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		System.out.println("Before Process");

		ParameterMap params = new ParameterMap();
		params.add("user_key", getConfiguration().getUserKey());

		ParameterMap usage = new ParameterMap();
		usage.add("hits", "1");
		params.add("usage", usage);

		AuthorizeResponse response = null;

		try {

			System.out.println("Service Token: " + getConfiguration().getServiceToken() + " Service Id: "
					+ getConfiguration().getServiceId());

			LOG.trace("Sending request");
			
			response = getEndpoint().getServiceApi().authrep(getConfiguration().getServiceToken(),
					getConfiguration().getServiceId(), params);
			
			LOG.trace("Got Response");

			System.out.println("AuthRep on App Id Success: " + response.success());

			if (response.success() == true) {

				System.out.println("Plan: " + response.getPlan());

			} else {
				System.out.println("Error: " + response.getErrorCode());
				System.out.println("Reason: " + response.getReason());
			}

		} catch (ServerError serverError) {
			serverError.printStackTrace();
		}

		System.out.println("Finished");

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
