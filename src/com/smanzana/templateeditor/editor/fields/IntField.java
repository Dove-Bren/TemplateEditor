package com.smanzana.templateeditor.editor.fields;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class IntField extends AEditorField<Integer> implements ActionListener {
	
	private JPanel wrapper;
	private JFormattedTextField textfield;
	
	public IntField(String title) {
		this(title, 0);
	}
	
	public IntField(String title, Integer startingValue) {
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.LINE_AXIS));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		wrapper.add(Box.createHorizontalGlue());
		JLabel label = new JLabel(title);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		wrapper.add(label);
		wrapper.add(Box.createRigidArea(new Dimension(20, 0)));
		
		this.textfield = new JFormattedTextField(NumberFormat.getIntegerInstance());
		textfield.addActionListener(this);
		wrapper.add(textfield);
		wrapper.add(Box.createHorizontalGlue());
		
		setObject(startingValue);

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
	public Integer getObject() {
		try {
			return Integer.parseInt(textfield.getText());
		} catch (NumberFormatException e) {
			return getOriginal();
		}
	}

	@Override
	protected void setCurrentObject(Integer obj) {
		textfield.setValue(obj);
	}
}
