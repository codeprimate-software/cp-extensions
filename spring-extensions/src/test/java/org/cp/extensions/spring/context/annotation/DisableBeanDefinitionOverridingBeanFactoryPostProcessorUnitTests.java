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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Unit Tests for {@link DisableBeanDefinitionOverridingBeanFactoryPostProcessor}.
 *
 * @author John Blum
 * @see org.junit.jupiter.api.Test
 * @see org.mockito.Mockito
 * @see org.cp.extensions.spring.context.annotation.DisableBeanDefinitionOverridingBeanFactoryPostProcessor
 * @since 0.2.0
 */
public class DisableBeanDefinitionOverridingBeanFactoryPostProcessorUnitTests {

  @Test
  public void registersSingleApplicationContextOnlyOnce() {

    ConfigurableApplicationContext mockApplicationContext = mock(ConfigurableApplicationContext.class);

    assertThat(DisableBeanDefinitionOverridingBeanFactoryPostProcessor.registerWith(mockApplicationContext))
      .isSameAs(mockApplicationContext);

    assertThat(DisableBeanDefinitionOverridingBeanFactoryPostProcessor.registerWith(mockApplicationContext))
      .isSameAs(mockApplicationContext);

    verify(mockApplicationContext, times(1))
      .addBeanFactoryPostProcessor(eq(DisableBeanDefinitionOverridingBeanFactoryPostProcessor.INSTANCE));

    verifyNoMoreInteractions(mockApplicationContext);
  }

  @Test
  public void registersMultipleApplicationContextsOnlyOnce() {

    ConfigurableApplicationContext mockApplicationContextOne = mock(ConfigurableApplicationContext.class);
    ConfigurableApplicationContext mockApplicationContextTwo = mock(ConfigurableApplicationContext.class);

    assertThat(DisableBeanDefinitionOverridingBeanFactoryPostProcessor.registerWith(mockApplicationContextOne))
      .isSameAs(mockApplicationContextOne);

    assertThat(DisableBeanDefinitionOverridingBeanFactoryPostProcessor.registerWith(mockApplicationContextTwo))
      .isSameAs(mockApplicationContextTwo);

    assertThat(DisableBeanDefinitionOverridingBeanFactoryPostProcessor.registerWith(mockApplicationContextTwo))
      .isSameAs(mockApplicationContextTwo);

    assertThat(DisableBeanDefinitionOverridingBeanFactoryPostProcessor.registerWith(mockApplicationContextOne))
      .isSameAs(mockApplicationContextOne);

    verify(mockApplicationContextOne, times(1))
      .addBeanFactoryPostProcessor(eq(DisableBeanDefinitionOverridingBeanFactoryPostProcessor.INSTANCE));

    verify(mockApplicationContextTwo, times(1))
      .addBeanFactoryPostProcessor(eq(DisableBeanDefinitionOverridingBeanFactoryPostProcessor.INSTANCE));

    verifyNoMoreInteractions(mockApplicationContextOne, mockApplicationContextTwo);
  }

  @Test
  public void postProcessesBeanFactoryCorrectly() {

    DefaultListableBeanFactory mockBeanFactory = mock(DefaultListableBeanFactory.class);

    DisableBeanDefinitionOverridingBeanFactoryPostProcessor.INSTANCE.postProcessBeanFactory(mockBeanFactory);

    verify(mockBeanFactory, times(1)).setAllowBeanDefinitionOverriding(eq(false));
    verifyNoMoreInteractions(mockBeanFactory);
  }

  @Test
  public void postProcessIncorrectBeanFactoryType() {

    ConfigurableListableBeanFactory mockBeanFactory = mock(ConfigurableListableBeanFactory.class);

    DisableBeanDefinitionOverridingBeanFactoryPostProcessor.INSTANCE.postProcessBeanFactory(mockBeanFactory);

    verifyNoInteractions(mockBeanFactory);
  }

  @Test
  public void postProcessNullBeanFactoryIsNullSafe() {
    DisableBeanDefinitionOverridingBeanFactoryPostProcessor.INSTANCE.postProcessBeanFactory(null);
  }
}
