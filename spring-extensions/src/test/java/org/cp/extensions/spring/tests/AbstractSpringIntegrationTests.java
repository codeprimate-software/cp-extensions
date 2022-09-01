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
package org.cp.extensions.spring.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cp.elements.lang.RuntimeExceptionsFactory.newIllegalStateException;

import java.util.Optional;
import java.util.function.Function;

import org.cp.elements.test.annotation.IntegrationTest;

import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Abstract base test class for implementing Spring Integration Tests.
 *
 * @author John Blum
 * @see org.cp.elements.test.annotation.IntegrationTest
 * @see org.springframework.context.ConfigurableApplicationContext
 * @since 0.1.0
 */
@SuppressWarnings("unused")
@IntegrationTest
public abstract class AbstractSpringIntegrationTests {

  @Autowired
  private ConfigurableApplicationContext applicationContext;

  protected Optional<ConfigurableApplicationContext> getApplicationContext() {
    return Optional.ofNullable(this.applicationContext);
  }

  protected @NonNull ConfigurableApplicationContext requireApplicationContext() {

    return getApplicationContext().orElseThrow(() ->
      newIllegalStateException("An ApplicationContext was not initialized; call newApplicationContext(Class<?>[]) first"));
  }

  protected <T extends ConfigurableApplicationContext> T assertApplicationContext(T applicationContext) {

    assertThat(applicationContext).isNotNull();

    assertThat(applicationContext.isActive())
      .describedAs("ApplicationContext [%s] was not active", applicationContext)
      .isTrue();

    assertThat(applicationContext.isRunning())
      .describedAs("ApplicationContext [%s] is not running", applicationContext)
      .isTrue();

    return applicationContext;
  }

  protected @NonNull ConfigurableApplicationContext newApplicationContext(Class<?>... configurationClasses) {

    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

    applicationContext.register(configurationClasses);
    applicationContext.registerShutdownHook();
    postProcessBeforeRefresh().apply(applicationContext).refresh();
    this.applicationContext = postProcessAfterRefresh().apply(applicationContext);

    return applicationContext;
  }

  protected <T extends ConfigurableApplicationContext> Function<T, T> postProcessBeforeRefresh() {
    return Function.identity();
  }

  protected <T extends ConfigurableApplicationContext> Function<T, T> postProcessAfterRefresh() {
    return Function.identity();
  }

  protected void closeApplicationContext(@Nullable ConfigurableApplicationContext applicationContext) {

    if (applicationContext != null) {
      applicationContext.close();
    }
  }

  @After
  public void closeApplicationContext() {
    closeApplicationContext(this.applicationContext);
  }
}
