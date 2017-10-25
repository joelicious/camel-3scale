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

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;

import threescale.v3.api.AuthorizeResponse;
import threescale.v3.api.ParameterMap;
import threescale.v3.api.ServerError;
import threescale.v3.api.ServiceApi;
import threescale.v3.api.impl.ServiceApiDriver;

/**
 * The 3scale-report component is used for sending emails with Amazon's SES
 * service.
 */
@UriEndpoint(scheme = "threescale-report", title = "threescale Report Service", syntax = "threescale-report:3scaleName", producerOnly = true, label = "threescale,report")
public class ReportEndpoint extends DefaultEndpoint {

	private ServiceApi threeScaleClient;

	@UriPath(description = "3scale Name")
	@Metadata(required = "true")
	private String threeScaleName;

	@UriParam(description = "3scale Configuration")
	protected ReportConfiguration configuration;

	public ReportEndpoint(String uri, Component component, ReportConfiguration configuration) {
		super(uri, component);
		this.configuration = configuration;
	}

	@Override
	public void doStart() throws Exception {
		super.doStart();
		threeScaleClient = ServiceApiDriver.createApi(configuration.getServerHost(), configuration.getServerPort(),
				true);

		ParameterMap params = new ParameterMap();
		params.add("app_key", configuration.getAPIKey());

		ParameterMap usage = new ParameterMap();
		usage.add("hits", "1");
		params.add("usage", usage);

		AuthorizeResponse response = null;

		try {

			response = threeScaleClient.authrep(null, null, params);
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

	}

	public Consumer createConsumer(Processor processor) throws Exception {
		throw new UnsupportedOperationException("You cannot receive messages from this endpoint");
	}

	public Producer createProducer() throws Exception {
		return new ReportProducer(this);
	}

	public boolean isSingleton() {
		return true;
	}

	public ReportConfiguration getConfiguration() {
		return configuration;
	}

}