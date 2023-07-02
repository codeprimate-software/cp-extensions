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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.cp.elements.lang.annotation.Nullable;
import org.cp.extensions.spring.tests.AbstractSpringIntegrationTests;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

/**
 * Abstract base test class for Spring container bean initialization order Integration Tests.
 *
 * @author John Blum
 * @see org.cp.extensions.spring.tests.AbstractSpringIntegrationTests
 * @see org.springframework.beans.factory.config.BeanPostProcessor
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @since 0.1.0
 */
@SuppressWarnings("unused")
public abstract class AbstractBeanInitializationOrderIntegrationTests extends AbstractSpringIntegrationTests {

  @Autowired
  private IterableBeanNamesPostProcessor beanNamesPostProcessor;

  protected @NonNull Iterable<String> getBeanNames() {
    return this.beanNamesPostProcessor;
  }

  /**
   * Spring {@link BeanPostProcessor} that collects {@link String bean names} of beans defined/declared
   * and managed by the Spring container.
   * <p>
   * The Spring {@link BeanPostProcessor} is {@link Iterable} over the {@link String bean names} it collects
   * during post-processing.
   *
   * @see org.springframework.beans.factory.config.BeanPostProcessor
   * @see java.lang.Iterable
   */
  protected interface IterableBeanNamesPostProcessor extends BeanPostProcessor, Iterable<String> { }

  @Configuration
  protected static class BeansTestConfiguration {

    @Bean
    IterableBeanNamesPostProcessor beanNamesPostProcessor() {

      return new IterableBeanNamesPostProcessor() {

        private final List<String> beanNames = new CopyOnWriteArrayList<>();

        @Override
        public @Nullable Object postProcessBeforeInitialization(@Nullable Object bean, @NonNull String beanName)
            throws BeansException {

          // Only record application (test) bean names; not Spring container infrastructure component bean names.
          if (isBeanOfInterest(beanName)) {
            this.beanNames.add(beanName);
          }

          return bean;
        }

        private boolean isBeanOfInterest(@Nullable String beanName) {
          return StringUtils.hasText(beanName) && Arrays.asList("A", "B", "C").contains(beanName);
        }

        @Override
        public Iterator<String> iterator() {
          return Collections.unmodifiableList(this.beanNames).iterator();
        }

        @Override
        public String toString() {
          return this.beanNames.toString();
        }
      };
    }
  }
}
