package com.smanzana.templateeditor.editor.fields;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.smanzana.templateeditor.IEditorOwner;
import com.smanzana.templateeditor.api.FieldData;
import com.smanzana.templateeditor.uiutils.ColoredLineBorder;
import com.smanzana.templateeditor.uiutils.UIColor;

/**
 * List of elements that are themselves editable.
 * When selected, a dialog is displayed for editing that element.
 * This list is made to be embedded in another editor.
 * @author Skyler
 */
public class GenericListField<T extends FieldData> extends AEditorField<List<T>> {
	
	private class ListItem extends JPanel implements MouseListener {
		
		private static final long serialVersionUID = -8288867064033323901L;
		private ColoredLineBorder border;
		private boolean selected;
		
		public ListItem(LayoutManager manager) {
			super(manager);
			this.addMouseListener(this);
			border = new ColoredLineBorder(Color.BLACK);
			UIColor.setColor(border, UIColor.Key.EDITOR_MAIN_FOREGROUND);
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			if (!arg0.isControlDown())
				clearSelection();
			
			select();
		}
		
		protected void deselect() {
			selected = false;
			this.setBorder(null);
		}
		
		protected void select() {
			selected = true;
			this.setBorder(border);
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			;
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			;
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			;
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			;
		}
	}
		
	private T base; // used to create new subfields
	private JPanel wrapper;
	private JPanel dataList; // components are STRICTLY ListItem
	private Map<EditorField<?>, T> fieldMap; // So we don't keep making them
	private Map<ListItem, EditorField<?>> listMap;
	
	public GenericListField(T baseEditor, List<T> fields) {
		this.base = baseEditor;
		fieldMap = new HashMap<>();
		listMap = new HashMap<>();
		
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.LINE_AXIS));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		UIColor.setColors(wrapper, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
		
		dataList = new JPanel();
		dataList.setLayout(new BoxLayout(dataList, BoxLayout.PAGE_AXIS));
		UIColor.setColors(dataList, UIColor.Key.EDITOR_MAIN_FOREGROUND, UIColor.Key.EDITOR_MAIN_BACKGROUND);
				
		for (T item : fields) {
			add(item);
		}

		JComponent panel = new JScrollPane(dataList);
		panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, 150));
		dataList.setMinimumSize(new Dimension(20, 200));
		dataList.setMaximumSize(new Dimension(Short.MAX_VALUE, 400));
		wrapper.add(panel);
		wrapper.add(Box.createRigidArea(new Dimension(20, 0)));
		
		panel = new JPanel();
		UIColor.setColors(panel, UIColor.Key.EDITOR_MAIN_FOREGROUND, UIColor.Key.EDITOR_MAIN_BACKGROUND);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(Box.createVerticalGlue());
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		JButton button = new JButton("New");
		button.setAlignmentX(.5f);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				add();
			}
		});
		panel.add(button);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		button = new JButton("Duplicate");
		button.setAlignmentX(.5f);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				duplicate();
			}
		});
		panel.add(button);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		button = new JButton("Delete");
		button.setAlignmentX(.5f);
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
		add(cloneBase());
	}
	
	private void add(T val) {
		EditorField<?> field = val.constructField();
		ListItem item = new ListItem(new BorderLayout());
		item.add(field.getComponent(), BorderLayout.CENTER);
		Dimension pref = item.getPreferredSize();
		item.setMaximumSize(new Dimension(Short.MAX_VALUE, pref.height));
		item.setMinimumSize(new Dimension(20, pref.height));
		field.setOwner(this.getOwner());
		fieldMap.put(field, val);
		listMap.put(item, field);
		dataList.add(item);
		
		wrapper.validate();
	}
	
	@SuppressWarnings("unchecked")
	private void duplicate() {
		List<ListItem> active = new LinkedList<>();
		for (ListItem item : listMap.keySet()) {
			if (item.selected)
				active.add(item);
		}
		
		if (!active.isEmpty()) {
			for (ListItem c : active) {
				add((T) fieldMap.get(listMap.get(c)).clone());
			}
			wrapper.validate();
			wrapper.repaint();
			
			update();
		}
	}
	
	private void remove() {
		List<ListItem> culls = new LinkedList<>();
		for (ListItem item : listMap.keySet()) {
			if (item.selected)
				culls.add(item);
		}
		
		if (!culls.isEmpty()) {
			for (ListItem c : culls) {
				dataList.remove(c);
				EditorField<?> field = listMap.get(c);
				fieldMap.remove(field);
				listMap.remove(c);
			}
			wrapper.validate();
			wrapper.repaint();
			
			update();
		}
	}

	@Override
	public List<T> getObject() {
		List<T> list = new LinkedList<>();
		
		for (EditorField<?> field : fieldMap.keySet()) {
			T val = fieldMap.get(field);
			val.fillFromField(field);
			list.add(val);
		}
		return list;
	}

	@Override
	protected void setCurrentObject(List<T> obj) {
		dataList.removeAll();
		fieldMap.clear();
		listMap.clear();
		for (T o : obj) {
			add(o);
		}
	}
	
	protected void clearSelection() {
		for (ListItem item : listMap.keySet()) {
			if (item.selected) {
				item.deselect();
			}
		}
	}
	
	@Override
	public void setOwner(IEditorOwner owner) {
		super.setOwner(owner);
		for (EditorField<?> field : fieldMap.keySet()) {
			field.setOwner(owner);
		}
	}
}
