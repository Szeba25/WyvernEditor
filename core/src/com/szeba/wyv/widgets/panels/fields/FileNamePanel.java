package com.szeba.wyv.widgets.panels.fields;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.ext.textfield.FileNameField;

public class FileNamePanel extends ObjectNamePanel {

	private int signalType = 0;
	
	public FileNamePanel(int ox, int oy, int rx, int ry, String text) {
		super(ox, oy, rx, ry, text);
		
		nameField = new FileNameField(getX(), getY(), 5, 25, 250);
		
		// The lolbug :D
		this.removeWidget(0);
		this.addWidget(0, nameField);
		
		// Set the enter focus
		this.setEnterFocusDefault(this.done);
		this.setEnterFocusRestricted(this.cancel);
	}

	@Override
	public void mainUpdate(int scrolled) {
		if (done.getSignal() != null) {
			setSignal(new Signal(signalType, ((FileNameField) nameField).getFileName()));
			if (closeOnOk) {
				setVisible(false);
			}
		}
		if (cancel.getSignal() != null) {
			setVisible(false);
		}
	}
	
	public void showWarning() {
		((FileNameField) nameField).showWarning();
		this.done.setFocused(false);
		this.nameField.setFocused(true);
	}

	public void setType(int type) {
		signalType = type;
	}
	
	public int getType() {
		return signalType;
	}
	
}
