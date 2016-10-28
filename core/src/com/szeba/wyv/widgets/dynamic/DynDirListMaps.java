package com.szeba.wyv.widgets.dynamic;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.ext.list.DirListMaps;

public class DynDirListMaps extends DirListMaps implements Dynamic {

	private String receiver;
	
	public DynDirListMaps(int ox, int oy, int rx, int ry, int w, int hval,
			String directory, String minDir) {
		super(ox, oy, rx, ry, w, hval, directory, minDir);
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
		openDirectory(getMinDir());
	}
	
	@Override
	public String dynGetCommandStringFormatter(String data) {
		return data;
	}

}
