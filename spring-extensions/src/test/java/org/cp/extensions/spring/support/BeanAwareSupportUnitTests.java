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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.function.Consumer;

import org.junit.Test;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationStartupAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.instrument.classloading.LoadTimeWeaver;

/**
 * Unit Tests for {@link BeanAwareSupport}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.cp.extensions.spring.support.BeanAwareSupport
 * @since 0.1.0
 */
public class BeanAwareSupportUnitTests {

	@Test
	public void applicationContextAwareInitializerWithApplicationContextAwareBean() {

		ApplicationContext mockApplicationContext = mock(ApplicationContext.class);

		ApplicationContextAware bean = mock(ApplicationContextAware.class);

		Consumer<Object> initializer = BeanAwareSupport.applicationContextAwareInitializer(mockApplicationContext);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verify(bean, times(1)).setApplicationContext(eq(mockApplicationContext));
		verifyNoMoreInteractions(bean);
		verifyNoInteractions(mockApplicationContext);
	}

	@Test
	public void applicationContextAwareInitializerWithNullApplicationContext() {

		ApplicationContextAware bean = mock(ApplicationContextAware.class);

		Consumer<Object> initializer = BeanAwareSupport.applicationEventPublisherAwareInitializer(null);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verifyNoInteractions(bean);
	}

	@Test
	public void applicationContextAwareInitializerWithNullObject() {

		ApplicationContext mockApplicationContext = mock(ApplicationContext.class);

		Consumer<Object> initializer = BeanAwareSupport.applicationContextAwareInitializer(mockApplicationContext);

		assertThat(initializer).isNotNull();

		initializer.accept(null);

		verifyNoInteractions(mockApplicationContext);
	}

	@Test
	public void applicationContextAwareInitializerWithObject() {

		ApplicationContext mockApplicationContext = mock(ApplicationContext.class);

		Object bean = mock(Object.class);

		Consumer<Object> initializer = BeanAwareSupport.applicationContextAwareInitializer(mockApplicationContext);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verifyNoInteractions(mockApplicationContext, bean);
	}

	@Test
	public void applicationEventPublisherAwareInitializerWithApplicationEventPublisherAwareBean() {

		ApplicationEventPublisher mockApplicationEventPublisher = mock(ApplicationEventPublisher.class);

		ApplicationEventPublisherAware bean = mock(ApplicationEventPublisherAware.class);

		Consumer<Object> initializer =
			BeanAwareSupport.applicationEventPublisherAwareInitializer(mockApplicationEventPublisher);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verify(bean, times(1)).setApplicationEventPublisher(mockApplicationEventPublisher);
		verifyNoMoreInteractions(bean);
		verifyNoInteractions(mockApplicationEventPublisher);
	}

	@Test
	public void applicationEventPublisherAwareInitializerWithNullApplicationEventPublisher() {

		ApplicationEventPublisherAware bean = mock(ApplicationEventPublisherAware.class);

		Consumer<Object> initializer = BeanAwareSupport.applicationEventPublisherAwareInitializer(null);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verifyNoInteractions(bean);
	}

	@Test
	public void applicationEventPublisherAwareInitializerWithNullObject() {

		ApplicationEventPublisher mockApplicationEventPublisher = mock(ApplicationEventPublisher.class);

		Consumer<Object> initializer =
			BeanAwareSupport.applicationEventPublisherAwareInitializer(mockApplicationEventPublisher);

		assertThat(initializer).isNotNull();

		initializer.accept(null);

		verifyNoInteractions(mockApplicationEventPublisher);
	}

	@Test
	public void applicationEventPublisherAwareInitializerWithObject() {

		ApplicationEventPublisher mockApplicationEventPublisher = mock(ApplicationEventPublisher.class);

		Object bean = mock(Object.class);

		Consumer<Object> initializer =
			BeanAwareSupport.applicationEventPublisherAwareInitializer(mockApplicationEventPublisher);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verifyNoInteractions(bean, mockApplicationEventPublisher);
	}

