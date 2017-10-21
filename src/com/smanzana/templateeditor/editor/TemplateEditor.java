package com.smanzana.templateeditor.editor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.smanzana.dungeonmaster.action.Action.TargetType;
import com.smanzana.dungeonmaster.action.subaction.SubAction;
import com.smanzana.dungeonmaster.action.subaction.SubDamage;
import com.smanzana.dungeonmaster.maker.SessionTemplate;
import com.smanzana.dungeonmaster.pawn.Attributes;
import com.smanzana.dungeonmaster.session.datums.ActionDatumData;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.ui.EditorDisplayable;
import com.smanzana.dungeonmaster.ui.app.UIColor;
import com.smanzana.dungeonmaster.utils.StatSet;
import com.smanzana.dungeonmaster.utils.ValueConstant;
import com.smanzana.templateeditor.editor.fields.BoolField;
import com.smanzana.templateeditor.editor.fields.DoubleField;
import com.smanzana.templateeditor.editor.fields.EditorField;
import com.smanzana.templateeditor.editor.fields.EditorListField;
import com.smanzana.templateeditor.editor.fields.EnumField;
import com.smanzana.templateeditor.editor.fields.GrabListField;
import com.smanzana.templateeditor.editor.fields.IntField;
import com.smanzana.templateeditor.editor.fields.TextField;
import com.smanzana.templateeditor.editor.fields.BoolField.BoolFieldCallback;
import com.smanzana.templateeditor.editor.fields.DoubleField.DoubleFieldCallback;
import com.smanzana.templateeditor.editor.fields.EditorListField.EditorListCallback;
import com.smanzana.templateeditor.editor.fields.EditorListField.EditorListFactory;
import com.smanzana.templateeditor.editor.fields.EnumField.EnumFieldCallback;
import com.smanzana.templateeditor.editor.fields.GrabListField.GrabFieldCallback;
import com.smanzana.templateeditor.editor.fields.IntField.IntFieldCallback;
import com.smanzana.templateeditor.editor.fields.TextField.TextFieldCallback;

/**
 * Editor for DatumData
 * @author Skyler
 *
 */
public class TemplateEditor extends JScrollPane implements IEditor {

	private static enum FieldType {
		STRING,
		BOOL,
		INT,
		DOUBLE,
		ATTRIBUTE,
		STATSET,
		
		SUBACTIONLIST,
		TARGETTYPE,
	}
	
	private static class DataStruct {
		private FieldType type;
		private Object value;
		
		public DataStruct(FieldType type, Object value) {
			this.type = type;
			this.value = value;
		}
		
		public FieldType getType() {
			return type;
		}
		
		public Object getValue() {
			return value;
		}
		
		public void setValue(Object newValue) {
			this.value = newValue;
		}
		
		public EditorField getEditorField(SessionTemplate template, String labelName) {
			EditorField field = null;
			
			switch (type) {
			case STRING:
				field = new TextField(labelName, new TextFieldCallback() {
					@Override
					public void setField(String value) {
						setValue(value);
						template.dirty();
					}
				}, value.toString());
				break;
			case BOOL:
				field = new BoolField(labelName, new BoolFieldCallback() {
					@Override
					public void setField(boolean value) {
						setValue(value);
						template.dirty();
					}
				}, (Boolean) value);
				break;
			case DOUBLE:
				field = new DoubleField(labelName, new DoubleFieldCallback() {
					@Override
					public void setField(double value) {
						setValue(value);
						template.dirty();
					}
				}, value.toString());
				break;
			case INT:
				field = new IntField(labelName, new IntFieldCallback() {
					@Override
					public void setField(int value) {
						setValue(value);
						template.dirty();
					}
				}, value.toString());
				break;
			case ATTRIBUTE:
				field = new EnumField<Attributes>(labelName, new EnumFieldCallback() {
					@Override
					public void setField(String valueName) {
						value = valueName;
					}
				}, (Attributes) value);
			}
			
			return field;
		}
		
		public DataNode asData(String keyName) {
			String strValue = null; // set if non-complex object
			//List<DataNode> listValue = null; // set if complex object
			DataNode node = null; // If object has native write, use that instead
			
			switch (type) {
			case STRING:
			case INT:
			case DOUBLE:
				strValue = value.toString();
				break;
			case BOOL:
				strValue = ((Boolean) value) == Boolean.TRUE ? "true" : "false";
				break;
			case ATTRIBUTE:
				strValue = ((Attributes) value).name();
			case STATSET:
				node = ((StatSet) value).write(keyName);
				break;
			case SUBACTIONLIST:
				node = DataNode.serializeAll(keyName, "subaction", ((List<? extends SubAction>) value));
				break;
			case TARGETTYPE:
				strValue = ((TargetType) value).name();
				break;			
			}
			
			if (node == null)
				node = new DataNode(keyName, strValue, null);
			
			return node;
		}
	}
	
