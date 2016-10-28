package com.szeba.wyv.widgets.ext.button;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.ShapePainter;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Widget;

/**
 * An extension of the Button, which is used by TabbedBars.
 * @author Szeba
 */
public class TabButton extends Button {

	private boolean star;
	private String fullPath;
	private boolean hasX;
	
	private ArrayList<Widget> referenceArray;
	
	public TabButton(int ox, int oy, int rx, int ry, int w, int h, String text, String fullPath) {
		super(ox, oy, rx, ry, w, h, text);
		
		// Tab buttons are subject to focus change
		setFocusLocked(false);
		setFocused(false);
		
		this.star = false;
		this.fullPath = fullPath;
		this.hasX = true;
		
		this.referenceArray = null;
	}
	
	@Override
	public Color getActiveBrdColor() {
		return Palette.WIDGET_PASSIVE_BRD;
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		// Draw background
		drawBackground(batch);
		drawHighlight(batch);
		
		// Highlight this button if active
		if (isFocused()) {
			ShapePainter.drawFilledRectangle(batch, getHighColor(), getX()+1, getY()+1, getW()-2, getH()-2);
		}
		
		// Draw the string centered on the Y value
		int ty = (int) ((getY() + getH()/2) - getBy() / 2);
		if (isFocused()) {
			if (star) {
				FontUtilities.print(batch, getText()+getListIDmark()+"*", getX()+5, ty);
			} else {
				FontUtilities.print(batch, getText()+getListIDmark(), getX()+5, ty);
			}
		} else {
			if (star) {
				FontUtilities.print(batch, Palette.GRAY, getText()+getListIDmark()+"*", getX()+5, ty);
			} else {
				FontUtilities.print(batch, Palette.GRAY, getText()+getListIDmark(), getX()+5, ty);
			}
		}
		
		if (hasX) {
			// Draw an X at the end of the button
			ShapePainter.drawRectangle(batch, Palette.GRAY, getX()+getW()-15, getY()+4, 13, getH()-7);
			if (mouseInsideX()) { 
				FontUtilities.print(batch, "X", getX()+getW()-15+2, ty+2);
			} else {
				FontUtilities.print(batch, Palette.GRAY, "X", getX()+getW()-15+2, ty+2);
			}
		}
		
		// Outlines
		drawOutline(batch);
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		if (hasX && mouseInsideX() && Wyvern.input.isButtonPressed(0)) {
			setSignal(new Signal(Signal.T_DEFAULT, fullPath));
		}
	}
	
	public void setReferenceArray(ArrayList<Widget> arr) {
		this.referenceArray = arr;
	}
	
	public void setXButton(boolean value) {
		hasX = value;
	}
	
	public String getFullPath() {
		return fullPath;
	}
	
	public void rename(String name, String fullPath, int buttonWidth) {
		this.setText(StringUtilities.cropString(name, buttonWidth-28));
		this.fullPath = fullPath;
	}
	
	public void setStar(boolean star) {
		this.star = star;
	}
	
	public boolean isStar() {
		return star;
	}
	
	public boolean mouseInsideX() {
		if (Wyvern.input.getX() > getX()+getW()-15 && Wyvern.input.getX() < getX()+getW() &&
				Wyvern.input.getY() > getY() && Wyvern.input.getY() < getY()+getH()) {
			return true;
		}
		return false;
	}
	
	private String getListIDmark() {
		int counter = 0;
		if (referenceArray != null) {
			for (Widget e : referenceArray) {
				if (e.equals(this)) {
					return "_"+Integer.toString(counter);
				} else {
					counter++;
				}
			}
			return "";
		} else {
			return "";
		}
	}
	
}
