package com.szeba.wyv.widgets.dynamic;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.Button;

public class DynButton extends Button implements Dynamic {

	private String receiver;
	
	public DynButton(int ox, int oy, int rx, int ry, int w, int h, String text) {
		super(ox, oy, rx, ry, w, h, text);
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
	}

	@Override
	public String dynGetValue() {
		return null;
	}

	@Override
	public void dynReset() {
	}
	
	@Override
	public String dynGetCommandStringFormatter(String data) {
		return data;
	}
	
}
