package com.szeba.wyv.widgets.ext.textfield;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.Separator;
import com.szeba.wyv.widgets.TextField;

public class ArrayField extends TextField {

	public ArrayField(int ox, int oy, int rx, int ry, int w, int count) {
		super(ox, oy, rx, ry, w, count);
	}

	@Override
	public Color getBkgColor() {
		return Palette.WIDGET_BKG4;
	}
	
	@Override
	public void modifyText(String text) {
		this.forceText(text);
	}
	
	@Override
	public String getText() {
		String fin = "" + Separator.array;
		for (int i = 0; i < super.getText().length(); i++) {
			if (i == super.getText().length()-1) {
				fin += (int) super.getText().charAt(i);
			} else {
				fin += (int) super.getText().charAt(i);
				fin += Separator.array;
			}
		}
		return fin;
	}
	
	public void setText(ArrayList<String> ar) {
		String fin = "";
		for (String s : ar) {
			fin += (char) Integer.parseInt(s);
		}
		setText(fin);
	}
	
}
