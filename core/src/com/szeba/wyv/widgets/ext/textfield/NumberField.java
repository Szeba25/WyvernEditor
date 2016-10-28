package com.szeba.wyv.widgets.ext.textfield;

import com.szeba.wyv.widgets.TextField;

public abstract class NumberField extends TextField {
	
	public NumberField(int ox, int oy, int rx, int ry, int w) {
		super(ox, oy, rx, ry, w, 1);
		setSelectAllOnFocus(true);
	}
	
	@Override
	protected void addThisString(String character) {
		if (except(character) || isValid(buildString(character))) {
			super.addThisString(character);
		}
	}
	
	public String getValue() {
		if (isValid(getText())) {
			return getText();
		} else {
			return defaultValue();
		}
	}
	
	protected abstract String defaultValue();
	
	protected abstract boolean except(String character);
	
	protected abstract boolean isValid(String character);
	
	protected String buildString(String character) {
		return getText().substring(0, getCursorPos()) + 
					character + getText().substring(getCursorPos(), getText().length());
	}
	
}
