package com.szeba.wyv.widgets.ext;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.utilities.MathUtilities;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.ShapePainter;
import com.szeba.wyv.widgets.Widget;

/**
 * Add a scrollable interface to a widget.
 * @author Szebaszti�n
 */
public class ScrollWidget extends Widget {

	private int trueSize;
	
	private double maxPixOff;
	private double pixOff;
	
	private boolean scrollBarGrabbed;
	private int scrollBarGrabPos;
	private int scrollBarWidth;
	private int scrollBarSize;
	private double maxScrollPos;
	private double scrollBarPos;
	private double scrollBarValue;
	
	public ScrollWidget(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
	}
	
	@Override
	public Color getBkgColor() {
		return Palette.WIDGET_BKG3;
	}
	
	public void fullReset(int trueSize) {
		fullReset(trueSize, getScrollBarWidth());
	}
	
	public void fullReset(int trueSize, int scrollBarWidth) {
		recalculate(trueSize, scrollBarWidth);
		setScrollBarGrabbed(false);
		setScrollBarGrabPos(0);
		setScrollBarPos(0.0);
		setPixOff(0.0);
	}
	
	public void recalculate(int trueSize) {
		recalculate(trueSize, getScrollBarWidth());
	}
	
	public void recalculate(int trueSize, int scrollBarWidth) {
		setTrueSize(trueSize);
		setScrollBarWidth(scrollBarWidth);
		calculateMaxPixOff();
		calculateScrollBarSize();
		calculateScrollBarValue();
		recapScrollBar();
		// Reset the scroll bar position
		scrollBarPos = pixOff / scrollBarValue;
	}
	
	private void recapScrollBar() {
		setScrollBarPos(MathUtilities.boundedVariable(scrollBarPos, 0.0, 0.0, maxScrollPos));
		setPixOff(MathUtilities.boundedVariable(getPixOff(), 0, 0, (int)maxPixOff));
	}
	
	public void setScrollBarWidth(int newWidth) {
		scrollBarWidth = newWidth;
	}
	
	public int getScrollBarWidth() {
		return this.scrollBarWidth;
	}
	
	public boolean getScrollBarGrabbed() {
		return scrollBarGrabbed;
	}
	
	public int getPixOff() {
		return (int)pixOff;
	}
	
	private void setTrueSize(int size) {
		trueSize = size;
		// True size can't be smaller than the widgets height
		if (trueSize < getH()) {
			trueSize = getH();
		}
	}
	
	private void setScrollBarGrabbed(boolean newValue) {
		scrollBarGrabbed = newValue;
	}
	
	private void setScrollBarGrabPos(int newValue) {
		scrollBarGrabPos = newValue;
	}
	
	private void setScrollBarPos(double newValue) {
		scrollBarPos = newValue;
	}

	private int getScrollBarPos() {
		// Scrollbar position
		return (int)scrollBarPos;
	}
	
	private void calculateScrollBarSize() {
		if (getH() < trueSize) {
			double percentage = (double) getH() / (double) trueSize;
			// Return the size
			scrollBarSize = (int) (percentage * getH());
			if (scrollBarSize < 1) { scrollBarSize = 1; }
		} else {
			scrollBarSize = 0;
		}
		
	}
	
	private void calculateScrollBarValue() {
		// Scrollbar value means how many listoff value one pixel is worth.
		if (scrollBarSize != 0) {
			// Divide the maximum listoff value by the maximum scrollbarposition.
			maxScrollPos = (getH() - scrollBarSize);
			scrollBarValue = maxPixOff / maxScrollPos;
		} else {
			// No scrolling will take place, set maxScrollPos to 0
			maxScrollPos = 0.0;
			scrollBarValue = 0.0;
		}
	}
	
	private boolean mouseInsideScrollBarRect() {
		if (Wyvern.input.getX() > (getX()+getW())-scrollBarWidth && Wyvern.input.getX() < getX()+getW() &&
				Wyvern.input.getY() > getY()+scrollBarPos &&
				Wyvern.input.getY() < getY()+scrollBarPos+scrollBarSize) {
			return true;
		}
		return false;
	}
	