	@Test
	public void applicationStartupAwareInitializerWithApplicationStartupAwareBean() {

		ApplicationStartup mockApplicationStartup = mock(ApplicationStartup.class);

		ApplicationStartupAware bean = mock(ApplicationStartupAware.class);

		Consumer<Object> initializer = BeanAwareSupport.applicationStartupAwareInitializer(mockApplicationStartup);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verify(bean, times(1)).setApplicationStartup(mockApplicationStartup);
		verifyNoMoreInteractions(bean);
		verifyNoInteractions(mockApplicationStartup);
	}

	@Test
	public void applicationStartupAwareInitializerWithNullApplicationStartup() {

		ApplicationStartupAware bean = mock(ApplicationStartupAware.class);

		Consumer<Object> initializer = BeanAwareSupport.applicationStartupAwareInitializer(null);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verifyNoInteractions(bean);
	}

	@Test
	public void applicationStartupAwareInitializerWithNullObject() {

		ApplicationStartup mockApplicationStartup = mock(ApplicationStartup.class);

		Consumer<Object> initializer = BeanAwareSupport.applicationStartupAwareInitializer(mockApplicationStartup);

		assertThat(initializer).isNotNull();

		initializer.accept(null);

		verifyNoInteractions(mockApplicationStartup);
	}

	@Test
	public void applicationStartupAwareInitializerWithObject() {

		ApplicationStartup mockApplicationStartup = mock(ApplicationStartup.class);

		Object bean = mock(Object.class);

		Consumer<Object> initializer = BeanAwareSupport.applicationStartupAwareInitializer(mockApplicationStartup);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verifyNoInteractions(bean, mockApplicationStartup);
	}

	@Test
	public void beanAwareInitializerInitializesBean() {

		TestAwareBean bean = mock(TestAwareBean.class);

		ApplicationContext mockApplicationContext = mock(ApplicationContext.class);

		ApplicationStartup mockApplicationStartup = mock(ApplicationStartup.class);

		ClassLoader mockClassLoader = mock(ClassLoader.class);

		Environment mockEnvironment = mock(Environment.class);

		LoadTimeWeaver mockLoadTimeWeaver = mock(LoadTimeWeaver.class);

		doReturn(mockApplicationStartup).when(mockApplicationContext).getBean(eq(ApplicationStartup.class));
		doReturn(mockLoadTimeWeaver).when(mockApplicationContext).getBean(eq(LoadTimeWeaver.class));
		doReturn(new String[] { "TestBean" }).when(mockApplicationContext).getBeanNamesForType(eq(bean.getClass()));
		doReturn(mockClassLoader).when(mockApplicationContext).getClassLoader();
		doReturn(mockEnvironment).when(mockApplicationContext).getEnvironment();

		Consumer<Object> initializer = BeanAwareSupport.beanAwareInitializer(mockApplicationContext);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verify(mockApplicationContext, times(1)).getBean(eq(ApplicationStartup.class));
		verify(mockApplicationContext, times(1)).getBean(eq(LoadTimeWeaver.class));
		verify(mockApplicationContext, times(1)).getBeanNamesForType(eq(bean.getClass()));
		verify(mockApplicationContext, times(1)).getClassLoader();
		verify(mockApplicationContext, times(1)).getEnvironment();
		verify(bean, times(1)).setApplicationContext(eq(mockApplicationContext));
		verify(bean, times(1)).setApplicationEventPublisher(eq(mockApplicationContext));
		verify(bean, times(1)).setApplicationStartup(eq(mockApplicationStartup));
		verify(bean, times(1)).setBeanClassLoader(eq(mockClassLoader));
		verify(bean, times(1)).setBeanFactory(eq(mockApplicationContext));
		verify(bean, times(1)).setBeanName(eq("TestBean"));
		verify(bean, times(1)).setEnvironment(eq(mockEnvironment));
		verify(bean, times(1)).setLoadTimeWeaver(eq(mockLoadTimeWeaver));
		verify(bean, times(1)).setMessageSource(eq(mockApplicationContext));
		verify(bean, times(1)).setResourceLoader(eq(mockApplicationContext));

		verifyNoMoreInteractions(bean, mockApplicationContext);

		verifyNoInteractions(mockClassLoader, mockEnvironment, mockApplicationStartup);
	}

