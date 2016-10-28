package com.szeba.wyv.container;

import java.util.ArrayList;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.input.Input;
import com.szeba.wyv.widgets.Widget;

/**
 * A Container represents any space which can contain Widgets (Widgets are also containers
 * themselves). The Container is focusable, and has a flag for visibility.
 * Basically ANY element on the screen is a container. (buttons, lists, panels)
 * @author Szeba
 */
public abstract class Container {

	// The widgets contained by this container
	private ArrayList<Widget> widgets;
	
	// The modal widgets
	private ArrayList<Widget> modalWidgets;
	
	/* When the modal widget is closed, only update the regular widgets, when this flag is false.
	 * This flag, for example, will prevent drawing on the map instantly when clicking the select button
	 * on the tileset picker. */
	private int modalUpdateDelay;
	
	// The focus and visibility flag
	private boolean focused;
	private boolean focusLocked;
	private boolean visible;
	private boolean tabFocus;
	private boolean cursorFocus;
	private boolean scrollFocus;
	
	// The tab focus repeating data
	private double tabDelay;
	private double tabRepeatDelay;
	
	// The enter focus data
	private Widget enterFocusDefault;
	private ArrayList<Widget> enterFocusRestricted;
	
	public Container() {
		widgets = new ArrayList<Widget>();
		modalWidgets = new ArrayList<Widget>();
		focused = false;
		focusLocked = false;
		visible = true;
		tabFocus = false;
		cursorFocus = false;
		scrollFocus = false;
		enterFocusDefault = null;
		enterFocusRestricted = new ArrayList<Widget>();
		tabDelay = Input.delay1;
		tabRepeatDelay = Input.repeat2;
	}
	
	/**
	 * Draw the container, and its widgets.
	 */
	public final void draw(SpriteBatch batch) {
		if (isVisible()) {
			
			// Drawing debug
			if (Wyvern.input.isKeyPressed(Keys.F7)) {
				System.out.println("Draw: " + this);
			}
			
			mainDraw(batch);
			for (int i = widgets.size()-1; i >= 0; i--) {
				widgets.get(i).draw(batch);
			}
			
			overDraw(batch);
			for (int i = modalWidgets.size()-1; i >= 0; i--) {
				modalWidgets.get(i).draw(batch);
			}
		}
	}
	
	/**
	 * Draw the containers widgets tooltip recursively.
	 */
	public final void drawAllTooltip(SpriteBatch batch) {
		if (isVisible()) {
			for (int i = widgets.size()-1; i >= 0; i--) {
				widgets.get(i).drawTooltip(batch);
				widgets.get(i).drawAllTooltip(batch);
			}
			for (int i = modalWidgets.size()-1; i >= 0; i--) {
				modalWidgets.get(i).drawTooltip(batch);
				modalWidgets.get(i).drawAllTooltip(batch);
			}
		}
	}
	
	/**
	 * Drawing method which needs to be implemented. This method is called
	 * inside the final draw method.
	 */
	public abstract void mainDraw(SpriteBatch batch);
	
	/**
	 * Drawing method which is called after rendering the contained widgets.
	 */
	public void overDraw(SpriteBatch batch) {
	}
	
	/**
	 * Update the container.
	 */
	public final void update(int scrolled) {
		if (isVisible() && isFocused()) {
			// Update this container
			
			// Updating debug
			if (Wyvern.input.isKeyPressed(Keys.F7)) {
				System.out.println("Update: " + this);
			}
			
			//mainUpdate(scrolled);
			// If there is a modal widget, update it, and skip the other update
			if (!modalWidgetUpdate(scrolled)) {
				// Set update delay to false if the user releases the left mouse button
				if (modalUpdateDelay > 0) {
					if (!Wyvern.input.isButtonHold(0)) {
						modalUpdateDelay--;
					}
				} else {
					// Set delay to 0...
					modalUpdateDelay = 0;
					// Update this container
					mainUpdate(scrolled);
					// Update widget focus
					updateFocus(scrolled);
					// Update widget focus by pressing tab
					repeatedFocusByTab();
					// Update widget focus by enter
					enterFocusUpdate();
					// There are no modal widgets, update all the widgets
					for (int i = widgets.size()-1; i >= 0; i--) {
						widgets.get(i).update(scrolled);
					}
				}
			}
		} else {
			passiveUpdate(scrolled);
		}
	}

