package com.szeba.wyv.widgets.panels.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Widget;

public class ModMapCloser extends Widget {

	private Button yesButton;
	private Button noButton;
	private Button cancelButton;
	
	private Signal savedSignal;
	
	public ModMapCloser(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
		
		yesButton = new Button(getX(), getY(), 5, 145, 60, 18, "yes");
		noButton = new Button(getX(), getY(), 70, 145, 60, 18, "no");
		cancelButton = new Button(getX(), getY(), 135, 145, 60, 18, "cancel");
		
		savedSignal = null;
		
		addWidget(yesButton);
		addWidget(noButton);
		addWidget(cancelButton);
		
		this.setEnterFocusDefault(yesButton);
		this.setEnterFocusRestricted(noButton, cancelButton);
		this.setTabFocus(true);
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			this.yesButton.setFocused(true);
			this.noButton.setFocused(false);
			this.cancelButton.setFocused(false);
		}
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		if (yesButton.getSignal() != null) {
			setSignal(new Signal(Signal.T_DEFAULT, "save", savedSignal.getParam(0)));
		} else if (noButton.getSignal() != null) {
			setSignal(new Signal(Signal.T_DEFAULT, "dontsave", savedSignal.getParam(0)));
		} else if (cancelButton.getSignal() != null) {
			setSignal(new Signal(Signal.T_DEFAULT, "cancel"));
		}
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		drawBackground(batch);
		FontUtilities.wrappedPrint(batch, "Save before closing " + savedSignal.getParam(0) + " ?", 
				getX() + 5, getY() + 8, getW() - 10);
		drawOutline(batch);
	}
	
	public void setSavedSignal(Signal signal) {
		savedSignal = signal;
	}
	
}
