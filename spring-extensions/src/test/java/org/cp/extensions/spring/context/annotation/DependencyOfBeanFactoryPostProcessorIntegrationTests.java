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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * Integration Tests for {@link DependencyOfBeanFactoryPostProcessor}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.cp.extensions.spring.context.annotation.DependencyOfBeanFactoryPostProcessor
 * @see org.springframework.beans.factory.config.BeanPostProcessor
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.test.context.junit.jupiter.SpringJUnitConfig
 * @since 0.1.0
 */
@SpringJUnitConfig
@SuppressWarnings("unused")
public class DependencyOfBeanFactoryPostProcessorIntegrationTests {

	@Autowired
	private Iterable<String> beanNames;

	@Test
	public void beansInitializedInDependencyOrder() {

		assertThat(this.beanNames)
			.describedAs("Expected [C, B, A]; but was %s", this.beanNames)
			.containsExactly("C", "B", "A");
	}

	interface IterableBeanNamesPostProcessor extends BeanPostProcessor, Iterable<String> { }

	@Configuration
	static class TestConfiguration {

		@Bean
		static DependencyOfBeanFactoryPostProcessor dependencyOfBeanFactoryPostProcessor() {
			return new DependencyOfBeanFactoryPostProcessor();
		}

		@Bean
		BeanPostProcessor beanNamesPostProcessor() {

			return new IterableBeanNamesPostProcessor() {

				private final List<String> beanNames = new ArrayList<>(3);

				@Override
				public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

					if (Arrays.asList("A", "B", "C").contains(beanName)) {
						this.beanNames.add(beanName);
					}

					return bean;
				}

				@Override
				public @NonNull Iterator<String> iterator() {
					return Collections.unmodifiableList(this.beanNames).iterator();
				}

				@Override
				public String toString() {
					return this.beanNames.toString();
				}
			};
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
