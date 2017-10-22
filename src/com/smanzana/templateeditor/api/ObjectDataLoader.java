package com.smanzana.templateeditor.api;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.smanzana.templateeditor.data.SimpleFieldData;
import com.smanzana.templateeditor.uiutils.TextUtil;

/**
 * Takes in a generic java object and breaks it down into a mapping of
 * keys and attributes.<br />
 * This class exists so that you don't have to write a map of manual
 * mappings between arbitrary keys and values and then parse them again.
 * That still works and is what I do. But here ya go. Now you don't have to.
 * @author Skyler
 *
 */
public class ObjectDataLoader<T> {
	
	private static class FieldWrapper {
		public Field field;
		public Object template;
		public String name;
		public String desc;
		public int key;
		
		public FieldWrapper(Field field, int key, Object template, String name, String desc) {
			this.field = field;
			this.template = template;
			this.name = name;
			this.desc = desc;
			this.key = key;
		}
	}

	private T templateObject; // Raw object for dissolution
	private List<T> valueList; // Raw list of values
	private int formattingNameIndex; // What index (in template) to use for the name. Not optional.
	private int formattingDescIndex; // what index (in template) to us for description. -1 means none.
	private Map<Integer, FieldData> template;
	private Map<Integer, FieldWrapper> fieldMap;
	private List<Map<Integer, FieldData>> listTemplates;
	private boolean valid;
	private int keyIndex;
	
	private ObjectDataLoader() {
		valid = false;
		keyIndex = 0;
		formattingNameIndex = -1;
		formattingDescIndex = -1;
		template = new HashMap<>();
		fieldMap = new HashMap<>();
		// TODO
	}
	
	public ObjectDataLoader(T templateObject) {
		this(templateObject, null);
	}
	
	public ObjectDataLoader(T templateObject, List<T> listItems) {
		this();
		
		this.templateObject = templateObject;
		this.valueList = listItems;
		
		try {
			this.dissolve();
		} catch (SecurityException e) {
			System.out.println("Encountered SecurityExeception for object " + templateObject + " ("
					+ templateObject == null ? "NULL" : templateObject.getClass() + ")");
			valid = false;
			return;
		}
		
		if (!valid)
			return;
		
		if (formattingNameIndex == -1)
			formattingNameIndex = 0;
		
		extractListMaps();
	}
	
	private int nextKey() {
		return keyIndex++;
	}
	
	/**
	 * Returns whether the loading of the passed object was successfull.
	 * If not, using this ObjectDataLoader will likely result in exceptions.
	 * @return
	 */
	public boolean isValid() {
		return valid;
	}
	
	// Parse templateObject and create mapping
	private void dissolve() throws SecurityException {
		// Scan for annotations
		// For now let's just go over everything
		// TODO annotations
		List<FieldWrapper> fields = new LinkedList<>();
		collectFields(fields, templateObject.getClass(), templateObject);
		for (FieldWrapper wrapper : fields) {
			Field f = wrapper.field;
			f.setAccessible(true);
			Object val = null;
			try {
				val = f.get(templateObject);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				System.out.println("Failed parsing field " + f.getName() + " on object type " + templateObject.getClass()
				+ ": " + e.getMessage());
				valid = false;
				return;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				System.out.println("Failed parsing field " + f.getName() + " on object type " + templateObject.getClass()
				+ ": " + e.getMessage());
				valid = false;
				return;
			}
			
			int key = wrapper.key;
			FieldData data = wrapField(f, val, wrapper.name == null ? TextUtil.pretty(f.getName()) : wrapper.name);
			if (data == null) {
				System.err.println("Could not dissolve field " + f.getName() + " on class " + templateObject.getClass().getName());
				System.err.println("Aborting");
				valid = false;
				return;
			}
			
			template.put(key, data);
			fieldMap.put(key, wrapper);
		}
		
		valid = true; // Didn't error, must be valid
	}
	
	private void collectFields(List<FieldWrapper> list, Class<?> clazz, Object o) {
		
		if (clazz == Object.class
				|| clazz.isPrimitive())
			return;
		
		Map<String, FieldWrapper> buffer = new HashMap<>();
		List<String> removedNames = new LinkedList<>();
		int nameKey = -1;
		int descKey = -1;
		
		for (Field f : clazz.getDeclaredFields()) {
			if (Modifier.isStatic(f.getModifiers()))
				continue;
			
			DataLoaderList aList = f.getAnnotation(DataLoaderList.class);
			DataLoaderName aName = f.getAnnotation(DataLoaderName.class);
			DataLoaderDescription aDesc = f.getAnnotation(DataLoaderDescription.class);
			if (!f.isAnnotationPresent(DataLoaderData.class)
					&& aList == null && aName == null && aDesc == null)
				continue;
			
			if (removedNames.contains(f.getName()))
				continue;
			
			Object template = null;
			String name = null;
			String desc = null;
			if (aList != null && aList.templateName() != null) {
				// Search for field by this name in current class
				// Also add it to list of ignored fields
				// Also remove from buffer if already added
				Field reference;
				try {
					reference = clazz.getDeclaredField(aList.templateName());
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Could not grab referenced field " + aList.templateName()
							+ " for field " + f.getName());
					reference = null;
				}
				
				if (reference == null) {
					System.err.println("Field lookup failed for field " + f.getName());
					continue;
				}
				
				try {
				template = reference.get(o);
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Failed field fetch for referenced field " + reference.getName());
					continue;
				}
				
				// Properly found a reference. Remove an add it where appropriate
				FieldWrapper booted = buffer.remove(reference.getName());
				if (booted != null) {
					// if it is namekey or desckey, remove those
					if (booted.key == nameKey)
						nameKey = -1;
					if (booted.key == descKey)
						descKey = -1;
				}
				removedNames.add(reference.getName());
			}
			
			int key = nextKey();
			
			// extract name and desc from DataLoaderData
			
			if (aName != null) // If this field is the display name
			if (nameKey == -1) // no other display name has been set
				nameKey = key;
			
			if (aDesc != null)
			if (descKey == -1)
				descKey = key;
			
			buffer.put(f.getName(), new FieldWrapper(f, key, template, name, desc)); // TODO pass in template object if exists
			
		}
		
