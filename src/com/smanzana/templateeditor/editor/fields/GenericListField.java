package com.smanzana.templateeditor.editor.fields;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.smanzana.templateeditor.api.FieldData;
import com.smanzana.templateeditor.uiutils.UIColor;

/**
 * List of elements that are themselves editable.
 * When selected, a dialog is displayed for editing that element.
 * This list is made to be embedded in another editor.
 * @author Skyler
 */
public class GenericListField<T extends FieldData> extends AEditorField<List<T>> {
		
	private T base; // used to create new subfields
	private DefaultListModel<T> data;
	private JList<T> dataList;
	private JPanel wrapper;

	
	public GenericListField(String title, T baseEditor, List<T> fields) {
		this.base = baseEditor;
		
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.LINE_AXIS));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		//wrapper.add(Box.createHorizontalGlue());
		UIColor.setColors(wrapper, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
		JLabel label = new JLabel(title);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		wrapper.add(label);
		wrapper.add(Box.createRigidArea(new Dimension(20, 0)));
		
		data = new DefaultListModel<>();
		dataList = new JList<T>(data);
		dataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dataList.setLayoutOrientation(JList.VERTICAL);
		UIColor.setColors(dataList, UIColor.Key.EDITOR_MAIN_FOREGROUND, UIColor.Key.EDITOR_MAIN_BACKGROUND);
		
		dataList.setCellRenderer((list, e, index, isSelected, focus) -> {
			return e.constructField().getComponent();
		});
				
		for (T item : fields) {
			data.addElement(item);
		}

		dataList.setVisibleRowCount(10);
		dataList.setMaximumSize(dataList.getPreferredScrollableViewportSize());
		
		Dimension med;
		med = new Dimension(dataList.getPreferredScrollableViewportSize().width, dataList.getPreferredScrollableViewportSize().height + 30);
		
		Dimension small = new Dimension(med.width - 30, med.height - 30);
		dataList.setPreferredSize(small);
		JComponent panel = new JScrollPane(dataList);
		panel.setPreferredSize(med);
		panel.setMinimumSize(panel.getMaximumSize());
		wrapper.add(panel);
		wrapper.add(Box.createRigidArea(new Dimension(20, 0)));
		
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(Box.createVerticalGlue());
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		JButton button = new JButton("New");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				add();
			}
		});
		panel.add(button);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		button = new JButton("Delete");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				remove();
			}
		});
		panel.add(button);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(Box.createVerticalGlue());
		
		wrapper.add(panel);
		//wrapper.add(Box.createRigidArea(new Dimension(label.getPreferredSize().width, 0)));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		//wrapper.add(Box.createHorizontalGlue());
		
		wrapper.validate();
	}
	
	public JPanel getComponent() {
		return wrapper;
	}
	
	// Signal one of the nested elements has changed, and we should do our part of updating
	private void update() {
		dirty();
	}
	
	@SuppressWarnings("unchecked")
	private T cloneBase() {
		return (T) base.clone();
	}
	
	private void add() {
		T val = cloneBase();

		data.addElement(val);
	}
	
	private void remove() {
		remove(dataList.getSelectedIndex());
	}
	
	private void remove(int index) {
		if (index == -1)
			return;
		
		data.remove(index);
		update();
	}

	@Override
	public List<T> getObject() {
		List<T> list = new LinkedList<>();
		Enumeration<T> it = data.elements();
		while (it.hasMoreElements())
			list.add(it.nextElement());
		return list;
	}

	@Override
	protected void setCurrentObject(List<T> obj) {
		data.clear();
		for (T o : obj) {
			data.addElement(o);
		}
	}
	
}