	/**
	 * Updates the enter button focus routine
	 */
	private void enterFocusUpdate() {
		if (enterFocusDefault != null) {
			if (Wyvern.input.isKeyPressed(Keys.ENTER)) {
				// If any restricted widgets are active, return
				for (Widget w : enterFocusRestricted) {
					if (w.isFocused()) {
						return;
					}
				}
				// If not, focus the default focus widget and defocus all others
				for (Widget w : widgets) {
					w.setFocused(false);
				}
				// Finally we set the default to be focused.
				this.enterFocusDefault.setFocused(true);
			}
			
			// We also set the enter focus default mark here.
			enterFocusDefault.setMarkedAsDefault(true);
			for (Widget w : enterFocusRestricted) {
				if (w.isFocused()) {
					enterFocusDefault.setMarkedAsDefault(false);
				}
			}	
		}
	}
	
	public void setEnterFocusDefault(Widget def) {
		this.enterFocusDefault = def;
	}
	
	public void setEnterFocusRestricted(Widget ... wid) {
		enterFocusRestricted.clear();
		for (Widget w1 : wid) {
			this.enterFocusRestricted.add(w1);
		}
	}
	
	public void addEnterFocusRestricted(Widget w) {
		this.enterFocusRestricted.add(w);
	}

	/**
	 * The update method which needs to be implemented. The method is called inside
	 * the final update method.
	 */
	public abstract void mainUpdate(int scrolled);
	
	/**
	 * The update method which is called if the container is not in focus.
	 */
	public void passiveUpdate(int scrolled) {
	}
	
	/**
	 * Add a new modal widget to this container
	 */
	public void addModalWidget(Widget w) {
		modalWidgets.add(w);
		// Modal widgets are always focused
		w.setFocused(true);
		w.setFocusLocked(true);
		w.setVisible(false);
	}
	
	/** 
	 * Checks for modal widget visibility. If there is one visible modal widget, update it.
	 */
	private boolean modalWidgetUpdate(int scrolled) {
		for (Widget w : modalWidgets) {
			if (w.isVisible()) {
				w.update(scrolled);
				modalUpdateDelay = w.getModalUpdateDelay();
				return true;
			}
		}
		return false;
	}
	
	public void addWidget(Widget w) {
		widgets.add(w);
	}
	
	public void addWidget(int index, Widget w) {
		widgets.add(index, w);
	}
	
	/**
	 * Remove one widget at the specified index.
	 */
	public Widget removeWidget(int index) {
		return widgets.remove(index);
	}
	
	public boolean isModalVisible() {
		for (Widget w : modalWidgets) {
			if (w.isVisible()) {
				return true;
			}
		}
		return false;
	}

	public void setFocused(boolean focused) {
		if (!isFocusLocked()) {
			this.focused = focused;
		}
	}
	
	public boolean isFocused() {
		return focused;
	}
	
	public void setFocusLocked(boolean focusLocked) {
		this.focusLocked = focusLocked;
	}
	
	public boolean isFocusLocked() {
		return focusLocked;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}
	
	public ArrayList<Widget> getWidgets() {
		return widgets;
	}
	
	public ArrayList<Widget> getModalWidgets() {
		return modalWidgets;
	}
	
	public Widget getWidget(int index) {
		return widgets.get(index);
	}
	
	/**
	 * Set the ability to use tab button to focus elements in this container.
	 */
	public void setTabFocus(boolean tabFocus) {
		this.tabFocus = tabFocus;
	}
	
	/**
	 * Set the ability to use cursor buttons to focus elements in this container.
	 */
	public void setCursorFocus(boolean cursorFocus) {
		this.cursorFocus = cursorFocus;
	}
	
	/**
	 * Set the ability to focus elements with mouse scrolling.
	 */
	public void setScrollFocus(boolean scrollFocus) {
		this.scrollFocus = scrollFocus;
	}
	
