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
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.cp.elements.test.annotation.SubjectUnderTest;

import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Unit Tests for {@link DisableBeanDefinitionOverridingApplicationContextInitializer}.
 *
 * @author John Blum
 * @see org.junit.jupiter.api.Test
 * @see org.mockito.Mockito
 * @see org.cp.extensions.spring.context.annotation.DisableBeanDefinitionOverridingApplicationContextInitializer
 * @see org.springframework.context.ApplicationContextInitializer
 * @since 0.2.0
 */
public class DisableBeanDefinitionOverridingApplicationContextInitializerUnitTests {

  @SubjectUnderTest
  private final DisableBeanDefinitionOverridingApplicationContextInitializer applicationContextInitializer
    = new DisableBeanDefinitionOverridingApplicationContextInitializer();

  @Test
  public void initializesApplicationContextCorrectly() {

    ConfigurableApplicationContext mockApplicationContext = mock(ConfigurableApplicationContext.class);

    this.applicationContextInitializer.initialize(mockApplicationContext);

    verify(mockApplicationContext, times(1))
      .addBeanFactoryPostProcessor(isA(DisableBeanDefinitionOverridingBeanFactoryPostProcessor.class));

    verifyNoMoreInteractions(mockApplicationContext);
  }

  @Test
  public void initializesGenericApplicationContextCorrectly() {

    GenericApplicationContext mockApplicationContext = mock(GenericApplicationContext.class);

    this.applicationContextInitializer.initialize(mockApplicationContext);

    verify(mockApplicationContext, times(1)).setAllowBeanDefinitionOverriding(eq(false));
    verifyNoMoreInteractions(mockApplicationContext);
  }
  @Test
  public void initializeNullApplicationContextIsNullSafe() {
    this.applicationContextInitializer.initialize(null);
  }
}
