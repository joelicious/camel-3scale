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

import threescale.v3.api.ServiceApi;
import threescale.v3.api.impl.ServiceApiDriver;

@UriEndpoint(scheme = "threescale-report", title = "3scale Report Service", syntax = "threescale-report:threescaleName", producerOnly = true, label = "report")
public class ReportEndpoint extends DefaultEndpoint {

	private ServiceApi serviceApi;

	@UriPath(description = "3scale Name")
	@Metadata(required = "true")
	private String threeScaleName;

	@UriParam(description = "3scale Configuration")
	protected ThreeScaleConfiguration conf;

	public ReportEndpoint(String uri, Component component, ThreeScaleConfiguration configuration) {
		super(uri, component);
		this.conf = configuration;
	}

	@Override
	public void doStart() throws Exception {
		System.out.println("Creating Report Client");
		super.doStart();
		serviceApi = ServiceApiDriver.createApi(conf.getServerHost(), conf.getServerPort(), true);
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
		return conf;
	}

	public ServiceApi getServiceApi() {
		return serviceApi;
	}

}