	private JPanel editor;
	private Map<String, DataStruct> dataMap;
	
	private TemplateEditor() {
		super();
		dataMap = new HashMap<>();
		
		editor = new JPanel();
		editor.setLayout(new BoxLayout(editor, BoxLayout.PAGE_AXIS));
		//editor.setBackground(Color.YELLOW);
		UIColor.setColors(editor, UIColor.Key.EDITOR_MAIN_FOREGROUND, UIColor.Key.EDITOR_MAIN_BACKGROUND);
		editor.setBorder(new EmptyBorder(20, 20, 20, 20));
	}
	
	// TODO DELETEME
	private static class Temp implements IEditorDisplayable {

		private static int INDEX = 0;
		
		private String name;
		private int ind;
		
		public Temp(String name) {
			this.name = name;
			this.ind = INDEX++;
		}
		
		@Override
		public String getEditorName() {
			return name;
		}

		@Override
		public String getEditorTooltip() {
			return "Runtime " + ind;
		}
	}
	// TODO ENDDELETEME
	
	private void finishConstructor() {
		// TODO iteratore over dataMap and create editor
		
		editor.add(new EnumField<Attributes>("Attribute", new EnumFieldCallback() {
			@Override
			public void setField(String valueName) {
				// TODO Auto-generated method stub
				System.out.println("Selected " + valueName);
			}
		}, Attributes.STRENGTH).getComponent());
		
		List<Temp> tempList = new LinkedList<>();
		List<Temp> exList = new LinkedList<>();
		tempList.add(new Temp("Fire"));
		tempList.add(new Temp("Water"));
		tempList.add(new Temp("Earth"));
		tempList.add(new Temp("Wind"));
		exList.add(new Temp("Light"));
		exList.add(new Temp("Dark"));
		
		editor.add((new GrabListField<Temp>("ListTest", new GrabFieldCallback() {

			@Override
			public void setField(List<Object> valueName) {
				System.out.println("Got list: ");
				for (Object o : valueName) {
					System.out.println("\t" + ((Temp) o).getEditorName());
				}
			}
			
		}, tempList, exList)).getComponent());
		
		editor.add((new EditorListField<SubAction>("EditorTest",
				new SubActionEditor(),
				new EditorListCallback() {
					@Override
					public void setField(List<Object> valueName) {
						for (Object o : valueName) {
							System.out.println("\t" + ((Temp) o).getEditorName());
						}
					}
				},
				new EditorListFactory() {
					@Override
					public Object construct() {
						return new SubDamage(new ValueConstant(1));
					}
				},
				new LinkedList<>(),
				true
				)).getComponent());
		
		editor.add(Box.createVerticalGlue());
		this.setViewportView(editor);
		this.validate();
	}
	
	// doesn't set as visible
	public TemplateEditor(SessionTemplate template, ActionDatumData data) {
		this();
		
		// This sucks, but oh well.
		// Going this route to keep UI stuff out of datumdatas.
		// If I cared less, I'd have the datum data have a handle on what
		// a) data it has in a format this editor could just parse or
		// b) be able to specify a editor subclass to use
		dataMap.put("name", wrap(FieldType.STRING, data.getName()));
		dataMap.put("description", wrap(FieldType.STRING, data.getDescription()));
		dataMap.put("beneficial", wrap(FieldType.BOOL, data.getBeneficial()));
		dataMap.put("isparty", wrap(FieldType.BOOL, data.isParty()));
		dataMap.put("partyprompt", wrap(FieldType.STRING, data.getPartyPrompt()));
		dataMap.put("targettype", wrap(FieldType.TARGETTYPE, data.getTargetType()));
		dataMap.put("attribute", wrap(FieldType.ATTRIBUTE, data.getAttribute()));
		dataMap.put("subactions", wrap(FieldType.SUBACTIONLIST, data.getSubactions()));
		
		finishConstructor();
	}
	
	private DataStruct wrap(FieldType type, Object val) {
		return new DataStruct(type, val);
	}
	
	private DataNode wrapData(String nodeName) {
		DataNode node = new DataNode(nodeName, null, new LinkedList<>());
		
		for (String key : dataMap.keySet()) {
			if (dataMap.get(key) == null)
				continue;
			node.addChild(dataMap.get(key).asData(key));
		}
		
		return node;
	}

	@Override
	public JComponent getComponent() {
		return this;
	}
	
	
	
}
