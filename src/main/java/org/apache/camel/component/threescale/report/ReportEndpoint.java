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
import org.apache.camel.component.threescale.ThreeScaleConfiguration;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import threescale.v3.api.ServiceApi;
import threescale.v3.api.impl.ServiceApiDriver;

@UriEndpoint(firstVersion = "2.22.0", scheme = "threescale-report", title = "3scale Report Service", syntax = "threescale-report:threescaleName", producerOnly = true, label = "report")
public class ReportEndpoint extends DefaultEndpoint {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReportEndpoint.class);

	private ServiceApi serviceApi;

	@UriPath(description = "3scale Name")
	@Metadata(required = "true")
	private String threeScaleName;

	@UriParam(description = "3scale Configuration")
	protected ThreeScaleConfiguration configuration;

	public ReportEndpoint(String uri, Component component, ThreeScaleConfiguration configuration) {
		super(uri, component);
		this.configuration = configuration;
	}

	@Override
	public void doStart() throws Exception {
		LOG.info("ReportEndpoint::doStart Creating Report Client");
		super.doStart();
		
		if (null == this.configuration.getServiceApi()) {
			LOG.info("ReportEndpoint::doStart serviceApi is null");
			serviceApi = ServiceApiDriver.createApi(configuration.getServerHost(), configuration.getServerPort(), true);
		} else {
			LOG.info("ReportEndpoint::doStart serviceApi is not null, grab from parameters");
			serviceApi = this.configuration.getServiceApi();
		}
	}

	@Override
	protected void doStop() throws Exception {
		serviceApi = null;
		super.doStop();
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

	public ThreeScaleConfiguration getConfiguration() {
		return configuration;
	}

	public ServiceApi getServiceApi() {
		return serviceApi;
	}

}