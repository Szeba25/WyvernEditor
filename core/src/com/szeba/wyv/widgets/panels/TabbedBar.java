package com.szeba.wyv.widgets.panels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.utilities.MathUtilities;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.ext.button.TabButton;

/** 
 * Tabbed bar is a reorderable bar which holds multiple buttons.
 * @author Szebaszti�n
 */
public class TabbedBar extends Widget {

	private int buttonWidth;
	private boolean counted;
	private int grabbedID;
	private boolean grabbable;
	
	public TabbedBar(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
		
		buttonWidth = 100;
		counted = false;
		grabbedID = -1;
		grabbable = true;
	}

	@Override
	public Color getActiveBrdColor() {
		return Palette.WIDGET_PASSIVE_BRD;
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		drawBackground(batch);
		drawOutline(batch);
	}

	@Override
	public void mainUpdate(int scrolled) {
		// Grab and drop
		if (grabbable) {
			controlGrabbing();
		}
	}
	
	@Override
	public void setRestricted(boolean value) {
		// Redirect restrictions to buttons
		for (Widget w : this.getWidgets()) {
			w.setRestricted(value);
		}
	}
	
	@Override
	public void setRestrictCoords(int x0, int x1, int y0, int y1) {
		// Redirect restrictions to buttons
		for (Widget w : this.getWidgets()) {
			w.setRestrictCoords(x0, x1, y0, y1);
		}
	}

	public void setCounted(boolean value) {
		counted = value;
	}
	
	public void setButtonWidth(int value) {
		buttonWidth = value;
		if (buttonWidth < 40) {
			buttonWidth = 40;
		}
	}
	
	public void setGrabbable(boolean value) {
		grabbable = value;
	}
	
	public void addButton(TabButton b) {
		addWidget(b);
	}
	
	public void addButton(int index, TabButton b) {
		addWidget(index, b);
	}
	
	public TabButton removeButton(int index) {
		return (TabButton) removeWidget(index);
	}
	
	public void addTab(String text, String fullPath) {
		// Get index
		TabButton addedButton = new TabButton(getX(), getY(), 0, 0, buttonWidth+1, getH(), 
				StringUtilities.cropString(text, buttonWidth-28), fullPath);
		if (counted) { addedButton.setReferenceArray(getWidgets()); }
		addButton(addedButton);
		recalculateButtons();
	}
	
	public void addTabWithoutX(String text, String fullPath) {
		// Get index
		TabButton addedButton = new TabButton(getX(), getY(), 0, 0, buttonWidth+1, getH(), 
				StringUtilities.cropString(text, buttonWidth-8), fullPath);
		addedButton.setXButton(false);
		if (counted) { addedButton.setReferenceArray(getWidgets()); }
		addButton(addedButton);
		recalculateButtons();
	}
	
	public TabButton getButton(String signal) {
		int index = -1;
		for (int i = 0; i < getWidgets().size(); i++) {
			if (((TabButton) getWidget(i)).getFullPath().equals(signal)) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			return (TabButton) getWidget(index);
		} else {
			return null;
		}
	}
	
	public void closeButton(String signal) {
		int index = -1;
		for (int i = 0; i < getWidgets().size(); i++) {
			if (((TabButton) getWidget(i)).getFullPath().equals(signal)) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			removeButton(index);
			// Highlight a new button
			int newIndex = MathUtilities.boundedVariable(index, 0, 0, getWidgets().size()-1);
			if (getWidgets().size() > 0) {
				getWidget(newIndex).setFocused(true);
			}
			grabbedID = -1;
			recalculateButtons();
		}
	}
	
	public String getActiveButton() {
		for (Widget w : getWidgets()) {
			if (w.isFocused()) {
				return ((TabButton) w).getFullPath();
			}
		}
		return null;
	}
	
	public void setActiveButton(String name, String fullPath) {
		boolean set = false;
		for (Widget b : getWidgets()) {
			if (((TabButton) b).getFullPath().equals(fullPath)) {
				b.setFocused(true);
				set = true;
			} else {
				b.setFocused(false);
			}
		}
		// If set is still false, add a button with this name, and make it focused
		if (set == false) {
			addTab(name, fullPath);
			getWidget(getWidgets().size()-1).setFocused(true);
		}
	}

	private void recalculateButtons() {
		for (int i = 0; i < getWidgets().size(); i++) {
			getWidget(i).setRX((i*buttonWidth));
		}
	}
	
	public int getButtonWidth() {
		return buttonWidth;
	}
	
	/** 
	 * Control how grabbing and dropping elements work 
	 */
	private void controlGrabbing() {
		if (grabbedID == -1 && Wyvern.input.isButtonPressed(0)) {
			// Grab one element
			for (int i = 0; i < getWidgets().size(); i++) {
				if (getWidget(i).mouseInside()) {
					grabbedID = i;
					break;
				}
			}
		} else if (grabbedID != -1 && !Wyvern.input.isButtonHold(0)) {
			// Drop one element...
			// Remove the button from the arraylist, and drop to an approriate position based on grabX
			TabButton button = removeButton(grabbedID);
			// Calculate new index
			int newIndex = (Wyvern.input.getX() - getX()) / buttonWidth;
			if (newIndex < 0) {newIndex = 0; }
			if (newIndex > getWidgets().size()) { newIndex = getWidgets().size(); }
			addButton(newIndex, button);
			recalculateButtons();
			grabbedID = -1;
		}
	}
	
}
