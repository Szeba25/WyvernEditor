package com.szeba.wyv.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.container.Container;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.ShapePainter;

public class Widget extends Container implements Restrictable {

	private static float tooltipTime = 0.1f;
	
	private int ox;  // Offset X
	private int oy;  // Offset Y
	private int rx;  // Real X
	private int ry;  // Real Y
	private int w;   // Width
	private int h;   // Height
	
	private Signal signal;
	
	private int tooltipX;
	private int tooltipY;
	private float tooltipAlpha;
	private float tooltipCounter;
	private String tooltip;
	
	private boolean restricted;
	private int x0_rest;
	private int x1_rest;
	private int y0_rest;
	private int y1_rest;
	
	private boolean markedAsDefault;
	
	public Widget(int ox, int oy, int rx, int ry, int w, int h) {
		// Set position and dimension
		this.ox = ox;
		this.oy = oy;
		this.rx = rx;
		this.ry = ry;
		this.w = w;
		this.h = h;
		
		// Default signal
		signal = null;
		
		tooltipX = 0;
		tooltipY = 0;
		tooltipAlpha = 0.0f;
		tooltipCounter = 0.0f;
		tooltip = "";
		
		restricted = false;
		x0_rest = 0;
		x1_rest = 0;
		y0_rest = 0;
		y1_rest = 0;
		
		markedAsDefault = false;
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		drawBackground(batch);
		drawOutline(batch);
	}

	@Override
	public void mainUpdate(int scrolled) {
	}
	
	public void setToCenter() {
		setRX((Gdx.graphics.getWidth()/2) - (this.w/2));
		setRY((Gdx.graphics.getHeight()/2) - (this.h/2));
	}
	
	public void drawBackground(SpriteBatch batch) {
		// Draw background color
		ShapePainter.drawFilledRectangle(batch, getBkgColor(), getX(), getY(), getW(), getH());
	}
	
	public void drawOutline(SpriteBatch batch) {
		// Draw outlines
		if (isFocused()) {
			ShapePainter.drawRectangle(batch, getActiveBrdColor(), getX(), getY(), getW(), getH());
		} else {
			ShapePainter.drawRectangle(batch, getPassiveBrdColor(), getX(), getY(), getW(), getH());
		}
	}
	
	public void drawHighlight(SpriteBatch batch) {
		// Highlight this widget
		if (mouseInside()) {
			ShapePainter.drawFilledRectangle(batch, getHighColor(), getX(), getY(), getW(), getH());
		}
	}
	
	public void resetTooltipVisibility() {
		tooltipAlpha = 0.0f;
		tooltipCounter = 0.0f;
	}
	
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
	
	public void drawTooltip(SpriteBatch batch) {
		if (tooltip.length() > 0) {
			if (tooltipCounter > tooltipTime && mouseInside()) {
				// Advance alpha.
				if (tooltipAlpha < 1.0f) {
					tooltipAlpha += 0.03f;
					if (tooltipAlpha > 1.0f) {
						tooltipAlpha = 1.0f;
					}
				}
			} else if (mouseInside()) {
				tooltipCounter += Wyvern.getDelta();
				if (tooltipCounter > tooltipTime) {
					this.tooltipX = Wyvern.input.getX();
					this.tooltipY = Wyvern.input.getY()+20;
					this.tooltipAlpha = 0.0f;
				}
			} else {
				this.resetTooltipVisibility();
			}
			this.drawTooltipBox(batch);
		}
	}
	
	private void drawTooltipBox(SpriteBatch batch) {
		ShapePainter.drawFilledRectangle(batch, 
				this.getBkgColor().r, 
				this.getBkgColor().g, 
				this.getBkgColor().b, 
				this.tooltipAlpha, this.tooltipX-2, this.tooltipY-2, 
				(int) FontUtilities.getBounds(tooltip).width+4, 19);
		FontUtilities.print(batch, null, tooltipAlpha, tooltip, this.tooltipX, this.tooltipY);
		ShapePainter.drawRectangle(batch, 
				this.getActiveBrdColor().r, 
				this.getActiveBrdColor().g, 
				this.getActiveBrdColor().b, 
				this.tooltipAlpha, this.tooltipX-2, this.tooltipY-2, 
				(int) FontUtilities.getBounds(tooltip).width+4, 19);
	}

	public boolean mouseInside() {
		// If restricted by screen coordinates.
		if (restricted && (Wyvern.input.getX() < this.x0_rest ||
						Wyvern.input.getX() > this.x1_rest ||
						Wyvern.input.getY() < this.y0_rest ||
						Wyvern.input.getY() > this.y1_rest)) {
			return false;
		}
		// Simple check.
		if (Wyvern.input.getX() > getX() && Wyvern.input.getX() < getX()+getW() &&
				Wyvern.input.getY() > getY() && Wyvern.input.getY() < getY()+getH()) {
			return true;
		}
		return false;
	}
	
	public int getX() {
		return ox + rx;
	}
	
	public int getOX() {
		return ox;
	}
	
	public int getRX() {
		return rx;
	}

	public void setRX(int x) {
		this.rx = x;
		this.setWOX();
	}
	
	public void setOX(int ox) {
		this.ox = ox;
		this.setWOX();
	}
	
	protected void setWOX() {
		// Also set this containers widgets offset X value!
		for (Widget e : this.getWidgets()) {
			e.setOX(getX());
		}
		for (Widget e : this.getModalWidgets()) {
			e.setOX(getX());
		}
	}

	public int getY() {
		return oy + ry;
	}
	
	public int getOY() {
		return oy;
	}
	
	public int getRY() {
		return ry;
	}

	public void setRY(int y) {
		this.ry = y;
		this.setWOY();
	}
	
	public void setOY(int oy) {
		this.oy = oy;
		this.setWOY();
	}
	
	protected void setWOY() {
		// Also set this containers widgets offset X value!
		for (Widget e : this.getWidgets()) {
			e.setOY(getY());
		}
		for (Widget e : this.getModalWidgets()) {
			e.setOY(getY());
		}
	}
	
	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public Color getHighColor() {
		return Palette.WIDGET_HIGHLIGHT;
	}

	public Color getActiveBrdColor() {
		return Palette.WIDGET_ACTIVE_BRD;
	}
	
	public Color getPassiveBrdColor() {
		return Palette.WIDGET_PASSIVE_BRD;
	}

	public Color getBkgColor() {
		return Palette.WIDGET_BKG;
	}

	public Signal getSignal() {
		if (signal != null) {
			Signal signal = this.signal;
			this.signal = null;
			return signal;
		}
		return null;
	}
	
	public void setSignal(Signal signal) {
		this.signal = signal;
	}
	
	public int getModalUpdateDelay() {
		return 3;
	}
	
	@Override
	public void setRestricted(boolean value) {
		restricted = value;
	}
	
	@Override
	public void setRestrictCoords(int x0, int x1, int y0, int y1) {
		this.x0_rest = x0;
		this.x1_rest = x1;
		this.y0_rest = y0;
		this.y1_rest = y1;
	}
	
	public void setMarkedAsDefault(boolean val) {
		this.markedAsDefault = val;
	}
	
	public boolean getMarkedAsDefault() {
		return this.markedAsDefault;
	}
	
}
