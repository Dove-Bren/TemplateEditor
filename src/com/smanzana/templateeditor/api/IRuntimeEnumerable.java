package com.smanzana.templateeditor.api;

import java.util.Map;

public interface IRuntimeEnumerable<T> {

	/**
	 * Return all possible values in a map, which maps between a display
	 * name for that value and the value itself.<br />
	 * For use with {@link com.smanzana.templateeditor.api.annotations.DataLoaderRuntimeEnum
	 * DataLoaderRuntimeEnum}.
	 * @param key The key given to the DataLoaderRuntimeEnum annotation for that field.
	 * This allows you to have multiple runtime-enumerable fields as long as they
	 * each have a unique key.
	 * @return
	 */
	public Map<String, T> fetchValidValues(String key);
	
}