	private boolean mouseInsideScrollBar() {
		if (Wyvern.input.getX() > (getX()+getW())-scrollBarWidth && Wyvern.input.getX() < getX()+getW() &&
				Wyvern.input.getY() > getY() && Wyvern.input.getY() < getY()+getH()) {
			return true;
		}
		return false;
	}
	
	private void setPixOff(double value) {
		pixOff = value;
	}
	
	private void calculateMaxPixOff() {
		maxPixOff = trueSize - getH();
	}
	
	@Override
	public void passiveUpdate(int scrolled) {
		setScrollBarGrabbed(false);
	}
	
	@Override
	public void drawOutline(SpriteBatch batch) {
		drawScrollBar(batch);
		if (isFocused()) {
			ShapePainter.drawRectangle(batch, getActiveBrdColor(), getX(), getY(), getW(), getH());
			ShapePainter.drawRectangle(batch, getActiveBrdColor(), getX()+getW()-getScrollBarWidth(), getY(), 
					getScrollBarWidth(), getH());
		} else {
			ShapePainter.drawRectangle(batch, getPassiveBrdColor(), getX(), getY(), getW(), getH());
			ShapePainter.drawRectangle(batch, getPassiveBrdColor(), getX()+getW()-getScrollBarWidth(), getY(), 
					getScrollBarWidth(), getH());
		}
	}
	
	public void drawBlackRects(SpriteBatch batch) {
		// Draw black rectangle above, and below, to hide list elements
		ShapePainter.drawFilledRectangle(batch, Palette.WIDGET_BKG, getX(), getY()-16, 
				getW(), 16);
		ShapePainter.drawFilledRectangle(batch, Palette.WIDGET_BKG, getX(), getY()+getH(), 
				getW(), 16+1);
	}
	
	private void drawScrollBar(SpriteBatch batch) {
		ShapePainter.drawFilledRectangle(batch, getBkgColor(), getX()+getW()-scrollBarWidth+1, 
				getY(), 
				scrollBarWidth-1, getH()-1);
		ShapePainter.drawFilledRectangle(batch, Palette.LIST_SCROLLBAR, getX()+getW()-scrollBarWidth+1, 
				getY()+getScrollBarPos(),
				scrollBarWidth-1, scrollBarSize);
	}
	
	public void scrollBarUpdate() {
		// Scroll the scrollbar with mouse, if focused
		if (scrollBarSize != 0) {
			if (scrollBarGrabbed) {
				// Move the bar with the mouse
				if (Wyvern.input.isButtonHold(0)) {
					scrollBarPos = MathUtilities.boundedVariable(
							(double) (Wyvern.input.getY()+(scrollBarGrabPos)), (double) -getY(), 0.0, maxScrollPos);
					pixOff = scrollBarPos * scrollBarValue;
				} else {
					scrollBarGrabbed = false;
				}
			} else if (mouseInsideScrollBar() && Wyvern.input.isButtonPressed(0)) {
				// Try to grab the scrollbar
				if (mouseInsideScrollBarRect()) {
					// Scrollbar is grabbed at a certain position
					scrollBarGrabPos = (int) ((getY()+scrollBarPos) - Wyvern.input.getY());
					scrollBarGrabbed = true;
				} else {
					// Scrollbar teleports, and grabbed at the middle
					scrollBarGrabPos = -(scrollBarSize / 2);
					scrollBarPos = MathUtilities.boundedVariable(
							(double) (Wyvern.input.getY()+(scrollBarGrabPos)), (double) -getY(), 0.0, maxScrollPos);
					pixOff = scrollBarPos * scrollBarValue;
					scrollBarGrabbed = true;
				}
			}
		}
	}

	public void scrollList(int scrolled, int jumpSize, int elementSize) {
		if (scrolled != 0 && !scrollBarGrabbed && scrollBarSize != 0) {
			pixOff = MathUtilities.boundedVariable(pixOff, scrolled * jumpSize * elementSize, 0.0, maxPixOff);
			scrollBarPos = pixOff / scrollBarValue;
		}
	}
	
}
