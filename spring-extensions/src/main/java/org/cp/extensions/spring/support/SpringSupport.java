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
package org.cp.extensions.spring.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.cp.elements.lang.Assert;
import org.cp.elements.lang.annotation.NullSafe;
import org.cp.elements.util.ArrayUtils;
import org.cp.elements.util.CollectionUtils;

import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Abstract utility class encapsulating utility methods used for working with Spring Framework infrastructure classes.
 *
 * @author John Blum
 * @since 0.1.0
 */
@SuppressWarnings("unused")
public abstract class SpringSupport {

	/**
	 * Adds an array of bean dependencies by {@link String name} to the given, required {@link BeanDefinition}.
	 *
	 * @param beanDefinition {@link BeanDefinition} to add the bean dependencies; must not be {@literal null}.
	 * @param beanNames array of {@link String bean names} for which the {@link BeanDefinition} depends on,
	 * or will have a dependency.
	 * @return the given {@link BeanDefinition}.
	 * @throws IllegalArgumentException if {@link BeanDefinition} is {@literal null}.
	 * @see org.springframework.beans.factory.config.BeanDefinition
	 */
	public static @NonNull BeanDefinition addDependsOn(@NonNull BeanDefinition beanDefinition, String... beanNames) {

		Assert.notNull(beanDefinition, "BeanDefinition is required");

		List<String> dependsOnList = new ArrayList<>();

		Collections.addAll(dependsOnList, ArrayUtils.nullSafeArray(beanDefinition.getDependsOn(), String.class));
		Collections.addAll(dependsOnList, ArrayUtils.nullSafeArray(beanNames, String.class));

		if (CollectionUtils.isNotEmpty(dependsOnList)) {
			beanDefinition.setDependsOn(dependsOnList.toArray(new String[0]));
		}

		return beanDefinition;
	}

	/**
	 * Dereferences the given, required bean by {@link String name}.
	 *
	 * @param beanName {@link String} containing the {@literal name} of the bean to dereference;
	 * must not be {@literal null} or {@literal empty}.
	 * @throws IllegalArgumentException if {@link String bean name} is {@literal null} or {@literal empty}.
	 * @return the dereferenced {@link String bean name}.
	 */
	public static @NonNull String dereferenceBean(@NonNull String beanName) {

		Assert.hasText(beanName, "Bean name [%s] is required", beanName);

		return String.format("%1$s%2$s", BeanFactory.FACTORY_BEAN_PREFIX, beanName);
	}

	/**
	 * Null-safe operation used to get the {@link Integer order} of the given {@link Object}.
	 *
	 * This method handles either the case when the given {@link Object} implements Spring's {@link Ordered} interface
	 * or when the given {@link Object} is annotated with Spring's {@link Order @Order} annotation.
	 *
	 * If the {@link Object} reference is {@literal null} or the given {@link Object} is not ordered,
	 * than {@literal null} is returned.
	 *
	 * @param target {@link Object} to evaluate; may be {@literal null}.
	 * @return the {@link Integer order} of the given {@link Object}; may be {@literal null}.
	 * @see org.springframework.core.annotation.Order
	 * @see org.springframework.core.Ordered
	 */
	@NullSafe
	public static @Nullable Integer getOrder(@Nullable Object target) {

		return target instanceof Ordered ? Integer.valueOf(((Ordered) target).getOrder())
			: target != null ? OrderUtils.getOrder(target.getClass())
			: null;
	}

	/**
	 * Gets an {@link Optional} {@link Object value} from the given {@link BeanDefinition}
	 * for the given {@link String named property}.
	 *
	 * @param beanDefinition {@link BeanDefinition} from which to get
	 * the {@link String named property} {@link Object value}.
	 * @param propertyName {@link String} containing the {@literal name} of the property.
	 * @return an {@link Optional} {@link Object value} for given {@link String named property}
	 * from the given {@link BeanDefinition}
	 * @see org.springframework.beans.factory.config.BeanDefinition
	 * @see #setPropertyReference(BeanDefinition, String, String)
	 * @see #setPropertyValue(BeanDefinition, String, Object)
	 */
	@NullSafe
	@SuppressWarnings("all")
	public static Optional<Object> getPropertyValue(@Nullable BeanDefinition beanDefinition,
			@Nullable String propertyName) {

		return Optional.ofNullable(beanDefinition)
			.map(BeanDefinition::getPropertyValues)
			.map(propertyValues -> propertyValues.getPropertyValue(propertyName))
			.map(PropertyValue::getValue);
	}

	/**
	 * Sets a reference to the given, required bean by {@link String name} for the given, required
	 * {@link String named property} of the given, required {@link BeanDefinition}.
	 *
	 * @param beanDefinition {@link BeanDefinition} containing the property to set; must not be {@literal null}.
	 * @param propertyName {@link String} containing the {@literal name} of the property to set;
	 * must not be {@literal null}.
	 * @param beanName {@link String} contain the {@link String name} of the bean reference used to set the property;
	 * must not be {@literal null}.
	 * @return the given {@link BeanDefinition}.
	 * @see org.springframework.beans.factory.config.RuntimeBeanReference
	 * @see org.springframework.beans.factory.config.BeanDefinition
	 */
	public static @NonNull BeanDefinition setPropertyReference(@NonNull BeanDefinition beanDefinition,
		@NonNull String propertyName, @NonNull String beanName) {

		beanDefinition.getPropertyValues().addPropertyValue(propertyName, new RuntimeBeanReference(beanName));

		return beanDefinition;
	}

	/**
	 * Sets the given {@link Object value} for the given, required property with {@link String name}
	 * on the given, required {@link BeanDefinition}.
	 *
	 * @param beanDefinition {@link BeanDefinition} containing the property to set; must not be {@literal null}.
	 * @param propertyName {@link String} containing the {@literal name} of the property to set;
	 * must not be {@literal null}.
	 * @param propertyValue {@link Object value} to set for the property of the bean.
	 * @return the given {@link BeanDefinition}.
	 * @see org.springframework.beans.factory.config.BeanDefinition
	 * @see #getPropertyValue(BeanDefinition, String)
	 */
	@SuppressWarnings("all")
	public static @NonNull BeanDefinition setPropertyValue(@NonNull BeanDefinition beanDefinition,
			@NonNull String propertyName, @Nullable Object propertyValue) {

		beanDefinition.getPropertyValues().addPropertyValue(propertyName, propertyValue);

		return beanDefinition;
	}
}
