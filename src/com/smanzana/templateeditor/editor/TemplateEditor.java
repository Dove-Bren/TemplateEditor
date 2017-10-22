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
import com.smanzana.templateeditor.uiutils.UIColor;
/**
 * Editor for DatumData
 * @author Skyler
 *
 */
public class TemplateEditor<T> extends JScrollPane implements IEditor<T> {
	
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
		
		EditorField<?> comp;
		for (Entry<T, FieldData> row : fieldMap.entrySet()) {
			String keyName = row.getValue().getName();
			
			comp = row.getValue().constructField();
			
			if (comp == null)
				continue;
			
			if (row.getValue().getDescription() != null) {
				comp.getComponent().setToolTipText(row.getValue().getFormattedDescription());
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
			data.fillFromField(entry.getValue().getField());
			out.put(entry.getKey(), data);
		}
		return out;
	}
}
