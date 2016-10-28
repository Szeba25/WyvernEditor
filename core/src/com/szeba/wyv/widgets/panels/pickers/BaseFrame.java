package com.szeba.wyv.widgets.panels.pickers;

import com.szeba.wyv.widgets.Widget;

public class BaseFrame extends Widget {

	public BaseFrame(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
	}

	protected String getFileName() {
		return "";
	}
	
	protected String getFileDir() {
		return "";
	}
	
	protected void setFile(String dir, String name) {
	}
	
	protected void reset() {
	}
	
}
