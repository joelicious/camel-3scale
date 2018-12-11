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

import org.apache.camel.RuntimeCamelException;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriParams;

import threescale.v3.api.ParameterMap;
import threescale.v3.api.ServiceApi;

@UriParams
public class ThreeScaleConfiguration implements Cloneable {
	
	@UriParam(defaultValue = "localhost") @Metadata(required = "true")
	private String serverHost;

	@UriParam(defaultValue = "8080") @Metadata(required = "true")
	private int serverPort;

	@UriParam
	private String serviceId;
	
	@UriParam
	private String serviceToken;
	
	@UriParam
	private String apiKey;
	
	@UriParam
	private String appId;
	
	@UriParam
	private String appKey;
	
	@UriParam
	private ServiceApi serviceApi;
	
	@UriParam
	private ParameterMap parameterMap;

	/***
	 * Constructor for ThreeScaleConfiguration.
	 */
	public ThreeScaleConfiguration() {
		this.parameterMap = new ParameterMap();
	}

	/***
	 * Retrieves the 3scale admin portal address
	 * 
	 * @return The 3scale admin portal address
	 */
	public String getServerHost() {
		return serverHost;
	}

	/***
	 * Set the 3scale admin portal address.
	 * 
	 * @param serverHost
	 */
	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	/***
	 * Retrieves the 3scale admin portal port
	 * 
	 * @return The 3scale admin portal port
	 */
	public int getServerPort() {
		return serverPort;
	}

	/***
	 * Set the 3scale admin portal port
	 * 
	 * @param serverPort
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	/***
	 * Retrieves the 3scale Service Identifier
	 * 
	 * @return The 3scale Service Identifier
	 */
	public String getServiceId() {
		return serviceId;
	}

	/***
	 * Set the 3scale Service Identifier
	 * 
	 * @param serviceId
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	/***
	 * Retrieves the 3scale Service Token
	 * 
	 * @return The 3scale Service Token
	 */
	public String getServiceToken() {
		return serviceToken;
	}

	/***
	 * Set the 3scale Service Token
	 * 
	 * @param serviceToken
	 */
	public void setServiceToken(String serviceToken) {
		this.serviceToken = serviceToken;
	}

	/***
	 * Retrieves the API Key
	 * 
	 * @return The API Key
	 */
	public String getApiKey() {
		return apiKey;
	}

	/*** 
	 * Set the 3scale API Key.
	 * 
	 * @param apiKey
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	/***
	 * Retrieves the Application Key
	 * 
	 * @return The Application Key
	 */
	public String getAppKey() {
		return appKey;
	}

	/***
	 * Set the Application Key. It is paired with the AppId for Authentication.
	 * 
	 * @param appKey
	 */
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	/***
	 * Retrieves the Application Identifier
	 * 
	 * @return The Application Identifier
	 */
	public String getAppId() {
		return appId;
	}

	/***
	 * Set the Application Identifier. It is paired with the AppKey for Authentication.
	 * 
	 * @param The Application Identifier
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/***
	 * Retrieve the Service API
	 * 
	 * @return The Service API
	 */
	public ServiceApi getServiceApi() {
		return serviceApi;
	}

	/***
	 * Set the Service API
	 * 
	 * @param serviceApi
	 */
	public void setServiceApi(ServiceApi serviceApi) {
		this.serviceApi = serviceApi;
	}

	/***
	 * Retrieves the Parameter Map. 
	 * 
	 * @return
	 */
	public ParameterMap getParameterMap() {
		return parameterMap;
	}
	
	/***
	 * Set the Parameter Map
	 * 
	 * @param parameterMap
	 */
	public void setParameterMap(ParameterMap parameterMap) {
		this.parameterMap = parameterMap;
	}

	/***
	 * Makes a copy of the ThreeScaleConfiguration and returns it.
	 * 
	 * @return
	 */
	public ThreeScaleConfiguration copy() {
		try {
			return (ThreeScaleConfiguration) clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeCamelException(e);
		}
	}

}
