package com.smanzana.templateeditor.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
/**
 * Marks the field as a container of a subclass of the current.<br />
 * The field must be non-null at time of ObjectLoading.<br />
 * As soon as a single @DataLoaderSubclass is hit, no other fields
 * (including those marked with @DataLoaderData, etc) will be parsed.
 * @author Skyler
 *
 */
public @interface DataLoaderSuperclass {
	
	String name() default "";
}
