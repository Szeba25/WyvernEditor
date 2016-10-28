package com.szeba.wyv.widgets.dynamic;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.ext.HolderEventName;
import com.szeba.wyv.widgets.ext.list.EventList;

public class DynEventPicker extends Widget implements Dynamic {

	private String receiver;
	
	private EventList list;
	
	private HolderEventName evname;
	private Button setEmpty;
	
	public DynEventPicker(int ox, int oy, int rx, int ry, int w, int h, boolean comev) {
		super(ox, oy, rx, ry, w, h);
		
		if (w < 200) {
			setW(200);
		}
		
		if (h < 200) {
			setH(200);
		}
		
		list = new EventList(getX(), getY(), 5, 28, getW()-10, (getH()-64)/16, comev);
		evname = new HolderEventName(getX(), getY(), 5, 5, getW()-10, 20);
		setEmpty = new Button(getX(), getY(), getW()-75, getH()-25, 70, 20, "set empty");
	
		this.addWidget(evname);
		this.addWidget(setEmpty);
		this.addWidget(list);
		
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		for (Widget w : this.getWidgets()) {
			w.setFocused(false);
		}
		if (focused) {
			this.list.setFocused(true);
		}
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		Signal s = null;
		s = list.getSignal();
		if (s != null) {
			evname.setText(s.getParam(0));
		}
		s = setEmpty.getSignal();
		if (s != null) {
			dynReset();
		}
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
		this.evname.setText(value);
		int id = list.getIDbyData(value);
		this.list.scrollToThis(id);
		this.list.selectIndex(id);
	}

	@Override
	public String dynGetValue() {
		return this.evname.getText();
	}

	@Override
	public void dynReset() {
		this.evname.setText("");
		this.list.refresh();
	}

	@Override
	public String dynGetCommandStringFormatter(String data) {
		return data;
	}
}
