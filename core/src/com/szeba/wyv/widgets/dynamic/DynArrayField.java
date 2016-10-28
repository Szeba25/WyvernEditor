package com.szeba.wyv.widgets.dynamic;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.ext.textfield.ArrayField;

public class DynArrayField extends ArrayField implements Dynamic{

	private String receiver;
	
	public DynArrayField(int ox, int oy, int rx, int ry, int w, int count) {
		super(ox, oy, rx, ry, w, count);
	}

	@Override
	public void dynSetReceiver(String receiver) {
		this.receiver = receiver;
	}

	@Override
	public String dynGetReceiver() {
		return receiver;
	}

	@Override
	public void dynProcessSignal(Signal signal) {
		if (signal.getType() == Signal.T_DIRLIST) {
			setText(signal.getParam(1) + "/" + signal.getParam(0));
		} else {
			if (signal.getLength() > 0) {
				setText(signal.getParam(0));
			}
		}
	}

	@Override
	public void dynSetValue(String value) {
		setText(StringUtilities.buildStringList(value));
	}

	@Override
	public String dynGetValue() {
		return getText();
	}

	@Override
	public void dynReset() {
		setText("");
	}
	
	@Override
	public String dynGetCommandStringFormatter(String data) {
		return data;
	}

}
