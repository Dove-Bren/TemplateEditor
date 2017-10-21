package com.smanzana.templateeditor.editor;

import java.awt.Dimension;
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
import com.smanzana.templateeditor.IEditorOwner;
import com.smanzana.templateeditor.editor.fields.BoolField;
import com.smanzana.templateeditor.editor.fields.DoubleField;
import com.smanzana.templateeditor.editor.fields.EditorField;
import com.smanzana.templateeditor.editor.fields.IntField;
import com.smanzana.templateeditor.editor.fields.NestedEditorField;
import com.smanzana.templateeditor.editor.fields.TextField;
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
	
	public TemplateEditor(IEditorOwner owner, Map<T, FieldData> fieldMap) {
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
		
		EditorField<?> comp;
		for (Entry<T, FieldData> row : fieldMap.entrySet()) {
			String keyName = row.getValue().getName();
			
			comp = null;
			switch (row.getValue().getType()) {
			case BOOL:
				comp = new BoolField(keyName, (Boolean) row.getValue().getValue());
				break;
			case DOUBLE:
				comp = new DoubleField(keyName, (Double) row.getValue().getValue());
				break;
			case INT:
				comp = new IntField(keyName, (Integer) row.getValue().getValue());
				break;
			case STRING:
				comp = new TextField(keyName, (String) row.getValue().getValue());
				break;
			case COMPLEX:
				comp = new NestedEditorField(keyName, row.getValue().getNestedTypes(), row.getValue().getFormatter());
				break;
			case LIST_COMPLEX:
				// TODO
				break;
			case LIST_DOUBLE:
				break;
			case LIST_INT:
				break;
			case LIST_STRING:
				break;
			case USER:
				comp = row.getValue().getUserDataType().getField();
				break;
			}
			
			if (comp == null)
				continue;
			
			if (row.getValue().getDescription() != null) {
				String buf = "";
				for (String line : row.getValue().getDescription()) {
					if (!buf.isEmpty())
						buf += System.getProperty("line.separator");
					buf += line;
				}
				comp.getComponent().setToolTipText(buf);
			}
			UIColor.setColors(comp.getComponent(), UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
			comp.getComponent().setPreferredSize(new Dimension(100, 25));
			editor.add(comp.getComponent());
			fields.put(row.getKey(), new DataPair<T>(row.getValue(), comp));
			comp.setOwner(owner);
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
	public Map<T, FieldData> fetchData() {
		Map<T, FieldData> out = new HashMap<>();
		for (Entry<T, DataPair<T>> entry : fields.entrySet()) {
			FieldData data = entry.getValue().getData();
			data.setValue(entry.getValue().getField().getObject());
			out.put(entry.getKey(), data);
		}
		return out;
	}
}
