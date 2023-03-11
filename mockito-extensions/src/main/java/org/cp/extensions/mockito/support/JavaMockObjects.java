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
import static org.mockito.Mockito.withSettings;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

import org.cp.elements.function.FunctionUtils;
import org.cp.elements.lang.Assert;
import org.cp.elements.lang.annotation.NotNull;
import org.cp.elements.lang.annotation.Nullable;
import org.cp.elements.text.FormatUtils;

import org.mockito.quality.Strictness;
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
   * Creates a new mock {@link Future} returning the given {@link Object result} as the asynchronous, computed value.
   *
   * @param <T> {@link Class type} of {@link Object result} returned by the mock {@link Future}.
   * @param result {@link Object value} returned by the mock {@link Future} as the asynchronous, computed result.
   * @return a new mock {@link Future}.
   * @throws MockObjectException if a mock {@link Future} cannot be created.
   * @see java.util.concurrent.Future
   * @see #mockFuture(Supplier)
   */
  public static @NotNull <T> Future<T> mockFuture(@Nullable T result) {
    return mockFuture(FunctionUtils.asSupplier(result));
  }

  /**
   * Creates a new mock {@link Future} returning the {@link Object result} from the given, required {@link Supplier}
   * as the asynchronous, computed value.
   *
   * @param <T> {@link Class type} of {@link Object result} returned by the mock {@link Future}.
   * @param result {@link Supplier} used to supply the {@link Object value} returned by the mock {@link Future}
   * as the asynchronous, computed result; must not be {@literal null}.
   * @return a new mock {@link Future}.
   * @throws IllegalArgumentException if the given {@link Supplier} is {@literal null}.
   * @throws MockObjectException if a mock {@link Future} cannot be created.
   * @see java.util.concurrent.Future
   * @see java.util.function.Supplier
   */
  @SuppressWarnings("unchecked")
  public static @NotNull <T> Future<T> mockFuture(@NotNull Supplier<T> result) {

    Assert.notNull(result,
      "Supplier used to supply the value returned by the mock Future as the result is required");

    try {

      Object lock = new Object();

      Future<T> mockFuture = mock(Future.class, withSettings().strictness(Strictness.LENIENT));

      AtomicBoolean cancelled = new AtomicBoolean(false);
      AtomicBoolean done = new AtomicBoolean(false);

      // A Future can be cancelled only if not completed, it has not already been cancelled, and no exception was thrown
      // while computing the result.
      Answer<Boolean> cancelAnswer = invocation -> {

        synchronized (lock) {
          if (!done.get() && cancelled.compareAndSet(false, true)) {
            done.set(true);
            return true;
          }

          return false;
        }
      };

      // NOTE: This get() Answer is used for both Future.get() and Future.get(timeout, :TimeUnit) operations,
      // and since the result is already known ahead-of-time, the timeout is ignored,
      // and no TimeoutException will be thrown.
      Answer<T> getAnswer = invocation -> {

        synchronized (lock) {
          done.set(true);
        }

        Assert.notInterrupted();

        if (cancelled.get()) {
          throw new CancellationException(String.format("Task [%s] was cancelled", mockFuture));
        }

        try {
          return result.get();
        }
        catch (Throwable cause) {
          String message = FormatUtils.format("Execution of task [%s] failed", mockFuture);
          throw new ExecutionException(message, cause);
        }
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
        result), cause);
    }
  }

  /**
   * Creates a new mock {@link Future} returning the given {@link Object result} as the asynchronous, computed value,
   * customized by the given, required {@link Function}.
   *
   * @param <T> {@link Class type} of {@link Object result} returned by the mock {@link Future}.
   * @param result {@link Object value} returned by the mock {@link Future} as the asynchronous, computed result.
   * @param futureCustomizationFunction {@link Function} used to customize the new mock {@link Future};
   * must not be {@literal null}.
   * @return a new mock {@link Future}.
   * @throws MockObjectException if a mock {@link Future} cannot be created.
   * @see #mockFuture(Supplier, Function)
   * @see java.util.concurrent.Future
   * @see java.util.function.Function
   */
  public static @NotNull <T> Future<T> mockFuture(@Nullable T result,
      @NotNull Function<Future<T>, Future<T>> futureCustomizationFunction) {

    return mockFuture(FunctionUtils.asSupplier(result), futureCustomizationFunction);
  }

  /**
   * Creates a new mock {@link Future} returning the {@link Object result} from the given, required {@link Supplier}
   * as the asynchronous, computed value, customized by the given, required {@link Function}.
   *
   * @param <T> {@link Class type} of {@link Object result} returned by the mock {@link Future}.
   * @param result {@link Supplier} used to supply the {@link Object value} returned by the mock {@link Future}
   * as the asynchronous, computed result; must not be {@literal null}.
   * @param futureCustomizationFunction {@link Function} used to customize the new mock {@link Future};
   * must not be {@literal null}.
   * @return a new mock {@link Future}.
   * @throws IllegalArgumentException if the given {@link Supplier} is {@literal null}.
   * @throws MockObjectException if a mock {@link Future} cannot be created.
   * @see java.util.concurrent.Future
   * @see java.util.function.Function
   * @see java.util.function.Supplier
   * @see #mockFuture(Supplier)
   */
  public static @NotNull <T> Future<T> mockFuture(@NotNull Supplier<T> result,
      @NotNull Function<Future<T>, Future<T>> futureCustomizationFunction) {

    return FunctionUtils.nullSafeFunction(futureCustomizationFunction).apply(mockFuture(result));
  }
}