	@Test
	public void beanClassLoaderAwareInitializerWithClassLoader() {

		BeanClassLoaderAware bean = mock(BeanClassLoaderAware.class);

		ClassLoader mockClassLoader = mock(ClassLoader.class);

		Consumer<Object> initializer = BeanAwareSupport.beanClassLoaderAwareInitializer(mockClassLoader);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verify(bean, times(1)).setBeanClassLoader(eq(mockClassLoader));
		verifyNoMoreInteractions(bean);
		verifyNoInteractions(mockClassLoader);
	}

	@Test
	public void beanClassLoaderAwareInitializerWithNullClassLoader() {

		BeanClassLoaderAware bean = mock(BeanClassLoaderAware.class);

		Consumer<Object> initializer = BeanAwareSupport.beanClassLoaderAwareInitializer(null);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verifyNoInteractions(bean);
	}

	@Test
	public void beanClassLoaderAwareInitializerWithNullObject() {

		ClassLoader mockClassLoader = mock(ClassLoader.class);

		Consumer<Object> initializer = BeanAwareSupport.beanClassLoaderAwareInitializer(mockClassLoader);

		assertThat(initializer).isNotNull();

		initializer.accept(null);

		verifyNoInteractions(mockClassLoader);
	}

	@Test
	public void beanClassLoaderAwareInitializerWithObject() {

		Object bean = mock(Object.class);

		ClassLoader mockClassLoader = mock(ClassLoader.class);

		Consumer<Object> initializer = BeanAwareSupport.beanClassLoaderAwareInitializer(mockClassLoader);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verifyNoInteractions(bean, mockClassLoader);
	}

	@Test
	public void beanFactoryAwareInitializerWithBeanFactoryAwareBean() {

		BeanFactory mockBeanFactory = mock(BeanFactory.class);

		BeanFactoryAware bean = mock(BeanFactoryAware.class);

		Consumer<Object> initializer = BeanAwareSupport.beanFactoryAwareInitializer(mockBeanFactory);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verify(bean, times(1)).setBeanFactory(eq(mockBeanFactory));
		verifyNoMoreInteractions(bean);
		verifyNoInteractions(mockBeanFactory);
	}

	@Test
	public void beanFactoryAwareInitializerWithNullBeanFactory() {

		BeanFactoryAware bean = mock(BeanFactoryAware.class);

		Consumer<Object> initializer = BeanAwareSupport.beanFactoryAwareInitializer(null);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verifyNoInteractions(bean);
	}

	@Test
	public void beanFactoryAwareInitializerWithNullObject() {

		BeanFactory mockBeanFactory = mock(BeanFactory.class);

		Consumer<Object> initializer = BeanAwareSupport.beanFactoryAwareInitializer(mockBeanFactory);

		assertThat(initializer).isNotNull();

		initializer.accept(null);

		verifyNoInteractions(mockBeanFactory);
	}

	@Test
	public void beanFactoryAwareInitializerWithObject() {

		BeanFactory mockBeanFactory = mock(BeanFactory.class);

		Object bean = mock(Object.class);

		Consumer<Object> initializer = BeanAwareSupport.beanFactoryAwareInitializer(mockBeanFactory);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verifyNoInteractions(bean, mockBeanFactory);
	}

	@Test
	public void beanNameAwareInitializerWithBeanNameAwareBean() {

		BeanNameAware bean = mock(BeanNameAware.class);

		Consumer<Object> initializer = BeanAwareSupport.beanNameAwareInitializer("TestBean");

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verify(bean, times(1)).setBeanName(eq("TestBean"));
		verifyNoMoreInteractions(bean);
	}

