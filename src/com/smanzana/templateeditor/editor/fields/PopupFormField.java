package com.smanzana.templateeditor.editor.fields;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.smanzana.templateeditor.EditorIconRegistry;
import com.smanzana.templateeditor.uiutils.UIColor;

/**
 * Field that expands into it's own sub editor.
 * Modal is not an editor. It's just some panel you provide.
 * @author Skyler
 * 
 */
public class PopupFormField<T> extends AEditorField<T> {
	
	private static final String DESC_MISSING = null;
	private static final String NAME_MISSING = "<default>";
	
	public static interface Formatter<T> {
		public String getDisplayName(EditorField<T> obj);
		public String getDisplayDescription(EditorField<T> obj);
	}
	
	private EditorField<T> nestedField;
	private Formatter<T> formatter;
	
	private JTextField display;
	private JPanel wrapper;
	
//	public static <T> NestedEditorField<T> create(String title, Map<T, FieldData<T>> fieldMap, IEditorDisplayFormatter<T> formatter) {
//		return new NestedEditorField<T>(title, fieldMap, formatter);
//	}
	public PopupFormField(EditorField<T> nestedField, Formatter<T> formatter) {
		this.nestedField = nestedField;
		this.formatter = formatter;
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.LINE_AXIS));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		UIColor.setColors(wrapper, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
		
		JPanel comp = new JPanel(new BorderLayout());
		display = new JTextField(20);
		display.setEditable(false);
		display.setText(NAME_MISSING);
		display.setToolTipText(DESC_MISSING);
		UIColor.setColors(display, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
		comp.add(display, BorderLayout.CENTER);
		
		JButton button = new JButton(EditorIconRegistry.get(EditorIconRegistry.Key.INSPECT));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				edit();
			}
		});
		button.setMargin(new Insets(0,0,0,0));
		comp.add(button, BorderLayout.EAST);
		
		
		display.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				doMousePressed(e);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				UIColor.setColors(display, UIColor.Key.EDITOR_MAIN_FOREGROUND, UIColor.Key.EDITOR_MAIN_BACKGROUND);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				UIColor.setColors(display, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
			}
		});
		
		
		wrapper.add(comp);
		//wrapper.add(Box.createRigidArea(new Dimension(20, 0)));
		//wrapper.add(Box.createHorizontalGlue());
		
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
	
	private void updateDataMap() {
		String name = formatter.getDisplayName(nestedField);
		String desc = formatter.getDisplayDescription(nestedField);
		
		display.setText(name != null ? name : NAME_MISSING);
		display.setToolTipText(desc != null ? desc : DESC_MISSING);
		this.dirty();
	}
	
	/**
	 * Returns if successful (not cancelled)
	 * @param value
	 * @return
	 */
	private boolean edit() {
		final StringBuffer cancelled = new StringBuffer();
		
		JDialog dialog = new JDialog((JFrame) null, "Editor", true);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setPreferredSize(new Dimension(640, 320));
		mainPanel.add(new JScrollPane(nestedField.getComponent()), BorderLayout.CENTER);
		
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
		UIColor.setColors(mainPanel, UIColor.Key.BASE_FOREGROUND, UIColor.Key.BASE_BACKGROUND);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		
		if (cancelled.length() == 0) {
			// Hit OK. Commit data map
			updateDataMap();
		} else {
			// Just don't commit data map.
		}
		
		return cancelled.length() == 0;
	}

	@Override
	public T getObject() {
		return nestedField.getObject();
	}

	@Override
	protected void setCurrentObject(T obj) {
		nestedField.setObject(obj);
	}
}
