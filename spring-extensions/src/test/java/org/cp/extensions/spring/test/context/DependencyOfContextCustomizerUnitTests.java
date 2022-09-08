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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.cp.extensions.spring.context.annotation.DependencyOfBeanFactoryPostProcessor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.MergedContextConfiguration;

/**
 * Unit Tests for {@link DependencyOfContextCustomizer}
 *
 * @author John Blum
 * @see org.junit.jupiter.api.Test
 * @see org.mockito.Mockito
 * @see org.cp.extensions.spring.test.context.DependencyOfContextCustomizer
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.test.context.TestContext
 * @since 1.0.0
 */
public class DependencyOfContextCustomizerUnitTests {

	@Test
	public void customizesApplicationContextCorrectly() {

		ConfigurableApplicationContext mockApplicationContext = mock(ConfigurableApplicationContext.class);

		ConfigurableEnvironment mockEnvironment = mock(ConfigurableEnvironment.class);

		doReturn(mockEnvironment).when(mockApplicationContext).getEnvironment();
		doReturn(true).when(mockEnvironment)
			.getProperty(eq(DependencyOfContextCustomizer.TEST_CONTEXT_CUSTOMIZATION_ENABLED_PROPERTY), eq(Boolean.TYPE),
				eq(DependencyOfContextCustomizer.DEFAULT_TEST_CONTEXT_CUSTOMIZATION_ENABLED));

		MergedContextConfiguration mockMergedContextConfiguration = mock(MergedContextConfiguration.class);

		new DependencyOfContextCustomizer().customizeContext(mockApplicationContext, mockMergedContextConfiguration);

		verify(mockApplicationContext, times(1)).getEnvironment();
		verify(mockEnvironment, times(1))
			.getProperty(eq(DependencyOfContextCustomizer.TEST_CONTEXT_CUSTOMIZATION_ENABLED_PROPERTY), eq(Boolean.TYPE),
				eq(DependencyOfContextCustomizer.DEFAULT_TEST_CONTEXT_CUSTOMIZATION_ENABLED));
		verify(mockApplicationContext, times(1))
			.addBeanFactoryPostProcessor(isA(DependencyOfBeanFactoryPostProcessor.class));
		verifyNoMoreInteractions(mockApplicationContext, mockEnvironment);
		verifyNoInteractions(mockMergedContextConfiguration);
	}

	@Test
	@SuppressWarnings("all")
	public void customizeNullApplicationContextIsNullSafe() {
		new DependencyOfContextCustomizer().customizeContext(null, mock(MergedContextConfiguration.class));
	}

	@Test
	public void customizeApplicationContextWhenEnvironmentIsNull() {

		ConfigurableApplicationContext mockApplicationContext = mock(ConfigurableApplicationContext.class);

		doReturn(null).when(mockApplicationContext).getEnvironment();

		MergedContextConfiguration mockMergedContextConfiguration = mock(MergedContextConfiguration.class);

		new DependencyOfContextCustomizer().customizeContext(mockApplicationContext, mockMergedContextConfiguration);

		verify(mockApplicationContext, times(1)).getEnvironment();
		verifyNoMoreInteractions(mockApplicationContext);
		verifyNoInteractions(mockMergedContextConfiguration);
	}

	@Test
	public void doesNotCustomizeApplicationContextWithCustomizationIsDisabled() {

		ConfigurableApplicationContext mockApplicationContext = mock(ConfigurableApplicationContext.class);

		ConfigurableEnvironment mockEnvironment = mock(ConfigurableEnvironment.class);

		doReturn(mockEnvironment).when(mockApplicationContext).getEnvironment();
		doReturn(false).when(mockEnvironment)
			.getProperty(eq(DependencyOfContextCustomizer.TEST_CONTEXT_CUSTOMIZATION_ENABLED_PROPERTY), eq(Boolean.TYPE),
				eq(DependencyOfContextCustomizer.DEFAULT_TEST_CONTEXT_CUSTOMIZATION_ENABLED));

		MergedContextConfiguration mockMergedContextConfiguration = mock(MergedContextConfiguration.class);

		new DependencyOfContextCustomizer().customizeContext(mockApplicationContext, mockMergedContextConfiguration);

		verify(mockApplicationContext, times(1)).getEnvironment();
		verify(mockEnvironment, times(1))
			.getProperty(eq(DependencyOfContextCustomizer.TEST_CONTEXT_CUSTOMIZATION_ENABLED_PROPERTY), eq(Boolean.TYPE),
				eq(DependencyOfContextCustomizer.DEFAULT_TEST_CONTEXT_CUSTOMIZATION_ENABLED));
		verify(mockApplicationContext, never()).addBeanFactoryPostProcessor(any(BeanFactoryPostProcessor.class));
		verifyNoMoreInteractions(mockApplicationContext, mockEnvironment);
		verifyNoInteractions(mockMergedContextConfiguration);
	}
}
