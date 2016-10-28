package com.szeba.wyv.widgets.dynamic;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.Separator;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.panels.pickers.SpritePicker;

public class DynSpritePicker extends SpritePicker implements Dynamic {

	private String receiver;
	
	public DynSpritePicker(int ox, int oy, int rx, int ry, int s) {
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
		// The spriteframe holds multiple data. The file directory, name, and the coordinate.
		// This information is stored in this format:
		// D:/PathToFile[CAN]file.png[CAN]3x3 (can is ascii 24)
		
		String[] splitted = StringUtilities.safeSplit(value, Separator.dynParameter);
		
		String fileDir = splitted[0];
		String fileName = splitted[1];
		
		// We use a different setfile for the coordinates.
		this.setFile(fileDir, fileName, splitted[2]);
	}

	@Override
	public String dynGetValue() {
		return this.getSpriteFrame().getFileDir() + 
				Separator.dynParameter + this.getSpriteFrame().getFileName() + 
				Separator.dynParameter + this.getSpriteFrame().getSpriteCoordStr();
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
