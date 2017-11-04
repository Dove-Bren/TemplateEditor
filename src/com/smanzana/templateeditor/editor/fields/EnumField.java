package com.smanzana.templateeditor.editor.fields;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.smanzana.templateeditor.uiutils.TextUtil;

public class EnumField<T extends Enum<T>> extends AEditorField<T> implements ActionListener {

	private JComboBox<String> combo;
	private JPanel wrapper;
	private Map<T, String> prettyMap;
	
	@SuppressWarnings("unchecked")
	public EnumField(T startSelection) {
		this((Class<T>) startSelection.getClass(), startSelection);
	}
	
	
	private EnumField(Class<T> clazz, T startSelection) {
		this.prettyMap = new HashMap<>();
		
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.LINE_AXIS));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		
		combo = new JComboBox<>();
		combo.setMinimumSize(new Dimension(50, 20));
		combo.setMaximumSize(new Dimension(300, 20));
		combo.setPreferredSize(new Dimension(100, 20));
		EnumSet.allOf(clazz).forEach((e) -> {
			String pretty = TextUtil.pretty(e.name());
			prettyMap.put(e, pretty);
			combo.addItem(pretty);
//			if (e == startSelection)
//				combo.setSelectedIndex(combo.getItemCount() - 1);
		});
		
		this.setObject(startSelection);
		
		combo.addActionListener(this);
		wrapper.add(combo);
		wrapper.add(Box.createRigidArea(new Dimension(20, 0)));
		wrapper.add(Box.createHorizontalGlue());
		
		wrapper.validate();
	}
	
	public JPanel getComponent() {
		return wrapper;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		markDirty();
	}
	
	private T lookup(String pretty) {
		for (T t : prettyMap.keySet())
			if (prettyMap.get(t).equals(pretty))
				return t;
		
		return null;
	}

	@Override
	public T getObject() {
		return lookup(combo.getSelectedItem().toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setCurrentObject(T obj) {
		if (obj == null) {
			combo.setSelectedIndex(0);
		} else {
			int i = 0;
			for(T e : ((Class<T>) obj.getClass()).getEnumConstants()) {
				if (e == obj) {
					combo.setSelectedIndex(i);
					break;
				}
				
				i++;
			}
		}
	}
	
}
