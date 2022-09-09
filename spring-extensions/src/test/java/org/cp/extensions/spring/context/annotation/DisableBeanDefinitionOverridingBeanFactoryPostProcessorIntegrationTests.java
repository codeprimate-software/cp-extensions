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

import java.util.function.Function;

import org.cp.elements.lang.Nameable;
import org.cp.extensions.spring.tests.AbstractSpringIntegrationTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.support.BeanDefinitionOverrideException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Integration Tests for {@link DisableBeanDefinitionOverridingBeanFactoryPostProcessor}.
 *
 * @author John Blum
 * @see org.junit.jupiter.api.Test
 * @see org.cp.extensions.spring.context.annotation.DisableBeanDefinitionOverridingBeanFactoryPostProcessor
 * @see org.cp.extensions.spring.tests.AbstractSpringIntegrationTests
 * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @since 0.2.0
 */
public class DisableBeanDefinitionOverridingBeanFactoryPostProcessorIntegrationTests
    extends AbstractSpringIntegrationTests {

  private @NonNull ConfigurableApplicationContext assertServiceBeanName(
      @NonNull ConfigurableApplicationContext applicationContext) {

    assertThat(applicationContext).isNotNull();
    assertThat(applicationContext.getBeanFactory()).isInstanceOf(DefaultListableBeanFactory.class);
    assertThat(((DefaultListableBeanFactory) applicationContext.getBeanFactory()).isAllowBeanDefinitionOverriding()).isFalse();

    NameableService service = applicationContext.getBean(NameableService.class);

    assertThat(service).isNotNull();
    assertThat(service.getBeanName()).isEqualTo("A");
    assertThat(service.getName()).isEqualTo("B");

    return applicationContext;
  }

  @Override
  protected <T extends ConfigurableApplicationContext> Function<T, T> postProcessBeforeRefresh() {

    return applicationContext -> {
      applicationContext.addBeanFactoryPostProcessor(DisableBeanDefinitionOverridingBeanFactoryPostProcessor.INSTANCE);
      return applicationContext;
    };
  }

  @Test
  public void beanDefinitionOverridingIsNotAllowed() {

    assertThatExceptionOfType(BeanDefinitionOverrideException.class)
      .isThrownBy(() -> assertServiceBeanName(newApplicationContext(TestConfigurationOne.class, TestConfigurationTwo.class)))
      .withMessageStartingWith("Invalid bean definition with name 'A'")
      .withNoCause();
  }

  @Configuration
  @SuppressWarnings("unused")
  static class TestConfigurationOne {

    @Bean("A")
    NameableService serviceA() {
      return NameableService.as("A");
    }
  }

  @Configuration
  @SuppressWarnings("unused")
  static class TestConfigurationTwo {

    @Bean("A")
    NameableService serviceB() {
      return NameableService.as("B");
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
