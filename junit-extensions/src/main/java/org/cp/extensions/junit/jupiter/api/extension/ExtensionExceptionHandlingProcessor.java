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
package org.cp.extensions.junit.jupiter.api.extension;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import org.cp.elements.lang.Assert;
import org.cp.elements.lang.annotation.NotNull;
import org.cp.elements.lang.annotation.NullSafe;
import org.cp.elements.util.ArrayUtils;
import org.cp.elements.util.CollectionUtils;

/**
 * Processor for JUnit Jupiter {@link Extension Extensions} possibly handle any exceptions thrown by
 * the {@link Extension Extensions} while being processed
 * using {@link ExtensionExceptionHandler ExtensionExceptionHandlers}.
 *
 * @author John Blum
 * @see org.cp.extensions.junit.jupiter.api.extension.ExtensionExceptionHandler
 * @see org.junit.jupiter.api.extension.ExtensionContext
 * @see org.junit.jupiter.api.extension.Extension
 * @since 0.1.0
 */
@FunctionalInterface
@SuppressWarnings("unused")
public interface ExtensionExceptionHandlingProcessor {

  /**
   * Factory method used to construct a new {@link ExtensionExceptionHandlingProcessor} initialized with
   * the array of {@link ExtensionExceptionHandler ExtensionExceptionHandlers} composed in a composition.
   *
   * @param exceptionHandlers array of {@link ExtensionExceptionHandler} objects to compose in a composition.
   * @return a new {@link ExtensionExceptionHandlingProcessor}.
   * @see org.cp.extensions.junit.jupiter.api.extension.ExtensionExceptionHandlingProcessor
   * @see org.cp.extensions.junit.jupiter.api.extension.ExtensionExceptionHandler
   * @see #of(Iterable)
   */
  @NullSafe
  static @NotNull ExtensionExceptionHandlingProcessor of(ExtensionExceptionHandler... exceptionHandlers) {
    return of(Arrays.asList(ArrayUtils.nullSafeArray(exceptionHandlers, ExtensionExceptionHandler.class)));
  }

  /**
   * Factory method used to construct a new {@link ExtensionExceptionHandlingProcessor} initialized with
   * the {@link Iterable} of {@link ExtensionExceptionHandler ExtensionExceptionHandlers} composed in a composition
   * and used to process {@link UnhandledExtensionException UnhandledExtensionExceptions} thrown by JUnit Jupiter
   * {@link Extension Extensions}.
   * <p>
   * {@link ExtensionExceptionHandler ExtensionExceptionHandlers} are called in the order
   * returned by the {@link Iterable} object.
   *
   * @param exceptionHandlers {@link Iterable} of {@link ExtensionExceptionHandler} objects to compose in a composition.
   * @return a new {@link ExtensionExceptionHandlingProcessor}.
   * @see org.cp.extensions.junit.jupiter.api.extension.ExtensionExceptionHandler#compose(ExtensionExceptionHandler)
   * @see org.cp.extensions.junit.jupiter.api.extension.ExtensionExceptionHandlingProcessor
   * @see #getExceptionHandler()
   * @see java.lang.Iterable
   */
  @NullSafe
  static @NotNull ExtensionExceptionHandlingProcessor of(Iterable<ExtensionExceptionHandler> exceptionHandlers) {

    ExtensionExceptionHandler composition = null;

    for (ExtensionExceptionHandler handler : CollectionUtils.nullSafeIterable(exceptionHandlers)) {
      if (handler != null) {
        composition = composition == null ? handler : composition.compose(handler);
      }
    }

    ExtensionExceptionHandler exceptionHandler = composition != null
      ? composition.compose(ExtensionExceptionHandler.RETHROW_CAUSE_AS_UNHANDLED_EXTENSION_EXCEPTION)
      : ExtensionExceptionHandler.RETHROW_CAUSE_AS_UNHANDLED_EXTENSION_EXCEPTION;

    return () -> Optional.of(exceptionHandler);
  }

  /**
   * Gets the configured, {@link Optional} {@link ExtensionExceptionHandler} used to handle {@link Throwable} objects
   * thrown by JUnit Jupiter {@link Extension Extensions}.
   *
   * @return the configured, {@link Optional} {@link ExtensionExceptionHandler} used to handle {@link Throwable} objects
   * thrown by JUnit Jupiter {@link Extension Extensions}.
   * @see org.cp.extensions.junit.jupiter.api.extension.ExtensionExceptionHandler
   * @see java.util.Optional
   */
  Optional<ExtensionExceptionHandler> getExceptionHandler();

  /**
   * Invokes {@link TestInstancePostProcessor#postProcessTestInstance(Object, ExtensionContext)}, handling any uncaught
   * {@link Exception Exceptions} thrown by the post processor {@link Extension}.
   *
   * @param testInstance {@link Object} representing the test class instance containing tests.
   * @param extensionContext {@link ExtensionContext} encapsulating the {@literal context}
   * for {@link Extension Extensions}.
   * @param testInstancePostProcessor {@link TestInstancePostProcessor} used to post process
   * the {@link Object test instance}; must not be {@literal null}.
   * @throws IllegalArgumentException if the {@link TestInstancePostProcessor} is {@literal null}.
   * @throws Exception if processing the {@link Object test instance} results in
   * an unhandled/uncaught {@link Exception}.
   * @see org.junit.jupiter.api.extension.TestInstancePostProcessor
   * @see org.junit.jupiter.api.extension.ExtensionContext
   */
  default void process(Object testInstance, ExtensionContext extensionContext,
      @NotNull TestInstancePostProcessor testInstancePostProcessor) throws Exception {

    Assert.notNull(testInstancePostProcessor, "TestInstancePostProcessor is required");

    try {
      testInstancePostProcessor.postProcessTestInstance(testInstance, extensionContext);
    }
    catch (Exception cause) {
      getExceptionHandler()
        .map(it -> { it.handle(extensionContext, cause); return true; })
        .orElseThrow(() -> cause);
    }
  }
}
