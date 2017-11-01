package com.smanzana.templateeditor.uiutils;

import java.awt.Color;

import javax.swing.border.LineBorder;

import com.smanzana.templateeditor.uiutils.UIColor.Colored;
import com.smanzana.templateeditor.uiutils.UIColor.Key;

public class ColoredLineBorder extends LineBorder implements Colored {

	private static final long serialVersionUID = -8644106405623377855L;

	public ColoredLineBorder(Color startingColor) {
		super(startingColor);
	}
	
	public void setColor(Color newColor) {
		this.lineColor = newColor;
	}

	@Override
	public void updateColor(Key key, Color newColor) {
		this.setColor(newColor);
	}
	
	public Color getColor() {
		return this.lineColor;
	}

}
