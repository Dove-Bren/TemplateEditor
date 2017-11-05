package com.smanzana.templateeditor.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks this field as only being one of a collection of values.
 * This annotation means there is a method to call at runtime to get
 * all possible values. In the editor, editing this field will then
 * mean picking one of those available values.
 * <p>
 * To use, tag a field and either:
 * <ol>
 * <li> also specify the name of a method that returns a map between a display
 * string and the value. The map should contain all valid values.</li>
 * <li> implement the
 * {@link com.smanzana.templateeditor.api.IRuntimeEnumerable IRuntimeEnumerable}
 * interface</li>
 * </ol>
 * </p>
 * @author Skyler
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DataLoaderRuntimeEnum {
	String value() default "";
}
