package com.szeba.wyv.widgets.dynamic;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.panels.pickers.TilesetPicker;

public class DynTilesetPicker extends TilesetPicker implements Dynamic {

	private String receiver;
	
	public DynTilesetPicker(int ox, int oy, int rx, int ry) {
		super(ox, oy, rx, ry);
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
		setTilesetByName(value);
	}

	@Override
	public String dynGetValue() {
		return getTilesetName();
	}

	@Override
	public void dynReset() {
		reset();
	}
	
	@Override
	public String dynGetCommandStringFormatter(String data) {
		return data;
	}

}
