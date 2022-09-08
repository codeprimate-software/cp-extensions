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

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.cp.elements.util.CollectionUtils;
import org.cp.extensions.spring.test.context.DependencyOfContextCustomizer;
import org.cp.extensions.spring.tests.AbstractSpringIntegrationTests;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * Integration Tests for {@link DependencyOfApplicationContextInitializer}.
 *
 * @author John Blum
 * @see org.junit.jupiter.api.Test
 * @see org.cp.extensions.spring.context.annotation.DependencyOfApplicationContextInitializer
 * @see org.cp.extensions.spring.context.annotation.DependencyOfBeanFactoryPostProcessor
 * @see org.cp.extensions.spring.tests.AbstractSpringIntegrationTests
 * @see org.springframework.context.ApplicationContextInitializer
 * @see org.springframework.context.ApplicationContext
 * @since 0.1.0
 */
@SpringJUnitConfig(initializers = DependencyOfApplicationContextInitializer.class)
public class DependencyOfApplicationContextInitializerIntegrationTests
    extends AbstractSpringIntegrationTests {

  @BeforeAll
  public static void disableTestContextCustomization() {
    System.setProperty(DependencyOfContextCustomizer.TEST_CONTEXT_CUSTOMIZATION_ENABLED_PROPERTY,
      Boolean.FALSE.toString());
  }

  @AfterAll
  public static void clearSystemProperties() {
    System.clearProperty(DependencyOfContextCustomizer.TEST_CONTEXT_CUSTOMIZATION_ENABLED_PROPERTY);
  }

  private boolean containsDependencyOfBeanFactoryPostProcessor(List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

    return CollectionUtils.nullSafeList(beanFactoryPostProcessors).stream()
      .anyMatch(DependencyOfBeanFactoryPostProcessor.class::isInstance);
  }

  @Test
  public void dependencyOfBeanFactoryPostProcessRegisteredWithTheApplicationContext() {

    boolean dependencyOBeanFactoryPostProcessorRegistered = getApplicationContext()
      .map(this::assertApplicationContext)
      .filter(AbstractApplicationContext.class::isInstance)
      .map(AbstractApplicationContext.class::cast)
      .map(AbstractApplicationContext::getBeanFactoryPostProcessors)
      .filter(this::containsDependencyOfBeanFactoryPostProcessor)
      .isPresent();

    assertThat(dependencyOBeanFactoryPostProcessorRegistered).isTrue();
  }
}
