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

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;

import threescale.v3.api.ServiceApi;
import threescale.v3.api.impl.ServiceApiDriver;

@UriEndpoint(scheme = "threescale-authrep", title = "3scale Authorize and Report Service", syntax = "threescale-authrep:threescaleName", producerOnly = true, label = "authrep")
public class AuthRepEndpoint extends DefaultEndpoint {

	private ServiceApi threeScaleClient;

	@UriPath(description = "3scale Name") @Metadata(required = "true") 
	private String threescaleName;
	
	@UriParam(description = "3scale Configuration")
	protected AuthRepConfiguration conf;

	public AuthRepEndpoint(String uri, Component component, AuthRepConfiguration configuration) {
		super(uri, component);
		this.conf = configuration;
	}

	@Override
	public void doStart() throws Exception {
		super.doStart();

		System.out.println("Host: " + conf.getServerHost() + " Port: " + conf.getServerPort());
		threeScaleClient = ServiceApiDriver.createApi(conf.getServerHost(), conf.getServerPort(), true);
		System.out.println("Setting of ServiceAPI client");

	}


	@Override
	protected void doStop() throws Exception {
		System.out.println("Stopping");
		super.doStop();
	}

	public Consumer createConsumer(Processor processor) throws Exception {
		throw new UnsupportedOperationException("You cannot receive messages from this endpoint");
	}

	public Producer createProducer() throws Exception {
		return new AuthRepProducer(this);
	}

	public boolean isSingleton() {
		return true;
	}

	public AuthRepConfiguration getConfiguration() {
		return conf;
	}

	public ServiceApi getServiceApi() {
		return threeScaleClient;
	}

}
