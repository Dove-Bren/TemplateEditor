package com.smanzana.templateeditor;

/**
 * Used to specify what type of data a field is.
 * All fields passed in to be edited should be mapped to exactly one of the below types.
 * @author Skyler
 *
 */
public enum FieldType {
	
	/** Basic boolean */
	BOOL,
	
	/** Basic integer */
	INT,
	
	/** Basic double */
	DOUBLE,
	
	/** Regular string */
	STRING,
	
	/** User-defined type. See {@link IUserType} */
	USER,
	
	/** Type that is defined as a list of fields which each have their own type */
	COMPLEX,
	
	/** List of basic integers */
	LIST_INT,
	
	/** List of basic doubles */
	LIST_DOUBLE,
	
	/** List of strings */
	LIST_STRING,
	
	/** List of complex objects. All objects have the same definition of subvalues */
	LIST_COMPLEX,
}
