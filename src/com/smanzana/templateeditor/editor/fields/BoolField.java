package com.smanzana.templateeditor.editor.fields;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class BoolField extends AEditorField<Boolean> implements ActionListener {

	private JRadioButton truefield;
	private JRadioButton falsefield;
	private JPanel wrapper;
	
	public BoolField() {
		this(false);
	}
	
	public BoolField(boolean startTrue) {
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.LINE_AXIS));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		
		
		wrapper.add(Box.createHorizontalGlue());
		wrapper.add(Box.createHorizontalGlue());
		falsefield = new JRadioButton("False");
		falsefield.addActionListener(this);
		wrapper.add(falsefield);
		wrapper.add(Box.createHorizontalGlue());
		wrapper.add(Box.createRigidArea(new Dimension(30, 0)));
		wrapper.add(Box.createHorizontalGlue());
		
		truefield = new JRadioButton("True");
		truefield.addActionListener(this);
		wrapper.add(truefield);
		wrapper.add(Box.createHorizontalGlue());
		wrapper.add(Box.createHorizontalGlue());
		
		setObject(startTrue);
		
		ButtonGroup group = new ButtonGroup();
		group.add(falsefield);
		group.add(truefield);
		
		
		
		wrapper.validate();
	}
	
	private void setValue(boolean state) {
		// Reset buttons to reflect current state
			truefield.setSelected(state);
			falsefield.setSelected(!state); 
	}
	
	public JPanel getComponent() {
		return wrapper;
	}

	@Override
	public Boolean getObject() {
		return truefield.isSelected();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		dirty();
	}

	@Override
	protected void setCurrentObject(Boolean obj) {
		setValue(obj);
	}
}
