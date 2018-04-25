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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.catalog.FunctionInspector;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binder.Binder;
import org.springframework.cloud.stream.binder.kafka.streams.annotations.KafkaStreamsProcessor;
import org.springframework.cloud.stream.converter.CompositeMessageConverterFactory;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author Mark Fisher
 * @author Marius Bogoevici
 */
@Configuration
@EnableConfigurationProperties(XStreamConfigurationProperties.class)
@ConditionalOnClass(Binder.class)
@ConditionalOnBean(FunctionCatalog.class)
@ConditionalOnProperty(name = "spring.cloud.stream.enabled", havingValue = "true", matchIfMissing = true)
public class XStreamAutoConfiguration {

	@Configuration
	@ConditionalOnProperty(name = "spring.cloud.function.stream.processor.enabled", havingValue = "true", matchIfMissing = true)
	@Conditional(ProcessorCondition.class)
	protected static class ProcessorConfiguration {

		@Autowired
		private XStreamConfigurationProperties properties;

		@Bean
		public XStreamListeningFunctionInvoker functionInvoker(FunctionCatalog registry,
															   FunctionInspector functionInspector,
															   @Lazy CompositeMessageConverterFactory compositeMessageConverterFactory) {
			return new XStreamListeningFunctionInvoker(registry, functionInspector,
					compositeMessageConverterFactory, "",
					properties.isShared());
		}

	}

	@Configuration
	@EnableBinding(KafkaStreamsProcessor.class)
	@Conditional(ProcessorCondition.class)
	protected class ProcessorBindingConfiguration {
	}

	private static class ProcessorCondition extends SpringBootCondition {

		@Override
		public ConditionOutcome getMatchOutcome(ConditionContext context,
				AnnotatedTypeMetadata metadata) {

			return ConditionOutcome.match(
					"Neither source nor sink is explicitly disabled");
		}
	}

}
