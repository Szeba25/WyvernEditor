package com.szeba.wyv.screens;

import com.szeba.wyv.Wyvern;
import com.szeba.wyv.widgets.Button;

/**
 * A general screen with a back button.
 * @author Szeba
 */
public class SubScreen extends GeneralScreen {

	private Button cancelButton;
	private Button doneButton;
	
	@Override
	public void init() {
		super.init();
		cancelButton = new Button(0, 0, 65, 5, 60, 30, "cancel");
		doneButton = new Button(0, 0, 0, 5, 60, 30, "done");
		addWidget(cancelButton);
		addWidget(doneButton);
	}

	@Override
	public void enter() {
		super.enter();
		cancelButton.setFocused(false);
		doneButton.setFocused(false);
	}
	
	@Override
	public void screenUpdate(int scrolled) {
		super.screenUpdate(scrolled);
		processReturnButtons(1);
	}
	
	public void cancelButtonEvent() {
		
	}
	
	public void doneButtonEvent() {
		
	}
	
	protected void processReturnButtons(int returnTo) {
		if (cancelButton.getSignal() != null) {
			cancelButtonEvent();
			Wyvern.screenChanger = returnTo;
		}
		if (doneButton.getSignal() != null) {
			doneButtonEvent();
			Wyvern.screenChanger = returnTo;
		}
	}
	
}
