package com.smanzana.templateeditor.editor.fields;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

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

import com.smanzana.templateeditor.IEditorDisplayFormatter;
import com.smanzana.templateeditor.editor.IEditor;

/**
 * List of elements that are themselves editable.
 * When selected, a dialog is displayed for editing that element.
 * This list is made to be embedded in another editor.
 * @author Skyler
 */
public class EditorListField<T extends IEditorDisplayFormatter> extends AEditorField<T> implements ActionListener {

	public static interface EditorListEditor<K> extends IEditor<K> {
		
		/**
		 * Set the current object we're editting.
		 * Type: DataWrapper of T
		 * @param obj
		 */
		public void setEdittingObject(Object obj);
		
		/**
		 * Called to check whether the given values are valid.
		 * If they are not valid, the editor cannot close without cancelling
		 * @return
		 */
		public boolean isEditorValid();
		
		/**
		 * Handy method for commit  changes made in the editor to the data wrapper.
		 * Called just before dialog is closed and editor should stop interacting
		 * with the object.
		 */
		public void commit();
		
		/**
		 * Reset the object to the state it was when setEdittingObject was last called.
		 * This is called when the editor cancels the operation.
		 */
		public void resetObject();
		
	}
	
	public static class DataWrapper<T> {
		private T data;
		
		public DataWrapper(T data) {
			this.data = data;
		}

		public T getData() {
			return data;
		}

		public void setData(T data) {
			this.data = data;
		}
	}
	
	private DefaultListModel<DataWrapper<T>> data;
	private JList<DataWrapper<T>> dataList;
	private JPanel wrapper;
	private EditorListCallback hook;
	private EditorListFactory factory;
	private EditorListEditor popupEditor;
	
	public EditorListField(String title, EditorListEditor popupEditor, EditorListCallback hook, EditorListFactory factory,
			List<T> options, boolean canAddRemove) {
		this.hook = hook;
		this.factory = factory;
		this.popupEditor = popupEditor;
		
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
		dataList = new JList<DataWrapper<T>>(data);
		dataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dataList.setLayoutOrientation(JList.VERTICAL);
		UIColor.setColors(dataList, UIColor.Key.EDITOR_MAIN_FOREGROUND, UIColor.Key.EDITOR_MAIN_BACKGROUND);
		dataList.setCellRenderer((list, e, index, isSelected, focus) -> {
			JLabel comp;
			
			comp = new JLabel("  " + e.getData().getEditorName());
			comp.setOpaque(true);

	        // check if this cell represents the current DnD drop location
	        JList.DropLocation dropLocation = list.getDropLocation();
	        if (dropLocation != null
	                && !dropLocation.isInsert()
	                && dropLocation.getIndex() == index) {

	        	UIColor.setColors(comp, UIColor.Key.EDITOR_MAIN_BACKGROUND, UIColor.Key.EDITOR_MAIN_FOREGROUND);

	        // check if this cell is selected
	        } else if (isSelected) {
	            UIColor.setColors(comp, UIColor.Key.EDITOR_MAIN_BACKGROUND, UIColor.Key.EDITOR_MAIN_FOREGROUND);
	        // unselected, and not the DnD drop location
	        } else {
	        	UIColor.setColors(comp, UIColor.Key.EDITOR_MAIN_FOREGROUND, UIColor.Key.EDITOR_MAIN_BACKGROUND);
	        };
			
			return comp;
		});
		
		dataList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				doMousePressed(e);
			}
		});
		
		for (T t : options) {
			data.addElement(new DataWrapper<T>(t));
		}

		dataList.setVisibleRowCount(20);
		dataList.setMaximumSize(dataList.getPreferredScrollableViewportSize());
		
		// TODO list + add/remove buttons
		Dimension med;
		med = new Dimension(300, dataList.getPreferredScrollableViewportSize().height + 30);
		
		Dimension small = new Dimension(med.width - 30, med.height - 30);
		dataList.setPreferredSize(small);
		JComponent panel = new JScrollPane(dataList);
		panel.setMaximumSize(med);
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

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (hook != null) {
			List<Object> list = new LinkedList<>();
			Enumeration<DataWrapper<T>> it = data.elements();
			while (it.hasMoreElements())
				list.add(it.nextElement().getData());
			hook.setField(list);
		}
	}
	
	public void doMousePressed(MouseEvent e) {
		if (e.getClickCount() != 2)
			return;
		
		if (dataList.isSelectionEmpty())
			return;
		
		DataWrapper<T> val = data.get(dataList.getSelectedIndex());
		
		edit(val);
	}
	
	/**
	 * Returns if successful (not cancelled)
	 * @param value
	 * @return
	 */
	private boolean edit(DataWrapper<T> value) {
		popupEditor.setEdittingObject(value);
		final StringBuffer cancelled = new StringBuffer();
		
		JDialog dialog = new JDialog((JFrame) null, "Editor", true);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(popupEditor.getComponent(), BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		JButton button = new JButton("OK");
		button.addActionListener(new ActionListener() {
			private JButton owner;
			{this.owner = button;}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!popupEditor.isEditorValid()) {
					this.owner.setEnabled(false);
					return;
				}
				
				popupEditor.commit();
				dialog.setVisible(false);	
			}
		});
		bottomPanel.add(button);
		bottomPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		
		// Try to make the OK button reactive
		mainPanel.addFocusListener(new FocusListener() {

			private void react() {
				button.setEnabled(popupEditor.isEditorValid());
			}
			
			@Override
			public void focusGained(FocusEvent arg0) {
				react();
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				react();
			}
		});
		
		JButton button2 = new JButton("Cancel");
		button2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelled.append("yup");
				popupEditor.resetObject();
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
		
		if (cancelled.length() == 0)
			actionPerformed(null);
		
		return cancelled.length() == 0;
	}
	
	private void add() {
		@SuppressWarnings("unchecked")
		DataWrapper<T> val = new DataWrapper<T>((T) factory.construct());
		
		
		if (edit(val))
			data.addElement(val);
	}
	
	private void remove() {
		remove(dataList.getSelectedIndex());
	}
	
	private void remove(int index) {
		data.remove(index);
		actionPerformed(null);
	}
	
}
