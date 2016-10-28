package com.szeba.wyv.widgets.dynamic;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.panels.pickers.ColorPicker;

public class DynColorPicker extends ColorPicker implements Dynamic {

	private String receiver;
	
	public DynColorPicker(int ox, int oy, int rx, int ry) {
		super(ox, oy, rx, ry);
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
		this.setColorString(value);
	}

	@Override
	public String dynGetValue() {
		return this.getColorString();
	}

	@Override
	public void dynReset() {
		this.reset();
	}

	@Override
	public String dynGetCommandStringFormatter(String data) {
		return data;
	}
	
}
