package com.smanzana.templateeditor.uiutils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

/**
 * Holds together all colors for different UI components
 * @author Skyler
 *
 */
public class UIColor {
	
	public static interface Colored {
		
		/**
		 * Called once a color has been updated in UIColor.
		 * Registered components should update as expected
		 * @param key
		 */
		public void updateColor(Key key, Color newColor);
	}

	public static enum Key {
		BASE_BACKGROUND(new Color(221, 213, 234)),
		BASE_FOREGROUND,
		BASE_NESTED_BACKGROUND(new Color(255, 255, 255)),
		BASE_SYSTEM(new Color(190, 198, 211)),
		EDITOR_LIST_BACKGROUND(new Color(245, 247, 220)),
		EDITOR_LIST_FOREGROUND(new Color(0, 0, 0)),
		EDITOR_MAIN_BACKGROUND(new Color(255, 255, 255)),
		EDITOR_MAIN_FOREGROUND(new Color(0, 0, 0)),
		EDITOR_MAIN_PANE_BACKGROUND(new Color(237, 237, 230)),
		EDITOR_MAIN_PANE_FOREGROUND(new Color(0, 0, 0)),
		BASE_TEXT_BACKGROUND,
		BASE_TEXT_FOREGROUND,
		;
		
		private Color def;
		
		private Key() {
			def = Color.BLACK;
		}
		
		private Key(Color def) {
			this.def = def;
		}
		
		public Color getDefault() {
			return def;
		}
		
		public String prettyName() {
			String raw = name();
			String buf = "";
				
			buf += raw.substring(0, 1);
			raw = raw.substring(1);
			raw = raw.toLowerCase();
			
			int pos;
			while (-1 != (pos = raw.indexOf('_'))) {
				// pos is position of first underscore.
				// copy up to pos into buf. Then copy char after pos as uppercase.
				// then set raw past capital char
				buf += raw.substring(0, pos);
				buf += raw.substring(pos + 1, pos + 2).toUpperCase();
				raw = raw.substring(pos + 2);
			}
			buf += raw;
			
			return buf;
		}
	}
	
	private static UIColor instance = null;
	
	public static UIColor instance() {
		if (instance == null)
			instance = new UIColor();
		
		return instance;
	}
	
	private static class DataStruct {
		private Color color;
		private List<Colored> cDependents;
		private List<JComponent> jDependentsForeground;
		private List<JComponent> jDependentsBackground;
		
		public DataStruct(Color color) {
			this.color = color;
			this.cDependents = new LinkedList<>();
			this.jDependentsForeground = new LinkedList<>();
			this.jDependentsBackground = new LinkedList<>();
		}
		
		public void addDependent(Colored c) {
			if (!this.cDependents.contains(c))
				this.cDependents.add(c);
		}
		
		public void addDependent(JComponent c) {
			addDependent(c, true);
		}
		
		public void addDependent(JComponent c, boolean background) {
			List<JComponent> list = (background ? jDependentsBackground : jDependentsForeground);
			if (!list.contains(c))
				list.add(c);
		}
		
		public void updateColor(Key key, Color newColor) {
			this.color = newColor;
			for (Colored c : cDependents) {
				if (c == null)
					continue;
				
				c.updateColor(key, newColor);
			}
			for (JComponent comp : jDependentsBackground) {
				if (comp == null)
					continue;
				
				comp.setBackground(newColor);
			}
			for (JComponent comp : jDependentsForeground) {
				if (comp == null)
					continue;
				
				comp.setForeground(newColor);
			}
		}
	}
	
	private Map<Key, DataStruct> colors;
	
	private UIColor() {
		colors = new EnumMap<>(Key.class);
		for (Key k : Key.values())
			colors.put(k, new DataStruct(k.getDefault()));
	}
	
	public void registerColors(JComponent comp, Key foreground, Key background) {
		DataStruct data;
		
		if (foreground != null) {
			data = colors.get(foreground);
			data.addDependent(comp, false);
			comp.setForeground(data.color);
		}
		
		if (background != null) {
			data = colors.get(background);
			
			if (background != foreground) // If same, would register twice
				data.addDependent(comp, true);
			
			comp.setBackground(data.color);
		}
	}
	
	public void registerColor(JComponent comp, Key color) {
		if (color != null) {
			DataStruct data = colors.get(color);
			data.addDependent(comp);
			comp.setBackground(data.color);
		}
	}
	
