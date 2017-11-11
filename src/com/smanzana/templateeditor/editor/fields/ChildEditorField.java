package com.smanzana.templateeditor.editor.fields;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.smanzana.templateeditor.IEditorOwner;
import com.smanzana.templateeditor.api.FieldData;
import com.smanzana.templateeditor.editor.TemplateEditor;
import com.smanzana.templateeditor.uiutils.UIColor;

/**
 * Special editor field which is actually a sub-editor.
 * Made for creating one of a handful types of children.
 * This works great for a class that has a reference to an abstract type
 * (like Item) that has enumerable concrete children (Equipment, Junk, Weapon)
 * that you'd like to be able to construct.
 * <p>
 * The idea here is you provide the editor with a list of 'types' and
 * a map between type and type-specification information.
 * That is, you provide a list of types and a map of type to submap between
 * integer and field data.
 * </p>
 * You get a combo box for free to switch type. The field also takes care of
 * displaying the correct fields to operate like a regular editor when
 * given a data map.
 * @author Skyler
 *
 */
public class ChildEditorField<T, O> extends AEditorField<O> implements ItemListener, IEditorOwner {
		
	public static interface GenericFactory<T, O> {
		/**
		 * Take a type and map of data and construct an object from it.
		 * @param type The type the data corresponds to
		 * @param data The raw data map
		 * @return An object constructed from the data
		 */
		public O constructFromData(T type, Map<Integer, FieldData> data);
		
		/**
		 * Like {@link #constructFromData(Object, Map)}, but without the data.
		 * Used internally to deal with null types. We have to do something!
		 * @param type
		 * @return
		 */
		public O constructDefault(T type);
		
		/**
		 * Construct a clone object.
		 * @param original
		 * @return
		 */
		public O constructClone(O original);
	}
	
	public static interface TypeResolver<T, O> {
		/**
		 * Resolve an object into it's subtype.
		 * @param obj
		 * @return
		 */
		public T resolve(O obj);
		
		/**
		 * Take an object and produce the corresponding data map
		 * @param obj
		 * @return
		 */
		public Map<Integer, FieldData> breakdown(O obj);
	}
	
	private JPanel wrapper;
	private Map<T, TemplateEditor<Integer>> typeEditors;
	private T currentType;
	private T defaultType;
	private GenericFactory<T, O> factory;
	private TypeResolver<T, O> resolver;
	
	public ChildEditorField(List<T> types, Map<T, Map<Integer, FieldData>> maps,
			GenericFactory<T, O> factory, O current) {
		this(types, maps, factory, current, null, null);
	}
	
	public ChildEditorField(List<T> types, Map<T, Map<Integer, FieldData>> maps,
			GenericFactory<T, O> factory, O current, TypeResolver<T, O> resolver) {
		this(types, maps, factory, current, resolver, null);
	}
	
	public ChildEditorField(List<T> types, Map<T, Map<Integer, FieldData>> maps,
			GenericFactory<T, O> factory, O current, TypeResolver<T, O> resolver,
			ListCellRenderer<T> customRenderer){
		this.factory = factory;
		this.resolver = resolver;
		
		typeEditors = new HashMap<>();
		
		JComboBox<T> comboField = new JComboBox<>();
		for (T t : types)
			comboField.addItem(t);
		
		defaultType = types.get(0);
		
		comboField.setEditable(false);
		if (customRenderer != null)
			comboField.setRenderer(customRenderer);
		
		comboField.addItemListener(this);
		comboField.setMaximumSize(new Dimension(Short.MAX_VALUE, comboField.getPreferredSize().height));
		
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.PAGE_AXIS));
		UIColor.setColors(wrapper, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
		wrapper.add(Box.createRigidArea(new Dimension(0, 10)));
		JLabel label = new JLabel("Type");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setAlignmentX(.5f);
		UIColor.setColors(label, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
		wrapper.add(label);
		wrapper.add(comboField);
		wrapper.add(Box.createRigidArea(new Dimension(0, 20)));
		
		for (T type : types) {
			TemplateEditor<Integer> editor = new TemplateEditor<Integer>(
					this, maps.get(type));
			typeEditors.put(type, editor);
			editor.setVisible(false);
			wrapper.add(editor);
		}
		
		
		wrapper.add(Box.createRigidArea(new Dimension(0, 10)));

		this.setObject(current);
		comboField.setSelectedItem(currentType);
	}
	
	private void updateField(T newType) {
		
		if (currentType != null)
			typeEditors.get(currentType).setVisible(false);
		typeEditors.get(newType).setVisible(true);

		currentType = newType;
		
		wrapper.setSize(new Dimension(wrapper.getWidth(), (int) wrapper.getPreferredSize().getHeight()));
		
		wrapper.validate();
		wrapper.repaint();
	}
	
	public JPanel getComponent() {
		return wrapper;
	}

	@Override
	public O getObject() {
		return factory.constructFromData(currentType, typeEditors.get(currentType).fetchData());
	}

	@Override
	public void setCurrentObject(O obj) {
		T newType;
		if (obj == null) {
			//newType = defaultType;
			obj = factory.constructDefault(defaultType);
		}
		
		// deduce type
		if (resolver == null) {
			System.err.println("Cannot set value of ChildEditorField as "
					+ "no resolver was provided!");
			return;
		}
		
		newType = resolver.resolve(obj);
		wrapper.remove(typeEditors.get(newType));
		TemplateEditor<Integer> editor = new TemplateEditor<Integer>(this, resolver.breakdown(obj));
		typeEditors.put(newType, editor);
		wrapper.add(editor);
		
		updateField(newType);
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		if (arg0.getStateChange() != ItemEvent.SELECTED)
			return;
		
		@SuppressWarnings("unchecked")
		T newType = (T) arg0.getItem();
		if (newType == currentType)
			return;
		
		updateField(newType);
		markDirty();
	}
	
	@Override
	public void dirty() {
		markDirty();
	}
}
