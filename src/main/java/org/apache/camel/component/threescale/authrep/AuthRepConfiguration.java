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

import org.apache.camel.spi.UriParam;

import threescale.v3.api.ParameterMap;
import threescale.v3.api.ServiceApi;

public class AuthRepConfiguration {

	@UriParam
	private String serverHost;
	@UriParam
	private int serverPort;
	@UriParam
	private ServiceApi serviceApi;
	@UriParam
	private String userKey;
	@UriParam
	private String serviceToken;
	@UriParam
	private String serviceId;
	@UriParam
	private ParameterMap parameterMap;

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
	 * Get 3scale User Key
	 */
	public String getUserKey() {
		return userKey;
	}

	/**
	 * 3scale User Key
	 */
	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public void setServiceToken(String serviceToken) {
		this.serviceToken = serviceToken;
	}

	public String getServiceToken() {
		return serviceToken;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setParameterMap(ParameterMap parameterMap) {
		this.parameterMap = parameterMap;
	}

	public ParameterMap getParameterMap() {
		return parameterMap;
	}
}