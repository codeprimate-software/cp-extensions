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

import org.cp.elements.lang.annotation.Nullable;
import org.cp.extensions.spring.context.annotation.DependencyOf;
import org.cp.extensions.spring.context.annotation.DependencyOfBeanFactoryPostProcessor;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.TestContext;

/**
 * Spring Test {@link ContextCustomizer} used to customize the {@link ConfigurableApplicationContext} by registering
 * (adding) an instance of the {@link DependencyOfBeanFactoryPostProcessor}, thereby enabling {@link DependencyOf}
 * annotation declarations on beans defined and managed in a Spring {@link TestContext}.
 *
 * @author John Blum
 * @see org.cp.extensions.spring.context.annotation.DependencyOf
 * @see org.cp.extensions.spring.context.annotation.DependencyOfBeanFactoryPostProcessor
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.test.context.ContextCustomizer
 * @see org.springframework.test.context.TestContext
 * @since 0.1.0
 */
public class DependencyOfContextCustomizer implements ContextCustomizer {

	public static final String TEST_CONTEXT_CUSTOMIZATION_ENABLED_PROPERTY =
		"codeprimate.extensions.spring.test.context.customization-enabled";

	protected static final boolean DEFAULT_TEST_CONTEXT_CUSTOMIZATION_ENABLED = true;

	/**
	 * Customizes the {@link ConfigurableApplicationContext} by registering (adding) an instance of
	 * the {@link DependencyOfBeanFactoryPostProcessor}.
	 *
	 * @param applicationContext {@link ConfigurableApplicationContext} to customize; must not be {@literal null}.
	 * @param mergedConfig {@link MergedContextConfiguration} containing the merged configuration for the test.
	 * @see org.cp.extensions.spring.context.annotation.DependencyOfBeanFactoryPostProcessor
	 * @see org.springframework.context.ConfigurableApplicationContext
	 * @see org.springframework.test.context.MergedContextConfiguration
	 */
	@Override
	public void customizeContext(@NonNull ConfigurableApplicationContext applicationContext,
			@NonNull MergedContextConfiguration mergedConfig) {

		if (isEnabled(applicationContext)) {
			DependencyOfBeanFactoryPostProcessor.registerWith(applicationContext);
		}
	}

	private boolean isEnabled(@Nullable ConfigurableApplicationContext applicationContext) {
		return applicationContext != null && isEnabled(applicationContext.getEnvironment());
	}

	private boolean isEnabled(@Nullable Environment environment) {

		return environment != null && environment.getProperty(TEST_CONTEXT_CUSTOMIZATION_ENABLED_PROPERTY, Boolean.TYPE,
			DEFAULT_TEST_CONTEXT_CUSTOMIZATION_ENABLED);
	}
}
