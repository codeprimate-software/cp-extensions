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
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.cp.elements.lang.RuntimeExceptionsFactory.newIllegalStateException;
import static org.cp.elements.lang.ThrowableAssertions.assertThatThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.cp.elements.lang.ThrowableOperation;
import org.cp.elements.util.stream.StreamUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.mockito.stubbing.Answer;

/**
 * Unit Tests for {@link ExtensionExceptionHandlingProcessor}.
 *
 * @author John Blum
 * @see org.junit.jupiter.api.Test
 * @see org.mockito.Mockito
 * @see org.cp.extensions.junit.jupiter.api.extension.ExtensionExceptionHandlingProcessor
 * @since 0.1.0
 */
public class ExtensionExceptionHandlingProcessorUnitTests {

  private void configureExtensionExceptionHandlers(Stream<ExtensionExceptionHandler> mockExceptionHandlers) {

    Answer<Boolean> rethrowAnswer = invocation -> {
      throw UnhandledExtensionException.wrap(invocation.getArgument(1, Throwable.class)).build();
    };

    StreamUtils.nullSafeStream(mockExceptionHandlers)
      .filter(Objects::nonNull)
      .forEach(mockExceptionHandler -> {
        doAnswer(rethrowAnswer).when(mockExceptionHandler).handle(any(ExtensionContext.class), any(Throwable.class));
        doCallRealMethod().when(mockExceptionHandler).compose(any());
      });
  }

  private void verifyExtensionExceptionHandlers(ExtensionContext extensionContext, Throwable cause,
      Stream<ExtensionExceptionHandler> mockExceptionHandlers) {

    List<ExtensionExceptionHandler> mockExceptionHandlerList = StreamUtils.nullSafeStream(mockExceptionHandlers)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());

    AtomicInteger count = new AtomicInteger(mockExceptionHandlerList.size());

