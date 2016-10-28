package com.szeba.wyv.widgets.dynamic;

import java.util.ArrayList;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.ext.BulletBoard;

public class DynBulletBoard extends BulletBoard implements Dynamic {

	private String receiver;
	
	public DynBulletBoard(int ox, int oy, int rx, int ry, int w,
			ArrayList<String> ar) {
		super(ox, oy, rx, ry, w, ar);
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
		this.setSelectedID(Integer.parseInt(value));
	}

	@Override
	public String dynGetValue() {
		return Integer.toString(this.getSelectedID());
	}

	@Override
	public void dynReset() {
		this.setSelectedID(0);
	}
	
	@Override
	public String dynGetCommandStringFormatter(String data) {
		return data;
	}

}
