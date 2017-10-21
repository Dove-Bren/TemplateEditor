package com.smanzana.templateeditor.editor;

import java.awt.Dimension;
import java.util.Map;
import java.util.Map.Entry;

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
import com.smanzana.templateeditor.editor.fields.TextField;
import com.smanzana.templateeditor.uiutils.TextUtil;
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
	public EnumMapEditor(IEditorOwner owner, Map<T, FieldData> enummap) {
		super();
		
		editor = new JPanel();
		editor.setLayout(new BoxLayout(editor, BoxLayout.PAGE_AXIS));
		//editor.setBackground(Color.YELLOW);
		UIColor.setColors(editor, UIColor.Key.EDITOR_MAIN_FOREGROUND, UIColor.Key.EDITOR_MAIN_BACKGROUND);
		editor.setBorder(new EmptyBorder(20, 20, 20, 20));
		
		EditorField<?> comp;
		for (Entry<T,FieldData> row : enummap.entrySet()) {
			String keyName = row.getValue().getName();
			if (keyName == null)
				TextUtil.pretty(row.getKey().name());
			
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
				// TODO
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
		}
		
		this.setViewportView(editor);
		this.validate();
	}

	@Override
	public JComponent getComponent() {
		return this;
	}
}
