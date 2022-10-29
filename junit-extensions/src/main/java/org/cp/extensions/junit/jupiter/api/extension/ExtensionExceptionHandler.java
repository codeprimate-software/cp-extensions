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

import org.cp.elements.lang.Assert;
import org.cp.elements.lang.ObjectUtils;
import org.cp.elements.lang.annotation.NotNull;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Interface defining a contract to handle different {@link Throwable} objects
 * thrown from JUnit Jupiter {@link Extension Extensions}.
 *
 * @author John Blum
 * @see java.lang.FunctionalInterface
 * @see java.lang.RuntimeException
 * @see org.junit.jupiter.api.extension.Extension
 * @see org.junit.jupiter.api.extension.LifecycleMethodExecutionExceptionHandler
 * @see org.junit.jupiter.api.extension.TestExecutionExceptionHandler
 * @since 0.1.0
 */
@FunctionalInterface
@SuppressWarnings("unused")
public interface ExtensionExceptionHandler {

  /**
   * {@link ExtensionExceptionHandler} implementation that simply rethrows the original {@link Throwable cause}
   * {@link UnhandledExtensionException#wrap(Throwable) wrapped} in an {@link UnhandledExtensionException}.
   *
   * Useful as the last handler in a composition.
   *
   * @see UnhandledExtensionException#wrap(Throwable)
   */
  ExtensionExceptionHandler RETHROW_CAUSE_AS_UNHANDLED_EXTENSION_EXCEPTION = (extensionContext, cause) -> {
    throw UnhandledExtensionException.wrap(cause)
      .with("Exception [%s] was not handled", ObjectUtils.getClassName(cause))
      .build();
  };

  /**
   * Handles, or simply processes the {@link Throwable} object thrown by a JUnit Jupiter {@link Extension}.
   *
   * If this handler is unable to {@literal handle} the thrown {@link Throwable} object, then this handler should wrap
   * the {@link Throwable} object as the {@literal cause} of an {@link UnhandledExtensionException} and rethrow
   * the {@link UnhandledExtensionException} allowing additional handlers in a chain of handlers to possibly handle
   * the {@link Throwable root cause}.
   *
   * @param context {@link ExtensionContext} encapsulating {@literal context} metadata
   * in which the {@link Extension} executes.
   * @param cause {@link Throwable} object thrown from the {@link Extension}.
   * @see org.junit.jupiter.api.extension.ExtensionContext
   * @see java.lang.Throwable
   */
  void handle(ExtensionContext context, Throwable cause);

  /**
   * Composes this {@link ExtensionExceptionHandler} with the given, required {@link ExtensionExceptionHandler}.
   *
   * This {@link ExtensionExceptionHandler} is invoked first to handle the {@link Throwable} object thrown by
   * the JUnit Jupiter {@link Extension}, and if unsuccessful, and the {@link #handle(ExtensionContext, Throwable)}
   * method implementation appropriately throws an {@link UnhandledExtensionException} wrapping the original
   * {@link Throwable} object as the {@literal cause} of the {@link UnhandledExtensionException}, then the given
   * {@link ExtensionExceptionHandler} will be invoked to handle the original {@link Throwable} object, on down
   * the composition chain until:
   * 1) the chain of {@link ExtensionExceptionHandler ExtensionExceptionHandlers} is exhausted,
   * 2) or an {@link ExtensionExceptionHandler} was able to successfully {@literal handle} the {@link Throwable},
   * 3) or another type of Exception is thrown and the chain is broken.
   *
   * @param handler next {@link ExtensionExceptionHandler} in the composition.
   * @return the new, composed {@link ExtensionExceptionHandler}.
   * @throws IllegalArgumentException if the {@link ExtensionExceptionHandler} is {@literal null}.
   * @see <a href="https://en.wikipedia.org/wiki/Composite_pattern">Composite Software Design Pattern</a>
   * @see #handle(ExtensionContext, Throwable)
   */
  default ExtensionExceptionHandler compose(@NotNull ExtensionExceptionHandler handler) {

    Assert.notNull(handler, "ExtensionExceptionHandler is required");

    return (extensionContext, cause) -> {
      try {
        this.handle(extensionContext, cause);
      }
      catch (UnhandledExtensionException exception) {
        handler.handle(extensionContext, exception.getCause());
      }
    };
  }
}
