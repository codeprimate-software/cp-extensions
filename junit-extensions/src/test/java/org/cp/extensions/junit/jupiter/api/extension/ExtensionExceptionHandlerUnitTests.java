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
import static org.cp.elements.lang.RuntimeExceptionsFactory.newIllegalStateException;
import static org.cp.elements.lang.ThrowableAssertions.assertThatIllegalArgumentException;
import static org.cp.elements.lang.ThrowableAssertions.assertThatIllegalStateException;
import static org.cp.elements.lang.ThrowableAssertions.assertThatThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.cp.elements.lang.ThrowableOperation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.stubbing.Answer;

/**
 * Unit Tests for {@link ExtensionExceptionHandler}.
 *
 * @author John Blum
 * @see org.junit.jupiter.api.Test
 * @see org.mockito.Mockito
 * @see org.cp.extensions.junit.jupiter.api.extension.ExtensionExceptionHandler
 * @since 0.1.0
 */
public class ExtensionExceptionHandlerUnitTests {

  @Test
  public void rethrowCauseAsUnhandledExtensionExceptionHandlerIsCorrect() {

    ExtensionContext mockExtensionContext = mock(ExtensionContext.class);

    ExtensionExceptionHandler handler = ExtensionExceptionHandler.RETHROW_CAUSE_AS_UNHANDLED_EXTENSION_EXCEPTION;

    assertThat(handler).isNotNull();

    assertThatThrowableOfType(UnhandledExtensionException.class)
      .isThrownBy(args -> {
        handler.handle(mockExtensionContext, new RuntimeException("TEST"));
        return false;
      })
      .havingMessage("Exception [%s] was not handled", RuntimeException.class.getName())
      .causedBy(RuntimeException.class)
      .havingMessage("TEST")
      .withNoCause();

    verifyNoInteractions(mockExtensionContext);
  }

  @Test
  public void rethrowNullCauseAsUnhandledExtensionExceptionHandlerIsNullSafe() {

    ExtensionContext mockExtensionContext = mock(ExtensionContext.class);

    ExtensionExceptionHandler handler = ExtensionExceptionHandler.RETHROW_CAUSE_AS_UNHANDLED_EXTENSION_EXCEPTION;

    assertThat(handler).isNotNull();

    assertThatThrowableOfType(UnhandledExtensionException.class)
      .isThrownBy(args -> {
        handler.handle(mockExtensionContext, null);
        return false;
      })
      .havingMessage("Exception [null] was not handled")
      .withNoCause();

    verifyNoInteractions(mockExtensionContext);
  }

  @Test
  public void composeWithExtensionExceptionHandler() {

    ExtensionExceptionHandler mockHandlerOne = mock(ExtensionExceptionHandler.class);
    ExtensionExceptionHandler mockHandlerTwo = mock(ExtensionExceptionHandler.class);

    doCallRealMethod().when(mockHandlerOne).compose(any());

    ExtensionExceptionHandler composedHandler = mockHandlerOne.compose(mockHandlerTwo);

    assertThat(composedHandler).isNotNull();
    assertThat(composedHandler).isNotEqualTo(mockHandlerOne);
    assertThat(composedHandler).isNotEqualTo(mockHandlerTwo);

    verify(mockHandlerOne, times(1)).compose(eq(mockHandlerTwo));
    verifyNoMoreInteractions(mockHandlerOne);
    verifyNoInteractions(mockHandlerTwo);
  }

  @Test
  public void composeWithNull() {

    ExtensionExceptionHandler mockHandler = mock(ExtensionExceptionHandler.class);

    doCallRealMethod().when(mockHandler).compose(any());

    assertThatIllegalArgumentException()
      .isThrownBy(args -> mockHandler.compose(null))
      .havingMessage("ExtensionExceptionHandler is required")
      .withNoCause();

    verify(mockHandler, times(1)).compose(eq(null));
    verifyNoMoreInteractions(mockHandler);
  }

  @Test
  @SuppressWarnings("all")
  public void compositionHandlesCauseWithFirstHandler() {

    ExtensionContext mockExtensionContext = mock(ExtensionContext.class);

    ExtensionExceptionHandler mockHandlerOne = mock(ExtensionExceptionHandler.class);
    ExtensionExceptionHandler mockHandlerTwo = mock(ExtensionExceptionHandler.class);

    doCallRealMethod().when(mockHandlerOne).compose(any());

    RuntimeException cause = new RuntimeException("TEST");

    ExtensionExceptionHandler composedHandler = mockHandlerOne.compose(mockHandlerTwo);

    assertThat(composedHandler).isNotNull();

    composedHandler.handle(mockExtensionContext, cause);

    verify(mockHandlerOne, times(1)).compose(eq(mockHandlerTwo));
    verify(mockHandlerOne, times(1)).handle(eq(mockExtensionContext), eq(cause));
    verifyNoInteractions(mockExtensionContext, mockHandlerTwo);
  }

