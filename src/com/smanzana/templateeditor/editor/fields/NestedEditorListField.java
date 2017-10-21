package com.smanzana.templateeditor.editor.fields;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import com.smanzana.templateeditor.FieldData;
import com.smanzana.templateeditor.IEditorDisplayFormatter;
import com.smanzana.templateeditor.IEditorOwner;
import com.smanzana.templateeditor.editor.IEditor;
import com.smanzana.templateeditor.editor.TemplateEditor;
import com.smanzana.templateeditor.uiutils.UIColor;

/**
 * List of elements that are themselves editable.
 * When selected, a dialog is displayed for editing that element.
 * This list is made to be embedded in another editor.
 * @author Skyler
 */
public class NestedEditorListField extends AEditorField<List<Map<Integer, FieldData>>> {
	
	// For ease of use <3
	private static class DataWrapper {
		public Map<Integer, FieldData> data;
		
		public DataWrapper(Map<Integer, FieldData> data) {
			this.data = data;
		}
	}
	
	private DataWrapper base; // used to create new subfields
	//private IEditorDisplayFormatter<Integer> formatter;
	private DefaultListModel<DataWrapper> data;
	private JList<DataWrapper> dataList;
	private JPanel wrapper;

	
	public NestedEditorListField(String title, Map<Integer, FieldData> baseMap,
			List<Map<Integer, FieldData>> fields, IEditorDisplayFormatter<Integer> formatter) {
		//this.formatter = formatter;
		this.base = new DataWrapper(baseMap);
		
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.LINE_AXIS));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		wrapper.add(Box.createHorizontalGlue());
		UIColor.setColors(wrapper, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
		JLabel label = new JLabel(title);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		wrapper.add(label);
		wrapper.add(Box.createRigidArea(new Dimension(20, 0)));
		
		data = new DefaultListModel<>();
		dataList = new JList<DataWrapper>(data);
		dataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dataList.setLayoutOrientation(JList.VERTICAL);
		UIColor.setColors(dataList, UIColor.Key.EDITOR_MAIN_FOREGROUND, UIColor.Key.EDITOR_MAIN_BACKGROUND);
		dataList.setCellRenderer((list, e, index, isSelected, focus) -> {
			JLabel comp;
			String name = formatter.getEditorName(e.data);
			
			comp = new JLabel("  " + name == null ? "<Default>" : name);
			comp.setOpaque(true);

	        
	        // check if this cell is selected
	        if (isSelected) {
	            UIColor.setColors(comp, UIColor.Key.EDITOR_MAIN_BACKGROUND, UIColor.Key.EDITOR_MAIN_FOREGROUND);
	        // unselected, and not the DnD drop location
	        } else {
	        	UIColor.setColors(comp, UIColor.Key.EDITOR_MAIN_FOREGROUND, UIColor.Key.EDITOR_MAIN_BACKGROUND);
	        };
	        
	        String desc = formatter.getEditorTooltip(e.data);
	        comp.setToolTipText(desc == null ? name : desc);
			
			return comp;
		});
		
		dataList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				doMousePressed(e);
			}
		});
		
		for (Map<Integer, FieldData> row : fields) {
			data.addElement(new DataWrapper(row));
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
		wrapper.add(Box.createRigidArea(new Dimension(label.getPreferredSize().width, 0)));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		wrapper.add(Box.createHorizontalGlue());
		
		wrapper.validate();
	}
	
	public JPanel getComponent() {
		return wrapper;
	}
	
	public void doMousePressed(MouseEvent e) {
		if (e.getClickCount() != 2)
			return;
		
		if (dataList.isSelectionEmpty())
			return;
		
		DataWrapper val = data.get(dataList.getSelectedIndex());
		
		edit(val);
	}
	
	/**
	 * Returns if successful (not cancelled)
	 * @param value
	 * @return
	 */
	private boolean edit(DataWrapper value) {
		IEditor<Integer> nestedEditor = new TemplateEditor<Integer>(
				new IEditorOwner() {
					@Override
					public void dirty() {
						; // do nothing
					}
				}, value.data);
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
			value.data = nestedEditor.fetchData();
			update();
		} else {
			// Just don't commit data map.
		}
		
		return cancelled.length() == 0;
	}
	
	// Signal one of the nested elements has changed, and we should do our part of updating
	private void update() {
		dirty();
	}
	
	private DataWrapper cloneBase() {
		Map<Integer, FieldData> clone = new HashMap<>();
		for (Integer key : base.data.keySet()) {
			clone.put(key, base.data.get(key).clone());
		}
		return new DataWrapper(clone);
	}
	
	private void add() {
		DataWrapper val = cloneBase();
		
		
		if (edit(val))
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
	public List<Map<Integer, FieldData>> getObject() {
		List<Map<Integer, FieldData>> list = new LinkedList<>();
		Enumeration<DataWrapper> it = data.elements();
		while (it.hasMoreElements())
			list.add(it.nextElement().data);
		return list;
	}

	@Override
	protected void setCurrentObject(List<Map<Integer, FieldData>> obj) {
		data.clear();
		for (Map<Integer, FieldData> map : obj) {
			data.addElement(new DataWrapper(map));
		}
	}
	
}
