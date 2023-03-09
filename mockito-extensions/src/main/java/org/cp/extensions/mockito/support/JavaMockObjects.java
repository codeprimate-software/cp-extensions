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
package org.cp.extensions.mockito.support;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import org.cp.elements.function.FunctionUtils;
import org.cp.elements.lang.Assert;
import org.cp.elements.lang.annotation.NotNull;
import org.cp.elements.lang.annotation.Nullable;
import org.cp.elements.text.FormatUtils;

import org.mockito.stubbing.Answer;

/**
 * Abstract base class used to construct common {@link Object Mock Objects} in Java with Mockito.
 *
 * @author John Blum
 * @see org.mockito.Mockito
 * @since 0.1.0
 */
public abstract class JavaMockObjects {

  /**
   * Creates a new mock {@link Future} object initialized with the given {@link Object value}
   * returned by the mock {@link Future} as the asynchronous, computed result.
   *
   * @param <T> {@link Class type} of {@link Object} returned by the mock {@link Future}.
   * @param value {@link Object} returned by the mock {@link Future} as the asynchronous, computed result.
   * @return a new mock {@link Future} that will return the given {@link Object value}.
   * @throws IllegalArgumentException if the given {@link Supplier} is {@literal null}.
   * @throws MockObjectException if the mock {@link Future} cannot be constructed.
   * @see java.util.concurrent.Future
   * @see #mockFuture(Supplier)
   */
  public static @NotNull <T> Future<T> mockFuture(@Nullable T value) {
    return mockFuture(FunctionUtils.asSupplier(value));
  }

  /**
   * Creates a new mock {@link Future} object initialized with the given {@link Supplier} used to supply
   * the {@link Object value} returned by the mock {@link Future} as the asynchronous, computed result.
   *
   * @param <T> {@link Class type} of {@link Object} returned by the mock {@link Future}.
   * @param value {@link Supplier} used to supply the {@link Object value} returned by the mock {@link Future};
   * must not be {@literal null}.
   * @return a new mock {@link Future} that will return a {@link Object value}
   * supplied by the given {@link Supplier}.
   * @throws IllegalArgumentException if the given {@link Supplier} is {@literal null}.
   * @throws MockObjectException if the mock {@link Future} cannot be constructed.
   * @see java.util.concurrent.Future
   * @see java.util.function.Supplier
   */
  @SuppressWarnings("unchecked")
  public static @NotNull <T> Future<T> mockFuture(@NotNull Supplier<T> value) {

    Assert.notNull(value, "Supplier used to supply the value returned by the mock Future is required");

    try {

      AtomicBoolean cancelled = new AtomicBoolean(false);
      AtomicBoolean done = new AtomicBoolean(false);

      Future<T> mockFuture = mock(Future.class);

      Answer<Boolean> cancelAnswer = invocation -> cancelled.compareAndSet(done.get(), true);

      Answer<T> getAnswer = invocation -> {

        Assert.notInterrupted();

        if (cancelled.get()) {
          throw new CancellationException(String.format("Task [%s] was cancelled", mockFuture));
        }

        T suppliedValue = value.get();

        done.set(true);

        return suppliedValue;
      };

      doAnswer(invocation -> cancelled.get()).when(mockFuture).isCancelled();
      doAnswer(invocation -> done.get()).when(mockFuture).isDone();
      doAnswer(cancelAnswer).when(mockFuture).cancel(anyBoolean());
      doAnswer(getAnswer).when(mockFuture).get();
      doAnswer(getAnswer).when(mockFuture).get(anyLong(), isA(TimeUnit.class));

      return mockFuture;
    }
    catch (Exception cause) {
      throw new MockObjectException(FormatUtils.format("Failed to create mock Future with value [%s]",
        value), cause);
    }
  }
}
