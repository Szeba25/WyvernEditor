package com.szeba.wyv.widgets.dynamic;

import java.util.ArrayList;

import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.ext.list.CollectionList;

public class DynCollectionList extends CollectionList implements Dynamic {

	private String receiver;
	
	public DynCollectionList(int ox, int oy, int rx, int ry, int w, int h, ArrayList<ListElement> ar, boolean unq) {
		super(ox, oy, rx, ry, w, h, ar, unq);
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
		if (signal.getType() == Signal.T_DIRLIST) {
			addString(signal.getParam(1) + "/" + signal.getParam(0));
		} else {
			if (signal.getLength() > 0) {
				addString(signal.getParam(0));
			}
		}
	}

	@Override
	public void dynSetValue(String value) {
		this.setList(StringUtilities.buildElementList(value));
	}

	@Override
	public String dynGetValue() {
		return StringUtilities.buildListString(this.getList().getElements());
	}

	@Override
	public void dynReset() {
		this.resetList();
	}

	@Override
	public String dynGetCommandStringFormatter(String data) {
		return data;
	}
	
}
