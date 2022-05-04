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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.cp.elements.lang.annotation.Experimental;

import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.AliasFor;

/**
 * The {@link DependencyOf} annotation is the inverse of Spring's {@link DependsOn} annotation and allows a bean
 * to declare itself as a dependency of another bean declared and managed inside the Spring container.
 * <p>
 * For example, using Spring's {@link DependsOn} annotation, {@literal bean A} can declare that
 * it {@literal depends on} {@literal bean B}. Therefore, {@literal bean B} will be created before
 * and destroyed after {@literal bean A}.
 * <p>
 * However, when using the {@link DependencyOf} annotation, {@literal bean B} can declare that it is
 * a required dependency for {@literal bean A}, or rather that {@literal bean A} should {@literal depend on}
 * {@literal bean B}.
 *
 * Therefore, the following bean definitions for A and B are equivalent:
 *
 * <pre>
 * <code>
 * {@literal Configuration}
 * public class ConfigurationUsingDependsOn {
 *
 *   {@literal @Bean}
 *   {@literal @DependsOn("b")}
 *   public A a() {
 *     return new A();
 *   }
 *
 *   {@literal @Bean}
 *   public B b() {
 *     return new B();
 *   }
 * }
 * </code>
 * </pre>
 *
 * And...
 *
 * <pre>
 * <code>
 * {@literal Configuration}
 * public class ConfigurationUsingDependencyOf {
 *
 *   {@literal @Bean}
 *   public A a() {
 *     return new A();
 *   }
 *
 *   {@literal @Bean}
 *   {@literal @DependencyOf("a")}
 *   public B b() {
 *     return new B();
 *   }
 * }
 * </code>
 * </pre>
 *
 * One advantage of this approach over {@link DependsOn} is that {@literal bean A} does not need to know all the beans
 * it is possibly dependent on, especially at runtime when additional collaborators or dependencies may be added
 * dynamically to the classpath. Therefore, additional dependencies of A can be added to the configuration automatically,
 * over time without having to go back and modify the bean definition for A. This is especially useful during testing.
 *
 * This feature is experimental.
 *
 * @author John Blum
 * @see java.lang.annotation.Documented
 * @see java.lang.annotation.Inherited
 * @see java.lang.annotation.Retention
 * @see java.lang.annotation.Target
 * @see java.lang.annotation.RetentionPolicy#RUNTIME
 * @see org.cp.elements.lang.annotation.Experimental
 * @see org.springframework.context.annotation.DependsOn
 * @since 0.1.0
 */
@Documented
@Inherited
@Experimental
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE })
@SuppressWarnings("unused")
public @interface DependencyOf {

	/**
	 * An array containing a list of {@link String names of beans} declared and managed in the Spring container
	 * that are dependent on this annotated bean.
	 *
	 * @return an array of {@link String bean names}.
	 * @see #value()
	 */
	@AliasFor("value")
	String[] beanNames() default {};

	/**
	 * An array containing a list of {@link String names of beans} declared and managed in the Spring container
	 * that are dependent on this annotated bean.
	 *
	 * @return an array of {@link String bean names}.
	 * @see #beanNames()
	 */
	@AliasFor("beanNames")
	String[] value() default {};

}
