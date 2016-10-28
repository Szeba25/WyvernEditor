package com.szeba.wyv.widgets.ext.textfield;

public class IntField extends NumberField {

	private int min;
	private int max;
	
	public IntField(int ox, int oy, int rx, int ry, int w, String mode, int max) {
		super(ox, oy, rx, ry, w);
		if (mode.equals("Z")) {
			this.min = max*-1;
			this.max = max;
		} else if (mode.equals("N")) {
			this.min = 0;
			this.max = max;
		} else if (mode.equals("Z+")) {
			this.min = 1;
			this.max = max;
		} else {
			System.err.println("Int field with invalid mode: " + mode);
		}
	}
	
	@Override
	protected boolean except(String character) {
		if (min < 0 && character.equals("-") && getText().length() == 0) {
			return true;
		}
		return false;
	}
	
	@Override
	protected boolean isValid(String character) {
		try {
			int n = Integer.parseInt(character);
			if (n < min || n > max) {
				return false;
			}
		} catch(NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	@Override
	protected String defaultValue() {
		if (this.min < 0) {
			return "0";
		} else {
			return Integer.toString(min);
		}
	}
	
}
