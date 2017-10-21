package com.smanzana.templateeditor.test;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;

import com.smanzana.templateeditor.FieldData;
import com.smanzana.templateeditor.IEditorDisplayFormatter;
import com.smanzana.templateeditor.IEditorOwner;
import com.smanzana.templateeditor.editor.EnumMapEditor;
import com.smanzana.templateeditor.uiutils.UIColor;

public class TestMain {
	
	private static enum Key {
		STRVAL_1,
		STRVAL_2,
		INTVAL_1,
		STRVAL_3,
		DOUBLEVAL_1,
		INTVAL_2,
		COMPLEX_1;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("TemplateEditor Test");
		
		Map<Key, FieldData> map = new EnumMap<>(Key.class);
		
		map.put(Key.STRVAL_1, FieldData.simple("Default1").name("Str1"));
		map.put(Key.STRVAL_2, FieldData.simple("Default2").name("Str2").desc("This field is super important!"));
		map.put(Key.INTVAL_1, FieldData.simple(-1).name("Int1"));
		map.put(Key.STRVAL_3, FieldData.simple("Default3").name("Str3"));
		map.put(Key.DOUBLEVAL_1, FieldData.simple(-1.0).name("Double1"));
		map.put(Key.INTVAL_2, FieldData.simple(1).name("Int2"));
		
		Map<Integer, FieldData> nested1 = new HashMap<>();
		nested1.put(1, FieldData.simple(true));
		nested1.put(2, FieldData.simple("NestedDefault1"));
		nested1.put(3, FieldData.simple("Default Name"));
		map.put(Key.COMPLEX_1, FieldData.complex(nested1, new IEditorDisplayFormatter<Integer>() {
			@Override
			public String getEditorName(Map<Integer, FieldData> dataMap) {
				return (String) dataMap.get(3).getValue();
			}
			@Override
			public String getEditorTooltip(Map<Integer, FieldData> dataMap) {
				return "Description not supported";
			}
		}));
		
		frame.getContentPane().add(new EnumMapEditor<Key>(
				new IEditorOwner() {
					@Override
					public void dirty() {
						System.out.println("dirty");
					}
				},				
				map));
		
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				frame.setVisible(false);
				frame.dispose();
				System.exit(0);
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
			}
		});
		
		JMenuBar bar = new JMenuBar();
		JMenu menu = new JMenu("Edit");
		bar.add(menu);
		menu.add(UIColor.createMenuItem("Color"));
		frame.setJMenuBar(bar);
		
		frame.pack();
		frame.setVisible(true);
	}

}
