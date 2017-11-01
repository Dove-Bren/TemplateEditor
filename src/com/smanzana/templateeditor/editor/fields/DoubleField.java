package com.smanzana.templateeditor.editor.fields;

import java.awt.Dimension;
import java.text.NumberFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DoubleField extends AEditorField<Double> implements DocumentListener {

	private JPanel wrapper;
	private JFormattedTextField textfield;
	
	public DoubleField() {
		this(0.0);
	}
	
	public DoubleField(Double startingValue) {
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.LINE_AXIS));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		
		this.textfield = new JFormattedTextField(NumberFormat.getNumberInstance());
		textfield.getDocument().addDocumentListener(this);
		wrapper.add(textfield);
		wrapper.add(Box.createHorizontalGlue());
		
		setObject(startingValue);

		wrapper.validate();
	}
	
	public JPanel getComponent() {
		return wrapper;
	}

	@Override
	public Double getObject() {
		try {
			return (Double.parseDouble(textfield.getText()));
		} catch (NumberFormatException e) {
			return this.getOriginal();
		}
	}

	@Override
	protected void setCurrentObject(Double obj) {
		textfield.setValue(obj);
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		dirty();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		dirty();
		
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		dirty();
		
	}
}
