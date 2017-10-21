package com.smanzana.templateeditor.editor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.smanzana.templateeditor.FieldData;
import com.smanzana.templateeditor.uiutils.UIColor;
/**
 * Editor for DatumData
 * @author Skyler
 *
 */
public class TemplateEditor<T> extends JScrollPane implements IEditor<T> {
	
//	private static class DataStruct {
//		private FieldType type;
//		private Object value;
//		
//		public DataStruct(FieldType type, Object value) {
//			this.type = type;
//			this.value = value;
//		}
//		
//		public FieldType getType() {
//			return type;
//		}
//		
//		public Object getValue() {
//			return value;
//		}
//		
//		public void setValue(Object newValue) {
//			this.value = newValue;
//		}
//		
//		public EditorField getEditorField(SessionTemplate template, String labelName) {
//			EditorField field = null;
//			
//			switch (type) {
//			case STRING:
//				field = new TextField(labelName, new TextFieldCallback() {
//					@Override
//					public void setField(String value) {
//						setValue(value);
//						template.dirty();
//					}
//				}, value.toString());
//				break;
//			case BOOL:
//				field = new BoolField(labelName, new BoolFieldCallback() {
//					@Override
//					public void setField(boolean value) {
//						setValue(value);
//						template.dirty();
//					}
//				}, (Boolean) value);
//				break;
//			case DOUBLE:
//				field = new DoubleField(labelName, new DoubleFieldCallback() {
//					@Override
//					public void setField(double value) {
//						setValue(value);
//						template.dirty();
//					}
//				}, value.toString());
//				break;
//			case INT:
//				field = new IntField(labelName, new IntFieldCallback() {
//					@Override
//					public void setField(int value) {
//						setValue(value);
//						template.dirty();
//					}
//				}, value.toString());
//				break;
//			case ATTRIBUTE:
//				field = new EnumField<Attributes>(labelName, new EnumFieldCallback() {
//					@Override
//					public void setField(String valueName) {
//						value = valueName;
//					}
//				}, (Attributes) value);
//			}
//			
//			return field;
//		}
//		
//		public DataNode asData(String keyName) {
//			String strValue = null; // set if non-complex object
//			//List<DataNode> listValue = null; // set if complex object
//			DataNode node = null; // If object has native write, use that instead
//			
//			switch (type) {
//			case STRING:
//			case INT:
//			case DOUBLE:
//				strValue = value.toString();
//				break;
//			case BOOL:
//				strValue = ((Boolean) value) == Boolean.TRUE ? "true" : "false";
//				break;
//			case ATTRIBUTE:
//				strValue = ((Attributes) value).name();
//			case STATSET:
//				node = ((StatSet) value).write(keyName);
//				break;
//			case SUBACTIONLIST:
//				node = DataNode.serializeAll(keyName, "subaction", ((List<? extends SubAction>) value));
//				break;
//			case TARGETTYPE:
//				strValue = ((TargetType) value).name();
//				break;			
//			}
//			
//			if (node == null)
//				node = new DataNode(keyName, strValue, null);
//			
//			return node;
//		}
//	}
	
	private static final long serialVersionUID = 1237901663396174434L;
	private JPanel editor;
	private Map<T, DataPair<T>> fields;
	
	public TemplateEditor(Map<T, FieldData<T>> fieldMap) {
		super();
		fields = new HashMap<>();
		
		editor = new JPanel();
		editor.setLayout(new BoxLayout(editor, BoxLayout.PAGE_AXIS));
		//editor.setBackground(Color.YELLOW);
		UIColor.setColors(editor, UIColor.Key.EDITOR_MAIN_FOREGROUND, UIColor.Key.EDITOR_MAIN_BACKGROUND);
		editor.setBorder(new EmptyBorder(20, 20, 20, 20));
		
		
//		List<Temp> tempList = new LinkedList<>();
//		List<Temp> exList = new LinkedList<>();
//		tempList.add(new Temp("Fire"));
//		tempList.add(new Temp("Water"));
//		tempList.add(new Temp("Earth"));
//		tempList.add(new Temp("Wind"));
//		exList.add(new Temp("Light"));
//		exList.add(new Temp("Dark"));
//		
//		editor.add((new GrabListField<Temp>("ListTest", new GrabFieldCallback() {
//
//			@Override
//			public void setField(List<Object> valueName) {
//				System.out.println("Got list: ");
//				for (Object o : valueName) {
//					System.out.println("\t" + ((Temp) o).getEditorName());
//				}
//			}
//			
//		}, tempList, exList)).getComponent());
		
		for (T key : fieldMap.keySet()) {
			// TODO add to fields
		}
		
		editor.add(Box.createVerticalGlue());
		this.setViewportView(editor);
		this.validate();
	}
	
	@Override
	public JComponent getComponent() {
		return this;
	}

	@Override
	public Map<T, FieldData<T>> fetchData() {
		Map<T, FieldData<T>> out = new HashMap<>();
		for (Entry<T, DataPair<T>> entry : fields.entrySet()) {
			FieldData<T> data = entry.getValue().getData();
			data.setValue(entry.getValue().getField().getObject());
			out.put(entry.getKey(), data);
		}
		return out;
	}
}
