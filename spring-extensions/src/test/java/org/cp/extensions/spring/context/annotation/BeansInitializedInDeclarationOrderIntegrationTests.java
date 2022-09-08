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

import org.cp.extensions.spring.test.context.DependencyOfContextCustomizer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * Integration Tests asserting the default bean initialization order applied by the Spring container
 * without either Spring's {@link DependsOn} annotation or Codeprimate Exentsions' {@link DependencyOf} annotation
 * is the order in which the beans are declared in configuration metadata.
 *
 * @author John Blum
 * @see org.junit.jupiter.api.Test
 * @see org.cp.extensions.spring.context.annotation.AbstractBeanInitializationOrderIntegrationTests
 * @see org.cp.extensions.spring.context.annotation.DependencyOf
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.context.annotation.DependsOn
 * @see org.springframework.context.annotation.Import
 * @see org.springframework.context.annotation.Profile
 * @see org.springframework.test.annotation.DirtiesContext
 * @see org.springframework.test.context.ActiveProfiles
 * @see org.springframework.test.context.junit.jupiter.SpringJUnitConfig
 * @since 0.1.0
 */
@ActiveProfiles("bean-declaration-order")
@DirtiesContext
@SpringJUnitConfig
@SuppressWarnings("unused")
public class BeansInitializedInDeclarationOrderIntegrationTests
    extends AbstractBeanInitializationOrderIntegrationTests {

  @BeforeAll
  public static void disableTestContextCustomization() {
    System.setProperty(DependencyOfContextCustomizer.TEST_CONTEXT_CUSTOMIZATION_ENABLED_PROPERTY,
      Boolean.FALSE.toString());
  }

  @AfterAll
  public static void clearSystemProperties() {
    System.clearProperty(DependencyOfContextCustomizer.TEST_CONTEXT_CUSTOMIZATION_ENABLED_PROPERTY);
  }

  @Test
  public void beansInitializedInDeclarationOrder() {

    assertThat(getBeanNames())
      .describedAs("Expected [A, B, C]; but was %s", getBeanNames())
      .containsExactly("A", "B", "C");
  }

  @Configuration
  @Import(BeansTestConfiguration.class)
  @Profile("bean-declaration-order")
  static class BeanDeclarationOrderedTestConfiguration {

    @Bean("A")
    @DependencyOf("B")
    Object beanOne() {
      return "A";
    }

    @Bean("B")
    Object beanTwo() {
      return "B";
    }

    @Bean("C")
    @DependencyOf("A")
    Object beanThree() {
      return "C";
    }
  }
}
