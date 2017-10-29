package com.smanzana.templateeditor.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks that the given type has a factory method.
 * With a marked factory, the type can be used in complex lists easily.
 * Without it, each list has to mark a factory independently.
 * @author Skyler
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DataLoaderFactory {
	String value() default "";
}
