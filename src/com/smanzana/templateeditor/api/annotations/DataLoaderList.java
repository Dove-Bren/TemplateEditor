package com.smanzana.templateeditor.api.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DataLoaderList {
	String templateName();
	String factoryName() default "";
}
