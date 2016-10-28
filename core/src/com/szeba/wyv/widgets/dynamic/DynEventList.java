package com.szeba.wyv.widgets.dynamic;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.ext.list.EventList;

public class DynEventList extends EventList implements Dynamic {

	private String receiver;
	
	public DynEventList(int ox, int oy, int rx, int ry, int w, int hval, boolean comev) {
		super(ox, oy, rx, ry, w, hval, comev);
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
		this.refresh();
	}
	
	@Override
	public String dynGetCommandStringFormatter(String data) {
		return data;
	}
}