	public void registerColor(Colored dependent, Key color) {
		if (color != null) {
			DataStruct data = colors.get(color);
			data.addDependent(dependent);
			dependent.updateColor(color, data.color);
		}
	}
	
	public void registerColors(Colored dependent, Key ... colors) {
		DataStruct data;
		
		if (colors != null)
		for (Key key : colors) {
			if (key == null)
				continue;
			
			data = this.colors.get(key);
			data.addDependent(dependent);
			dependent.updateColor(key, data.color);
		}
	}
	
	/**
	 * Looks up the color for the given keys and sets the provided component's foreground and background.
	 * Furthermore, registers the given component to be updated any time the color that matches
	 * the given key is updated.
	 */
	public static void setColors(JComponent comp, Key foreground, Key background) {
		instance().registerColors(comp, foreground, background);
	}
	
	/**
	 * Looks up the color for the given key and sets the provided component's <b>Background</b>.
	 * Furthermore, registers the given component to be updated any time the color that matches
	 * the given key is updated.
	 * @param comp
	 * @param color
	 */
	public static void setColor(JComponent comp, Key color) {
		instance().registerColor(comp, color);
	}
	
	/**
	 * Registers the given colored instance as dependent on the provided color.
	 * Then calls the updateColor method to provide it with the correct color.
	 * @param dependent
	 * @param color
	 */
	public static void setColor(Colored dependent, Key color) {
		instance().registerColor(dependent, color);
	}
	
	/**
	 * Like {@link #registerColor(Colored, Key)} but with a whole host of colors.
	 * @param dependent
	 * @param colors
	 */	
	public static void setColors(Colored dependent, Key ... colors) {
		instance().registerColors(dependent, colors);
	}

