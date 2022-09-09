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

import org.cp.elements.lang.Nameable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.TestWatcher;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Integration Tests for {@link DisableBeanDefinitionOverridingApplicationContextInitializer}.
 *
 * @author John Blum
 * @see org.junit.jupiter.api.Test
 * @see org.cp.extensions.spring.context.annotation.DisableBeanDefinitionOverridingApplicationContextInitializer
 * @see org.springframework.context.ApplicationContextInitializer
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.context.annotation.Profile
 * @see org.springframework.test.context.ActiveProfiles
 * @see org.springframework.test.context.junit.jupiter.SpringJUnitConfig
 * @since 0.2.0
 */
@ActiveProfiles({ "test-configuration-one", "test-configuration-two" })
@ExtendWith(TestWatcher.class)
@SpringJUnitConfig(initializers = DisableBeanDefinitionOverridingApplicationContextInitializer.class)
public class DisableBeanDefinitionOverridingApplicationContextInitializerIntegrationTests {

  @Test
  public void failsToLoadContext() { }

  @Configuration
  @Profile("test-configuration-one")
  @SuppressWarnings("unused")
  static class TestConfigurationOne {

    @Bean("A")
    DisableBeanDefinitionOverridingBeanFactoryPostProcessorIntegrationTests.NameableService serviceA() {
      return DisableBeanDefinitionOverridingBeanFactoryPostProcessorIntegrationTests.NameableService.as("A");
    }
  }

  @Configuration
  @Profile("test-configuration-two")
  @SuppressWarnings("unused")
  static class TestConfigurationTwo {

    @Bean("A")
    DisableBeanDefinitionOverridingBeanFactoryPostProcessorIntegrationTests.NameableService serviceB() {
      return DisableBeanDefinitionOverridingBeanFactoryPostProcessorIntegrationTests.NameableService.as("B");
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
