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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.function.Consumer;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Unit Tests for {@link SpringSupport}.
 *
 * @author John Blum
 * @see org.junit.jupiter.api.Test
 * @see org.mockito.Mockito
 * @see org.cp.extensions.spring.support.SpringSupport
 * @since 0.1.0
 */
public class SpringSupportUnitTests {

	@Test
	public void addDependsOnAppendsToBeanDependencies() {

		BeanDefinition mockBeanDefinition = mock(BeanDefinition.class);

		doReturn(new String[] { "beanie", "mrBean" }).when(mockBeanDefinition).getDependsOn();

		SpringSupport.addDependsOn(mockBeanDefinition, "beanOne", "beanTwo");

		verify(mockBeanDefinition, times(1)).getDependsOn();
		verify(mockBeanDefinition, times(1))
			.setDependsOn("beanie", "mrBean", "beanOne", "beanTwo");
		verifyNoMoreInteractions(mockBeanDefinition);
	}

	@Test
	public void addDependsOnIsNullSafe() {

		BeanDefinition mockBeanDefinition = mock(BeanDefinition.class);

		doReturn(null).when(mockBeanDefinition).getDependsOn();

		SpringSupport.addDependsOn(mockBeanDefinition, (String[]) null);

		verify(mockBeanDefinition, times(1)).getDependsOn();
		verifyNoMoreInteractions(mockBeanDefinition);
	}

	@Test
	public void addDependsOnSetsBeanDependencies() {

		BeanDefinition mockBeanDefinition = mock(BeanDefinition.class);

		doReturn(new String[0]).when(mockBeanDefinition).getDependsOn();

		SpringSupport.addDependsOn(mockBeanDefinition, "beanOne", "beanTwo");

		verify(mockBeanDefinition, times(1)).getDependsOn();
		verify(mockBeanDefinition, times(1)).setDependsOn("beanOne", "beanTwo");
		verifyNoMoreInteractions(mockBeanDefinition);
	}

	@Test
	@SuppressWarnings("all")
	public void addDependsOnToNullBean() {

		assertThatIllegalArgumentException()
			.isThrownBy(() -> SpringSupport.addDependsOn(null, "beanDependency"))
			.withMessage("BeanDefinition is required")
			.withNoCause();
	}

	@Test
	public void beanInitializedCorrectly() throws Exception {

		InitializingBean bean = mock(InitializingBean.class);

		Consumer<Object> consumer = SpringSupport.beanInitializer();

		assertThat(consumer).isNotNull();

		consumer.accept(bean);

		verify(bean, times(1)).afterPropertiesSet();
		verifyNoMoreInteractions(bean);
	}

	@Test
	public void beanInitializerIgnoresNonInitializingBean() {

		Object bean = spy(new Object());

		Consumer<Object> consumer = SpringSupport.beanInitializer();

		assertThat(consumer).isNotNull();

		consumer.accept(bean);

		verifyNoInteractions(bean);
	}

	@Test
	public void baenInitializerIsNullSafe() {

		Consumer<Object> consumer = SpringSupport.beanInitializer();

		assertThat(consumer).isNotNull();

		consumer.accept(null);
	}
	@Test
	public void dereferencBeanWithBlankNameThrowsIllegalArgumentException() {

		assertThatIllegalArgumentException()
			.isThrownBy(() -> SpringSupport.dereferenceBean("  "))
			.withMessage("Bean name [  ] is required")
			.withNoCause();
	}

	@Test
	public void dereferencBeanWithEmptyNameThrowsIllegalArgumentException() {

		assertThatIllegalArgumentException()
			.isThrownBy(() -> SpringSupport.dereferenceBean(""))
			.withMessage("Bean name [] is required")
			.withNoCause();
	}

	@Test
	@SuppressWarnings("all")
	public void dereferencBeanWithNullNameThrowsIllegalArgumentException() {

		assertThatIllegalArgumentException()
			.isThrownBy(() -> SpringSupport.dereferenceBean(null))
			.withMessage("Bean name [null] is required")
			.withNoCause();
	}

	@Test
	public void dereferenceBeanWithName() {
		assertThat(SpringSupport.dereferenceBean("mockBean")).isEqualTo("&mockBean");
	}

	@Test
	public void getOrderFromObjectReturnsNull() {
		assertThat(SpringSupport.getOrder("test")).isNull();
	}

	@Test
	public void getOrderFromNullIsNullSafeAndReturnsNull() {
		assertThat(SpringSupport.getOrder(null)).isNull();
	}

	@Test
	public void getOrderFromOrderAnnotatedBeanReturnsOrder() {
		assertThat(SpringSupport.getOrder(new TestOrderAnnotatedBean())).isEqualTo(4);
	}

	@Test
	public void getOrderFromOrderedBeanReturnsOrder() {
		assertThat(SpringSupport.getOrder(new TestOrderedBean())).isEqualTo(2);
	}

