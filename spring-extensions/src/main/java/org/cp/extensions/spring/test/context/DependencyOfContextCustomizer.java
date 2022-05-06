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
package org.cp.extensions.spring.test.context;

import org.cp.extensions.spring.context.annotation.DependencyOf;
import org.cp.extensions.spring.context.annotation.DependencyOfBeanFactoryPostProcessor;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.TestContext;

/**
 * Spring Test {@link ContextCustomizer} used to customize the {@link ConfigurableApplicationContext} by registering
 * an instance of the {@link DependencyOfBeanFactoryPostProcessor}, thereby enabling {@link DependencyOf} annotation
 * declarations on beans defined in a Spring {@link TestContext}.
 *
 * @author John Blum
 * @see org.cp.extensions.spring.context.annotation.DependencyOfBeanFactoryPostProcessor
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.test.context.ContextCustomizer
 * @since 0.1.0
 */
public class DependencyOfContextCustomizer implements ContextCustomizer {

	/**
	 * Customizes the {@link ConfigurableApplicationContext} by registering an instance of
	 * the {@link DependencyOfBeanFactoryPostProcessor}.
	 *
	 * @param applicationContext {@link ConfigurableApplicationContext} to customize; must not be {@literal null}.
	 * @param mergedConfig {@link MergedContextConfiguration} containing the merged configuration for the test.
	 * @see org.cp.extensions.spring.context.annotation.DependencyOfBeanFactoryPostProcessor
	 * @see org.springframework.test.context.MergedContextConfiguration
	 * @see org.springframework.context.ConfigurableApplicationContext
	 */
	@Override
	public void customizeContext(@NonNull ConfigurableApplicationContext applicationContext,
			@NonNull MergedContextConfiguration mergedConfig) {

		DependencyOfBeanFactoryPostProcessor.registerWith(applicationContext);
	}
}
