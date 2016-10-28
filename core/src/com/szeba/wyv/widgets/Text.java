package com.szeba.wyv.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.StringUtilities;

/**
 * Basic uneditable line of text.
 * @author Szeba
 */
public class Text extends Widget {
	
	private String text;
	private String croppedText;
	
	public Text(int ox, int oy, int rx, int ry, int w, int h, String text) {
		// Initialize basic values
		super(ox, oy, rx, ry, w, h);
		
		// Texts are not subject to focus change
		setFocused(true);
		setFocusLocked(true);
		
		// Set values of this text
		setText(text);
		
	}
	
	@Override
	public Color getActiveBrdColor() {
		return getPassiveBrdColor();
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		super.mainDraw(batch);
		FontUtilities.print(batch, croppedText, getX()+2, getY()+2);
	}
	
	@Override
	public void mainUpdate(int scrolled) {
	}
	
	public void setText(String text) {
		
		this.text = text;
		
		// If the text is borderless, show all the text.
		if (this.getW() > 0) {
			croppedText = StringUtilities.cropString(text, this.getW()-5);
			if (!croppedText.equals(text)) {
				this.setTooltip(text);
			}
		} else {
			croppedText = text;
		}
	}
	
	public String getText() {
		return text;
	}
	
}
