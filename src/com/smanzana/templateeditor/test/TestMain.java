package com.smanzana.templateeditor.test;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import com.smanzana.templateeditor.IEditorOwner;
import com.smanzana.templateeditor.api.FieldData;
import com.smanzana.templateeditor.api.IEditorDisplayFormatter;
import com.smanzana.templateeditor.api.ObjectDataLoader;
import com.smanzana.templateeditor.api.ObjectDataLoader.IFactory;
import com.smanzana.templateeditor.data.SimpleFieldData;
import com.smanzana.templateeditor.editor.EnumMapEditor;
import com.smanzana.templateeditor.editor.fields.GrabListField;
import com.smanzana.templateeditor.uiutils.UIColor;

public class TestMain {
	
	private static enum Key {
		STRVAL_1,
		STRVAL_2,
		INTVAL_1,
		STRVAL_3,
		DOUBLEVAL_1,
		INTVAL_2,
		COMPLEX_1,
		COMPLEX_2,
		LISTINT_1,
		SUBSET_1,
		AUTO_1,
		LISTAUTO_1,
		AUTO_2,
	}
	
	static ObjectDataLoader<TestObject> loader;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		JFrame frame = new JFrame("TemplateEditor Test");
		
		Map<Key, FieldData> map = new EnumMap<>(Key.class);
		
		map.put(Key.STRVAL_1, FieldData.simple("Default1").name("Str1"));
		map.put(Key.STRVAL_2, FieldData.simple("Default2").name("Str2").desc("This field is super important!"));
		map.put(Key.INTVAL_1, FieldData.simple(-1).name("Int1"));
		map.put(Key.STRVAL_3, FieldData.simple("Default3").name("Str3"));
		map.put(Key.DOUBLEVAL_1, FieldData.simple(-1.0).name("Double1"));
		map.put(Key.INTVAL_2, FieldData.simple(1).name("Int2").desc("line1").desc("line2"));
		
		Map<Integer, FieldData> nested1 = new HashMap<>();
		nested1.put(1, FieldData.simple(true).name("Survives to end"));
		nested1.put(2, FieldData.simple("NestedDefault1").name("Description"));
		nested1.put(3, FieldData.simple("Default Name").name("Name"));
		map.put(Key.COMPLEX_1, FieldData.complex(nested1, new IEditorDisplayFormatter<Integer>() {
			@Override
			public String getEditorName(Map<Integer, FieldData> dataMap) {
				return (String) ( (SimpleFieldData) dataMap.get(3)).getValue();
			}
			@Override
			public String getEditorTooltip(Map<Integer, FieldData> dataMap) {
				return (String) ((SimpleFieldData) dataMap.get(2)).getValue();
			}
		}).name("Super Power"));
		
		Map<Integer, FieldData> nested2 = new HashMap<>();
		nested2.put(1, FieldData.simple("Default Name").name("Name"));
		nested2.put(2, FieldData.simple(false).name("Important"));
		nested2.put(3, FieldData.simple("NestedDefault1").name("Description"));
		List<Map<Integer, FieldData>> complexList = new LinkedList<>();
		
		Map<Integer, FieldData> nestedobj = new HashMap<>();
		nestedobj.put(1, FieldData.simple("Default Name").name("Name"));
		nestedobj.put(2, FieldData.simple(false).name("Important"));
		nestedobj.put(3, FieldData.simple("NestedDefault1").name("Description"));
		nestedobj.put(4, map.get(Key.COMPLEX_1).clone().name("Nested Object"));
		complexList.add(nestedobj);
		
		map.put(Key.COMPLEX_2, FieldData.complexList(nested2, new IEditorDisplayFormatter<Integer>() {
			@Override
			public String getEditorName(Map<Integer, FieldData> dataMap) {
				if (dataMap == null)
					return null;
				return (String) ( (SimpleFieldData) dataMap.get(1)).getValue();
			}
			@Override
			public String getEditorTooltip(Map<Integer, FieldData> dataMap) {
				if (dataMap == null)
					return null;
				return (String) ( (SimpleFieldData) dataMap.get(3)).getValue();
			}
		}, complexList).name("Spell List"));
		
		List<Integer> intlist = new LinkedList<>();
		intlist.add(99);
		intlist.add(98);
		intlist.add(97);
		intlist.add(97);
		intlist.add(97);
		map.put(Key.LISTINT_1, FieldData.listInt(intlist).name("Owned Numbers"));
		
		List<String> stringSet = new LinkedList<>();
		List<String> subset = new LinkedList<>();
		String str;
		
		str = "Fire";
		stringSet.add(str);
		subset.add(str);
		str = "Water";
		stringSet.add(str);
		subset.add(str);
		str = "Earth";
		stringSet.add(str);
		str = "Wind";
		stringSet.add(str);
		subset.add(str);
		str = "Light";
		stringSet.add(str);
		str = "Dark";
		stringSet.add(str);
		map.put(Key.SUBSET_1, FieldData.subset(stringSet, subset, new GrabListField.DisplayFormatter<String>() {
			@Override
			public String getListDataName(String data) {
				return data;
			}

			@Override
			public String getListDataDesc(String data) {
				return null;
			}
		}).name("Element Affinities").desc("What elements does this creature thrive in"));
		
		ObjectDataLoader<TestObject> loader = new ObjectDataLoader<>(new TestSubObject("Name1", "No Description", 5, false, 66));
		
		if (!loader.isValid())
			System.exit(1);
		
		map.put(Key.AUTO_1, FieldData.complexObject(loader).name("Auto Value"));
		
		List<TestObject> testList = new LinkedList<>();
		IFactory<TestObject> testFactory = new IFactory<TestObject>() {
			@Override
			public TestObject construct() {
				return new TestObject("", "", 0, false);
			}
		};
		TestObject template = new TestObject("name", "desc", 1, true);
		testList.add(new TestObject("name1", "desc1", 1, false));
		testList.add(new TestObject("name2", "desc2", 4, true));
		testList.add(new TestObject("name3", "desc3", 9, true));
		map.put(Key.LISTAUTO_1, FieldData.complexObject(new ObjectDataLoader<TestObject>(template, testList, testFactory)));
		
		TestMain.loader = new ObjectDataLoader<>(new TestSubObject("STR1", "DESC2", 13, true, 55));
		if (!TestMain.loader.isValid())
			System.exit(1);
		
		map.put(Key.AUTO_2, FieldData.complexObject(TestMain.loader));
		
		///////////////////////////////////////
		
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
				
				close();
				
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
		
		frame.setPreferredSize(new Dimension(640, 480));
		
		frame.pack();
		frame.setVisible(true);
	}
	
	private static void close() {
		// Check out what we get back! or something!
		TestObject o = loader.fetchEdittedValue();
		System.out.print(o);
	}

}
