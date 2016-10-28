package com.szeba.wyv.widgets.dynamic;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.ext.button.SwitchButton;

public class DynSwitchButton extends SwitchButton implements Dynamic {

	private String receiver;
	
	public DynSwitchButton(int ox, int oy, int rx, int ry, int w, int h,
			String text1, String text2) {
		super(ox, oy, rx, ry, w, h, text1, text2);
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
		this.setState(Integer.parseInt(value));
	}

	@Override
	public String dynGetValue() {
		return Integer.toString(this.getState());
	}

	@Override
	public void dynReset() {
		this.setState(0);
	}

	@Override
	public String dynGetCommandStringFormatter(String data) {
		return this.getStateName(Integer.parseInt(data));
	}
}
