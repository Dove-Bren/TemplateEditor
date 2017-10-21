package com.smanzana.templateeditor.editor;

import java.awt.Dimension;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.smanzana.dungeonmaster.ui.app.swing.editors.fields.StepField;
import com.smanzana.dungeonmaster.ui.app.swing.editors.fields.StepField.StepFieldCallback;
import com.smanzana.templateeditor.IEditorOwner;
import com.smanzana.templateeditor.editor.fields.BoolField;
import com.smanzana.templateeditor.editor.fields.BoolField.BoolFieldCallback;
import com.smanzana.templateeditor.editor.fields.DoubleField;
import com.smanzana.templateeditor.editor.fields.DoubleField.DoubleFieldCallback;
import com.smanzana.templateeditor.editor.fields.EditorField;
import com.smanzana.templateeditor.editor.fields.IntField;
import com.smanzana.templateeditor.editor.fields.IntField.IntFieldCallback;
import com.smanzana.templateeditor.editor.fields.TextField;
import com.smanzana.templateeditor.editor.fields.TextField.TextFieldCallback;
import com.smanzana.templateeditor.uiutils.UIColor;

/**
 * Editor that specializes in configuring values that are mapped to compile-time enumerated
 * sets of keys.
 * In other words, works great for configs that are backed by an enum of keys.
 * @author Skyler
 *
 */
public class EnumMapEditor<T extends Enum<T>> extends JScrollPane implements IEditor {

	private static final long serialVersionUID = -4533006684394006640L;
	private JPanel editor;
	
	// doesn't set as visible
	public EnumMapEditor(IEditorOwner owner, Map<T, Object> enummap) {
		super();
		
		editor = new JPanel();
		editor.setLayout(new BoxLayout(editor, BoxLayout.PAGE_AXIS));
		//editor.setBackground(Color.YELLOW);
		UIColor.setColors(editor, UIColor.Key.EDITOR_MAIN_FOREGROUND, UIColor.Key.EDITOR_MAIN_BACKGROUND);
		editor.setBorder(new EmptyBorder(20, 20, 20, 20));
		
		EditorField comp;
		for (T key : enummap.keySet()) {
			comp = null;
			switch (config.getFieldType(keyName)) {
			case BOOL:
				comp = new BoolField(keyName, new BoolFieldCallback() {
					@Override
					public void setField(boolean value) {
						config.setValue(keyName, value);
						template.dirty();
					}
				}, (Boolean) config.getValue(keyName));
				break;
			case DOUBLE:
				comp = new DoubleField(keyName, new DoubleFieldCallback() {
					@Override
					public void setField(double value) {
						config.setValue(keyName, value);
						template.dirty();
					}
				}, config.getValue(keyName).toString());
				break;
			case INT:
				comp = new IntField(keyName, new IntFieldCallback() {
					@Override
					public void setField(int value) {
						config.setValue(keyName, value);
						template.dirty();
					}
				}, config.getValue(keyName).toString());
				break;
			case STRING:
				// See note about crappiness below.
				if (isStepList(config)) {
					comp = new StepField(keyName, new StepFieldCallback() {
						@Override
						public void setField(String value) {
							config.setValue(keyName, value);
							template.dirty();
						}
					}, StepList.deserialize((String) config.getValue(keyName)));
				} else {
					comp = new TextField(keyName, new TextFieldCallback() {
						@Override
						public void setField(String value) {
							config.setValue(keyName, value);
							template.dirty();
						}
					}, (String) config.getValue(keyName));
				}
				break;
			}
			
			if (comp == null)
				continue;
			
			if (config.getComments(keyName) != null) {
				String buf = "";
				for (String line : config.getComments(keyName)) {
					if (!buf.isEmpty())
						buf += System.getProperty("line.separator");
					buf += line;
				}
				comp.getComponent().setToolTipText(buf);
			}
			UIColor.setColors(comp.getComponent(), UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
			comp.getComponent().setPreferredSize(new Dimension(100, 25));
			editor.add(comp.getComponent());
		}
		
		this.setViewportView(editor);
		this.validate();
	}
	
	private boolean isStepList(Config<?> config) {
		// Crappy. Oh well. Just doing what works :)
		return (config instanceof CombatBonusConfig ||
			    config instanceof RollTableConfig);
	}

	@Override
	public JComponent getComponent() {
		return this;
	}
	
	
	
}
