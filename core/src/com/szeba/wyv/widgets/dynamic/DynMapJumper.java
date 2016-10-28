package com.szeba.wyv.widgets.dynamic;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.panels.map.JumpPanel;

public class DynMapJumper extends JumpPanel implements Dynamic {

	String receiver;
	
	public DynMapJumper(int ox, int oy, int rx, int ry) {
		super(ox, oy, rx, ry);
		
		// We remove the close button
		this.removeWidget(2);
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
