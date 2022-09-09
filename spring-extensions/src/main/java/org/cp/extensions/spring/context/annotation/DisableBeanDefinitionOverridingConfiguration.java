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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

/**
 * Spring {@link Configuration} class used to register the {@link DisableBeanDefinitionOverridingBeanFactoryPostProcessor}
 * with the Spring container and disable, or rather not allow bean definition overriding.
 *
 * @author John Blum
 * @see org.cp.extensions.spring.context.annotation.DisableBeanDefinitionOverridingBeanFactoryPostProcessor
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.context.annotation.Bean
 * @since 0.2.0
 */
@Configuration
@SuppressWarnings("unused")
public class DisableBeanDefinitionOverridingConfiguration {

  @Bean
  static @NonNull DisableBeanDefinitionOverridingBeanFactoryPostProcessor disableBeanDefinitionOverridingBeanFactoryPostProcessor() {
    return DisableBeanDefinitionOverridingBeanFactoryPostProcessor.INSTANCE;
  }
}
