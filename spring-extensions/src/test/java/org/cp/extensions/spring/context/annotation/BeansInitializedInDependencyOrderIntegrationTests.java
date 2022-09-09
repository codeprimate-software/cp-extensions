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
 * Integration Tests asserting the initialization order of beans defined, declared and managed by the Spring container
 * is determined by the Spring {@link DependsOn} annotation and dependency order.
 *
 * @author John Blum
 * @see org.junit.jupiter.api.Test
 * @see org.cp.extensions.spring.context.annotation.AbstractBeanInitializationOrderIntegrationTests
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
@ActiveProfiles("bean-dependency-order")
@DirtiesContext
@SpringJUnitConfig
@SuppressWarnings("unused")
public class BeansInitializedInDependencyOrderIntegrationTests
    extends AbstractBeanInitializationOrderIntegrationTests {

  @Test
  public void beansInitializedInDependencyOrder() {

    assertThat(getBeanNames())
      .describedAs("Expected [C, B, A]; but was %s", getBeanNames())
      .containsExactly("C", "B", "A");
  }

  @Configuration
  @Import(BeansTestConfiguration.class)
  @Profile("bean-dependency-order")
  static class BeanDependencyOrderTestConfiguration {

    @Bean("A")
    @DependsOn("B")
    Object beanOne() {
      return "A";
    }

    @Bean("B")
    @DependsOn("C")
    Object beanTwo() {
      return "B";
    }

    @Bean("C")
    Object beanThree() {
      return "C";
    }
  }
}