	@Test
	public void getPropertyValueIsCorrect() {

		BeanDefinition mockBeanDefinition  = mock(BeanDefinition.class);

		MutablePropertyValues mockPropertyValues = mock(MutablePropertyValues.class);

		PropertyValue mockPropertyValue = mock(PropertyValue.class);

		doReturn(mockPropertyValues).when(mockBeanDefinition).getPropertyValues();
		doReturn(mockPropertyValue).when(mockPropertyValues).getPropertyValue(eq("mockProperty"));
		doReturn("test").when(mockPropertyValue).getValue();

		assertThat(SpringSupport.getPropertyValue(mockBeanDefinition, "mockProperty").orElse(null))
			.isEqualTo("test");

		verify(mockBeanDefinition, times(1)).getPropertyValues();
		verify(mockPropertyValues, times(1)).getPropertyValue(eq("mockProperty"));
		verify(mockPropertyValue, times(1)).getValue();
		verifyNoMoreInteractions(mockBeanDefinition, mockPropertyValues, mockPropertyValue);
	}

	@Test
	@SuppressWarnings("all")
	public void setPropertyReferenceIsCorrect() {

		BeanDefinition mockBeanDefinition = mock(BeanDefinition.class);

		MutablePropertyValues propertyValues = new MutablePropertyValues();

		doReturn(propertyValues).when(mockBeanDefinition).getPropertyValues();

		assertThat(SpringSupport.setPropertyReference(mockBeanDefinition, "target", "mockBean"))
			.isSameAs(mockBeanDefinition);

		Object propertyValue = propertyValues.getPropertyValue("target").getValue();

		assertThat(propertyValue).isInstanceOf(RuntimeBeanReference.class);

		assertThat(propertyValue)
			.asInstanceOf(InstanceOfAssertFactories.type(RuntimeBeanReference.class))
			.extracting(RuntimeBeanReference::getBeanName)
			.isEqualTo("mockBean");

		verify(mockBeanDefinition, times(1)).getPropertyValues();
		verifyNoMoreInteractions(mockBeanDefinition);
	}

	@Test
	@SuppressWarnings("all")
	public void setPropertyValueIsCorrect() {

		BeanDefinition mockBeanDefinition = mock(BeanDefinition.class);

		MutablePropertyValues propertyValues = new MutablePropertyValues();

		doReturn(propertyValues).when(mockBeanDefinition).getPropertyValues();

		assertThat(SpringSupport.setPropertyValue(mockBeanDefinition,"mockProperty", "test"))
			.isSameAs(mockBeanDefinition);

		assertThat(propertyValues.getPropertyValue("mockProperty").getValue()).isEqualTo("test");

		verify(mockBeanDefinition, times(1)).getPropertyValues();
		verifyNoMoreInteractions(mockBeanDefinition);
	}

	@Test
	public void reregisterBeanSuccessfully() {

		BeanDefinition mockBeanDefinition = mock(BeanDefinition.class);
		BeanDefinitionRegistry mockBeanDefinitionRegistry = mock(BeanDefinitionRegistry.class);

		assertThat(SpringSupport.reregister(mockBeanDefinitionRegistry, "TestBean", mockBeanDefinition))
			.isSameAs(mockBeanDefinitionRegistry);

		verify(mockBeanDefinitionRegistry, times(1)).removeBeanDefinition(eq("TestBean"));
		verify(mockBeanDefinitionRegistry, times(1))
			.registerBeanDefinition(eq("TestBean"), eq(mockBeanDefinition));
		verifyNoMoreInteractions(mockBeanDefinitionRegistry);
		verifyNoInteractions(mockBeanDefinition);
	}

	@Test
	@SuppressWarnings("all")
	public void reregisterBeanWithNullBeanDefinition() {

		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> SpringSupport.reregister(mock(BeanDefinitionRegistry.class), "TestBean", null))
			.withMessage("BeanDefinition to register is required")
			.withNoCause();
	}

	@Test
	@SuppressWarnings("all")
	public void reregisterBeanWithNullBeanDefinitionRegistry() {

		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> SpringSupport.reregister(null, "TestBean", mock(BeanDefinition.class)))
			.withMessage("BeanDefinitionRegistry is required")
			.withNoCause();
	}

	private void testRegisterBeanWithInvalidBeanName(String beanName) {

		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> SpringSupport.reregister(mock(BeanDefinitionRegistry.class), beanName, mock(BeanDefinition.class)))
			.withMessage("Bean name [%s] is required")
			.withNoCause();
	}

	@Test
	public void reregisterBeanWithBlankBeanName() {
		testRegisterBeanWithInvalidBeanName("  ");
	}

	@Test
	public void reregisterBeanWithEmptyBeanName() {
		testRegisterBeanWithInvalidBeanName("");
	}

	@Test
	public void reregisterBeanWithNullBeanName() {
		testRegisterBeanWithInvalidBeanName(null);
	}

	@Order(4)
	static class TestOrderAnnotatedBean { }

	static class TestOrderedBean implements Ordered {

		@Override
		public int getOrder() {
			return 2;
		}
	}
}
