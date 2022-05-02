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

import java.util.Optional;
import java.util.function.Consumer;

import org.cp.elements.lang.ObjectUtils;
import org.cp.elements.lang.StringUtils;
import org.cp.elements.lang.annotation.NullSafe;
import org.cp.elements.util.ArrayUtils;

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
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Abstract utility class containing factory methods for handling Spring bean aware interfaces and initialization.
 *
 * @author John Blum
 * @see java.util.function.Consumer
 * @see org.springframework.beans.factory.BeanClassLoaderAware
 * @see org.springframework.beans.factory.BeanFactoryAware
 * @see org.springframework.beans.factory.BeanNameAware
 * @see org.springframework.context.ApplicationContextAware
 * @see org.springframework.context.ApplicationEventPublisherAware
 * @see org.springframework.context.ApplicationStartupAware
 * @see org.springframework.context.EnvironmentAware
 * @see org.springframework.context.MessageSourceAware
 * @see org.springframework.context.ResourceLoaderAware
 * @see org.springframework.context.weaving.LoadTimeWeaverAware
 * @since 0.1.0
 */
@SuppressWarnings("unused")
public abstract class BeanAwareSupport {

	protected static final Consumer<Object> NO_OP = bean -> {};

	/**
	 * Factory method used to initialize a Spring managed bean implementing the {@link ApplicationContextAware}
	 * interface with the given {@link ApplicationContext}.
	 *
	 * @param applicationContext reference to the {@link ApplicationContext}.
	 * @return a {@link Consumer} used to initialize a Spring managed bean implementing
	 * the {@link ApplicationContextAware} interface with the given {@link ApplicationContext}.
	 * @see org.springframework.context.ApplicationContextAware
	 * @see org.springframework.context.ApplicationContext
	 * @see java.util.function.Consumer
	 */
	@NullSafe
	public static @NonNull Consumer<Object> applicationContextAwareInitializer(
			@Nullable ApplicationContext applicationContext) {

		return applicationContext == null ? NO_OP : bean -> {
			if (bean instanceof ApplicationContextAware) {
				((ApplicationContextAware) bean).setApplicationContext(applicationContext);
			}
		};
	}

	/**
	 * Factory method used to initialize a Spring managed bean implementing the {@link ApplicationEventPublisherAware}
	 * interface with the given {@link ApplicationEventPublisher}.
	 *
	 * @param applicationEventPublisher reference to the {@link ApplicationEventPublisher}.
	 * @return a {@link Consumer} used to initialize a Spring managed bean implementing
	 * the {@link ApplicationEventPublisherAware} interface with the given {@link ApplicationEventPublisher}.
	 * @see org.springframework.context.ApplicationEventPublisherAware
	 * @see org.springframework.context.ApplicationEventPublisher
	 * @see java.util.function.Consumer
	 */
	@NullSafe
	public static @NonNull Consumer<Object> applicationEventPublisherAwareInitializer(
			@Nullable ApplicationEventPublisher applicationEventPublisher) {

		return applicationEventPublisher == null ? NO_OP : bean -> {
			if (bean instanceof ApplicationEventPublisherAware) {
				((ApplicationEventPublisherAware) bean).setApplicationEventPublisher(applicationEventPublisher);
			}
		};
	}

	/**
	 * Factory method used to initialize a Spring managed bean implementing the {@link ApplicationStartupAware}
	 * interface with the given {@link ApplicationStartup}.
	 *
	 * @param applicationStartup reference to the {@link ApplicationStartup}.
	 * @return a {@link Consumer} used to initialize a Spring managed bean implementing
	 * the {@link ApplicationStartupAware} interface with the given {@link ApplicationStartup}.
	 * @see org.springframework.context.ApplicationStartupAware
	 * @see org.springframework.core.metrics.ApplicationStartup
	 * @see java.util.function.Consumer
	 */
	@NullSafe
	public static @NonNull Consumer<Object> applicationStartupAwareInitializer(
			@Nullable ApplicationStartup applicationStartup) {

		return applicationStartup == null ? NO_OP : bean -> {
			if (bean instanceof ApplicationStartupAware) {
				((ApplicationStartupAware) bean).setApplicationStartup(applicationStartup);
			}
		};
	}

