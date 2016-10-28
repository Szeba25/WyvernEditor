package com.szeba.wyv.widgets.dynamic;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.panels.VarNum;

public class DynVarNum extends VarNum implements Dynamic {

	private String receiver;
	
	public DynVarNum(int ox, int oy, int rx, int ry) {
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
		if (signal.getLength() > 0) {
			this.setVariable(signal.getParam(0));
		}
	}

	@Override
	public void dynSetValue(String value) {
		try {
			this.setNumber(Integer.parseInt(value));
			this.setState(0);
		} catch (NumberFormatException nfe) {
			this.setVariable(value);
			this.setState(1);
		}
	}

	@Override
	public String dynGetValue() {
		return this.getValue();
	}

	@Override
	public void dynReset() {
		reset();
	}

	@Override
	public String dynGetCommandStringFormatter(String data) {
		if (this.getState() == 1) {
			return getHolder().getText() + " (" + getHolder().getVariableName() + ")";
		} else {
			return this.getValue();
		}
	}
}
