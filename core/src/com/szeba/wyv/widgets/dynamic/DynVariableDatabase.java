package com.szeba.wyv.widgets.dynamic;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.panels.VariableDatabaseCls;

public class DynVariableDatabase extends VariableDatabaseCls implements Dynamic {

	private String receiver;
	
	public DynVariableDatabase(int ox, int oy, int rx, int ry) {
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
		if (signal != null) {
			// Works perfectly this way, so I will leave it...
			this.setVisible(true);
		}
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
