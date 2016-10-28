package com.szeba.wyv.widgets.dynamic;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.Slider;

public class DynSlider extends Slider implements Dynamic {

	private String receiver;
	
	public DynSlider(int ox, int oy, int rx, int ry, int w, int h, int min, int max, String format) {
		super(ox, oy, rx, ry, w, h, min, max, format);
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
		setSliderValue(Double.parseDouble(value));
	}

	@Override
	public String dynGetValue() {
		return Double.toString(this.getSliderValue());
	}

	@Override
	public void dynReset() {
		this.setSliderValue(this.getMinValue());
	}

	@Override
	public String dynGetCommandStringFormatter(String data) {
		return data;
	}
}