    mockExceptionHandlerList.forEach(mockExceptionHandler -> {
      if (count.decrementAndGet() > 0) {
        verify(mockExceptionHandler, times(1))
          .compose(eq(mockExceptionHandlerList.get(count.get())));
      }
      verify(mockExceptionHandler, times(1)).handle(eq(extensionContext), eq(cause));
      verifyNoMoreInteractions(mockExceptionHandler);
    });
  }

  private void testExtensionExceptionHandlingProcessorOfExtensionExceptionHandlers(
      Function<Stream<ExtensionExceptionHandler>, ExtensionExceptionHandlingProcessor> toProcessorFunction) {

    ExtensionExceptionHandler[] mockExceptionHandlers = {
      null,
      mock(ExtensionExceptionHandler.class, "ONE"),
      null, null,
      mock(ExtensionExceptionHandler.class, "TWO"),
      null
    };

    configureExtensionExceptionHandlers(Stream.of(mockExceptionHandlers));

    ExtensionContext mockExtensionContext = mock(ExtensionContext.class);

    ExtensionExceptionHandlingProcessor processor = toProcessorFunction.apply(Stream.of(mockExceptionHandlers));

    assertThat(processor).isNotNull();
    assertThat(processor.getExceptionHandler()).isPresent();
    assertThat(processor.getExceptionHandler().orElse(null))
      .isNotEqualTo(ExtensionExceptionHandler.RETHROW_CAUSE_AS_UNHANDLED_EXTENSION_EXCEPTION);

    RuntimeException cause = new RuntimeException("TEST");

    assertThatThrowableOfType(UnhandledExtensionException.class)
      .isThrownBy(ThrowableOperation.from(args -> processor.getExceptionHandler()
        .orElseThrow(() -> newIllegalStateException("ExtensionExceptionHandler was null; O.o"))
        .handle(mockExtensionContext, cause)))
      .causedBy(RuntimeException.class)
      .havingMessage("TEST")
      .withNoCause();

    verifyExtensionExceptionHandlers(mockExtensionContext, cause, Stream.of(mockExceptionHandlers));
    verifyNoInteractions(mockExtensionContext);
  }

  @Test
  public void composeProcessorWithArray() {
    testExtensionExceptionHandlingProcessorOfExtensionExceptionHandlers(mockExceptionHandlers ->
      ExtensionExceptionHandlingProcessor.of(mockExceptionHandlers.toArray(ExtensionExceptionHandler[]::new)));
  }

  @Test
  public void composeProcessorWithEmptyArray() {

    ExtensionExceptionHandlingProcessor processor = ExtensionExceptionHandlingProcessor.of();

    assertThat(processor).isNotNull();
    assertThat(processor.getExceptionHandler().orElse(null))
      .isEqualTo(ExtensionExceptionHandler.RETHROW_CAUSE_AS_UNHANDLED_EXTENSION_EXCEPTION);
  }

  @Test
  public void composeProcessorWithNullArray() {

    ExtensionExceptionHandlingProcessor processor =
      ExtensionExceptionHandlingProcessor.of((ExtensionExceptionHandler[]) null);

    assertThat(processor).isNotNull();
    assertThat(processor.getExceptionHandler().orElse(null))
      .isEqualTo(ExtensionExceptionHandler.RETHROW_CAUSE_AS_UNHANDLED_EXTENSION_EXCEPTION);
  }

  @Test
  public void composeProcessorWithSingleElementArray() {

    ExtensionExceptionHandler mockExceptionHandler = mock(ExtensionExceptionHandler.class);

    configureExtensionExceptionHandlers(Stream.of(mockExceptionHandler));

    ExtensionExceptionHandlingProcessor processor = ExtensionExceptionHandlingProcessor.of(mockExceptionHandler);

    assertThat(processor).isNotNull();
    assertThat(processor.getExceptionHandler()).isPresent();
    assertThat(processor.getExceptionHandler().orElse(null))
      .isNotEqualTo(ExtensionExceptionHandler.RETHROW_CAUSE_AS_UNHANDLED_EXTENSION_EXCEPTION)
      .isNotEqualTo(mockExceptionHandler);

    verify(mockExceptionHandler, times(1))
      .compose(eq(ExtensionExceptionHandler.RETHROW_CAUSE_AS_UNHANDLED_EXTENSION_EXCEPTION));

    verifyNoMoreInteractions(mockExceptionHandler);
  }

  @Test
  public void composeProcessorWithSingleNullElementArray() {

    ExtensionExceptionHandlingProcessor processor = ExtensionExceptionHandlingProcessor.of(new ExtensionExceptionHandler[] { null });

    assertThat(processor).isNotNull();
    assertThat(processor.getExceptionHandler()).isPresent();
    assertThat(processor.getExceptionHandler().orElse(null))
      .isEqualTo(ExtensionExceptionHandler.RETHROW_CAUSE_AS_UNHANDLED_EXTENSION_EXCEPTION);
  }

  @Test
  public void composeProcessorWithIterable() {
    testExtensionExceptionHandlingProcessorOfExtensionExceptionHandlers(mockExceptionHandlers ->
      ExtensionExceptionHandlingProcessor.of(mockExceptionHandlers.collect(Collectors.toList())));
  }

  @Test
  public void composeProcessorWithEmptyIterable() {

    ExtensionExceptionHandlingProcessor processor =
      ExtensionExceptionHandlingProcessor.of(Collections::emptyIterator);

    assertThat(processor).isNotNull();
    assertThat(processor.getExceptionHandler().orElse(null))
      .isEqualTo(ExtensionExceptionHandler.RETHROW_CAUSE_AS_UNHANDLED_EXTENSION_EXCEPTION);
  }

  @Test
  public void composeProcessorWithNullIterable() {

    ExtensionExceptionHandlingProcessor processor =
      ExtensionExceptionHandlingProcessor.of((Iterable<ExtensionExceptionHandler>) null);

    assertThat(processor).isNotNull();
    assertThat(processor.getExceptionHandler().orElse(null))
      .isEqualTo(ExtensionExceptionHandler.RETHROW_CAUSE_AS_UNHANDLED_EXTENSION_EXCEPTION);
  }

  @Test
  public void processesTestInstancePostProcessor() throws Exception {

    ExtensionContext mockExtensionContext = mock(ExtensionContext.class);

    ExtensionExceptionHandler mockExceptionHandler = mock(ExtensionExceptionHandler.class);

    TestInstancePostProcessor mockTestInstancePostProcessor = mock(TestInstancePostProcessor.class);

    configureExtensionExceptionHandlers(Stream.of(mockExceptionHandler));

    ExtensionExceptionHandlingProcessor processor = ExtensionExceptionHandlingProcessor.of(mockExceptionHandler);

    assertThat(processor).isNotNull();
    assertThat(processor.getExceptionHandler()).isPresent();

    processor.process(this, mockExtensionContext, mockTestInstancePostProcessor);

    verify(mockExceptionHandler, times(1))
      .compose(eq(ExtensionExceptionHandler.RETHROW_CAUSE_AS_UNHANDLED_EXTENSION_EXCEPTION));

    verify(mockExceptionHandler, never()).handle(any(), any());

    verify(mockTestInstancePostProcessor, times(1))
      .postProcessTestInstance(eq(this), eq(mockExtensionContext));

    verifyNoMoreInteractions(mockExceptionHandler, mockTestInstancePostProcessor);
    verifyNoInteractions(mockExtensionContext);
  }

  @Test
  @SuppressWarnings("all")
  public void processingTestInstancePostProcessorHandlesException() throws Exception {

    ExtensionContext mockExtensionContext = mock(ExtensionContext.class);

    ExtensionExceptionHandler mockExceptionHandler = mock(ExtensionExceptionHandler.class);

    RuntimeException cause = new IllegalStateException("TEST");

    TestInstancePostProcessor mockTestInstancePostProcessor = mock(TestInstancePostProcessor.class);

    doCallRealMethod().when(mockExceptionHandler).compose(any(ExtensionExceptionHandler.class));
    doThrow(cause).when(mockTestInstancePostProcessor).postProcessTestInstance(any(), any());

    ExtensionExceptionHandlingProcessor processor = ExtensionExceptionHandlingProcessor.of(mockExceptionHandler);

    assertThat(processor).isNotNull();
    assertThat(processor.getExceptionHandler()).isPresent();

    processor.process(this, mockExtensionContext, mockTestInstancePostProcessor);

    verify(mockExceptionHandler, times(1))
      .compose(eq(ExtensionExceptionHandler.RETHROW_CAUSE_AS_UNHANDLED_EXTENSION_EXCEPTION));

    verify(mockExceptionHandler, times(1)).handle(eq(mockExtensionContext), eq(cause));

    verify(mockTestInstancePostProcessor, times(1))
      .postProcessTestInstance(eq(this), eq(mockExtensionContext));

    verifyNoMoreInteractions(mockExceptionHandler, mockTestInstancePostProcessor);
    verifyNoInteractions(mockExtensionContext);
  }

  @Test
  @SuppressWarnings("all")
  public void processingTestInstancePostProcessorWithNoExceptionHandlersThrowsException() throws Exception {

    ExtensionContext mockExtensionContext = mock(ExtensionContext.class);

    RuntimeException cause = new IllegalStateException("TEST");

    TestInstancePostProcessor mockTestInstancePostProcessor = mock(TestInstancePostProcessor.class);

    doThrow(cause).when(mockTestInstancePostProcessor).postProcessTestInstance(any(), any());

    ExtensionExceptionHandlingProcessor processor = ExtensionExceptionHandlingProcessor.of();

    assertThat(processor).isNotNull();
    assertThat(processor.getExceptionHandler()).isPresent();

    assertThatThrowableOfType(UnhandledExtensionException.class)
      .isThrownBy(ThrowableOperation.from(args ->
        processor.process(this, mockExtensionContext, mockTestInstancePostProcessor)))
      .causedBy(IllegalStateException.class)
      .havingMessage("TEST")
      .withNoCause();

    verify(mockTestInstancePostProcessor, times(1))
      .postProcessTestInstance(eq(this), eq(mockExtensionContext));

    verifyNoInteractions(mockExtensionContext);
  }

  @Test
  public void postProcessNullTestInstancePostProcessor() {

    assertThatIllegalArgumentException()
      .isThrownBy(() -> ExtensionExceptionHandlingProcessor.of()
        .process(this, mock(ExtensionContext.class), null))
      .withMessage("TestInstancePostProcessor is required")
      .withNoCause();
  }
}
