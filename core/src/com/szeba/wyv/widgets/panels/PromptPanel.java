package com.szeba.wyv.widgets.panels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Widget;

public class PromptPanel extends Widget {

	private String text;
	private Button yes;
	private Button no;
	
	public PromptPanel(int ox, int oy, int rx, int ry, String text) {
		super(ox, oy, rx, ry, 275, 90);
		
		this.text = text;
		yes = new Button(getX(), getY(), 55, 65, 80, 20, "Yes");
		no = new Button(getX(), getY(), 140, 65, 80, 20, "No");
		
		addWidget(yes);
		addWidget(no);
		
		this.setEnterFocusDefault(yes);
		this.setEnterFocusRestricted(no);
		
		this.setTabFocus(true);
		this.setCursorFocus(true);
	}
	
	public void changeText(String text) {
		this.text = text;
	}
	
	public void setYesFocused() {
		yes.setFocused(true);
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		if (yes.getSignal() != null) {
			setSignal(new Signal(Signal.T_DEFAULT, "yes"));
			yes.setFocused(false);
			setVisible(false);
		}
		if (no.getSignal() != null) {
			setSignal(new Signal(Signal.T_DEFAULT, "no"));
			no.setFocused(false);
			setVisible(false);
		}
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		this.drawBackground(batch);
		this.drawOutline(batch);
		FontUtilities.wrappedPrint(batch, text, getX()+5, getY()+5, getW()-10);
	}

}
