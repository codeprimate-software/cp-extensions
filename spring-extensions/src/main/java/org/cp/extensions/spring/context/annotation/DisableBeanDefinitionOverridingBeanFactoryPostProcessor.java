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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.cp.elements.lang.Assert;
import org.cp.elements.lang.annotation.NullSafe;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Spring {@link BeanFactoryPostProcessor} used to disable, or rather not allow bean definition overriding
 * in the Spring container.
 *
 * @author John Blum
 * @see org.cp.extensions.spring.context.annotation.DisableBeanDefinitionOverridingApplicationContextInitializer
 * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor
 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 * @see org.springframework.beans.factory.support.BeanDefinitionRegistry
 * @see org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
 * @see org.springframework.stereotype.Component
 * @see org.springframework.core.PriorityOrdered
 * @since 0.2.0
 */
@SuppressWarnings("unused")
@Component
public class DisableBeanDefinitionOverridingBeanFactoryPostProcessor
    implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {

  public static final DisableBeanDefinitionOverridingBeanFactoryPostProcessor INSTANCE =
    new DisableBeanDefinitionOverridingBeanFactoryPostProcessor();

  protected static final boolean ALLOW_BEAN_DEFINITION_OVERRIDING = false;

  protected static final int DEFAULT_ORDER = PriorityOrdered.HIGHEST_PRECEDENCE;

  private static final Map<ConfigurableApplicationContext, DisableBeanDefinitionOverridingBeanFactoryPostProcessor> registrations
    = new ConcurrentHashMap<>();

  /**
   * Factory method used to register this {@link DisableBeanDefinitionOverridingBeanFactoryPostProcessor}
   * with the given, required {@link ConfigurableApplicationContext}.
   *
   * @param <T> {@link Class type} of {@link ConfigurableApplicationContext}.
   * @param applicationContext {@link ConfigurableApplicationContext} used for registration;
   * must not be {@literal null}.
   * @return the given {@link ConfigurableApplicationContext}.
   * @throws IllegalArgumentException if the {@link ConfigurableApplicationContext} is {@literal null}.
   * @see org.springframework.context.ConfigurableApplicationContext
   */
  public static @NonNull <T extends ConfigurableApplicationContext> T registerWith(@NonNull T applicationContext) {

    Assert.notNull(applicationContext, "ApplicationContext is required");

    registrations.computeIfAbsent(applicationContext, it -> {
      it.addBeanFactoryPostProcessor(INSTANCE);
      return INSTANCE;
    });

    return applicationContext;
  }

  /**
   * Returns the {@link Integer order} of this {@link DisableBeanDefinitionOverridingBeanFactoryPostProcessor}
   * relative to other registered Spring container {@link BeanFactoryPostProcessor BeanFactoryPostProcessors}.
   *
   * @return the {@link Integer order} of this {@link DisableBeanDefinitionOverridingBeanFactoryPostProcessor}.
   * @see org.springframework.core.PriorityOrdered
   */
  @Override
  public int getOrder() {
    return DEFAULT_ORDER;
  }

  /**
   * Post processes the given {@link BeanDefinitionRegistry} by disabling (disallowing) bean definition overriding
   * if the registry is a {@link ConfigurableListableBeanFactory}.
   *
   * @param registry {@link BeanDefinitionRegistry} to post process.
   * @throws BeansException if processing the {@link BeanDefinitionRegistry} fails.
   * @see org.springframework.beans.factory.support.BeanDefinitionRegistry
   * @see #postProcessBeanFactory(ConfigurableListableBeanFactory)
   */
  @NullSafe @Override
  public void postProcessBeanDefinitionRegistry(@Nullable BeanDefinitionRegistry registry) throws BeansException {

    if (registry instanceof DefaultListableBeanFactory) {
      postProcessBeanFactory((DefaultListableBeanFactory) registry);
    }
  }

  /**
   * Post processes the given {@link ConfigurableListableBeanFactory} by disabling (disallowing) bean definition
   * overriding.
   *
   * @param beanFactory {@link ConfigurableListableBeanFactory} to post process.
   * @throws BeansException if processing the {@link ConfigurableListableBeanFactory} fails.
   * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowBeanDefinitionOverriding(boolean)
   * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory
   */
  @NullSafe @Override
  public void postProcessBeanFactory(@Nullable ConfigurableListableBeanFactory beanFactory) throws BeansException {

    if (beanFactory instanceof DefaultListableBeanFactory) {
      ((DefaultListableBeanFactory) beanFactory).setAllowBeanDefinitionOverriding(ALLOW_BEAN_DEFINITION_OVERRIDING);
    }
  }
}
