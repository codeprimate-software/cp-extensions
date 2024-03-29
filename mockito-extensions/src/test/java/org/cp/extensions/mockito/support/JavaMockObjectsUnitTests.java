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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.doAnswer;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import org.cp.elements.lang.ObjectUtils;
import org.cp.elements.lang.ThrowableAssertions;

import edu.umd.cs.mtc.MultithreadedTestCase;
import edu.umd.cs.mtc.TestFramework;

/**
 * Unit Tests for {@link JavaMockObjects}.
 *
 * @author John Blum
 * @see org.junit.jupiter.api.Test
 * @see org.cp.extensions.mockito.support.JavaMockObjects
 * @since 0.1.0
 */
public class JavaMockObjectsUnitTests {

  @Test
  public void mockFutureGetReturnsValue() throws Exception {

    Future<Object> mockFuture = JavaMockObjects.mockFuture("test");

    assertThat(mockFuture).isNotNull();
    assertThat(mockFuture).isNotCancelled();
    assertThat(mockFuture).isNotDone();
    assertThat(mockFuture.get()).isEqualTo("test");
    assertThat(mockFuture.cancel(true)).isFalse();
    assertThat(mockFuture).isNotCancelled();
    assertThat(mockFuture).isDone();
    assertThat(mockFuture.get(10L, TimeUnit.MILLISECONDS)).isEqualTo("test");
    assertThat(mockFuture).isNotCancelled();
    assertThat(mockFuture).isDone();
  }

  @Test
  public void mockFutureCancelledSuccessfully() {

    Future<Object> mockFuture = JavaMockObjects.mockFuture("test");

    assertThat(mockFuture).isNotNull();
    assertThat(mockFuture).isNotCancelled();
    assertThat(mockFuture).isNotDone();
    assertThat(mockFuture.cancel(true)).isTrue();
    assertThat(mockFuture).isCancelled();
    assertThat(mockFuture).isDone();
  }

  @Test
  public void mockFutureCannotBeCancelledTwice() {

    Future<Object> mockFuture = JavaMockObjects.mockFuture("test");

    assertThat(mockFuture).isNotNull();
    assertThat(mockFuture).isNotCancelled();
    assertThat(mockFuture).isNotDone();
    assertThat(mockFuture.cancel(false)).isTrue();
    assertThat(mockFuture).isCancelled();
    assertThat(mockFuture).isDone();
    assertThat(mockFuture.cancel(true)).isFalse();
    assertThat(mockFuture).isCancelled();
    assertThat(mockFuture).isDone();
  }

  @Test
  public void mockFutureCannotBeExecutedWhenCancelled() {

    Future<Object> mockFuture = JavaMockObjects.mockFuture("test");

    assertThat(mockFuture).isNotNull();
    assertThat(mockFuture.cancel(true)).isTrue();
    assertThat(mockFuture).isCancelled();
    assertThat(mockFuture).isDone();

    assertThatExceptionOfType(CancellationException.class)
      .isThrownBy(mockFuture::get)
      .withMessage("Task [%s] was cancelled", mockFuture)
      .withNoCause();
  }

  @Test
  public void mockFutureFailureThrowsExecutionException() {

    Supplier<Object> value = () -> { throw new RuntimeException("error"); };

    Future<Object> mockFuture = JavaMockObjects.mockFuture(value);

    assertThat(mockFuture).isNotNull();

    ThrowableAssertions.assertThatThrowableOfType(ExecutionException.class)
      .isThrownBy(args -> mockFuture.get())
      .havingMessage("Execution of task [%s] failed", mockFuture)
      .causedBy(RuntimeException.class)
      .havingMessage("error")
      .withNoCause();

    assertThat(mockFuture).isDone();
    assertThat(mockFuture).isNotCancelled();
    assertThat(mockFuture.cancel(true)).isFalse();
    assertThat(mockFuture).isNotCancelled();
    assertThat(mockFuture).isDone();
  }

  @Test
  public void mockFutureWasInterrupted() throws Throwable {
    TestFramework.runOnce(new InterruptedThreadCallingFutureGetTestCase());
  }

  @Test
  public void mockFutureWithNullSupplier() {

    assertThatIllegalArgumentException()
      .isThrownBy(() -> JavaMockObjects.mockFuture(null))
      .withMessage("Supplier used to supply the value returned by the mock Future as the result is required")
      .withNoCause();
  }

  @Test
  public void customizeMockFunction() throws Exception {

    AtomicBoolean done = new AtomicBoolean(false);
    AtomicBoolean result = new AtomicBoolean(true);

    Future<Boolean> mockFuture = JavaMockObjects.mockFuture(result.get(), future -> {

      ObjectUtils.doOperationSafely(args -> doAnswer(invocation -> {
        done.set(true);
        return result.compareAndSet(true, false);
      }).when(future).get(), result.get());

      doAnswer(invocation -> done.get()).when(future).isDone();

      return future;
    });

    assertThat(mockFuture).isNotNull();
    assertThat(mockFuture).isNotCancelled();
    assertThat(mockFuture).isNotDone();
    assertThat(mockFuture.get()).isTrue();
    assertThat(mockFuture).isNotCancelled();
    assertThat(mockFuture).isDone();
    assertThat(mockFuture.get()).isFalse();
    assertThat(mockFuture).isNotCancelled();
    assertThat(mockFuture).isDone();
    assertThat(mockFuture.get()).isFalse();
    assertThat(mockFuture).isNotCancelled();
    assertThat(mockFuture).isDone();
  }

  @SuppressWarnings("unused")
  public static class InterruptedThreadCallingFutureGetTestCase extends MultithreadedTestCase {

    public void thread1() {

      Thread.currentThread().setName("Interrupted Thread calling Future.get()");

      assertTick(0);

      Future<String> mockFuture = JavaMockObjects.mockFuture("test");

      Thread.currentThread().interrupt();

      assertThat(Thread.currentThread().isInterrupted()).isTrue();
      assertThat(mockFuture).isNotNull();
      assertThat(mockFuture).isNotCancelled();
      assertThat(mockFuture).isNotDone();

      assertThatExceptionOfType(InterruptedException.class)
        .isThrownBy(mockFuture::get)
        .withMessage("Thread [Interrupted Thread calling Future.get()] was interrupted")
        .withNoCause();

      assertThat(mockFuture.cancel(true)).isFalse();
      assertThat(mockFuture).isNotCancelled();
      assertThat(mockFuture).isDone();
    }
  }
}
