package com.smanzana.templateeditor.editor;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.smanzana.dungeonmaster.action.subaction.SubAction;
import com.smanzana.dungeonmaster.action.subaction.SubApplyEffect;
import com.smanzana.dungeonmaster.action.subaction.SubDamage;
import com.smanzana.dungeonmaster.action.subaction.SubHeal;
import com.smanzana.dungeonmaster.battle.effects.Effect;
import com.smanzana.dungeonmaster.pawn.Attributes;
import com.smanzana.dungeonmaster.ui.app.UIColor;
import com.smanzana.dungeonmaster.utils.NoteUtil;
import com.smanzana.dungeonmaster.utils.ValueSpecifier;
import com.smanzana.templateeditor.editor.fields.NestedEditorListField;
import com.smanzana.templateeditor.editor.fields.EnumField;
import com.smanzana.templateeditor.editor.fields.NestedEditorListField.EditorListEditor;
import com.smanzana.templateeditor.editor.fields.EnumField.EnumFieldCallback;
import com.smanzana.templateeditor.editor.fields.IntField.IntFieldCallback;

public class SubActionEditor extends JScrollPane implements EditorListEditor {

	private static enum DataKey {
		AMOUNT_HP,
		AMOUNT_MP,
		AMOUNT_STAMINA,
		AMOUNT,
		EFFECT,
		ATTRIBUTE,
	}
	
	private JPanel editor;
	private NestedEditorListField.DataWrapper<SubAction> currentObject;
	private SubAction backup;
	private Map<String, String> prettyMap;
	private JComboBox<String> combo;
	private Map<DataKey, Object> dataMap;
	private Map<DataKey, JComponent> reqFields;
	
	private DataKey[] currentReqs;
	private DataKey[] reqEffect;
	private DataKey[] reqDamage;
	private DataKey[] reqHeal;
	
