package com.szeba.wyv.widgets.ext.list;

import java.util.ArrayList;

import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.List;

public class ButtonList extends List {

	public ButtonList(int ox, int oy, int rx, int ry, int w, int hval,
			ArrayList<ListElement> elements, boolean drop) {
		super(ox, oy, rx, ry, w, hval, elements, drop);
	}

	@Override
	protected void processDoubleClickedElement(int id) {
		// nope.
	}
	
	@Override
	protected void processClickedElement(int id) {
		super.processClickedElement(id);
		setSignal(new Signal(Signal.T_DEFAULT, getSelected().getData()));
	}
	
}
