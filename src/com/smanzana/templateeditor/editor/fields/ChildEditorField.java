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
	
//	private static abstract class ValueEditor<T extends ValueSpecifier> extends JPanel {
//		private static final long serialVersionUID = 1L;
//
//		public abstract T getValueSpecifier();
//		
//		protected ValueEditor() {
//			super();
//			color();
//		}
//		
//		protected void color() {
//			color(this);
//		}
//		
//		protected void color(JComponent comp) {
//			UIColor.setColors(comp, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
//		}
//	}
//	
//	private static class ConstantField extends ValueEditor<ValueConstant> {
//		
//		private static final long serialVersionUID = 3128237228839503771L;
//		private IntField field;
//		
//		public ConstantField(IEditorOwner owner, int startingVal) {
//			super();
//
//			field = new IntField(startingVal);
//			field.setOwner(owner);
//			JLabel label = new JLabel("Constant Value");
//			UIColor.setColors(label, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
//			this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
//			this.add(Box.createRigidArea(new Dimension(10, 0)));
//			this.add(label);
//			this.add(Box.createRigidArea(new Dimension(20, 0)));
//			color(field.getComponent());
//			this.add(field.getComponent());
//			this.add(Box.createRigidArea(new Dimension(10, 0)));
//		}
//		
//		public int getValue() {
//			return field.getObject();
//		}
//		
//		public void setValue(int value) {
//			field.setObject(value);
//		}
//
//		@Override
//		public ValueConstant getValueSpecifier() {
//			return new ValueConstant(getValue());
//		}
//	}
//	
//	private static class RangeField extends ValueEditor<ValueRange> implements IEditorOwner {
//		
//		private static final long serialVersionUID = 312823727939503771L;
//		private IntField fieldMin;
//		private IntField fieldMax;
//		
//		public RangeField(IEditorOwner owner, int startingMin, int startingMax) {
//			super();
//
//			fieldMin = new IntField(startingMin);
//			fieldMax = new IntField(startingMax);
//			fieldMin.setOwner(owner);
//			fieldMax.setOwner(owner);
//			JLabel label = new JLabel("Value Range: ");
//			color(label);
//			this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
//			this.add(Box.createRigidArea(new Dimension(10, 0)));
//			this.add(label);
//			this.add(Box.createRigidArea(new Dimension(20, 0)));
//			color(fieldMin.getComponent());
//			this.add(fieldMin.getComponent());
//			this.add(Box.createRigidArea(new Dimension(10, 0)));
//			label = new JLabel(" - ");
//			color(label);
//			this.add(label);
//			this.add(Box.createRigidArea(new Dimension(10, 0)));
//			color(fieldMax.getComponent());
//			this.add(fieldMax.getComponent());
//			this.add(Box.createRigidArea(new Dimension(10, 0)));
//
//		}
//		
//		public int getMin() {
//			return fieldMin.getObject();
//		}
//		
//		public int getMax() {
//			return fieldMax.getObject();
//		}
//		
//		public void setValues(int min, int max) {
//			fieldMin.setObject(min);
//			fieldMax.setObject(max);
//		}
//
//		@Override
//		public void dirty() {
//			int min = getMin(),
//					max = getMax();
//			if (min > max) {
//				fieldMin.setObject(max);
//				fieldMax.setObject(min);
//			} else if (min == max) {
//				fieldMax.setObject(min + 1);
//			}
//		}
//
//		@Override
//		public ValueRange getValueSpecifier() {
//			return new ValueRange(getMin(), getMax());
//		}
//	}
//	
//	private static class DiceField extends ValueEditor<Dice> implements IEditorOwner {
//		
//		private static final long serialVersionUID = 31246464939673771L;
//		private IntField fieldNum;
//		private IntField fieldFaces;
//		private BoolField fieldZero;
//		
//		public DiceField(IEditorOwner owner, int startingNum, int startingFaces, boolean includeZero) {
//			super();
//
//			fieldNum = new IntField(startingNum);
//			fieldNum.setOwner(owner);
//			fieldFaces = new IntField(startingFaces);
//			fieldFaces.setOwner(owner);
//			fieldZero = new BoolField(includeZero);
//			fieldZero.setOwner(owner);
//			JLabel label = new JLabel("Dice: ");
//			color(label);
//			this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
//			this.add(Box.createRigidArea(new Dimension(10, 0)));
//			this.add(label);
//			this.add(Box.createRigidArea(new Dimension(20, 0)));
//			color(fieldNum.getComponent());
//			this.add(fieldNum.getComponent());
//			label = new JLabel("d");
//			color(label);
//			label.setHorizontalAlignment(JLabel.CENTER);
//			label.setBorder(null);
//			this.add(label);
//			color(fieldFaces.getComponent());
//			this.add(fieldFaces.getComponent());
//			this.add(Box.createRigidArea(new Dimension(30, 0)));
//			label = new JLabel("0-bounded: ");
//			color(label);
//			this.add(label);
//			this.add(Box.createRigidArea(new Dimension(5, 0)));
//			color(fieldZero.getComponent());
//			this.add(fieldZero.getComponent());
//			this.add(Box.createRigidArea(new Dimension(10, 0)));
//			this.add(Box.createHorizontalGlue());
//
//		}
//		
//		public int getNum() {
//			return fieldNum.getObject();
//		}
//		
//		public int getFaces() {
//			return fieldFaces.getObject();
//		}
//		
//		public boolean getZero() {
//			return fieldZero.getObject();
//		}
//		
//		public void setValues(int num, int faces, boolean zero) {
//			fieldNum.setObject(num);
//			fieldFaces.setObject(faces);
//			fieldZero.setObject(zero);
//		}
//
//		@Override
//		public void dirty() {
//			int num = getNum(),
//				faces = getFaces();
//			
//			if (num < 1)
//				fieldNum.setObject(1);
//			if (faces < 2)
//				fieldFaces.setObject(2);
//		}
//
//		@Override
//		public Dice getValueSpecifier() {
//			return new Dice(getNum(), getFaces(), getZero());
//		}
//	}
//	
//	private static class DiceSetField extends ValueEditor<DiceSet> {
//		
//		private static final long serialVersionUID = -1107297129151127038L;
//
//		private static class DiceSetFieldData extends FieldData {
//			private DiceField field;
//			private IEditorOwner cloneOwner;
//			
//			public DiceSetFieldData(IEditorOwner owner, DiceField field) {
//				this.field = field;
//				cloneOwner = owner;
//			}
//			
//			@Override
//			public FieldData clone() {
//				return new DiceSetFieldData(cloneOwner, new DiceField(
//						cloneOwner,
//						field.getNum(),
//						field.getFaces(),
//						field.getZero()
//						));
//			}
//
//			@Override
//			public EditorField<?> constructField() {
//				return new EditorField<DiceField>() {
//
//					@Override
//					public JComponent getComponent() {
//						return field;
//					}
//
//					@Override
//					public DiceField getObject() {
//						return null;
//					}
//
//					@Override
//					public void setObject(DiceField obj) {
//						;
//					}
//
//					@Override
//					public DiceField getOriginal() {
//						return field;
//					}
//
//					@Override
//					public void setOwner(IEditorOwner owner) {
//						;
//					}
//				};
//			}
//
//			@Override
//			public void fillFromField(EditorField<?> field) {
//				;
//			}
//		}
//		
//		private GenericListField<DiceSetFieldData> list;
//		
//		public DiceSetField(IEditorOwner owner, Dice template, List<Dice> inputList) {
//			super();
//
//			List<DiceSetFieldData> dataList = new ArrayList<>(inputList.size());
//			for (Dice f : inputList)
//				dataList.add(new DiceSetFieldData(owner, new DiceField(owner, f.getDieCount(), f.getDieFaces(), f.includesZero())));
//			list = new GenericListField<>(new DiceSetFieldData(owner, new DiceField(owner, template.getDieCount(), template.getDieFaces(), template.includesZero())), dataList);
//			list.setOwner(owner);
//			color(list.getComponent());
//			JLabel label = new JLabel("Dice Set: ");
//			color(label);
//			this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
//			this.add(Box.createRigidArea(new Dimension(10, 0)));
//			this.add(label);
//			this.add(Box.createRigidArea(new Dimension(20, 0)));
//			this.add(list.getComponent());
//			this.add(Box.createRigidArea(new Dimension(10, 0)));
//
//		}
//		
//		public List<Dice> getDice() {
//			List<DiceSetFieldData> data = list.getObject();
//			List<Dice> out = new ArrayList<>(data.size());
//			for (DiceSetFieldData d : data)
//				out.add(d.field.getValueSpecifier());
//			return out;
//		}
//		
//		public void setValues(IEditorOwner owner, List<Dice> newDice) {
//			List<DiceSetFieldData> dataList = new ArrayList<>(newDice.size());
//			for (Dice f : newDice)
//				dataList.add(new DiceSetFieldData(owner, new DiceField(owner, f.getDieCount(), f.getDieFaces(), f.includesZero())));
//			
//			list.setObject(dataList);
//		}
//
//		@Override
//		public DiceSet getValueSpecifier() {
//			DiceSet set = new DiceSet();
//			for (Dice f : getDice()) {
//				set.addDice(f);
//			}
//			return set;
//		}
//	}
	
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