	/**
	 * Creates and returns a menu item that, when selected, displays a dialog
	 * that allows the user to pick and replace colors for each color key.
	 * This includes setting up an action listener. You should not need to
	 * register one manually
	 * @return
	 */
	public static JMenuItem createMenuItem(String menuString) {
		JMenuItem item = new JMenuItem(menuString);
		JDialog dialog = new JDialog((JFrame) null, "Set Color Preferences", true);
		item.addActionListener((ActionEvent arg0) -> {
			Map<Key, Color> backup = new EnumMap<>(Key.class);
			for (Key k : Key.values())
				backup.put(k, instance().colors.get(k).color);
			
			
			JPanel mainPanel = new JPanel(new BorderLayout());
			JPanel bottomPanel = new JPanel();
			JPanel topPanel = new JPanel();
			JButton button;
			
			topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
			topPanel.add(Box.createRigidArea(new Dimension(20, 0)));
			button = new JButton("Load Scheme");
			button.addActionListener((ActionEvent event) -> {
				JFileChooser fc = new JFileChooser(new File("."));
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(File arg0) {
						return arg0.isDirectory() || (arg0.getName().contains(".") &&
							arg0.getName().substring(arg0.getName().lastIndexOf('.') + 1).equals("sch"));
					}

					@Override
					public String getDescription() {
						return "Application Scheme Files (*.sch)";
					}
					
				});
				fc.showOpenDialog(null);
				
				File sel = fc.getSelectedFile();
				if (sel != null) {
					if (!sel.exists())
						JOptionPane.showMessageDialog(null, "Could not find the selected file", "Error opening template", JOptionPane.PLAIN_MESSAGE);
					else {
						try {
							loadFromFile(sel);
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("Encountered FileIO errors reading scheme file");
						}
					}
				}
			});		
			topPanel.add(button);
			topPanel.add(Box.createRigidArea(new Dimension(10, 0)));
			
			button = new JButton("Save Scheme");
			button.addActionListener((ActionEvent event) -> {
				JFileChooser fc = new JFileChooser(new File("."));
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(File arg0) {
						return arg0.isDirectory() || (arg0.getName().contains(".") &&
							arg0.getName().substring(arg0.getName().lastIndexOf('.') + 1).equals(".sch"));
					}

					@Override
					public String getDescription() {
						return "Application Scheme Files (*.sch)";
					}
					
				});
				fc.showSaveDialog(null);
				
				File sel = fc.getSelectedFile();
				
				if (!fc.getFileFilter().accept(sel)) {
					sel = new File(sel.getAbsolutePath(), ".sch");
				}
				
				if (sel != null) {
					try {
						saveToFile(sel);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Encountered FileIO errors reading scheme file");
					}
				}
			});		
			topPanel.add(button);
			
			topPanel.add(Box.createRigidArea(new Dimension(10, 0)));
			topPanel.add(Box.createHorizontalGlue());
			mainPanel.add(topPanel, BorderLayout.NORTH);
			
			bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
			bottomPanel.add(Box.createHorizontalGlue());
			
			button = new JButton("Default");
			button.addActionListener((ActionEvent event) -> {
				for (Key k : Key.values())
					instance().colors.get(k).updateColor(k, k.getDefault());
			});
			bottomPanel.add(button);
			bottomPanel.add(Box.createRigidArea(new Dimension(20, 0)));
			
			button = new JButton("OK");
			button.addActionListener((ActionEvent event) -> {
				// close. Nothing to do
				dialog.setVisible(false);
			});
			bottomPanel.add(button);
			bottomPanel.add(Box.createRigidArea(new Dimension(10, 0)));
			
			button = new JButton("Cancel");
			button.addActionListener((ActionEvent event) -> {
				// revert back to backup
				for (Key k : Key.values())
					instance().colors.get(k).updateColor(k, backup.get(k));
				
				dialog.setVisible(false);
			});
			bottomPanel.add(button);
			bottomPanel.add(Box.createRigidArea(new Dimension(10, 0)));
			
			mainPanel.add(bottomPanel, BorderLayout.SOUTH);
			JPanel contentPanel = new JPanel();
			contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
			contentPanel.setBorder(new EmptyBorder(10,10,10,10));
			contentPanel.add(new JLabel("Colors"));
			
			//TODO make pretty <3
			for (Key k : Key.values()) {
				JPanel sub = new JPanel();
				sub.setLayout(new BoxLayout(sub, BoxLayout.LINE_AXIS));
				sub.add(Box.createRigidArea(new Dimension(10, 0)));
				sub.add(new JLabel(k.prettyName()));
				sub.add(Box.createRigidArea(new Dimension(20, 0)));
				sub.add(Box.createHorizontalGlue());
				JPanel swatch = new JPanel();
				swatch.setMaximumSize(new Dimension(32, 32));
				swatch.setMinimumSize(swatch.getMaximumSize());
				UIColor.setColor(swatch, k);
				sub.add(swatch);
				sub.add(Box.createRigidArea(new Dimension(10, 0)));
				
				sub.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						DataStruct data = instance().colors.get(k);
						Color selected = JColorChooser.showDialog(dialog, "Select Color", data.color);
						if (selected != null) {
							data.updateColor(k, selected);
						}
					}
				});
				
				contentPanel.add(sub);
			}
			
			mainPanel.add(contentPanel, BorderLayout.CENTER);
			
			JOptionPane content = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION,
					null, new Object[]{});
			
			dialog.setContentPane(content);
			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			dialog.pack();
			dialog.setVisible(true);
			
			// dialog has closed.
			
			// Leaks references to swatches. They leak AND they get updated even though they're expired
		});
			
		
		return item;
	}
	
	public static void saveToFile(File outFile) throws FileNotFoundException {
		String buf = "";
		
		for (Key k : Key.values()) {
			Color c = instance().colors.get(k).color;
			buf += k.name() + ":" + c.getRGB() + ",";
		}
		
		PrintWriter writer = new PrintWriter(outFile);
		writer.println(buf);
		writer.close();
	}
	
	public static void loadFromFile(File inFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(inFile));
		String raw = reader.readLine();
		reader.close();
		
		for (String sub : raw.split(",")) {
			int pos = sub.indexOf(':');
			if (pos == -1)
				continue;
			
			String key = sub.substring(0, pos);
			Key real;
			try {
				real = Key.valueOf(key);
			} catch (Exception e) {
				System.err.println("Failed to find key for read value: " + key);
				continue;
			}
			
			int rgb;
			try {
				rgb = Integer.parseInt(sub.substring(pos+1));
			} catch (NumberFormatException e) {
				System.err.println("Found invalid RGB value: " + sub.substring(pos+1));
				continue;
			}
			
			instance().colors.get(real).updateColor(real, new Color(rgb));
		}
		
	}
}
