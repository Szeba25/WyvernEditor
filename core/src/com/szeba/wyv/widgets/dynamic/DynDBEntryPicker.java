package com.szeba.wyv.widgets.dynamic;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.ext.HolderDBEntry;

public class DynDBEntryPicker extends Widget implements Dynamic {

	private String receiver;
	
	private DynDBList dblist;
	
	private HolderDBEntry entry;
	private Button setEmpty;
	
	public DynDBEntryPicker(int ox, int oy, int rx, int ry, int w, int h, String entryName) {
		super(ox, oy, rx, ry, w, h);
		
		if (w < 200) {
			setW(200);
		}
		
		if (h < 200) {
			setH(200);
		}
		
		dblist = new DynDBList(getX(), getY(), 5, 28, getW()-10, (getH()-48)/16, entryName);
		entry = new HolderDBEntry(getX(), getY(), 5, 5, getW()-10, 20);
		setEmpty = new Button(getX(), getY(), getW()-75, getH()-25, 70, 20, "set empty");
		
		this.addWidget(entry);
		this.addWidget(setEmpty);
		this.addWidget(dblist);
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		for (Widget w : this.getWidgets()) {
			w.setFocused(false);
		}
		if (focused) {
			this.dblist.setFocused(true);
		}
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		Signal s = null;
		s = dblist.getSignal();
		if (s != null) {
			entry.setText(s.getParam(0));
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
		this.entry.setText(value);
		int id = entry.getID();
		this.dblist.scrollToThis(id);
		this.dblist.selectIndex(id);
	}

	@Override
	public String dynGetValue() {
		return this.entry.getText();
	}

	@Override
	public void dynReset() {
		this.entry.setText("");
		this.dblist.dynReset();
	}
	
	@Override
	public String dynGetCommandStringFormatter(String data) {
		return entry.getText() + " (" + entry.getEntryName() + ")";
	}

}