		list.addAll(buffer.values());
		
		// DISCONNECT
		// no way to know if values ended up in or were ignored
		
		// If nameField or DescField made it in (not ignored), set index appropriately
		if (nameKey != -1 && formattingNameIndex == -1)
			formattingNameIndex = nameKey;
		
		if (descKey != -1 && formattingDescIndex == -1)
			formattingDescIndex = descKey;
		
		collectFields(list, clazz.getSuperclass(), o);
		
	}
	
	// Takes template and creates copies for each list value
	private void extractListMaps() {
		// Assumes we're valid
		
		if (valueList == null)
			return;
		
		listTemplates = new ArrayList<>(valueList.size());
		
		// Does this not iterate if empty?? // TODO I forget
		for (T item : valueList) {
			// For each item, create a copy of template by pulling out fields
			
			Map<Integer, FieldData> rowMap = new HashMap<>();
			for (Entry<Integer, FieldWrapper> row : fieldMap.entrySet()) {
				// Type-safe cause of generics; we know these fields exist in list objects
				Field field = row.getValue().field;
				try {
					rowMap.put(row.getKey(), wrapField(field, field.get(item),
							row.getValue().name == null
								? TextUtil.pretty(field.getName())
								: row.getValue().name));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					System.out.println("Failed parsing field " + field.getName() + " on child object type " + templateObject.getClass()
					+ ": " + e.getMessage());
					valid = false;
					return;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					System.out.println("Failed parsing field " + field.getName() + " on child object type " + templateObject.getClass()
					+ ": " + e.getMessage());
					valid = false;
					return;
				}
			}
			
			listTemplates.add(rowMap);
		}
	}
	
	@SuppressWarnings("unchecked")
	private <U> FieldData wrapField(Field f, Object value, String name) {
		Class<?> clazz = f.getType();
		if (clazz.isPrimitive()) {
			
			if (clazz.equals(Integer.class) || clazz.equals(int.class))
				return FieldData.simple((Integer) value).name(name);
			if (clazz.equals(Double.class) || clazz.equals(double.class))
				return FieldData.simple((Double) value).name(name);
			if (clazz.equals(Boolean.class) || clazz.equals(boolean.class))
				return FieldData.simple((Boolean) value).name(name);
			System.err.println("Unsupported primitive type: " + clazz);
			return null;
		}
		if (clazz.equals(String.class))
			return FieldData.simple((String) value).name(name);
		
		if (clazz.isAssignableFrom(List.class)) {
			// It's a list
			Class<?> subclazz = (Class<?>) ((ParameterizedType)f.getGenericType()).getActualTypeArguments()[0];
			System.out.println("Debug: Got generic type " + subclazz + " for list: " + clazz);
			
			if (subclazz.equals(String.class))
				return FieldData.listString((List<String>) value).name(name);
			if (subclazz.equals(Integer.class))
				return FieldData.listInt((List<Integer>) value).name(name);
			if (subclazz.equals(Double.class))
				return FieldData.listDouble((List<Double>) value).name(name);
			
			System.err.println("Unsupported list nested type: " + subclazz + " from " + clazz);
			if (subclazz.isPrimitive()) {
				System.err.println("This primitive type isn't supported :(");
			} else {
				System.err.println("List of complex types should be passed through via the "
						+ "two-argument constructor");
			}
			
			return null;
		}
		
		// Not a list, not a primitive. Looking like complex
		
		// TODO nice feature would be registered listeners that could check and see if they're applicable
		// Then pass off to them.
		
		// Nothing else; assume nested complex class
		ObjectDataLoader<?> loader = new ObjectDataLoader<>(value);
		if (loader.getListData() != null) {
			return FieldData.complexList(loader.getFieldMap(), loader.getFormatter(), loader.getListData()).name(name);
		} else
			return FieldData.complex(loader.getFieldMap(), loader.getFormatter()).name(name);
	}
	
	public Map<Integer, FieldData> getFieldMap() {
		if (!valid)
			return null;
		
		return template;
	}
	
	public IEditorDisplayFormatter<Integer> getFormatter() {
		if (!valid)
			return null;
		
		return new IEditorDisplayFormatter<Integer>() {
			@Override
			public String getEditorName(Map<Integer, FieldData> dataMap) {
				FieldData data = dataMap.get(formattingNameIndex);
				if (data instanceof SimpleFieldData) {
					Object o = ((SimpleFieldData) data).getValue();
					return o == null ? "" : o.toString();
				}
				
				// This means we messed up and have a non-simple field for a name
				if (data == null)
					return "";
				
				return data.toString();
			}
			@Override
			public String getEditorTooltip(Map<Integer, FieldData> dataMap) {
				if (formattingDescIndex == -1)
					return null;
				
				FieldData data = dataMap.get(formattingDescIndex);
				if (data instanceof SimpleFieldData) {
					Object o = ((SimpleFieldData) data).getValue();
					return o == null ? "" : o.toString();
				}
				
				// This means we messed up and have a non-simple description
				if (data == null)
					return "";
				
				return data.toString(); // Will look ugly and prompt investigation
			}
		};
	}
	
	public List<Map<Integer, FieldData>> getListData() {
		if (!valid)
			return null;
		
		return listTemplates;
	}
	
}
