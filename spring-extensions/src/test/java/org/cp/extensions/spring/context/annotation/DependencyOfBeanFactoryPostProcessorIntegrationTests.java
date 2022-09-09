/*
 * Copyright 2011-Present Author or Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cp.extensions.spring.context.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.cp.extensions.spring.test.context.DependencyOfContextCustomizer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * Integration Tests for {@link DependencyOfBeanFactoryPostProcessor}.
 *
 * @author John Blum
 * @see org.junit.jupiter.api.Test
 * @see org.cp.extensions.spring.context.annotation.DependencyOfBeanFactoryPostProcessor
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.context.annotation.Import
 * @see org.springframework.test.annotation.DirtiesContext
 * @see org.springframework.test.context.junit.jupiter.SpringJUnitConfig
 * @since 0.1.0
 */
@DirtiesContext
@SpringJUnitConfig
@SuppressWarnings("unused")
public class DependencyOfBeanFactoryPostProcessorIntegrationTests
		extends AbstractBeanInitializationOrderIntegrationTests {

	@BeforeAll
	public static void disableTestContextCustomization() {
		System.setProperty(DependencyOfContextCustomizer.TEST_CONTEXT_CUSTOMIZATION_ENABLED_PROPERTY,
			Boolean.FALSE.toString());
	}

	@AfterAll
	public static void clearSystemProperties() {
		System.clearProperty(DependencyOfContextCustomizer.TEST_CONTEXT_CUSTOMIZATION_ENABLED_PROPERTY);
	}

	@Test
	public void beansInitializedInInverseDependencyOrder() {

		assertThat(getBeanNames())
			.describedAs("Expected [C, B, A]; but was %s", getBeanNames())
			.containsExactly("C", "B", "A");
	}

	@Configuration
	@Import(BeansTestConfiguration.class)
	static class TestConfiguration {

		@Bean
		static DependencyOfBeanFactoryPostProcessor dependencyOfBeanFactoryPostProcessor() {
			return new DependencyOfBeanFactoryPostProcessor();
		}

		@Bean("A")
		Object beanOne() {
			return "A";
		}

		@Bean("B")
		@DependencyOf("A")
		Object beanTwo() {
			return "B";
		}

		@Bean("C")
		@DependencyOf("B")
		Object beanThree() {
			return "C";
		}
	}
}
