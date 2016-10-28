package com.szeba.wyv.widgets;

/**
 * A restrictable element on the screen, which is unable to act, if the mouse is outside
 * the restricted coordinates.
 */
public interface Restrictable {

	public void setRestricted(boolean value);
	public void setRestrictCoords(int x0, int x1, int y0, int y1);
	
}
