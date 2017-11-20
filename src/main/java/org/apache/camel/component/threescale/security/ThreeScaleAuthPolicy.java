
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
package org.apache.camel.component.threescale.security;

import javax.cache.CacheManager;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;

import org.apache.camel.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.spi.AuthorizationPolicy;
import org.apache.camel.spi.RouteContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import threescale.v3.api.ServiceApi;
import threescale.v3.api.impl.ServiceApiDriver;

public class ThreeScaleAuthPolicy implements AuthorizationPolicy {

	private static final Logger LOG = LoggerFactory.getLogger(ThreeScaleAuthPolicy.class);

	private ServiceApi serviceApi;
	private CacheManager cacheManager = null;

	public ThreeScaleAuthPolicy(String host, int port, CachingProvider cachingProvider) {

		this.serviceApi = ServiceApiDriver.createApi(host, port, true);

		if (cachingProvider != null) {
			
			this.cacheManager = cachingProvider.getCacheManager();

			boolean cacheFound = false;
			Iterable<String> cacheNames = this.cacheManager.getCacheNames();
			for (String s : cacheNames) {
				if (s.equalsIgnoreCase("threeScaleCache")) {
					cacheFound = true;
				}
			}

			if (!cacheFound) {
				MutableConfiguration<String, String> config = new MutableConfiguration<String, String>();
				config.setTypes(String.class, String.class);
				config.setStoreByValue(true);
				config.setStatisticsEnabled(true);
				config.setManagementEnabled(true);
				config.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(Duration.FIVE_MINUTES));
				this.cacheManager.createCache("threeScaleCache", config);
			}
		}
	}

	public void beforeWrap(RouteContext routeContext, ProcessorDefinition<?> definition) {

	}

	public Processor wrap(RouteContext routeContext, final Processor processor) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("Securing route {} using 3scale auth {}", routeContext.getRoute().getId(), this);
		}

		return new ThreeScaleAuthProcessor(processor, this);

	}

	public ServiceApi getServiceApi() {
		return serviceApi;
	}

	public CacheManager getCacheManager() {
		return this.cacheManager;
	}


}
