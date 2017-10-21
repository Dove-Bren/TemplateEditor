package com.smanzana.templateeditor.editor.fields;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.smanzana.templateeditor.EditorIconRegistry;
import com.smanzana.templateeditor.FieldData;
import com.smanzana.templateeditor.IEditorDisplayFormatter;
import com.smanzana.templateeditor.editor.IEditor;
import com.smanzana.templateeditor.editor.TemplateEditor;
import com.smanzana.templateeditor.uiutils.UIColor;

/**
 * Field that expands into it's own sub editor.
 * Ideally will expand to be inplace if there's enough space, or just display
 * a short line where you launch a modal editor from. #TODO lol
 * It should HAVE to expand if it's in a modal
 * @author Skyler
 * 
 * @param <K> Type that indexes the nested fieldmaps
 */
public class NestedEditorField<K> extends AEditorField<Map<K, FieldData>> {
	
	private static final String DESC_MISSING = "";
	private static final String NAME_MISSING = "<default>";
	
	
	private Map<K, FieldData> dataMap;
	private IEditorDisplayFormatter<K> formatter;
	
	private JTextField display;
	private JPanel wrapper;
	
	public NestedEditorField(String title, Map<K, FieldData> fieldMap, IEditorDisplayFormatter<K> formatter) {
		this.dataMap = fieldMap; // Don't actually use it till we create nested editor
		this.formatter = formatter;
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.LINE_AXIS));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		wrapper.add(Box.createHorizontalGlue());
		UIColor.setColors(wrapper, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
		JLabel label = new JLabel(title);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		wrapper.add(label);
		wrapper.add(Box.createRigidArea(new Dimension(20, 0)));
		
		JPanel comp = new JPanel(new BorderLayout());
		display = new JTextField(20);
		display.setEditable(false);
		display.setText(NAME_MISSING);
		display.setToolTipText(DESC_MISSING);
		comp.add(display, BorderLayout.CENTER);
		
		JButton button = new JButton(EditorIconRegistry.get(EditorIconRegistry.Key.INSPECT));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				edit();
			}
		});
		comp.add(button, BorderLayout.EAST);
		
		
		display.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				doMousePressed(e);
			}
		});
		
		
		wrapper.add(comp);
		wrapper.add(Box.createRigidArea(new Dimension(20, 0)));
		wrapper.add(Box.createHorizontalGlue());
		
		wrapper.validate();
	}
	
	public JPanel getComponent() {
		return wrapper;
	}
	
	public void doMousePressed(MouseEvent e) {
		if (e.getClickCount() != 2)
			return;
		
		edit();
	}
	
	private void updateDataMap(Map<K, FieldData> map) {
		dataMap = map;
		String name = formatter.getEditorName(map);
		String desc = formatter.getEditorTooltip(map);
		
		display.setText(name != null ? name : NAME_MISSING);
		display.setToolTipText(desc != null ? desc : DESC_MISSING);
	}
	
	/**
	 * Returns if successful (not cancelled)
	 * @param value
	 * @return
	 */
	private boolean edit() {
		IEditor<K> nestedEditor = new TemplateEditor<K>(dataMap);
		final StringBuffer cancelled = new StringBuffer();
		
		JDialog dialog = new JDialog((JFrame) null, "Editor", true);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(nestedEditor.getComponent(), BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		JButton button = new JButton("OK");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// don't set canceled. Everything else will happen automatically
				dialog.setVisible(false);	
			}
		});
		bottomPanel.add(button);
		bottomPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		
		JButton button2 = new JButton("Cancel");
		button2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelled.append("yup");
				dialog.setVisible(false);
			}
		});
		bottomPanel.add(button2);
		bottomPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
		
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
		JOptionPane content = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION,
				null, new Object[]{});
		
		dialog.setContentPane(content);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.pack();
		dialog.setVisible(true);
		
		if (cancelled.length() == 0) {
			// Hit OK. Commit data map
			updateDataMap(nestedEditor.fetchData());
		} else {
			// Just don't commit data map.
		}
		
		return cancelled.length() == 0;
	}

	@Override
	public Map<K, FieldData> getObject() {
		return dataMap;
	}

	@Override
	protected void setCurrentObject(Map<K, FieldData> obj) {
		dataMap = obj;
	}
}
