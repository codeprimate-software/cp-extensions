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

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

/**
 * Spring {@link ContextCustomizerFactory} implementation used to create a new instance of
 * the {@link DependencyOfContextCustomizer}.
 *
 * @author John Blum
 * @see org.cp.extensions.spring.test.context.DependencyOfContextCustomizer
 * @see org.springframework.test.context.ContextCustomizerFactory
 * @see org.springframework.test.context.ContextCustomizer
 * @since 0.1.0
 */
@SuppressWarnings("unused")
public class DependencyOfContextCustomizerFactory implements ContextCustomizerFactory {

	/**
	 * Constructs and returns a new instance of {@link DependencyOfContextCustomizer}.
	 *
	 * @param testClass {@link Class type} of the test.
	 * @param configurationAttributes {@link List} of {@link ContextConfigurationAttributes}
	 * encapsulating the configuration attributes declared in the {@link ContextConfiguration} annotation
	 * annotated on the {@link Class test class}.
	 * @see org.cp.extensions.spring.test.context.DependencyOfContextCustomizer
	 * @see org.springframework.test.context.ContextCustomizer
	 */
	@Override
	public @NonNull ContextCustomizer createContextCustomizer(Class<?> testClass,
			List<ContextConfigurationAttributes> configurationAttributes) {

		return new DependencyOfContextCustomizer();
	}
}
