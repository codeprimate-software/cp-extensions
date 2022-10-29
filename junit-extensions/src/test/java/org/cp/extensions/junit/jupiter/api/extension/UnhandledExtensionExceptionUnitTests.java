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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link UnhandledExtensionException}.
 *
 * @author John Blum
 * @see java.lang.Throwable
 * @see org.junit.jupiter.api.Test
 * @see org.cp.extensions.junit.jupiter.api.extension.UnhandledExtensionException
 * @since 0.1.0
 */
public class UnhandledExtensionExceptionUnitTests {

  @Test
  @SuppressWarnings("all")
  public void wrapThrowable() {

    Throwable expectedCause = new RuntimeException("TEST");

    UnhandledExtensionException.Builder builder = UnhandledExtensionException.wrap(expectedCause);

    assertThat(builder).isNotNull();
    assertThat(builder.getCause()).isEqualTo(expectedCause);
    assertThat(builder.getMessage()).isNotPresent();

    UnhandledExtensionException exception = builder.build();

    assertThat(exception).isNotNull();
    assertThat(exception.getCause()).isEqualTo(expectedCause);
    assertThat(exception.getMessage()).isEqualTo("%s: %s",
      expectedCause.getClass().getName(), expectedCause.getMessage());
  }

  @Test
  @SuppressWarnings("all")
  public void wrapThrowableWithMessage() {

    Throwable expectedCause = new RuntimeException("TEST");

    UnhandledExtensionException.Builder builder = UnhandledExtensionException.wrap(expectedCause)
      .with("This is a %s message", "mock");

    assertThat(builder).isNotNull();
    assertThat(builder.getCause()).isEqualTo(expectedCause);
    assertThat(builder.getMessage()).isPresent();

    UnhandledExtensionException exception = builder.build();

    assertThat(exception).isNotNull();
    assertThat(exception.getCause()).isEqualTo(expectedCause);
    assertThat(exception.getMessage()).isEqualTo("This is a mock message");
  }

  @Test
  public void wrapNullThrowableWithNoMessageIsNullSafe() {

    UnhandledExtensionException.Builder builder = UnhandledExtensionException.wrap(null)
      .with("  ", "mock", "test");

    assertThat(builder).isNotNull();
    assertThat(builder.getCause()).isNull();
    assertThat(builder.getMessage()).isNotPresent();

    UnhandledExtensionException exception = builder.build();

    assertThat(exception).isNotNull();
    assertThat(exception.getCause()).isNull();
    assertThat(exception.getMessage()).isNull();
  }
}
