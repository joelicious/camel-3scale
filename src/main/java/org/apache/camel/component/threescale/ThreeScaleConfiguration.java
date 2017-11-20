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
package org.apache.camel.component.threescale;

import threescale.v3.api.ParameterMap;
import threescale.v3.api.ServiceApi;

public class ThreeScaleConfiguration {

	/***
	 * The host of the 3scale Admin Portal
	 */
	private String serverHost;

	/***
	 * The port of the 3scale Admin Portal
	 */
	private int serverPort;

	/***
	 * The service identifier.
	 */
	private String serviceId;

	/***
	 * The service token
	 */
	private String serviceToken;

	/***
	 * The API Key is the simplest form of credentials. By default, the name of
	 * the key parameter is user_key.
	 */
	private String apiKey;

	/***
	 * The App ID represents the Application Identifier. It is paired with the
	 * AppKey for Authentication.
	 */
	private String appId;

	/***
	 * The AppKey represents the Application Key. It is paired with the App ID
	 * for Authentication.
	 */
	private String appKey;

	/***
	 * The 3scale Service API.
	 */
	private ServiceApi serviceApi;

	/***
	 * A ParameterMap that customizes the 3scale service calls.
	 */
	private ParameterMap parameterMap;

	/***
	 * Constructor for ThreeScaleConfiguration.
	 */
	public ThreeScaleConfiguration() {
		this.parameterMap = new ParameterMap();
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

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceToken() {
		return serviceToken;
	}

	public void setServiceToken(String serviceToken) {
		this.serviceToken = serviceToken;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public ServiceApi getServiceApi() {
		return serviceApi;
	}

	public void setServiceApi(ServiceApi serviceApi) {
		this.serviceApi = serviceApi;
	}

	public void setParameterMap(ParameterMap parameterMap) {
		this.parameterMap = parameterMap;
	}

	public ParameterMap getParameterMap() {
		return parameterMap;
	}

}
