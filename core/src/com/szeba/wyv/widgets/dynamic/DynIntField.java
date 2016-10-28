package com.szeba.wyv.widgets.dynamic;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.ext.textfield.IntField;

public class DynIntField extends IntField implements Dynamic {

	private String receiver;
	
	public DynIntField(int ox, int oy, int rx, int ry, int w, String mode, int max) {
		super(ox, oy, rx, ry, w, mode, max);
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
	}
	
	@Override
	public void dynSetValue(String value) {
		setText(value);
	}

	@Override
	public String dynGetValue() {
		return getValue();
	}

	@Override
	public void dynReset() {
		setText(this.defaultValue());
	}
	
	@Override
	public String dynGetCommandStringFormatter(String data) {
		return data;
	}
}
