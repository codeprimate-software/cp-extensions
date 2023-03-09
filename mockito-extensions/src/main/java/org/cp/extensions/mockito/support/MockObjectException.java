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

/**
 * {@link RuntimeException} used to indicate a problem with the use of a {@link Object Mock Object}.
 *
 * @author John Blum
 * @see java.lang.RuntimeException
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class MockObjectException extends RuntimeException {

  /**
   * Constructs a new instance of {@link MockObjectException} having no {@link String message}
   * and no {@link Throwable cause}.
   */
  public MockObjectException() { }

  /**
   * Constructs a new instance of {@link MockObjectException} initialized with the given {@link String mesage}
   * to describe this {@link RuntimeException}.
   *
   * @param message {@link String} describing this {@link RuntimeException}.
   */
  public MockObjectException(String message) {
    super(message);
  }

  /**
   * Constructs a new instance of {@link MockObjectException} initialized with the given {@link Throwable cause}
   * used as the underlying reason this {@link RuntimeException} was thrown.
   *
   * @param cause {@link Throwable} used as the underlying reason this {@link RuntimeException} was thrown.
   */
  public MockObjectException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new instance of {@link MockObjectException} initialized with the given {@link String mesage}
   * to describe this {@link RuntimeException} along with a given {@link Throwable cause} for the reason
   * this {@link RuntimeException} was thrown.
   *
   * @param message {@link String} describing this {@link RuntimeException}.
   * @param cause {@link Throwable} used as the underlying reason this {@link RuntimeException} was thrown.
   */
  public MockObjectException(String message, Throwable cause) {
    super(message, cause);
  }
}
