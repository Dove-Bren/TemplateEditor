package com.smanzana.templateeditor.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DataLoaderData {
	/**
	 * Sets the label name for this field in the editor.
	 * When left empty, is a slightly cleaned up version of the member name.
	 */
	String name() default "";
	
	/**
	 * Like name, but the tooltip description.
	 * Defaults to 'null' for no tooltip
	 */
	String description() default "";
	
	/**
	 * Expands a nested complex type into the current object for editting.
	 * E.g. it will look like all fields of the marked type are actually in this
	 * class even though they aren't.
	 */
	boolean expand() default false;
}
