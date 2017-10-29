package com.smanzana.templateeditor.editor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JLabel;
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
		
		editor = new JPanel(new GridBagLayout());
		//editor.setBackground(Color.YELLOW);
		UIColor.setColors(editor, UIColor.Key.EDITOR_MAIN_FOREGROUND, UIColor.Key.EDITOR_MAIN_BACKGROUND);
		editor.setBorder(new EmptyBorder(20, 20, 20, 20));
		
		GridBagConstraints cons = new GridBagConstraints();
		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.weighty = 0.0;
		cons.anchor = GridBagConstraints.NORTH;
		int consRow = 0;
		EditorField<?> comp;
		for (Entry<T, FieldData> row : fieldMap.entrySet()) {
			String keyName = row.getValue().getName();
			
			comp = row.getValue().constructField();
			
			if (comp == null)
				continue;
			
			UIColor.setColors(comp.getComponent(), UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
			comp.getComponent().setMaximumSize(new Dimension(Short.MAX_VALUE, comp.getComponent().getPreferredSize().height));
			fields.put(row.getKey(), new DataPair<T>(row.getValue(), comp));
			comp.setOwner(owner);
			
			JLabel label = new JLabel(keyName);
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			label.setHorizontalAlignment(JLabel.LEADING);
			label.setMinimumSize(new Dimension(100, 10));
			label.setOpaque(true);
			label.setBorder(new EmptyBorder(0, 5, 0, 0));
			UIColor.setColors(label, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
		
			if (row.getValue().getDescription() != null) {
				comp.getComponent().setToolTipText(row.getValue().getFormattedDescription());
				label.setToolTipText(row.getValue().getFormattedDescription());
			}
			
			cons.gridy = consRow++;
			cons.gridx = 0;
			cons.weightx = 0.0;
			cons.anchor = GridBagConstraints.BASELINE_LEADING;
			cons.fill = GridBagConstraints.BOTH;
			editor.add(label, cons);
			
			JPanel filler = new JPanel();
			UIColor.setColors(filler, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
			cons.gridx = 1;
			cons.weightx = 1.0;
			editor.add(filler, cons);
			
			cons.gridx = 0;
			cons.gridy = consRow++;
			cons.anchor = GridBagConstraints.CENTER;
			cons.weightx = 0.0;
			filler = new JPanel();
			UIColor.setColors(filler, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
			editor.add(filler, cons);
			
			cons.gridx = 1;
			cons.weightx = 1.0;
			editor.add(comp.getComponent(), cons);
		}
		
		cons.gridx = 0;
		cons.gridy = consRow;
		cons.weighty = 1.0;
		cons.gridwidth = 2;
		cons.fill = GridBagConstraints.BOTH;
		JPanel spacer = new JPanel();
		UIColor.setColors(spacer, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
		editor.add(spacer, cons);
		
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