	/**
	 * Returns a {@link Consumer} that initializes a Spring managed bean for all possible permutations of
	 * {@literal *Aware} interfaces.
	 *
	 * @param applicationContext reference to the {@link ApplicationContext} used to initialize the Spring managed bean.
	 * @return a {@link Consumer} used to initialize a Spring managed bean using the given {@link ApplicationContext}.
	 * @see #applicationContextAwareInitializer(ApplicationContext)
	 * @see #applicationEventPublisherAwareInitializer(ApplicationEventPublisher)
	 * @see #applicationStartupAwareInitializer(ApplicationStartup)
	 * @see #beanClassLoaderAwareInitializer(ClassLoader)
	 * @see #beanFactoryAwareInitializer(BeanFactory)
	 * @see #beanNameAwareInitializer(String)
	 * @see #environmentAwareInitializer(Environment)
	 * @see #loadTimeWeaverAwareInitializer(LoadTimeWeaver)
	 * @see #messageSourceAwareInitializer(MessageSource)
	 * @see #resourceLoaderAwareInitializer(ResourceLoader)
	 * @see org.springframework.context.ApplicationContext
	 * @see java.util.function.Consumer
	 */
	public static @NonNull Consumer<Object> beanAwareInitializer(@Nullable ApplicationContext applicationContext) {

		return applicationContext == null ? NO_OP : bean -> {
			applicationContextAwareInitializer(applicationContext)
				.andThen(applicationEventPublisherAwareInitializer(applicationContext))
				.andThen(applicationStartupAwareInitializer(resolveApplicationStartup(applicationContext)))
				.andThen(beanClassLoaderAwareInitializer(applicationContext.getClassLoader()))
				.andThen(beanFactoryAwareInitializer(applicationContext))
				.andThen(beanNameAwareInitializer(resolveBeanName(applicationContext, bean)))
				.andThen(environmentAwareInitializer(applicationContext.getEnvironment()))
				.andThen(loadTimeWeaverAwareInitializer(resolveLoadTimeWeaver(applicationContext)))
				.andThen(messageSourceAwareInitializer(applicationContext))
				.andThen(resourceLoaderAwareInitializer(applicationContext))
				.accept(bean);
		};
	}

	private static @NonNull ApplicationStartup resolveApplicationStartup(@Nullable ApplicationContext applicationContext) {

		return Optional.ofNullable(applicationContext)
			.map(it -> ObjectUtils.doOperationSafely(arg -> it.getBean(ApplicationStartup.class),
				ApplicationStartup.DEFAULT))
			.orElse(ApplicationStartup.DEFAULT);
	}

	private static @Nullable String resolveBeanName(@Nullable ApplicationContext applicationContext,
			@Nullable Object bean) {

		String resolvedBeanName = null;

		if (applicationContext != null && bean != null) {

			String[] beanNames = applicationContext.getBeanNamesForType(bean.getClass());

			resolvedBeanName = ArrayUtils.nullSafeLength(beanNames) == 1 ? beanNames[0] : null;
		}

		return resolvedBeanName;
	}

	private static @Nullable LoadTimeWeaver resolveLoadTimeWeaver(@Nullable ApplicationContext applicationContext) {

		return Optional.ofNullable(applicationContext)
			.map(it -> ObjectUtils.doOperationSafely(arg -> it.getBean(LoadTimeWeaver.class)))
			.orElse(null);
	}

	/**
	 * Factory method used to initialize a Spring managed bean implementing the {@link BeanClassLoaderAware} interface
	 * with the given {@link ClassLoader}.
	 *
	 * @param classLoader {@link ClassLoader} used by the Spring container to load bean definition classes.
	 * @return a {@link Consumer} used to initialize a Spring managed bean implementing the {@link BeanClassLoaderAware}
	 * interface with the given {@link ClassLoader}.
	 * @see org.springframework.beans.factory.BeanClassLoaderAware
	 * @see java.util.function.Consumer
	 * @see java.lang.ClassLoader
	 */
	@NullSafe
	public static @NonNull Consumer<Object> beanClassLoaderAwareInitializer(@Nullable ClassLoader classLoader) {

		return classLoader == null ? NO_OP : bean -> {
			if (bean instanceof BeanClassLoaderAware) {
				((BeanClassLoaderAware) bean).setBeanClassLoader(classLoader);
			}
		};
	}

	/**
	 * Factory method used to initialize a Spring managed bean implementing the {@link BeanFactoryAware} interface
	 * with the given {@link BeanFactory}.
	 *
	 * @param beanFactory reference to the {@link BeanFactory}.
	 * @return a {@link Consumer} used to initialize a Spring managed bean implementing the {@link BeanFactoryAware}
	 * interface with the given {@link BeanFactory}.
	 * @see org.springframework.beans.factory.BeanFactoryAware
	 * @see org.springframework.beans.factory.BeanFactory
	 * @see java.util.function.Consumer
	 */
	@NullSafe
	public static @NonNull Consumer<Object> beanFactoryAwareInitializer(@Nullable BeanFactory beanFactory) {

		return beanFactory == null ? NO_OP : bean -> {
			if (bean instanceof BeanFactoryAware) {
				((BeanFactoryAware) bean).setBeanFactory(beanFactory);
			}
		};
	}

