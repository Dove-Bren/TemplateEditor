package com.smanzana.templateeditor.editor.fields;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextField extends AEditorField<String> implements DocumentListener {
	private JTextField textfield;
	private JPanel wrapper;
	
	public TextField() {
		this("");
	}
	
	public TextField(String startingText) {
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.LINE_AXIS));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		
		this.textfield = new JTextField(startingText, 20);
		Dimension cur = this.textfield.getPreferredSize();
		this.textfield.setMaximumSize(new Dimension(Short.MAX_VALUE, cur.height));
		textfield.getDocument().addDocumentListener(this);
		wrapper.add(textfield);
		wrapper.add(Box.createHorizontalGlue());

		wrapper.validate();
	}
	
	public JPanel getComponent() {
		return wrapper;
	}

	@Override
	public String getObject() {
		return textfield.getText();
	}

	@Override
	protected void setCurrentObject(String obj) {
		textfield.setText(obj);
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		markDirty();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		markDirty();
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		markDirty();
	}
}
