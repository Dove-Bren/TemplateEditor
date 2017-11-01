package com.smanzana.templateeditor.editor.fields;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TextField extends AEditorField<String> implements ActionListener {
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
		textfield.addActionListener(this);
		wrapper.add(textfield);
		wrapper.add(Box.createHorizontalGlue());

		wrapper.validate();
	}
	
	public JPanel getComponent() {
		return wrapper;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		dirty();
	}

	@Override
	public String getObject() {
		return textfield.getText();
	}

	@Override
	protected void setCurrentObject(String obj) {
		textfield.setText(obj);
	}
}