	@Test
	public void beanNameAwareInitializerWithNoBeanName() {

		BeanNameAware bean = mock(BeanNameAware.class);

		Consumer<Object> initializer = BeanAwareSupport.beanNameAwareInitializer(null)
			.andThen(BeanAwareSupport.beanNameAwareInitializer("  "))
			.andThen(BeanAwareSupport.beanNameAwareInitializer(""));

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verifyNoInteractions(bean);
	}

	@Test
	public void beanNameAwareInitializerWithNullObject() {

		Consumer<Object> initializer = BeanAwareSupport.beanNameAwareInitializer("TestBean");

		assertThat(initializer).isNotNull();

		initializer.accept(null);
	}

	@Test
	public void beanNameAwareInitializerWithObject() {

		Object bean = mock(Object.class);

		Consumer<Object> initializer = BeanAwareSupport.beanNameAwareInitializer("TestBean");

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verifyNoInteractions(bean);
	}

	@Test
	public void environmentAwareInitializerWithEnvironmentAwareBean() {

		Environment mockEnvironment = mock(Environment.class);

		EnvironmentAware bean = mock(EnvironmentAware.class);

		Consumer<Object> initializer = BeanAwareSupport.environmentAwareInitializer(mockEnvironment);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verify(bean, times(1)).setEnvironment(eq(mockEnvironment));
		verifyNoMoreInteractions(bean);
		verifyNoInteractions(mockEnvironment);
	}

	@Test
	public void environmentAwareInitializerWithNullEnvironment() {

		EnvironmentAware bean = mock(EnvironmentAware.class);

		Consumer<Object> initializer = BeanAwareSupport.environmentAwareInitializer(null);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verifyNoInteractions(bean);
	}

	@Test
	public void environmentAwareInitializerWithNullObject() {

		Environment mockEnvironment = mock(Environment.class);

		Consumer<Object> initializer = BeanAwareSupport.environmentAwareInitializer(mockEnvironment);

		assertThat(initializer).isNotNull();

		initializer.accept(null);

		verifyNoInteractions(mockEnvironment);
	}

	@Test
	public void environmentAwareInitializerWithObject() {

		Environment mockEnvironment = mock(Environment.class);

		Object bean = mock(Object.class);

		Consumer<Object> initializer = BeanAwareSupport.environmentAwareInitializer(mockEnvironment);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verifyNoInteractions(bean, mockEnvironment);
	}

	@Test
	public void loadTimeWeaverAwareInitializerWithLoadTimeAwareBean() {

		LoadTimeWeaver mockLoadTimeWeaver = mock(LoadTimeWeaver.class);

		LoadTimeWeaverAware bean = mock(LoadTimeWeaverAware.class);

		Consumer<Object> initializer = BeanAwareSupport.loadTimeWeaverAwareInitializer(mockLoadTimeWeaver);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verify(bean, times(1)).setLoadTimeWeaver(eq(mockLoadTimeWeaver));
		verifyNoMoreInteractions(bean);
		verifyNoInteractions(mockLoadTimeWeaver);
	}

	@Test
	public void loadTimeWeaverAwareInitializerNullLoadTimeWeaver() {

		LoadTimeWeaverAware bean = mock(LoadTimeWeaverAware.class);

		Consumer<Object> initializer = BeanAwareSupport.loadTimeWeaverAwareInitializer(null);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verifyNoInteractions(bean);
	}

	@Test
	public void loadTimeWeaverAwareInitializerNullObject() {

		LoadTimeWeaver mockLoadTimeWeaver = mock(LoadTimeWeaver.class);

		Consumer<Object> initializer = BeanAwareSupport.loadTimeWeaverAwareInitializer(mockLoadTimeWeaver);

		assertThat(initializer).isNotNull();

		initializer.accept(null);

		verifyNoInteractions(mockLoadTimeWeaver);
	}

