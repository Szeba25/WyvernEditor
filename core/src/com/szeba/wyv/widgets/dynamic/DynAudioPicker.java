package com.szeba.wyv.widgets.dynamic;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.Separator;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.panels.pickers.AudioPicker;

public class DynAudioPicker extends AudioPicker implements Dynamic {

	private String receiver;
	
	public DynAudioPicker(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
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
		
		String filePath = splitted[0];
		String fileName = splitted[1];
		int volume = Integer.parseInt(splitted[2]);
		int pan = Integer.parseInt(splitted[3]);
		int pitch = Integer.parseInt(splitted[4]);
		
		this.setFile(filePath, fileName, volume, pan, pitch);
	}

	@Override
	public String dynGetValue() {
		return this.getFileDir() + Separator.dynParameter + 
				this.getFileName() + Separator.dynParameter +
				this.getVolume() + Separator.dynParameter +
				this.getPan() + Separator.dynParameter +
				this.getPitch();
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
