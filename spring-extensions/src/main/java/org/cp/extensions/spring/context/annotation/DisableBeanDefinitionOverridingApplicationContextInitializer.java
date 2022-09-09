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

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.Nullable;

/**
 * Spring {@link ApplicationContextInitializer} used to disable (disallow) bean definition overriding
 * in the Spring container.
 *
 * @author John Blum
 * @see org.cp.extensions.spring.context.annotation.DisableBeanDefinitionOverridingBeanFactoryPostProcessor
 * @see org.springframework.context.ApplicationContextInitializer
 * @see org.springframework.context.ConfigurableApplicationContext
 * @since 0.2.0
 */
@SuppressWarnings("unused")
public class DisableBeanDefinitionOverridingApplicationContextInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  public static final DisableBeanDefinitionOverridingApplicationContextInitializer INSTANCE
    = new DisableBeanDefinitionOverridingApplicationContextInitializer();

  protected static final boolean ALLOW_BEAN_DEFINITION_OVERRIDING
    = DisableBeanDefinitionOverridingBeanFactoryPostProcessor.ALLOW_BEAN_DEFINITION_OVERRIDING;

  /**
   * Initializes the {@link ConfigurableApplicationContext} by registering (adding)
   * the {@link DisableBeanDefinitionOverridingBeanFactoryPostProcessor} to disable (disallow)
   * bean definition overriding.
   *
   * This method shortcircuits if the given {@link ConfigurableApplicationContext}
   * is a {@link GenericApplicationContext}.
   *
   * @param applicationContext {@link ConfigurableApplicationContext} to initialize; must not be {@literal null}.
   * @see org.cp.extensions.spring.context.annotation.DisableBeanDefinitionOverridingBeanFactoryPostProcessor
   * @see org.springframework.context.ConfigurableApplicationContext#addBeanFactoryPostProcessor(BeanFactoryPostProcessor)
   * @see org.springframework.context.support.GenericApplicationContext#setAllowBeanDefinitionOverriding(boolean)
   * @see org.springframework.context.ConfigurableApplicationContext
   */
  @Override
  @SuppressWarnings("all")
  public void initialize(@Nullable ConfigurableApplicationContext applicationContext) {

    if (applicationContext instanceof GenericApplicationContext) {
      ((GenericApplicationContext) applicationContext).setAllowBeanDefinitionOverriding(ALLOW_BEAN_DEFINITION_OVERRIDING);
    }
    else if (applicationContext != null) {
      DisableBeanDefinitionOverridingBeanFactoryPostProcessor.registerWith(applicationContext);
    }
  }
}
