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
 * 
 * If the parent class does not implement the
 * {@link com.smanzana.templateeditor.api.IRuntimeEnumerable IRuntimeEnumerable}
 * interface, the value given to the tag is used to get all potential values.
 * 
 * If, however, the parent class <i>does</i> implement the class, the value is instead
 * used as a key and passed in to the 
 * {@link com.smanzana.templateeditor.api.IRuntimeEnumerable#fetchValidValues(String)
 * fetchValidValues(String)} method. This allows multiple runtime-enumerable fields
 * in the same class.
 * </p>
 * @author Skyler
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DataLoaderRuntimeEnum {
	String value() default "";
}
