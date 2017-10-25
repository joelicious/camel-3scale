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

import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriParams;

import threescale.v3.api.ServiceApi;

@UriParams
public class ReportConfiguration {

	@UriParam
	private ServiceApi serviceApi;
	@UriParam
	private String apiKey;
	@UriParam
	private String serverHost;
	@UriParam
	private int serverPort;

	/**
	 * Get 3scale Service API
	 */
	public ServiceApi getServiceApi() {
		return serviceApi;
	}

	/**
	 * 3scale Service API
	 */
	public void setServiceApi(ServiceApi serviceApi) {
		this.serviceApi = serviceApi;
	}

	/**
	 * Get 3scale Service Key
	 */
	public String getAPIKey() {
		return apiKey;
	}

	/**
	 * 3scale Service Key
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * Get 3scale Server Host
	 * @return serverHost
	 */
	public String getServerHost() {
		return serverHost;
	}

	/**
	 * Set Server Host
	 * @param serverHost
	 */
	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	/**
	 * Get 3scale Server Port
	 * @return
	 */
	public int getServerPort() {
		return serverPort;
	}

	/** 
	 * Set 3scale Server Port
	 * @param serverPort
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

}