package com.szeba.wyv.widgets.panels.pickers;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Text;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.ext.list.DirList;

public class BasePicker extends Widget {

	protected Text name;
	
	public BasePicker(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
	}
	
	protected void scrollListToFile(DirList list, String dir, String name) {
		
		list.returnToRoot();
		
		if (dir.length() > 0 && name.length() > 0) {
			list.setDirectory(dir);
			if (name.endsWith("@spriteset")) {
				name = name.substring(0, name.length()-10);
			}
			int id = list.getIDbyName(name);
			list.selectIndex(id);
			list.scrollToThis(id);
		}
	}
	
	protected void updateCycle(DirList list, BaseFrame frame, Button empty) {
		Signal s = null;
		s = list.getSignal();
		if (s != null) {
			if (s.getType() == Signal.T_INVALID_DIRLIST) {
				// Invalid directory list check
				processEmptyButton(s);
				list.returnToRoot();
			} else {
				frame.setFile(s.getParam(1), s.getParam(0));
				this.setName(s.getParam(0));
			}
		}
		s = empty.getSignal();
		if (s != null) {
			processEmptyButton(s);
			list.returnToRoot();
		}
	}
	
	protected void processEmptyButton(Signal s) { }
	
	protected void setFile(String fileDir, String fileName) { }
	
	protected void reset() { }

	/**
	 * No need to implement cropping here... 
	 */
	public void setName(String name) {
		this.name.setText(name);
	}
	
}
