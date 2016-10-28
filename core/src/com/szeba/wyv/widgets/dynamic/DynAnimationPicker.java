package com.szeba.wyv.widgets.dynamic;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.Separator;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.panels.pickers.AnimationPicker;

public class DynAnimationPicker extends AnimationPicker implements Dynamic {

	private String receiver;
	
	public DynAnimationPicker(int ox, int oy, int rx, int ry, int s) {
		super(ox, oy, rx, ry, s);
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
		
		String[] splitted = StringUtilities.safeSplit(value, Separator.dynParameter);
		
		String fileDir = splitted[0];
		String fileName = splitted[1];
		
		this.setFile(fileDir, fileName);
	}

	@Override
	public String dynGetValue() {
		return this.getAnimFrame().getFileDir() + 
				Separator.dynParameter + this.getAnimFrame().getFileName();
	}

	@Override
	public void dynReset() {
		this.reset();
	}

	@Override
	public String dynGetCommandStringFormatter(String data) {
		return data;
	}

}
