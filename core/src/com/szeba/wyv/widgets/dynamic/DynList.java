package com.szeba.wyv.widgets.dynamic;

import java.util.ArrayList;

import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.List;

public class DynList extends List implements Dynamic {

	private String receiver;
	
	public DynList(int ox, int oy, int rx, int ry, int w, int hval,
			ArrayList<ListElement> elements, boolean drop) {
		super(ox, oy, rx, ry, w, hval, elements, drop);
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
		// Nothing happens here.
	}

	@Override
	public String dynGetCommandStringFormatter(String data) {
		return data;
	}
}