	// doesn't set as visible
	public SubActionEditor() {
		//need to do ANOTHER editor (like listEditor but for single things). Then hook up effects to be able to use it.
		super();
		prettyMap = new HashMap<>();
		dataMap = new EnumMap<>(DataKey.class);
		reqFields = new EnumMap<>(DataKey.class);
		
		reqEffect = new DataKey[]{DataKey.EFFECT};
		reqDamage = new DataKey[]{DataKey.AMOUNT};
		reqHeal = new DataKey[]{DataKey.AMOUNT_HP, DataKey.AMOUNT_MP, DataKey.AMOUNT_STAMINA};
		currentReqs = reqDamage;
		
		combo = new JComboBox<String>();
		for (String type : SubAction.getRegisteredTypes()) {
			prettyMap.put(type, NoteUtil.pretty(type));
			combo.addItem(prettyMap.get(type));
			if (type.equalsIgnoreCase("damage"))
				combo.setSelectedIndex(combo.getItemCount() - 1);
		}
		combo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (arg0.getStateChange() != ItemEvent.SELECTED)
					return;
				
				String item = (String) arg0.getItem();
				switchType(getKeyName(item)); // convert back from pretty
			}
		});
		
		editor = new JPanel();
		editor.setLayout(new BoxLayout(editor, BoxLayout.PAGE_AXIS));
		UIColor.setColors(editor, UIColor.Key.EDITOR_MAIN_FOREGROUND, UIColor.Key.EDITOR_MAIN_BACKGROUND);
		editor.setBorder(new EmptyBorder(20, 20, 20, 20));
		
		editor.add(combo);
		editor.add(Box.createRigidArea(new Dimension(0, 10)));
		
		JComponent comp = (new ValueField("Amount", new IntFieldCallback() {
			@Override
			public void setField(int value) {
				dataMap.put(DataKey.AMOUNT, value);
			}
		}, getActiveField(DataKey.AMOUNT))).getComponent();
		comp.setBorder(new EmptyBorder(5, 0, 5, 0));
		reqFields.put(DataKey.AMOUNT, comp);
		editor.add(comp);
		
		comp = (new ValueField("Health Amount", new IntFieldCallback() {
			@Override
			public void setField(int value) {
				dataMap.put(DataKey.AMOUNT_HP, value);
			}
		}, getActiveField(DataKey.AMOUNT_HP))).getComponent();
		comp.setBorder(new EmptyBorder(5, 0, 5, 0));
		reqFields.put(DataKey.AMOUNT_HP, comp);
		editor.add(comp);
		
		comp = (new ValueField("Mana Amount", new IntFieldCallback() {
			@Override
			public void setField(int value) {
				dataMap.put(DataKey.AMOUNT_MP, value);
			}
		}, getActiveField(DataKey.AMOUNT_MP))).getComponent();
		comp.setBorder(new EmptyBorder(5, 0, 5, 0));
		reqFields.put(DataKey.AMOUNT_MP, comp);
		editor.add(comp);
		
		comp = (new ValueField("Stamina Amount", new IntFieldCallback() {
			@Override
			public void setField(int value) {
				dataMap.put(DataKey.AMOUNT_STAMINA, value);
			}
		}, getActiveField(DataKey.AMOUNT_STAMINA))).getComponent();
		comp.setBorder(new EmptyBorder(5, 0, 5, 0));
		reqFields.put(DataKey.AMOUNT_STAMINA, comp);
		editor.add(comp);
		
		comp = (new ValueField("Stamina Amount", new IntFieldCallback() {
			@Override
			public void setField(int value) {
				dataMap.put(DataKey.AMOUNT_STAMINA, value);
			}
		}, getActiveField(DataKey.AMOUNT_STAMINA))).getComponent();
		comp.setBorder(new EmptyBorder(5, 0, 5, 0));
		reqFields.put(DataKey.AMOUNT_STAMINA, comp);
		editor.add(comp);
		
		comp = (new EditorSingleField<Effect>("Effect", new EditorSingleFieldCallback() {
			@Override
			public void setField(Object value) {
				dataMap.put(DataKey.EFFECT, value);
			}
		}, getActiveField(DataKey.EFFECT))).getComponent();
		comp.setBorder(new EmptyBorder(5, 0, 5, 0));
		reqFields.put(DataKey.EFFECT, comp);
		editor.add(comp);
		
		comp = (new EnumField<Attributes>("Attribute", new EnumFieldCallback() {
			@Override
			public void setField(String valueName) {
				dataMap.put(DataKey.ATTRIBUTE, Attributes.valueOf(valueName));
			}
		}, (Attributes) getActiveField(DataKey.ATTRIBUTE))).getComponent();
		comp.setBorder(new EmptyBorder(5, 0, 5, 0));
		reqFields.put(DataKey.EFFECT, comp);
		editor.add(comp);
		
		
		this.setViewportView(editor);
		this.validate();
	}
	
	private Object getActiveField(DataKey key) {
		// Pulls (potentially) the active value set on the currentObject.
		switch (key) {
		case AMOUNT:
			if (currentObject.getData() instanceof SubDamage)
				return ((SubDamage) currentObject.getData()).getDamage();
			return null;
		case AMOUNT_HP:
			if (currentObject.getData() instanceof SubHeal)
				return ((SubHeal) currentObject.getData()).getHp();
			return null;
		case AMOUNT_MP:
			if (currentObject.getData() instanceof SubHeal)
				return ((SubHeal) currentObject.getData()).getMp();
			return null;
		case AMOUNT_STAMINA:
			if (currentObject.getData() instanceof SubHeal)
				return ((SubHeal) currentObject.getData()).getStamina();
			return null;
		case ATTRIBUTE:
			return null;
		case EFFECT:
			if (currentObject.getData() instanceof SubApplyEffect)
				return ((SubApplyEffect) currentObject.getData()).getEffect();
			return null;
		}
		
		return null;
	}
	
	private String getKeyName(String pretty) {
		for (String key : prettyMap.keySet()) {
			if (prettyMap.get(key).equals(pretty))
				return key;
		}
		
		return null;
	}
	
	// PASS IN NON-PRETTY TYPE
	private void switchType(String newType) {
		// TODO do this better. Do this in a way that keeps UI stuff out of SubAction
		// but exposes what that subaction needs
		if (newType.equalsIgnoreCase("damage")) {
			currentReqs = reqDamage;
		} else if (newType.equalsIgnoreCase("heal")) {
			currentReqs = reqHeal;
		} else if (newType.equalsIgnoreCase("applyeffect")) {
			currentReqs = reqEffect;
		}
		
		for (DataKey key : reqFields.keySet()) {
			reqFields.get(key).setVisible(false);
		}
		for (DataKey key : currentReqs) {
			reqFields.get(key).setVisible(true);
		}
		
		this.validate();
	}
	
	@Override
	public JComponent getComponent() {
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setEdittingObject(Object obj) {
		currentObject = (NestedEditorListField.DataWrapper<SubAction>) obj;
		backup = SubAction.fromData(currentObject.getData().write("dummy"));
	}

	@Override
	public void resetObject() {
		currentObject.setData(backup);
	}

	@Override
	public boolean isEditorValid() {
		for (DataKey key : currentReqs) {
			if (dataMap.get(key) == null)
				return false;
		}
		
		return true;
	}

	@Override
	public void commit() {
		// This is where we actually make the subaction and set it in the wrapper
		// Don't do it on the fly so that we don't createa million objects
		currentObject.setData(castSubAction());
	}
	
	private SubAction castSubAction() {
		// get current type and do all the magic
		String type = (String) combo.getSelectedItem();
		SubAction sub = SubAction.constructFromType(type);
		
		if (sub instanceof SubApplyEffect) {
			SubApplyEffect o = (SubApplyEffect) sub;
			o.setEffect((Effect) dataMap.get(DataKey.EFFECT));
		} else if (sub instanceof SubDamage) {
			SubDamage o = (SubDamage) sub;
			o.setDamage((ValueSpecifier) dataMap.get(DataKey.AMOUNT));
		} else if (sub instanceof SubHeal) {
			SubHeal o = (SubHeal) sub;
			o.setHp((ValueSpecifier) dataMap.get(DataKey.AMOUNT_HP));
			o.setMp((ValueSpecifier) dataMap.get(DataKey.AMOUNT_MP));
			o.setStamina((ValueSpecifier) dataMap.get(DataKey.AMOUNT_STAMINA));
		}
		
		
		return sub;
	}
	
	
	
}
