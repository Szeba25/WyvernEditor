package com.szeba.wyv.widgets.ext.button;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.Button;

public class SwitchButton extends Button {

	private String text1;
	private String text2;
	private int state;
	
	public SwitchButton(int ox, int oy, int rx, int ry, int w, int h, String text1, String text2) {
		super(ox, oy, rx, ry, w, h, text1);
		
		this.text1 = text1;
		this.text2 = text2;
		state = 0;
		
	}

	@Override
	public void mainUpdate(int scrolled) {
		super.mainUpdate(scrolled);
		Signal s = this.getSignal();
		if (s != null) {
			if (state == 0) {
				state = 1;
				this.setText(text2);
			} else {
				state = 0;
				this.setText(text1);
			}
		}
	}
	
	public void setState(int state) {
		this.state = state;
		if (state == 0) {
			this.setText(text1);
		} else {
			this.setText(text2);
		}
	}
	
	public String getStateName(int state) {
		if (state == 0) {
			return text1;
		} else {
			return text2;
		}
	}
	
	public int getState() {
		return state;
	}
}