	public boolean focusOnScroll(int scrolled) {
		if (scrolled != 0 && scrollFocus) {
			return true;
		}
		return false;
	}
	
	/**
	 * Force modal update delay to be zero.
	 */
	public void killModalUpdateDelay() {
		this.modalUpdateDelay = 0;
	}
	
	/**
	 * Update widget focus inside this container.
	 */
	private void updateFocus(int scrolled) {
		
		boolean mouseInput = (Wyvern.input.isButtonPressed(0) || Wyvern.input.isButtonPressed(1));
		
		if ((mouseInput || scrolled != 0) && widgets.size() > 0) {
			// Update focus
			Widget toIgnore = null;
			for (Widget w : widgets) {
				if (w.isVisible() && !w.isFocusLocked() && w.mouseInside() &&
						(mouseInput || w.focusOnScroll(scrolled)) ) {
					w.setFocused(true);
					toIgnore = w;
					break;
				}
			}
			// Make all the other widgets non focused
			if (toIgnore != null) {
				for (Widget w : widgets) {
					if (w != toIgnore && !w.isFocusLocked()) {
						w.setFocused(false);
					}
				}
			}
		}
	}
	
	/**
	 * Repeated tab focus main method
	 */
	private void repeatedFocusByTab() {
		if (((tabFocus && Wyvern.input.isKeyHold(Keys.TAB)) ||
				(cursorFocus && (Wyvern.input.isKeyHold(Keys.RIGHT) || Wyvern.input.isKeyHold(Keys.LEFT))))
				&& widgets.size() > 1) {
			// Do the first ocassion
			if (tabDelay == Input.delay1) {
				doFocusByTab();
				tabDelay -= Wyvern.getDelta();
			} else if (tabDelay > 0) {
				tabDelay -= Wyvern.getDelta();
			} else if (tabDelay <= 0) {
				// Repeat!
				if (tabRepeatDelay > 0) {
					tabRepeatDelay -= Wyvern.getDelta();
				} else {
					tabRepeatDelay = Input.repeat2;
					doFocusByTab();
				}
			}
		} else {
			// Reset repeating
			tabDelay = Input.delay1;
			tabRepeatDelay = Input.repeat2;
		}
	}
	
	/**
	 * Make the next focusable widget focused by pressing the TAB key.
	 */
	private void doFocusByTab() {
		// Get the currently active widget index if possible
		int currentIndex = 0;
		Widget w = null;
		for (int i = 0; i < this.widgets.size(); i++) {
			w = widgets.get(i);
			if (w.isVisible() && !w.isFocusLocked() && w.isFocused()) {
				currentIndex = i;
				w.setFocused(false);
				break;
			}
		}
		/* 
		 * SHIFT is holded down! search backwards! 
		 */
		if (Wyvern.input.isKeyHold(Keys.SHIFT_LEFT) || Wyvern.input.isKeyHold(Keys.SHIFT_RIGHT)
				|| (cursorFocus && Wyvern.input.isKeyHold(Keys.LEFT))) {
			int checkIndex = currentIndex-1;
			// Search for a widget to focus on
			while(true) {
				if (checkIndex == currentIndex) {
					if (w != null) {
						w.setFocused(true);
					}
					break;
				} else if (checkIndex >= 0) {
					Widget c = widgets.get(checkIndex);
					if (c.isVisible() && !c.isFocusLocked() && !c.isFocused()) {
						c.setFocused(true);
						break;
					}
					checkIndex--;
				} else {
					checkIndex = widgets.size()-1;
				}
			}
		} else {
			int checkIndex = currentIndex+1;
			// Search for a widget to focus on
			while(true) {
				if (checkIndex == currentIndex) {
					if (w != null) {
						w.setFocused(true);
					}
					break;
				} else if (checkIndex < widgets.size()) {
					Widget c = widgets.get(checkIndex);
					if (c.isVisible() && !c.isFocusLocked() && !c.isFocused()) {
						c.setFocused(true);
						break;
					}
					checkIndex++;
				} else {
					checkIndex = 0;
				}
			}
		}
		// END of searching methods.
	}
	
}
