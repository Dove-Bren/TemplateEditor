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

import com.smanzana.templateeditor.IEditorOwner;
import com.smanzana.templateeditor.api.FieldData;
import com.smanzana.templateeditor.editor.fields.EditorField;
import com.smanzana.templateeditor.uiutils.TextUtil;
import com.smanzana.templateeditor.uiutils.UIColor;

/**
 * Editor that specializes in configuring values that are mapped to compile-time enumerated
 * sets of keys.
 * In other words, works great for configs that are backed by an enum of keys.
 * @author Skyler
 *
 */
public class EnumMapEditor<T extends Enum<T>> extends JScrollPane implements IEditor<T> {

	private static final long serialVersionUID = -4533006684394006640L;
	private JPanel editor;
	private Map<T, DataPair<T>> fields;
	
	// doesn't set as visible
	public EnumMapEditor(IEditorOwner owner, Map<T, FieldData> enummap) {
		super();
		fields = new HashMap<>();
		
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
			// TODO use keyName
			
			comp = row.getValue().constructField();
			
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
			comp.getComponent().setMaximumSize(new Dimension(Short.MAX_VALUE, comp.getComponent().getPreferredSize().height));
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
			data.fillFromField(entry.getValue().getField());
			out.put(entry.getKey(), data);
		}
		return out;
	}
	
	
}
