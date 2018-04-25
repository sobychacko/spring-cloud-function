/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.function.stream.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Mark Fisher
 */
@ConfigurationProperties(prefix = "spring.cloud.function.stream")
public class XStreamConfigurationProperties {

	private Processor processor = new Processor();

	private boolean shared;

	public static final String ROUTE_KEY = "stream_routekey";

	public Processor getProcessor() {
		return this.processor;
	}

	public boolean isShared() {
		return this.shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

	public static class Processor {

		/**
		 * The name of a single processor to wire up to the input and output channels.
		 * Default is null, which means all functions are bound.
		 */
		private String name;

		/**
		 * Flag to be able to switch off binding consumers to input streams.
		 */
		private boolean enabled;

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isEnabled() {
			return this.enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

	}

}