  @Test
  @SuppressWarnings("all")
  public void compositionHandlesCauseWithSecondHandler() {

    ExtensionContext mockExtensionContext = mock(ExtensionContext.class);

    ExtensionExceptionHandler mockHandlerOne = mock(ExtensionExceptionHandler.class);
    ExtensionExceptionHandler mockHandlerTwo = mock(ExtensionExceptionHandler.class);

    doCallRealMethod().when(mockHandlerOne).compose(any());

    doAnswer(invocation -> {
      throw UnhandledExtensionException.wrap(invocation.getArgument(1, Throwable.class)).build();
    }).when(mockHandlerOne).handle(any(), any());

    RuntimeException cause = new RuntimeException("TEST");

    ExtensionExceptionHandler composedHandler = mockHandlerOne.compose(mockHandlerTwo);

    assertThat(composedHandler).isNotNull();

    composedHandler.handle(mockExtensionContext, cause);

    verify(mockHandlerOne, times(1)).compose(eq(mockHandlerTwo));
    verify(mockHandlerOne, times(1)).handle(eq(mockExtensionContext), eq(cause));
    verify(mockHandlerTwo, times(1)).handle(eq(mockExtensionContext), eq(cause));
    verifyNoMoreInteractions(mockHandlerOne, mockHandlerTwo);
    verifyNoInteractions(mockExtensionContext);
  }

  @Test
  @SuppressWarnings("all")
  public void compositionIsExhausted() {

    Answer answer = invocation -> {
      throw UnhandledExtensionException.wrap(invocation.getArgument(1, Throwable.class)).build();
    };

    ExtensionContext mockExtensionContext = mock(ExtensionContext.class);

    ExtensionExceptionHandler mockHandlerOne = mock(ExtensionExceptionHandler.class);
    ExtensionExceptionHandler mockHandlerTwo = mock(ExtensionExceptionHandler.class);

    doCallRealMethod().when(mockHandlerOne).compose(any());
    doAnswer(answer).when(mockHandlerOne).handle(any(), any());
    doAnswer(answer).when(mockHandlerTwo).handle(any(), any());

    RuntimeException cause = new RuntimeException("TEST");

    ExtensionExceptionHandler composedHandler = mockHandlerOne.compose(mockHandlerTwo);

    assertThat(composedHandler).isNotNull();

    assertThatThrowableOfType(UnhandledExtensionException.class)
      .isThrownBy(ThrowableOperation.from(args -> composedHandler.handle(mockExtensionContext, cause)))
      .causedBy(RuntimeException.class)
      .havingMessage("TEST")
      .withNoCause();

    verify(mockHandlerOne, times(1)).compose(eq(mockHandlerTwo));
    verify(mockHandlerOne, times(1)).handle(eq(mockExtensionContext), eq(cause));
    verify(mockHandlerTwo, times(1)).handle(eq(mockExtensionContext), eq(cause));
    verifyNoMoreInteractions(mockHandlerOne, mockHandlerTwo);
    verifyNoInteractions(mockExtensionContext);
  }

  @Test
  public void compositionThrowsUncaughtException() {

    ExtensionContext mockExtensionContext = mock(ExtensionContext.class);

    ExtensionExceptionHandler mockHandlerOne = mock(ExtensionExceptionHandler.class);
    ExtensionExceptionHandler mockHandlerTwo = mock(ExtensionExceptionHandler.class);

    doCallRealMethod().when(mockHandlerOne).compose(any());

    doAnswer(invocation -> {
      throw newIllegalStateException(invocation.getArgument(1, Throwable.class), "MOCK");
    }).when(mockHandlerOne).handle(any(), any());

    RuntimeException cause = new RuntimeException("TEST");

    ExtensionExceptionHandler composedHandler = mockHandlerOne.compose(mockHandlerTwo);

    assertThat(composedHandler).isNotNull();

    assertThatIllegalStateException()
      .isThrownBy(ThrowableOperation.from(args -> composedHandler.handle(mockExtensionContext, cause)))
      .havingMessage("MOCK")
      .causedBy(RuntimeException.class)
      .havingMessage("TEST")
      .withNoCause();

    verify(mockHandlerOne, times(1)).compose(eq(mockHandlerTwo));
    verify(mockHandlerOne, times(1)).handle(eq(mockExtensionContext), eq(cause));
    verifyNoInteractions(mockExtensionContext, mockHandlerTwo);
  }
}
