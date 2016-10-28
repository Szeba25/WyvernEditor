package com.szeba.wyv.widgets;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.ShapePainter;
import com.szeba.wyv.utilities.TextBounds;

/**
 * Basic clickable button. Buttons can hold one line of text, and return
 * their text as signal when clicked.
 * @author Szeba
 */
public class Button extends Widget {
	
	private String text;
	private TextureRegion region;
	private int bx;
	private int by;
	
	public Button(int ox, int oy, int rx, int ry, String text) {
		this(ox, oy, rx, ry, 0, 0, text);
		
		// Calculate width and height by text size
		TextBounds bounds = FontUtilities.getBounds(text);
		setW((int) (bounds.width + 14));
		setH((int) (bounds.height + 10));
	}
	
	public Button(int ox, int oy, int rx, int ry, int w, int h, String text) {
		// Initialize basic values
		super(ox, oy, rx, ry, w, h);
		
		region = null;
		
		// Set values of this button
		setText(text);
	}
	
	@Override
	public Color getBkgColor() {
		return Palette.WIDGET_BKG3;
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		int offset = 0;
		if ( ((Wyvern.input.isButtonHold(0) && mouseInside()) || Wyvern.input.isKeyHold(Keys.ENTER)
				|| Wyvern.input.isKeyHold(Keys.SPACE))
				&& isFocused() ) {
			offset = 1;
		}
		if (region == null) {
			// Main
			drawBackground(batch);
			drawHighlight(batch);
			drawOutline(batch);
			// Draw the string centered
			int tx = (int) ((getX() + getW()/2) - getBx() / 2) + offset;
			int ty = (int) ((getY() + getH()/2) - getBy() / 2) + offset;
			FontUtilities.print(batch, getText(), tx, ty);
		} else {
			batch.draw(region, getX() + offset, getY() + offset);
		}
		// Default button mark for enter
		if (this.getMarkedAsDefault()) {
			ShapePainter.drawRectangle(batch, getPassiveBrdColor(), getX()+2, getY()+2, getW()-4, getH()-4);
		}
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		// Return the buttons text as signal
		if ( ((Wyvern.input.isLeftButtonReleased() && mouseInside()) ||
				Wyvern.input.isSpaceReleased() || Wyvern.input.isEnterReleased()) && isFocused() ) {
			setSignal(new Signal(Signal.T_DEFAULT, text));
		}
	}
	
	public void setRegion(TextureRegion region) {
		this.region = region;
	}
	
	public void setText(String text) {
		this.text = text;
		TextBounds bounds = FontUtilities.getBounds(text);
		bx = (int) bounds.width;
		by = (int) bounds.height;
	}
	
	public String getText() {
		return text;
	}
	
	public int getBx() {
		return bx;
	}
	
	public int getBy() {
		return by;
	}
	
}
