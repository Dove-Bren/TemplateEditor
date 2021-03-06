package com.smanzana.templateeditor.api;

import java.awt.Component;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.smanzana.templateeditor.api.annotations.DataLoaderData;
import com.smanzana.templateeditor.api.annotations.DataLoaderDescription;
import com.smanzana.templateeditor.api.annotations.DataLoaderFactory;
import com.smanzana.templateeditor.api.annotations.DataLoaderList;
import com.smanzana.templateeditor.api.annotations.DataLoaderName;
import com.smanzana.templateeditor.api.annotations.DataLoaderRuntimeEnum;
import com.smanzana.templateeditor.data.ComplexFieldData;
import com.smanzana.templateeditor.data.CustomFieldData;
import com.smanzana.templateeditor.data.EnumFieldData;
import com.smanzana.templateeditor.data.MapFieldData;
import com.smanzana.templateeditor.data.ReferenceFieldData;
import com.smanzana.templateeditor.data.SimpleFieldData;
import com.smanzana.templateeditor.data.SubclassFieldData;
import com.smanzana.templateeditor.data.SubsetFieldData;
import com.smanzana.templateeditor.editor.fields.ChildEditorField.GenericFactory;
import com.smanzana.templateeditor.editor.fields.ChildEditorField.TypeResolver;
import com.smanzana.templateeditor.editor.fields.GrabListField.DisplayFormatter;
import com.smanzana.templateeditor.uiutils.TextUtil;
import com.smanzana.templateeditor.uiutils.UIColor;

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
	
	public static interface IFactory<T> {
		/** Construct a new T. All @DataLoaderData fields will be overwritten **/ 
		public T construct();
	}
	
	private static class FieldWrapper {
		public Field field;
		public Object template;
		public String name;
		public String desc;
		public int key;
		public IFactory<?> factory;
		public Field parentField; // Flat-embedded field's parent field
		
		public FieldWrapper(Field field, int key, Object template,
				IFactory<?> factory, String name, String desc,
				Field parentField) {
			this.field = field;
			this.template = template;
			this.name = name;
			this.desc = desc;
			this.key = key;
			this.factory = factory;
			this.parentField = parentField;
		}
	}
	
	private static class DataWrapper {
		public FieldData data;
		public ObjectDataLoader<?> loader;
		public boolean isList;
		
		public static DataWrapper wrap(FieldData data) {
			return new DataWrapper(data, null, false);
		}
		
		public DataWrapper(FieldData data, ObjectDataLoader<?> loader, boolean isList) {
			this.data = data;
			this.loader = loader;
			this.isList = isList;
		}
	}

	private T templateObject; // Raw object for dissolution
	//private List<T> valueList; // Raw list of values
	private int formattingNameIndex; // What index (in template) to use for the name. Not optional.
	private int formattingDescIndex; // what index (in template) to us for description. -1 means none.
	private Map<Integer, FieldWrapper> fieldMap;
	private Map<Integer, DataWrapper> template;
	private boolean passthrough; // no actual fieldmap. Just one datawrapper. pass back val
	private List<Map<Integer, FieldData>> listTemplates; // Templates of initialized data. Used to create later complex data
														 // TODO rip it out. If anything, sore the complex data instead
	private IFactory<T> factory;
	private boolean valid;
	private int keyIndex;
	
	private ObjectDataLoader() {
		valid = false;
		keyIndex = 0;
		formattingNameIndex = -1;
		formattingDescIndex = -1;
		template = new HashMap<>();
		fieldMap = new HashMap<>();
	}
	
	/**
	 * Creates a dataloader based on the passed object.<br />
	 * The provided object is used heavily internally. Continuing to use
	 * the object after constructing the loader off of it produced undefined behavior.
	 * <br />
	 * To create a list of objects instead of a single one, use {@link #ObjectDataLoader(Object, List)} instead.
	 * @param templateObject
	 */
	public ObjectDataLoader(T templateObject) {
		this(templateObject, null, null);
	}
	
	/**
	 * Creates a new ObjectDataLoader to edit the provided list of objects.
	 * If you just need a list of primitives, use the regular {@link #ObjectDataLoader(Object)}
	 * constructor and pass in the list.<br />
	 * The provided object is used heavily internally. Continuing to use
	 * the object after constructing the loader off of it produced undefined behavior.
	 * <br />
	 * 
	 * @param templateObject
	 * @param listItems
	 */
	public ObjectDataLoader(T templateObject, List<T> listItems, IFactory<T> factory) {
		this(templateObject, listItems, factory, "");
	}
	
	private ObjectDataLoader(T templateObject, List<T> listItems, IFactory<?> factory, String empty) {
		this(templateObject, listItems, factory, empty, false);
	}
	
	@SuppressWarnings("unchecked")
	private ObjectDataLoader(T templateObject, List<T> listItems, IFactory<?> factory, String empty, boolean passthrough) {
		this();
		
		this.passthrough = passthrough;
		this.templateObject = templateObject;
		this.factory = (IFactory<T>) factory;
		//this.valueList = listItems;
		
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
		
		extractListMaps(listItems);
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
		dissolve(templateObject, true);
	}
	
	private void dissolve(Object obj, boolean refreshFields) {
		
		// Case 1 : ISuperclass
		if (!passthrough && obj instanceof ISuperclass) {
			// Type marked as having a set of subclasses
			List<ISuperclass> subs = ((ISuperclass) obj).getChildTypes();
			Map<ISuperclass, Map<Integer, FieldData>> dataMaps = new HashMap<>();
			Map<ISuperclass, ObjectDataLoader<?>> loaders = new HashMap<>();
			for (ISuperclass o : subs) {
				ObjectDataLoader<?> loader = new ObjectDataLoader<>(o, null, null, "", true);
				Map<Integer, FieldData> dataMap = loader.getFieldMap();
				dataMaps.put(o, dataMap);
				loaders.put(o, loader);
			}
			
			SubclassFieldData<?, ?> fieldData = FieldData.subclass(
					subs, dataMaps,
					new GenericFactory<ISuperclass, ISuperclass>() {
						@Override
						public ISuperclass constructFromData(ISuperclass type, Map<Integer, FieldData> data) {
							ObjectDataLoader<?> loader = loaders.get(type);
							for (Integer i : data.keySet())
								loader.template.get(i).data = data.get(i);
							return ((ISuperclass) loader.fetchEdittedValue()).cloneObject();
						}

						@Override
						public ISuperclass constructDefault(ISuperclass type) {
							return type;
						}

						@Override
						public ISuperclass constructClone(ISuperclass original) {
							return original.cloneObject();
						}
					},
					new TypeResolver<ISuperclass, ISuperclass>() {
						@Override
						public ISuperclass resolve(ISuperclass obj) {
							for (ISuperclass o : loaders.keySet()) {
								if (o.getChildName(o).equals(obj.getChildName(obj)))
									return o;
							}
							return null;
						}

						@Override
						public Map<Integer, FieldData> breakdown(ISuperclass obj) {
							ObjectDataLoader<?> loader = loaders.get(resolve(obj));
							loader.dissolve(obj, false);
							return loader.getFieldMap();
						}
					},
					(ISuperclass) obj,
					new ListCellRenderer<ISuperclass>() {
						private JLabel label;
						@Override
						public Component getListCellRendererComponent(JList<? extends ISuperclass> arg0,
								ISuperclass arg1, int arg2, boolean arg3, boolean arg4) {
							if (label == null) {
								label = new JLabel();
								label.setOpaque(true);
							}
							
							label.setText(TextUtil.pretty(arg1.getChildName(arg1)));
							
							if (arg3)
								UIColor.setColors(label, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
							else
								UIColor.setColors(label, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND);
							return label;
						}
					}
					);
			
			template.put(0, DataWrapper.wrap(fieldData));
			formattingNameIndex = 0;
			
		} else {
			// Scan for annotations
			Collection<FieldWrapper> fields;
			if (refreshFields) {
				fields = new LinkedList<>();
				collectFields(fields, obj.getClass(), obj);
				// OTHER
				for (FieldWrapper wrapper : fields) {
					wrapper.field.setAccessible(true);
					fieldMap.put(wrapper.key, wrapper);
				}
			} else {
				fields = fieldMap.values();
			}
			
			for (FieldWrapper wrapper : fields) {
				Object parent = obj;
				Field f = wrapper.field;
				//f.setAccessible(true);
				Object val = null;
				try {
					if (wrapper.parentField != null) {
						wrapper.parentField.setAccessible(true);
						parent = wrapper.parentField.get(obj);
						val = f.get(parent);
					} else {
						val = f.get(obj);
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					System.out.println("Failed parsing field " + f.getName() + " on object type " + obj.getClass()
					+ ": " + e.getMessage());
					valid = false;
					return;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					System.out.println("Failed parsing field " + f.getName() + " on object type " + obj.getClass()
					+ ": " + e.getMessage());
					valid = false;
					return;
				}
				
				int key = wrapper.key;
				DataWrapper data = wrapField(f, f.getType(), val, wrapper.template, parent, wrapper.factory, pullName(wrapper), pullDesc(wrapper));
				if (data == null) {
					System.err.println("Could not dissolve field " + f.getName() + " on class " + templateObject.getClass().getName());
					System.err.println("Aborting");
					valid = false;
					return;
				}
				
				template.put(key, data);			
			}
		}
		
		valid = true; // Didn't error, must be valid
	}
	
	private void collectFields(Collection<FieldWrapper> list, Class<?> clazz, Object o) {
		collectFields(list, clazz, o, null);
	}
	
	private void collectFields(Collection<FieldWrapper> list, Class<?> clazz, Object o, Field parent){
		
		if (clazz == Object.class
				|| isPrimitiveType(clazz))
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
			DataLoaderData aData = f.getAnnotation(DataLoaderData.class);
			DataLoaderRuntimeEnum aEnum = f.getAnnotation(DataLoaderRuntimeEnum.class);
			if (aData == null && aList == null && aName == null && aDesc == null && aEnum == null)
				continue;
			
			if (removedNames.contains(f.getName()))
				continue;
			
			Object template = null;
			Class<?> templateClass = null;
			String name = null;
			String desc = null;
			IFactory<?> factory = null;
			
			if (aList != null && aList.templateName() != null) {
				// Search for field by this name in current class
				// Also add it to list of ignored fields
				// Also remove from buffer if already added
				Field reference;
				try {
					reference = clazz.getDeclaredField(aList.templateName());
					reference.setAccessible(true);
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
					templateClass = reference.getType();
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Failed field fetch for referenced field " + reference.getName());
					continue;
				}
				
				if (template == null) {
					System.err.println("Cannot use null template for list in ObjectDataLoader: "
							+ "List: " + f.getName() + " - Template: " + reference.getName() + " - "
							+ "Class: " + clazz.getName());
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
				
				// Need factory. If they supplied a name, use that method in the same class.
				// If not, try default no param constructor
				if (aList.factoryName() != null && !aList.factoryName().trim().isEmpty()) {
					// They provided a name
					Method factoryMethod;
					try {
						factoryMethod = clazz.getDeclaredMethod(aList.factoryName(), (Class<?>[]) null);
						if (!factoryMethod.getReturnType().equals(templateClass)) {
							System.err.println("Found factory, but return type (" + factoryMethod.getReturnType().getName()
									+ ") does not match type of object stored at field ("
									+ templateClass + ") (field: " + f.getName() + "    class: "
									+ clazz.getName() + ")");
							continue;
						}
					} catch (NoSuchMethodException e) {
						System.err.println("Was given factory method name " + aList.factoryName() + " for field "
								+ f.getName() + " (class " + clazz.getName() + ") but lookup failed!");
						continue;
					}
					factory = new IFactory<Object>() {
						@Override
						public Object construct() {
							try {
								factoryMethod.setAccessible(true);
								return factoryMethod.invoke(o, (Object[]) null);
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
							return null;
						}
					};
				
				}
				
				if (factory == null && templateClass.isAnnotationPresent(DataLoaderFactory.class)) {
					DataLoaderFactory aFact = templateClass.getAnnotation(DataLoaderFactory.class);
					// If aFact has a string, use that method as factory.
					// Otherwise, assume 'construct()' is defined
					if (aFact.value() != null && !aFact.value().trim().isEmpty()) {
						String methodname = aFact.value();
						// They provided a name
						Method factoryMethod;
						try {
							factoryMethod = templateClass.getDeclaredMethod(methodname, (Class<?>[]) null);
							if (!factoryMethod.getReturnType().equals(templateClass)) {
								System.err.println("Found factory, but return type (" + factoryMethod.getReturnType().getName()
										+ ") does not match type of object stored at field ("
										+ templateClass + ") (field: " + f.getName() + "    class: "
										+ clazz.getName() + ")");
								continue;
							}
							if (!Modifier.isStatic(factoryMethod.getModifiers())) {
								System.err.println("Found named factory, but it's non-static! ("
									+ "class: " + templateClass.getName() + ")");
							}
						} catch (NoSuchMethodException e) {
							System.err.println("Was given factory method name " + methodname + " for field "
									+ f.getName() + " (class " + templateClass.getName() + ") but lookup failed!");
							continue;
						}
						factory = new IFactory<Object>() {
							@Override
							public Object construct() {
								try {
									return factoryMethod.invoke(null, (Object[]) null);
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								}
								return null;
							}
						};
					}
					
					if (factory == null) {
						// Assume a 'construct()' method
						String methodname = "construct";
						// They provided a name
						Method factoryMethod;
						try {
							factoryMethod = templateClass.getDeclaredMethod(methodname, (Class<?>[]) null);
							if (!factoryMethod.getReturnType().equals(templateClass)) {
								System.err.println("Found factory, but return type (" + factoryMethod.getReturnType().getName()
										+ ") does not match type of object stored at field ("
										+ templateClass + ") (field: " + f.getName() + "    class: "
										+ templateClass.getName() + ")");
								continue;
							}
							if (!Modifier.isStatic(factoryMethod.getModifiers())) {
								System.err.println("Found default (construct) factory, but it's non-static! ("
									+ "class: " + templateClass.getName() + ")");
							}
						} catch (NoSuchMethodException e) {
							System.err.println("Could not find construct() factory method for @DataLoaderFactory marked class " 
									+ templateClass.getName() + ".");
							continue;
						}
						factory = new IFactory<Object>() {
							@Override
							public Object construct() {
								try {
									return factoryMethod.invoke(null, (Object[]) null);
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								}
								return null;
							}
						};
					}
				}
				
				if (factory == null && !Modifier.isAbstract(templateClass.getModifiers())
						&& !Modifier.isInterface(templateClass.getModifiers())) {
					// Try no-param constructor
					Constructor<?> cons;
					try {
						cons = templateClass.getConstructor((Class<?>[]) null);
					} catch (NoSuchMethodException | SecurityException e) {
						System.err.println("Could not find default constructor for nested class "
								+ templateClass.getName() + " (Listed in class "
								+ clazz.getName() + " | field " + f.getName() + ")");
						continue;
					}
					factory = new IFactory<Object>() {
						@Override
						public Object construct() {
							try {
								return cons.newInstance((Object[]) null);
							} catch (InstantiationException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
							return null;
						}
					};
				}
				
				if (factory == null) {
					System.err.println("Unable to resolve factory for complex list type " + templateClass.getName()
					 + " (Field: " + reference.getName() + " | class " + clazz.getName());
				}
			}
			
			if (factory == null && Map.class.isAssignableFrom(f.getType())) {
				// To a little special factory stuff for maps
				//Class<?> keyclazz = (Class<?>) ((ParameterizedType)f.getGenericType()).getActualTypeArguments()[0];
				Class<?> valueclazz = (Class<?>) ((ParameterizedType)f.getGenericType()).getActualTypeArguments()[1];
				
				if (valueclazz.equals(Integer.class)) {
					if (template == null)
						template = FieldData.simple(0);
					factory = new IFactory<Integer>() {
						@Override
						public Integer construct() {
							return 0;
						}
					};
				} else if (valueclazz.equals(Double.class)) {
					if (template == null)
						template = FieldData.simple(0.0);
					factory = new IFactory<Double>() {
						@Override
						public Double construct() {
							return 0.0;
						}
					};
				} else if (valueclazz.equals(Boolean.class)) {
					if (template == null)
						template = FieldData.simple(false);
					factory = new IFactory<Boolean>() {
						@Override
						public Boolean construct() {
							return false;
						}
					};
				} else if (valueclazz.equals(String.class)) {
					if (template == null)
						template = FieldData.simple("");
					factory = new IFactory<String>() {
						@Override
						public String construct() {
							return "";
						}
					};
				}
			}
			
			if (aData != null && aData.expand()) {
				// Don't do regular stuff;
				// extract from nested type and then continue
				Class<?> expandType = f.getType();
				if (ISuperclass.class.isAssignableFrom(expandType)) {
					System.out.println("Warning: ISuperclass marked object being expanded. These tags are incompatible!");
				}
				try {
					f.setAccessible(true);
					collectFields(list, expandType, f.get(o), f);
				} catch (Exception e) {
					System.err.println("Encountered error fetching element for recursion: " + f.getName());
					e.printStackTrace();
					continue;
				}
				
				continue;
			}
			
			int key = nextKey();
			
			// extract name and desc from DataLoaderData
			if (aData != null) {
				if (aData.name() != null && !aData.name().trim().isEmpty())
					name = aData.name();
				if (aData.description() != null && !aData.description().trim().isEmpty())
					desc = aData.description();
			}
			
			if (aName != null) // If this field is the display name
			if (nameKey == -1) // no other display name has been set
				nameKey = key;
			
			if (aDesc != null)
			if (descKey == -1)
				descKey = key;
			
			buffer.put(f.getName(),
					new FieldWrapper(f, key, template, factory, name, desc, parent));
			
		}
		
		list.addAll(buffer.values());
		
		// If nameField or DescField made it in (not ignored), set index appropriately
		if (nameKey != -1 && formattingNameIndex == -1)
			formattingNameIndex = nameKey;
		
		if (descKey != -1 && formattingDescIndex == -1)
			formattingDescIndex = descKey;
		
		collectFields(list, clazz.getSuperclass(), o);
		
	}
	
	// Takes template and creates copies for each list value
	private void extractListMaps(List<T> valueList) {
		// Assumes we're valid
		
		if (valueList == null)
			return;
		
		listTemplates = new ArrayList<>(valueList.size());
		
		for (T item : valueList) {
			// For each item, create a copy of template by pulling out fields
			
			Map<Integer, FieldData> rowMap = new HashMap<>();
			if (fieldMap.isEmpty()) {
				@SuppressWarnings("unchecked")
				SubclassFieldData<?,T> d = (SubclassFieldData<?,T>) template.get(0).data.clone();
				d.setValue(item);
				rowMap.put(0, d);
			} else
			for (Entry<Integer, FieldWrapper> row : fieldMap.entrySet()) {
				// Type-safe cause of generics; we know these fields exist in list objects
				Field field = row.getValue().field;
				try {
					rowMap.put(row.getKey(), wrapField(field, field.getType(), field.get(item), row.getValue().template, item, row.getValue().factory,
							pullName(row.getValue()), pullDesc(row.getValue())).data);
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
	
	private String pullName(FieldWrapper wrapper) {
		return wrapper.name == null ? TextUtil.pretty(wrapper.field.getName()) : wrapper.name; 
	}
	
	private String pullDesc(FieldWrapper wrapper) {
		return wrapper.desc; 
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <U> DataWrapper wrapField(Field f, Class<?> clazz, Object value, Object listTemplate, Object parent, IFactory<?> factory, String name, String description) {
		//Class<?> clazz = f.getType();
		
		// First! runtime enumerable? :P
		if (f != null)
		{
			DataLoaderRuntimeEnum aEnum = f.getAnnotation(DataLoaderRuntimeEnum.class);
			if (aEnum != null) {
				// All values known. Use a reference field
				Map<String, Object> values;
				if (IRuntimeEnumerable.class.isAssignableFrom(parent.getClass())) {
					values = ((IRuntimeEnumerable) parent).fetchValidValues(
							aEnum.value());
				} else if (aEnum.value() != null && !aEnum.value().trim().isEmpty()) {
					try {
						Method enumerator = parent.getClass().getDeclaredMethod(aEnum.value(), String.class);
						values = (Map<String, Object>) enumerator.invoke(parent);
					} catch (Exception e) {
						values = null;
						e.printStackTrace();
						System.err.println("Unable to find declare enumeration method: " + aEnum.value());
					}
				} else {
					values = null;
					System.err.println("Field marked as runtime-enumeration but "
							+ "no value-fetching method supplied!");
				}
				
				if (values != null) {
					// If single, FieldData.reference
					// If list, FieldData.subset
					if (value instanceof List) {
						List<Object> valueList = new ArrayList<>(values.values());
						final Map<String, Object> ref = values;
						return DataWrapper.wrap(
							FieldData.subset(valueList, (List) value,
									new DisplayFormatter<Object>() {

										@Override
										public String getListDataName(Object data) {
											for (Entry<String, Object> row : ref.entrySet())
												if (row.getValue().equals(data))
													return row.getKey();
											
											return "X> " + data.toString() + " <X";
										}

										@Override
										public String getListDataDesc(Object data) {
											return null;
										}
							}).name(name).desc(description)
						);
					} else {
						return DataWrapper.wrap(
							FieldData.reference(values, value)
							.name(name).desc(description)
						);
					}
				}
			}
		}
		
		if (isPrimitiveType(clazz)) {
			
			if (clazz.equals(Integer.class) || clazz.equals(int.class))
				return DataWrapper.wrap(FieldData.simple((Integer) (value == null ? 0 : value)).name(name).desc(description));
			if (clazz.equals(Double.class) || clazz.equals(double.class))
				return DataWrapper.wrap(FieldData.simple((Double) (value == null ? 0.0 : value)).name(name).desc(description));
			if (clazz.equals(Boolean.class) || clazz.equals(boolean.class))
				return DataWrapper.wrap(FieldData.simple((Boolean) (value == null ? false : value)).name(name).desc(description));
			System.err.println("Unsupported primitive type: " + clazz);
			return null;
		}
		if (clazz.equals(String.class))
			return DataWrapper.wrap(FieldData.simple((String) (value == null ? "" : value)).name(name).desc(description));
		if (clazz.isEnum())
			return DataWrapper.wrap(FieldData.enumSelection((Enum) value).name(name).desc(description));
		
		if (clazz.isAssignableFrom(List.class)) {
			// It's a list
			if (f == null) {
				// Nested list in something like a map. DO NOT SUPPORT
				System.out.println("Found list nested under a complex field "
						+ (name == null ? "" : "(" + name + ")")
						+ ". This is not supported.");
				return null;
			}
			Class<?> subclazz = (Class<?>) ((ParameterizedType)f.getGenericType()).getActualTypeArguments()[0];
			
			if (subclazz.equals(String.class))
				return DataWrapper.wrap(FieldData.listString((List<String>) value).name(name).desc(description));
			if (subclazz.equals(Integer.class))
				return DataWrapper.wrap(FieldData.listInt((List<Integer>) value).name(name).desc(description));
			if (subclazz.equals(Double.class))
				return DataWrapper.wrap(FieldData.listDouble((List<Double>) value).name(name).desc(description));
			
			if (isPrimitiveType(subclazz)) {
				System.err.println("Unsupported list nested type: " + subclazz + " from " + clazz);
				System.err.println("This primitive type isn't supported :(");
				return null;
			}
			
			// Try to use template
			if (listTemplate != null) {
				
				//Check if it's a piece of CustomData
				if (directlyImplements(subclazz, ICustomData.class)) {
					List<ICustomData> customlist = new LinkedList<>();
					for (ICustomData d : (List<? extends ICustomData>) value) {
						customlist.add(d);
					}
					return DataWrapper.wrap(new CustomFieldData((ICustomData) listTemplate, customlist)
							.name(name).desc(description));
				}
				
				ObjectDataLoader<?> loader = new ObjectDataLoader<>(listTemplate, (List) value, factory, "");
				DataWrapper wrapper = new DataWrapper(
						FieldData.complexList(loader.getFieldMap(), loader.getFormatter(), loader.getListData())
						.name(name).desc(description),
						loader, true);
				return wrapper;
			}
			
			System.err.println("Unsupported list nested type: " + subclazz + " from " + clazz);
			System.err.println("Mark complex lists with @DataLoaderList and point to a template base class");
			return null;
		}
		
		if (Map.class.isAssignableFrom(clazz)){
			
			if (f == null) {
				// Nested map in something like a map. DO NOT SUPPORT
				System.out.println("Found map nested under a complex field "
						+ (name == null ? "" : "(" + name + ")")
						+ ". This is not supported.");
				return null;
			}
			
			Class<?> keyclazz = (Class<?>) ((ParameterizedType)f.getGenericType()).getActualTypeArguments()[0];
			Class<?> valueclazz = (Class<?>) ((ParameterizedType)f.getGenericType()).getActualTypeArguments()[1];
			Map<Object, FieldData> dataMap;
			Map<?, ?> map = (Map<?, ?>) value;
			// Mimic the map type of value
			if (map instanceof EnumMap) {
				dataMap = (Map<Object, FieldData>) newEnumMap((Class<? extends Enum>)keyclazz);
			} else if (map instanceof TreeMap) {
				dataMap = new TreeMap<>();
			} else if (map instanceof LinkedHashMap) {
				dataMap = new LinkedHashMap<>();
			} else {
				dataMap = new HashMap<>();
			}
			
			for (Entry<?, ?> row : map.entrySet()) {
				if (row.getValue() == null)
					dataMap.put(row.getKey(), null);
				else {
					DataWrapper subwrapper = wrapField(null, valueclazz, row.getValue(), null, null, null, null, null);
					// really only care about the FieldData we get with the wrapper
					dataMap.put(row.getKey(), subwrapper.data);
				}
			}
			
			// Deduce a template for adding
			FieldData template = null;
			ObjectDataLoader<?> templateLoader = null;
			if (factory != null) {
				if (listTemplate == null) {
					listTemplate = factory.construct();
				}
				
				if (listTemplate instanceof FieldData) {
					// We may have already figured out a template
					template = (FieldData) listTemplate;
				} else if (directlyImplements(valueclazz, ICustomData.class)) {
					template = FieldData.custom((ICustomData) listTemplate);
				} else {
					templateLoader = new ObjectDataLoader<>(listTemplate, null, factory, "");
					template = FieldData.complex(templateLoader.getFieldMap(), templateLoader.getFormatter());
				}
			} else {
				System.err.println("Unable to resolve factory for map data! (Field: " + f.getName() + " | class " + valueclazz + ")");
				System.err.println("Supply a factory when mapping to non-primitive data with the list annotation.");
			}
			
			DataWrapper wrapper = DataWrapper.wrap(FieldData.map(dataMap, template)
					.name(name).desc(description));
			wrapper.loader = templateLoader;
			return wrapper;
		}
		
		// Not a list, not a primitive. Looking like complex
		
		// TODO nice feature would be registered listeners that could check and see if they're applicable
		// Then pass off to them.
		
		// None of the above. Check if it's a piece of CustomData
		if (directlyImplements(clazz, ICustomData.class)) {
			ICustomData custom = (ICustomData) value;
			return DataWrapper.wrap(FieldData.custom(custom).name(name).desc(description));
		}
		
		// Nothing else; assume nested complex class
		ObjectDataLoader<?> loader = new ObjectDataLoader<>(value);
		DataWrapper wrapper = new DataWrapper(
		//if (loader.getListData() != null) {
		//	return FieldData.complexList(loader.getFieldMap(), loader.getFormatter(), loader.getListData()).name(name).desc(description);
		//} else
			FieldData.complex(loader.getFieldMap(), loader.getFormatter()).name(name).desc(description),
			loader, false);
		return wrapper;
	}
	
	private Map<Integer, FieldData> unwrapData(Map<Integer, DataWrapper> map) {
		Map<Integer, FieldData> ret = new HashMap<>();
		
		for (Entry<Integer, DataWrapper> row : map.entrySet()) {
			ret.put(row.getKey(), row.getValue().data);
		}
		
		return ret;
	}
	
	private <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Class<K> clazz) {
		return new EnumMap<K, V>(clazz);
	}
	
	public Map<Integer, FieldData> getFieldMap() {
		if (!valid)
			return null;
		
		return unwrapData(template);
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
	
	public void updateData(Map<Integer, FieldData> dataMap) {
		if (listTemplates != null && listTemplates.size() > 0) {
			System.err.println("updateData does not work for list objects!");
		}
		
		for (Integer i : dataMap.keySet()) {
			template.get(i).data = dataMap.get(i);
		}
	}
	
	/**
	 * Returns the object that represents the current state of the values being editted.
	 * 
	 * <br />
	 * The object returned is the same future fetchEditedValue calls will edit and
	 * return. As such, only fetch and keep a reference to the value if you know
	 * no more edits are going to be made.<br />
	 * All values that are not @DataLoaderData or similar remain in the same state as
	 * passed in, except if modified from outside
	 * @return
	 */
	public T fetchEdittedValue() {
		return formSingle();
	}
	
	// Secret! Internal method! hehe
	// pulls out data from the given field and replaces them in template, then fetches like regular
	private T fetchEdittedValue(ComplexFieldData data) {
		for (Entry<Integer, FieldData> row : data.getNestedTypes().entrySet()) {
			template.get(row.getKey()).data = row.getValue();
		}
		return fetchEdittedValue();
		
	}
	
	/**
	 * Just like fetchEdittedValue except returns the modified list
	 * of objects provided during construction.
	 * @return
	 */
	public List<T> fetchEdittedList(ComplexFieldData data) {
		return formList(data);
	}
	
	private T formSingle() {
		// Opposite of Dissolve hehe
		// Use map of int keys to get data from template+listTemplates.
		// Use same keys and fieldMap to find the field to insert it into.
		// Insert into templateObject or each element of valueList if non-null
		
		templateObject = formSingleElement(templateObject, template);
		return templateObject;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object fetchValue(DataWrapper data, FieldWrapper wrapper, Object oldVal) {
		Object val = null;
		
		if (data.data instanceof SimpleFieldData) {
			val = ((SimpleFieldData)data.data).getValue();
		} else if (data.data instanceof EnumFieldData<?>) {
			val = ((EnumFieldData<?>) data.data).getSelection();
		} else if (data.data instanceof ComplexFieldData) {
			// We MUST have a loader, then
			if (data.loader == null) {
				System.err.println("Found complex data with no associated loader!");
				(new Exception()).printStackTrace();
				val = null;
			}
			ComplexFieldData cfd = (ComplexFieldData) data.data;
			if (data.isList) {
				data.loader.listTemplates = cfd.getListData();
				val = data.loader.fetchEdittedList((ComplexFieldData) data.data);
			} else {
				val = data.loader.fetchEdittedValue(cfd);
			}
		} else if (data.data instanceof CustomFieldData) {
			CustomFieldData cfd = (CustomFieldData) data.data;
			if (cfd.isList()) {
				val = cfd.getDataList();
			} else
				val = cfd.getData();
		} else if (data.data instanceof SubclassFieldData) {
			val = ((SubclassFieldData<?, ?>) data.data).getValue();
		} else if (data.data instanceof MapFieldData) {
			Map<Object, Object> map = new LinkedHashMap<>();
			Map<Object, Object> oldMap = (Map<Object, Object>) oldVal;
			Map<?, ?> retMap = ((MapFieldData<?>) data.data).getMapping();
			for (Object k : retMap.keySet()) {
				FieldData d = (FieldData) retMap.get(k);
				
				// Load up loader with val to set fields on
				if (data.loader != null) { // If not a simple no-loader field
					((ObjectDataLoader) data.loader).templateObject = oldMap.get(k);
					if (data.loader.templateObject == null) // construct a new one instead
						((ObjectDataLoader) data.loader).templateObject = wrapper.factory.construct();
				}
				
				if (d == null)
					map.put(k, null);
				else
					map.put(k, fetchValue(new DataWrapper(d, data.loader, false), wrapper, null));
			}
			val = map;
		} else if (data.data instanceof ReferenceFieldData) {
			val = ((ReferenceFieldData<?>) data.data).getSelection();
		} else if (data.data instanceof SubsetFieldData) {
			val = ((SubsetFieldData<?>) data.data).getSelection();
		} else {
			System.err.println("Missing case handler in ObjectDataLoader (formSingleElement X fetchValue");
		}
		
		return val;
	}

	@SuppressWarnings("unchecked")
	private T formSingleElement(T obj, Map<Integer, DataWrapper> dataMap) {
		// Use fields from fieldMap
		for (Integer key : dataMap.keySet()) {
			FieldWrapper wrapper = fieldMap.get(key);
			DataWrapper data = dataMap.get(key);
			
			// TODO user added registration to counter dissolve here
			
			try {
				Object oldVal = null;
				if (wrapper != null) {
					if (wrapper.parentField != null) {
						// Not actually on obj but nested
						Object actual = wrapper.parentField.get(obj);
						oldVal = wrapper.field.get(actual);
					} else {
						oldVal = wrapper.field.get(obj);
					}
				}
				Object val = fetchValue(data, wrapper, oldVal);
				if (val == null)
					continue;
				
				// Passthrough mode logic
				if (fieldMap.isEmpty())
					return (T) val;
			
				if (wrapper.parentField != null) {
					// Not actually on obj but nested
					Object actual = wrapper.parentField.get(obj);
					wrapper.field.set(actual, val);
				} else {
					wrapper.field.set(obj, val);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				System.err.println("Could not convert back to original data type for field "
						+ wrapper.field.getName());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				System.err.println("Encountered Access Violation on setting field "
						+ wrapper.field.getName());
			}
		}
		
		return obj;
	}
	
	private List<T> formList(ComplexFieldData fieldData) {
		// We are a loader that was given a list.
		// That means template is our template and fieldMap is a map per object
		// We need to get list of maps from complex list object, and then
		// for each one, do a formSingle
		// That means we'll hvae to create T's...
		
		// We have a factory. We can do this.
		// For each map passed back, construct a factory and then formSingleElement on it
		
		// We need something to ask for maps to iterate over.
		// We should either get or have a handle to the source of our list.
		// Likely we should have it. So invert the data flow for objectloaders
		// that are lists; instead of creating a complex from it, the loader
		// creates the complex and you can get a reference to it
		
		List<T> output = new LinkedList<>();
		
		if (fieldData != null)
		for (Map<Integer, FieldData> realValues : fieldData.getListData()) {
			T obj = factory.construct();
			
			for (Integer key : realValues.keySet()) {
				// Grant real FieldData onto template's data wrapper
				template.get(key).data = realValues.get(key);
			}
			
			obj = formSingleElement(obj, template);
			
			output.add(obj);
		}
		
		return output;
	}
	
	public static final boolean isPrimitiveType(Class<?> clazz) {
		if (clazz.isPrimitive())
			return true;
		return (clazz.equals(Integer.class)
			|| clazz.equals(Boolean.class)
			|| clazz.equals(Double.class));
	}
	
	public static final boolean directlyImplements(Class<?> clazz, Class<?> iface) {
		if (clazz == null || iface == null)
			return false;
		Class<?>[] types = clazz.getInterfaces();
		for (Class<?> t : types)
			if (t.equals(iface))
				return true;
		
		return false;
	}
	
}
