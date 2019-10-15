package com.szeba.wyv.widgets.dynamic;

import java.util.ArrayList;

import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.ext.list.DropList;

public class DynDropList extends DropList implements Dynamic {

	private String receiver;
	
	public DynDropList(int ox, int oy, int rx, int ry, int w, int hval,
					   ArrayList<ListElement> elements) {
		super(ox, oy, rx, ry, w, hval, elements);
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
		setVisible(true);
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
		// Nothing happens here
	}

	@Override
	public String dynGetCommandStringFormatter(String data) {
		return data;
	}
}
