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
package org.cp.extensions.spring.context.annotation;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.cp.elements.lang.Assert;
import org.cp.elements.lang.StringUtils;
import org.cp.elements.util.ArrayUtils;
import org.cp.extensions.spring.support.SpringSupport;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Spring {@link BeanFactoryPostProcessor} implementation used to post process {@link BeanDefinition BeanDefinitions}
 * declared in the Spring container, annotated with the {@link DependencyOf} annotation.
 *
 * @author John Blum
 * @see java.lang.annotation.Annotation
 * @see org.cp.extensions.spring.context.annotation.DependencyOf
 * @see org.springframework.beans.factory.config.BeanDefinition
 * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor
 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 * @see org.springframework.context.annotation.DependsOn
 * @since 0.1.0
 */
@SuppressWarnings("unused")
public class DependencyOfBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	public static final DependencyOfBeanFactoryPostProcessor INSTANCE = new DependencyOfBeanFactoryPostProcessor();

	protected static final Class<? extends Annotation> DEPENDENCY_OF_TYPE = DependencyOf.class;

	protected static final String VALUE_ATTRIBUTE_NAME = "value";

	private static final Map<ConfigurableApplicationContext, DependencyOfBeanFactoryPostProcessor> registrations =
		new ConcurrentHashMap<>();

	/**
	 * Null-safe factory method used to register an instance of the {@link DependencyOfBeanFactoryPostProcessor} with
	 * the given, required {@link ConfigurableApplicationContext}.
	 *
	 * The {@code registerWith(..)} factory method ensures only a single registration of
	 * the {@link DependencyOfBeanFactoryPostProcessor} per {@link ConfigurableApplicationContext}.
	 *
	 * @param <T> {@link Class type} of {@link ConfigurableApplicationContext}.
	 * @param applicationContext {@link ConfigurableApplicationContext} on which to register an instance of
	 * the {@link DependencyOfBeanFactoryPostProcessor}; must not be {@literal null}.
	 * @return the given {@link ConfigurableApplicationContext}.
	 * @throws IllegalArgumentException if {@link ConfigurableApplicationContext} is {@literal null}.
	 * @see org.springframework.context.ConfigurableApplicationContext
	 */
	public static @NonNull <T extends ConfigurableApplicationContext> T registerWith(@NonNull T applicationContext) {

		Assert.notNull(applicationContext, "ApplicationContext is required");

		registrations.computeIfAbsent(applicationContext, it -> {
			it.addBeanFactoryPostProcessor(INSTANCE);
			return INSTANCE;
		});

		return applicationContext;
	}

	/**
	 * Post processes the {@link ConfigurableListableBeanFactory} by searching for managed beans that claim (declare)
	 * to be a {@link DependencyOf dependency of} other beans declared and managed inside the Spring container.
	 *
	 * Out-of-the-box, the Spring container does not support this configuration arrangement, and therefore, requires
	 * additional processing to set up this inverse {@link DependsOn depends on} relationship.
	 *
	 * @param beanFactory {@link ConfigurableListableBeanFactory} to post process.
	 * @throws BeansException if an exception occurs while processing the {@link ConfigurableListableBeanFactory}.
	 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory
	 */
	@Override
	public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {

		String[] dependencyOfAnnotatedBeanNames =
			ArrayUtils.nullSafeArray(beanFactory.getBeanNamesForAnnotation(DEPENDENCY_OF_TYPE), String.class);

		for (String beanName : dependencyOfAnnotatedBeanNames) {

			Annotation dependencyOf = beanFactory.findAnnotationOnBean(beanName, DEPENDENCY_OF_TYPE);

			Optional.ofNullable(dependencyOf)
				.map(this::getAnnotationAttributes)
				.map(this::getValueAttribute)
				.ifPresent(dependentBeanNames -> {
					for (String dependentBeanName : dependentBeanNames) {
						Optional.ofNullable(dependentBeanName)
							.filter(StringUtils::hasText)
							.map(beanFactory::getBeanDefinition)
							.ifPresent(dependentBeanDefinition ->
								SpringSupport.addDependsOn(dependentBeanDefinition, beanName));
					}
				});
		}
	}

	@SuppressWarnings("all")
	private @Nullable AnnotationAttributes getAnnotationAttributes(@NonNull Annotation annotation) {

		return annotation != null
			? AnnotationAttributes.fromMap(AnnotationUtils.getAnnotationAttributes(annotation))
			: null;
	}

	@SuppressWarnings("all")
	private @Nullable String[] getValueAttribute(@NonNull AnnotationAttributes annotationAttributes) {

		return annotationAttributes != null
			? annotationAttributes.getStringArray(VALUE_ATTRIBUTE_NAME)
			: null;
	}
}
