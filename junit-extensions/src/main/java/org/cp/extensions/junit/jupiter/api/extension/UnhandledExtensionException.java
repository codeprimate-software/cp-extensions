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

import java.util.Optional;

import org.cp.elements.lang.StringUtils;
import org.cp.elements.lang.annotation.NotNull;
import org.cp.elements.lang.annotation.Nullable;

import org.junit.jupiter.api.extension.Extension;

/**
 * A {@link RuntimeException} implementation representing an unhandled Exception thrown from
 * a JUnit Jupiter {@link Extension}.
 *
 * @author John Blum
 * @see java.lang.RuntimeException
 * @since 0.1.0
 */
@SuppressWarnings("unused")
public class UnhandledExtensionException extends RuntimeException {

  /**
   * Factory method used to wrap the given {@link Throwable} object by building a new instance of
   * {@link UnhandledExtensionException} initialized with the {@link Throwable} object as the cause of
   * the new {@link UnhandledExtensionException}.
   *
   * @param cause {@link Throwable} object used as the cause of the new {@link UnhandledExtensionException}.
   * @return a new {@link UnhandledExtensionException.Builder}.
   * @see UnhandledExtensionException.Builder
   */
  public static @NotNull UnhandledExtensionException.Builder wrap(@Nullable Throwable cause) {
    return new UnhandledExtensionException.Builder(cause);
  }

  /**
   * Constructs a new, uninitialized instance of {@link UnhandledExtensionException}.
   */
  public UnhandledExtensionException() { }

  /**
   * Constructs a new instance of {@link UnhandledExtensionException} initialized with the given {@link String message}
   * description the error.
   *
   * @param message {@link String} containing a description of this exception.
   */
  public UnhandledExtensionException(String message) {
    super(message);
  }

  /**
   * Constructs a new instance of {@link UnhandledExtensionException} initialized with the given {@link Throwable}
   * used as the cause of this Exception.
   *
   * @param cause {@link Throwable} object used as the cause of this exception.
   */
  public UnhandledExtensionException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new instance of {@link UnhandledExtensionException} initialized with the given {@link String message}
   * description the error along with the given {@link Throwable} used as the cause of this Exception.
   *
   * @param message {@link String} containing a description of this exception.
   * @param cause {@link Throwable} object used as the cause of this exception.
   */
  public UnhandledExtensionException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * {@link Builder} used to build a {@link UnhandledExtensionException}.
   *
   * @see <a href="https://en.wikipedia.org/wiki/Builder_pattern">Builder Software Design Pattern</a>
   * @see org.cp.elements.lang.Builder
   */
  public static class Builder implements org.cp.elements.lang.Builder<UnhandledExtensionException> {

    @Nullable
    private String message;

    @Nullable
    private final Throwable cause;

    protected Builder(@Nullable Throwable cause) {
      this.cause = cause;
    }

    protected @Nullable Throwable getCause() {
      return this.cause;
    }

    protected Optional<String> getMessage() {
      return Optional.ofNullable(this.message)
        .filter(StringUtils::hasText);
    }

    public @NotNull Builder with(@Nullable String message, Object... args) {
      this.message = StringUtils.hasText(message) ? String.format(message, args) : null;
      return this;
    }

    public @NotNull UnhandledExtensionException build() {

      return getMessage()
        .map(message -> new UnhandledExtensionException(message, getCause()))
        .orElseGet(() -> new UnhandledExtensionException(getCause()));
    }
  }
}