	/**
	 * Factory method used to initialize a Spring managed bean implementing the {@link BeanNameAware} interface
	 * with the given {@link String bean name}.
	 *
	 * @param beanName {@link String} containing the {@literal name} of the Spring managed bean.
	 * @return a {@link Consumer} used to initialize a Spring managed bean implementing the {@link BeanNameAware}
	 * interface with the given {@link String bean name}.
	 * @see org.springframework.beans.factory.BeanNameAware
	 * @see java.util.function.Consumer
	 */
	@NullSafe
	public static @NonNull Consumer<Object> beanNameAwareInitializer(@Nullable String beanName) {

		return hasNoText(beanName) ? NO_OP : bean -> {
			if (bean instanceof BeanNameAware) {
				((BeanNameAware) bean).setBeanName(beanName);
			}
		};
	}

	private static boolean hasNoText(@Nullable String text) {
		return !StringUtils.hasText(text);
	}

	/**
	 * Factory method used to initialize a Spring managed bean implementing the {@link EnvironmentAware} interface
	 * with the given {@link Environment}.
	 *
	 * @param environment reference to the {@link Environment}.
	 * @return a {@link Consumer} used to initialize a Spring managed bean implementing the {@link EnvironmentAware}
	 * interface with the given {@link Environment}.
	 * @see org.springframework.context.EnvironmentAware
	 * @see org.springframework.core.env.Environment
	 * @see java.util.function.Consumer
	 */
	@NullSafe
	public static @NonNull Consumer<Object> environmentAwareInitializer(@Nullable Environment environment) {

		return environment == null ? NO_OP : bean -> {
			if (bean instanceof EnvironmentAware) {
				((EnvironmentAware) bean).setEnvironment(environment);
			}
		};
	}

	/**
	 * Factory method used to initialize a Spring managed bean implementing the {@link LoadTimeWeaverAware} interface
	 * with the given {@link LoadTimeWeaver}.
	 *
	 * @param loadTimeWeaver reference to the {@link LoadTimeWeaver}.
	 * @return a {@link Consumer} used to initialize a Spring managed bean implementing the {@link LoadTimeWeaverAware}
	 * interface with the given {@link LoadTimeWeaver}.
	 * @see org.springframework.context.weaving.LoadTimeWeaverAware
	 * @see org.springframework.instrument.classloading.LoadTimeWeaver
	 * @see java.util.function.Consumer
	 */
	@NullSafe
	public static @NonNull Consumer<Object> loadTimeWeaverAwareInitializer(@Nullable LoadTimeWeaver loadTimeWeaver) {

		return loadTimeWeaver == null ? NO_OP : bean -> {
			if (bean instanceof LoadTimeWeaverAware) {
				((LoadTimeWeaverAware) bean).setLoadTimeWeaver(loadTimeWeaver);
			}
		};
	}

	/**
	 * Factory method used to initialize a Spring managed bean implementing the {@link MessageSourceAware} interface
	 * with the given {@link MessageSource}.
	 *
	 * @param messageSource reference to the {@link MessageSource}.
	 * @return a {@link Consumer} used to initialize a Spring managed bean implementing the {@link MessageSourceAware}
	 * interface with the given {@link MessageSource}.
	 * @see org.springframework.context.MessageSourceAware
	 * @see org.springframework.context.MessageSource
	 * @see java.util.function.Consumer
	 */
	@NullSafe
	public static @NonNull Consumer<Object> messageSourceAwareInitializer(@Nullable MessageSource messageSource) {

		return messageSource == null ? NO_OP : bean -> {
			if (bean instanceof MessageSourceAware) {
				((MessageSourceAware) bean).setMessageSource(messageSource);
			}
		};
	}

	/**
	 * Factory method used to initialize a Spring managed bean implementing the {@link ResourceLoaderAware} interface
	 * with the given {@link ResourceLoader}.
	 *
	 * @param resourceLoader reference to the {@link ResourceLoader}.
	 * @return a {@link Consumer} used to initialize a Spring managed bean implementing the {@link ResourceLoaderAware}
	 * interface with the given {@link ResourceLoader}.
	 * @see org.springframework.context.ResourceLoaderAware
	 * @see org.springframework.core.io.ResourceLoader
	 * @see java.util.function.Consumer
	 */
	@NullSafe
	public static @NonNull Consumer<Object> resourceLoaderAwareInitializer(@Nullable ResourceLoader resourceLoader) {

		return resourceLoader == null ? NO_OP : bean -> {
			if (bean instanceof ResourceLoaderAware) {
				((ResourceLoaderAware) bean).setResourceLoader(resourceLoader);
			}
		};
	}
}