	@Test
	public void loadTimeWeaverAwareInitializerObject() {

		Object bean = mock(Object.class);

		LoadTimeWeaver mockLoadTimeWeaver = mock(LoadTimeWeaver.class);

		Consumer<Object> initializer = BeanAwareSupport.loadTimeWeaverAwareInitializer(mockLoadTimeWeaver);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verifyNoInteractions(bean, mockLoadTimeWeaver);
	}

	@Test
	public void messageSourceAwareInitializerWithMessageSourceAwareBean() {

		MessageSource mockMessageSource = mock(MessageSource.class);

		MessageSourceAware bean = mock(MessageSourceAware.class);

		Consumer<Object> initializer = BeanAwareSupport.messageSourceAwareInitializer(mockMessageSource);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verify(bean, times(1)).setMessageSource(eq(mockMessageSource));
		verifyNoMoreInteractions(bean);
		verifyNoInteractions(mockMessageSource);
	}

	@Test
	public void messageSourceAwareInitializerWithNullMessageSource() {

		MessageSourceAware bean = mock(MessageSourceAware.class);

		Consumer<Object> initializer = BeanAwareSupport.messageSourceAwareInitializer(null);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verifyNoInteractions(bean);
	}

	@Test
	public void messageSourceAwareInitializerWithNullObject() {

		MessageSource mockMessageSource = mock(MessageSource.class);

		Consumer<Object> initializer = BeanAwareSupport.messageSourceAwareInitializer(mockMessageSource);

		assertThat(initializer).isNotNull();

		initializer.accept(null);

		verifyNoInteractions(mockMessageSource);
	}

	@Test
	public void messageSourceAwareInitializerWithObject() {

		Object bean = mock(Object.class);

		MessageSource mockMessageSource = mock(MessageSource.class);

		Consumer<Object> initializer = BeanAwareSupport.messageSourceAwareInitializer(mockMessageSource);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verifyNoInteractions(bean, mockMessageSource);
	}

	@Test
	public void resourceLoaderAwareInitializerWithResourceLoaderAwareBean() {

		ResourceLoader mockResourceLoader = mock(ResourceLoader.class);

		ResourceLoaderAware bean = mock(ResourceLoaderAware.class);

		Consumer<Object> initializer = BeanAwareSupport.resourceLoaderAwareInitializer(mockResourceLoader);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verify(bean, times(1)).setResourceLoader(eq(mockResourceLoader));
		verifyNoMoreInteractions(bean);
		verifyNoInteractions(mockResourceLoader);
	}

	@Test
	public void resourceLoaderAwareInitializerWithNullResourceLoader() {

		ResourceLoaderAware bean = mock(ResourceLoaderAware.class);

		Consumer<Object> initializer = BeanAwareSupport.resourceLoaderAwareInitializer(null);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verifyNoInteractions(bean);
	}

	@Test
	public void resourceLoaderAwareInitializerWithNullObject() {

		ResourceLoader mockResourceLoader = mock(ResourceLoader.class);

		Consumer<Object> initializer = BeanAwareSupport.resourceLoaderAwareInitializer(mockResourceLoader);

		assertThat(initializer).isNotNull();

		initializer.accept(null);

		verifyNoInteractions(mockResourceLoader);
	}

	@Test
	public void resourceLoaderAwareInitializerWithObject() {

		ResourceLoader mockResourceLoader = mock(ResourceLoader.class);

		Object bean = mock(Object.class);

		Consumer<Object> initializer = BeanAwareSupport.resourceLoaderAwareInitializer(mockResourceLoader);

		assertThat(initializer).isNotNull();

		initializer.accept(bean);

		verifyNoInteractions(bean, mockResourceLoader);
	}

	interface TestAwareBean extends ApplicationContextAware, ApplicationEventPublisherAware, ApplicationStartupAware,
		BeanClassLoaderAware, BeanFactoryAware, BeanNameAware, EnvironmentAware, LoadTimeWeaverAware,
		MessageSourceAware, ResourceLoaderAware {

	}
}
