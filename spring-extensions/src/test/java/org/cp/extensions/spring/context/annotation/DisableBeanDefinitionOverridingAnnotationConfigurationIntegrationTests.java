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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.cp.elements.lang.Nameable;
import org.cp.extensions.spring.tests.AbstractSpringIntegrationTests;

import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionOverrideException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Integration Tests for {@link DisableBeanDefinitionOverriding},
 * {@link DisableBeanDefinitionOverridingApplicationContextInitializer}
 * and {@link DisableBeanDefinitionOverridingConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.context.annotation.Import
 * @see org.cp.extensions.spring.context.annotation.DisableBeanDefinitionOverriding
 * @see org.cp.extensions.spring.context.annotation.DisableBeanDefinitionOverridingApplicationContextInitializer
 * @see org.cp.extensions.spring.context.annotation.DisableBeanDefinitionOverridingConfiguration
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class DisableBeanDefinitionOverridingAnnotationConfigurationIntegrationTests
    extends AbstractSpringIntegrationTests {

  @BeforeClass
  public static void configureAssertJ() {
    Assertions.setMaxStackTraceElementsDisplayed(100);
  }

  @AfterClass
  public static void assertionWasCalled() {
    assertThat(DisabledBeanDefinitionOverridingTestConfiguration.ASSERTION_CALLED.get()).isTrue();
  }

  @Test
  public void beanDefinitionOverridingAllowedByDefault() {

    ConfigurableApplicationContext applicationContext =
      newApplicationContext(OverriddenNameableServiceBeanTestConfiguration.class);

    assertThat(applicationContext).isNotNull();
    assertThat(applicationContext.isActive()).isTrue();
    assertThat(applicationContext.isRunning()).isTrue();
    assertThat(applicationContext.containsBean("A")).isTrue();
    assertThat(applicationContext.containsBean("B")).isFalse();
    assertThat(applicationContext.getBeanFactory()).isInstanceOf(DefaultListableBeanFactory.class);
    assertThat(((DefaultListableBeanFactory) applicationContext.getBeanFactory()).isAllowBeanDefinitionOverriding()).isTrue();

    NameableService nameableService = applicationContext.getBean("A", NameableService.class);

    assertThat(nameableService).isNotNull();
    assertThat(nameableService.getBeanName()).isEqualTo("A");
    assertThat(nameableService.getName()).isEqualTo("B");
  }

  @Test
  public void disabledBeanDefinitionOverridingThrowsBeansException() {

    assertThatExceptionOfType(BeanDefinitionOverrideException.class)
      .isThrownBy(() -> newApplicationContext(DisabledBeanDefinitionOverridingTestConfiguration.class))
      .withMessageStartingWith("TEST")
      .withNoCause();
  }

  @Configuration
  static class NameableServiceBeanTestConfiguration {

    @Bean("A")
    NameableService serviceA() {
      return NameableService.as("A");
    }
  }

  @Configuration
  @Import(NameableServiceBeanTestConfiguration.class)
  static class OverriddenNameableServiceBeanTestConfiguration {

    @Bean("A")
    NameableService serviceB() {
      return NameableService.as("B");
    }
  }

  @Configuration
  @DisableBeanDefinitionOverriding
  @Import(OverriddenNameableServiceBeanTestConfiguration.class)
  static class DisabledBeanDefinitionOverridingTestConfiguration {

    static AtomicBoolean ASSERTION_CALLED = new AtomicBoolean(false);

    @Bean
    @SuppressWarnings("all")
    static @NonNull BeanFactoryPostProcessor beanFactoryAllowBeanDefinitionOverridingAssertingPostProcessor() {

      return beanFactory -> {

        boolean allowBeanDefinitionOverriding = Optional.ofNullable(beanFactory)
          .filter(DefaultListableBeanFactory.class::isInstance)
          .map(DefaultListableBeanFactory.class::cast)
          .map(DisabledBeanDefinitionOverridingTestConfiguration::assertionCalled)
          .map(DefaultListableBeanFactory::isAllowBeanDefinitionOverriding)
          .orElse(true);

        assertThat(allowBeanDefinitionOverriding).isFalse();
      };
    }

    private static @NonNull DefaultListableBeanFactory assertionCalled(@NonNull DefaultListableBeanFactory beanFactory) {
      ASSERTION_CALLED.set(true);
      return beanFactory;
    }
  }

  @Getter
  @Service
  @ToString(of = "name")
  @EqualsAndHashCode(of = "name")
  @RequiredArgsConstructor(staticName = "as")
  static class NameableService implements BeanNameAware, Nameable<String> {

    @Setter
    private String beanName;

    @lombok.NonNull
    private final String name;

  }
}
