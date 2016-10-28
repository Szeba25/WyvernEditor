package com.szeba.wyv.widgets.ext;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Widget;

public class Warning extends Widget {

	public static Warning widget;
	public static int updateDelay;
	
	private String text;
	private Button okButton;
	
	public Warning(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
		
		// The warning window is always focused
		setFocused(true);
		setFocusLocked(true);
		
		setVisible(false);
		
		text = "";
		okButton = new Button(getX(), getY(), getW()-75, getH()-25, 70, 20, "ok");
		okButton.setFocused(true);
		
		addWidget(okButton);
		
		setEnterFocusDefault(okButton);
	}
	
	@Override
	public Color getBkgColor() {
		return Palette.LIGHT_RED;
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		drawBackground(batch);
		FontUtilities.wrappedPrint(batch, text, getX()+5, getY()+5, getW()-10);
		drawOutline(batch);
	}

	@Override
	public void mainUpdate(int scrolled) {
		Signal signal = okButton.getSignal();
		if (signal != null) {
			setVisible(false);
			this.text = "";
		}
		updateDelay = 3;
	}

	public static int getUpdateDelay() {
		return updateDelay;
	}
	
	public static void setUpdateDelay(int b) {
		updateDelay = b;
	}
	
	public static void subUpdateDelay() {
		updateDelay--;
	}
	
	public static void initWarning() {
		Warning.widget = new Warning(0, 0, 0, 0, 500, 280);
		Warning.widget.setToCenter();
	}
	
	public static void showWarning(String text) {
		if (widget != null) {
			System.err.println("WARNING: " + text);
			widget.text += "- " + text + "\n";
			widget.setVisible(true);
		} else {
			System.err.println("WARNING (non-initialized): " + text);
		}
	}
	
}
