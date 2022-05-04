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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * Unit Tests for {@link DependencyOfBeanFactoryPostProcessor}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.cp.extensions.spring.context.annotation.DependencyOfBeanFactoryPostProcessor
 * @since 1.0.0
 */
public class DependencyOfBeanFactoryPostProcessorUnitTests {

	@Test
	@SuppressWarnings("all")
	public void postProcessBeanFactory() {

		BeanDefinition beanOne = mock(BeanDefinition.class);
		BeanDefinition beanTwo = mock(BeanDefinition.class);

		ConfigurableListableBeanFactory mockBeanFactory = mock(ConfigurableListableBeanFactory.class);

		doReturn(new String[] { "TestBean" }).when(mockBeanFactory).getBeanNamesForAnnotation(eq(DependencyOf.class));

		doAnswer(invocation -> TestBean.class.getAnnotation(DependencyOf.class))
			.when(mockBeanFactory).findAnnotationOnBean(eq("TestBean"), eq(DependencyOf.class));

		doReturn(beanOne).when(mockBeanFactory).getBeanDefinition(eq("BeanOne"));
		doReturn(beanTwo).when(mockBeanFactory).getBeanDefinition(eq("BeanTwo"));

		new DependencyOfBeanFactoryPostProcessor().postProcessBeanFactory(mockBeanFactory);

		verify(mockBeanFactory, times(1)).getBeanNamesForAnnotation(eq(DependencyOf.class));
		verify(mockBeanFactory, times(1)).findAnnotationOnBean(eq("TestBean"), eq(DependencyOf.class));
		verify(mockBeanFactory, times(1)).getBeanDefinition(eq("BeanOne"));
		verify(mockBeanFactory, times(1)).getBeanDefinition(eq("BeanTwo"));
		verify(beanOne, times(1)).getDependsOn();
		verify(beanOne, times(1)).setDependsOn(new String[] { "TestBean" });
		verify(beanTwo, times(1)).getDependsOn();
		verify(beanTwo, times(1)).setDependsOn(new String[] { "TestBean" });
		verifyNoMoreInteractions(mockBeanFactory, beanOne, beanTwo);
	}

	@Component("TestBean")
	@DependencyOf({ "BeanOne", "BeanTwo" })
	//@DependencyOf(beanNames = { "BeanOne", "BeanTwo" })
	static class TestBean { }

}
