package com.szeba.wyv.widgets.panels.event;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.panels.pickers.SpritePicker;

public class EventSpriteChanger extends SpritePicker {
	
	private Button cancelButton;
	private Button doneButton;
	
	public EventSpriteChanger(int ox, int oy, int rx, int ry) {
		super(ox, oy, rx, ry, 480);
		
		doneButton = new Button(getX(), getY(), getW()-225, getH()-25, 70, 20, "done");
		cancelButton = new Button(getX(), getY(), getW()-150, getH()-25, 70, 20, "cancel");
		
		this.addWidget(doneButton);
		this.addWidget(cancelButton);
		
		this.setTabFocus(true);
		this.setEnterFocusDefault(doneButton);
		this.setEnterFocusRestricted(cancelButton, emptyButton, this.getSpriteList());
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		for (Widget w : getWidgets()) {
			w.setFocused(false);
		}
		this.spriteList.setFocused(true);
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		super.mainUpdate(scrolled);
		
		Signal s = null;
		s = doneButton.getSignal();
		if (s != null) {
			// Send a signal about the path and name of the sprite
			if (getSpriteList().isValidSelectedID()) {
				this.setSignal(new Signal(Signal.T_DEFAULT, getSpriteFrame().getFileDir(), 
						getSpriteFrame().getFileName(),
						getSpriteFrame().getSpriteCoordStr()));
				this.setVisible(false);
			}
		}
		s = cancelButton.getSignal();
		if (s != null) {
			this.setVisible(false);
		}
	}

	@Override
	protected void processEmptyButton(Signal s) {
		super.processEmptyButton(s);
		if (s.getType() != Signal.T_INVALID_DIRLIST) {
			this.setSignal(new Signal(Signal.T_DEFAULT, "", "", "0x0"));
			this.setVisible(false);
		}
	}
	
}
