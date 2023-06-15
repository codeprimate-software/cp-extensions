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
package org.cp.extensions.spring.test.context;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import org.springframework.test.context.ContextCustomizer;

/**
 * Unit Tests for {@link DependencyOfContextCustomizerFactory}.
 *
 * @author John Blum
 * @see org.junit.jupiter.api.Test
 * @see org.mockito.Mockito
 * @see org.cp.extensions.spring.test.context.DependencyOfContextCustomizerFactory
 * @since 1.0.0
 */
public class DependencyOfContextCustomizerFactoryUnitTests {

	@Test
	public void createContextCustomizerIsSuccessful() {

		Class<?> testClass = DependencyOfContextCustomizerFactoryUnitTests.class;

		ContextCustomizer contextCustomizer = new DependencyOfContextCustomizerFactory()
			.createContextCustomizer(testClass, Collections.emptyList());

		assertThat(contextCustomizer).isInstanceOf(DependencyOfContextCustomizer.class);
	}
}
