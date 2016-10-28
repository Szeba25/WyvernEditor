package com.szeba.wyv.widgets.ext.list;

import java.util.ArrayList;

import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.List;

public class CommonEventList extends List {

	public CommonEventList(int ox, int oy, int rx, int ry, int w, int hval,
			ArrayList<ListElement> elements) {
		super(ox, oy, rx, ry, w, hval, elements, true);
	}
	
	@Override
	protected void processDoubleClickedElement(int id) {
		setSignal(new Signal(Signal.T_EDIT, getElement(id).getData()));
	}
	
	@Override
	protected void processClickedElement(int id) {
		super.processClickedElement(id);
		setSignal(new Signal(Signal.T_DEFAULT, getElement(id).getData()));
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		super.mainUpdate(scrolled);
	}
	
}
