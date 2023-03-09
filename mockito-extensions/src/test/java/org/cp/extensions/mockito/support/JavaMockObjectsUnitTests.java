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

import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

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

    Future<String> mockFuture = JavaMockObjects.mockFuture("test");

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
  public void mockFutureWasCancelled() {

    Future<String> mockFuture = JavaMockObjects.mockFuture("test");

    assertThat(mockFuture).isNotNull();
    assertThat(mockFuture).isNotCancelled();
    assertThat(mockFuture).isNotDone();
    assertThat(mockFuture.cancel(true)).isTrue();
    assertThat(mockFuture).isCancelled();
    assertThat(mockFuture).isNotDone();

    assertThatExceptionOfType(CancellationException.class)
      .isThrownBy(mockFuture::get)
      .withMessage("Task [%s] was cancelled", mockFuture)
      .withNoCause();
  }

  @Test
  public void mockFutureWasInterrupted() throws Throwable {
    TestFramework.runOnce(new InterruptedThreadCallingFutureGetTestCase());
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
    }
  }
}
