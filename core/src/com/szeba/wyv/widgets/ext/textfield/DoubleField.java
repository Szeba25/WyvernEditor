package com.szeba.wyv.widgets.ext.textfield;

public class DoubleField extends NumberField {

	private double min;
	private double max;
	
	public DoubleField(int ox, int oy, int rx, int ry, int w, String mode, double max) {
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
			System.err.println("Double field with invalid mode: " + mode);
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
			double n = Double.parseDouble(character);
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
			return Double.toString(min);
		}
	}
	
}
