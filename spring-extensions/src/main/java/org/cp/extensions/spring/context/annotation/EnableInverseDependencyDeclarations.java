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
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * Spring {@link Annotation} enabling the declaration of inverse dependency relationships between beans
 * defined, declared and managed in the Spring container using the {@link DependencyOf} annotation.
 *
 * @author John Blum
 * @see java.lang.annotation.Documented
 * @see java.lang.annotation.Inherited
 * @see java.lang.annotation.Retention
 * @see java.lang.annotation.Target
 * @see java.lang.annotation.ElementType#ANNOTATION_TYPE
 * @see java.lang.annotation.ElementType#TYPE
 * @see java.lang.annotation.RetentionPolicy#RUNTIME
 * @see org.cp.extensions.spring.context.annotation.InverseDependencyDeclarationsConfiguration
 * @see org.springframework.context.annotation.Import
 * @since 0.1.0
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE })
@Import(InverseDependencyDeclarationsConfiguration.class)
@SuppressWarnings("unused")
public @interface EnableInverseDependencyDeclarations {

}
