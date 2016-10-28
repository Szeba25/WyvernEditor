package com.szeba.wyv.widgets;

import java.util.ArrayList;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.input.Input;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.ShapePainter;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.ext.ScrollWidget;
import com.szeba.wyv.widgets.ext.list.DropList;

/**
 * A simple scrollable list.
 * @author Szeba
 */
public class List extends ScrollWidget {
	
	private int elementSize;
	
	private ArrayList<ListElement> elements;

	private boolean multiSelection;
	private boolean mouseoverHighlight;
	private boolean printCommandString;
	private boolean enableCrop;
	private int selectedID;
	private int selectedEndID;
	
	protected DropList droplist;
	
	private double doubleClickTimer;
	
	private double arrowDelay;
	private double repeatDelay;
	
	public List(int ox, int oy, int rx, int ry, int w, int hval, 
			ArrayList<ListElement> elements, boolean drop) {
		super(ox, oy, rx, ry, w, hval*16);
		
		elementSize = 16;
		setScrollBarWidth(16);
		
		multiSelection = false;
		mouseoverHighlight = false;
		printCommandString = false;
		enableCrop = true;
		
		doubleClickTimer = 0;
		
		arrowDelay = Input.delay1;
		repeatDelay = Input.repeat2;
		
		setElements(elements);
		
		// Add a droplist.
		if (drop) {
			ArrayList<ListElement> ar = new ArrayList<ListElement>();
			droplist = new DropList(getX(), getY(), 0, 0, 0, 0, ar);
			droplist.setScrollBarWidth(0);
			addModalWidget(droplist);
		}
		
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		// Draw background
		drawBackground(batch);
		
		// Determine where to start the list drawing, and where to end it
		int startID = getPixOff() / elementSize;
		int endID = startID + (getH() / elementSize) + 1;
		int iconOffset = 0; // If the element has an icon, this is the icon offset value
		
		// Loop in the elements and draw them
		for (int i = startID; (i < getListSize() && i < endID); i++) {
			// Get the element in this index
			ListElement current = elements.get(i);
			
			if (current.isMarked()) {
				drawElementMark(batch, i);
			}
			if (mouseoverHighlight && getElementMouseover(i)) {
				drawElementHighlight(batch, i);
			}
			if (i == selectedID || insideMultiSelection(i)) {
				drawElementHighlight(batch, i);
			}
			if (drawTransparently(current)) {
				batch.setColor(1f, 1f, 1f, 0.5f);
			} else {
				batch.setColor(Palette.BATCH);
			}
			iconOffset = 0;
			// Draw icon
			if (current.getType() > -1) {
				batch.draw(Wyvern.cache.getFileIcon(current.getType()),
						getX()+1, getY()+(i*elementSize)-getPixOff());
				iconOffset = 16;
			}
			iconOffset += current.getIndentPixSize();
			// Draw text. Draw command string data instead, if its enabled (does not work with cropping!)
			if (!printCommandString) {
				Color c = Palette.COMMAND_COLOR[current.getColor()];
				FontUtilities.print(batch, c, batch.getColor().a, current.getName(), 
						getX()+1+iconOffset, getY()+1+(i*elementSize)-getPixOff());
			} else {
				Color c = Palette.COMMAND_COLOR[current.getColor()];
				FontUtilities.print(batch, c, batch.getColor().a, current.getCommandString(), 
						getX()+1+iconOffset, getY()+1+(i*elementSize)-getPixOff());
			}
			// Reset color back
			batch.setColor(Palette.BATCH);
		}
		drawBlackRects(batch);
		// Draw outlines
		drawOutline(batch);
	}

	public boolean drawTransparently(ListElement current) {
		return false;
	}

	@Override
	public void mainUpdate(int scrolled) {
		scrollList(scrolled, 2, elementSize);
		scrollBarUpdate();
		cursorUpdate();
		elementsClicked();
		if (doubleClickTimer > 0) {
			doubleClickTimer -= Wyvern.getDelta();
		}
		processSelectAll();
		processAbcSearch();
		// Droplist
		if (Wyvern.input.isButtonHold(1) && droplistShouldOpen() && mouseInside() &&
				droplist != null && !droplist.isVisible()) {
			this.doClick(false);
			this.openDroplist();
		}
	}
	
	@Override
	public void passiveUpdate(int scrolled) {
		if (droplist != null && droplist.isVisible()) {
			droplist.setVisible(false);
		}
	}
	
	@Override
	public void setH(int h) {
		super.setH(elementSize * h);
	}
	
	protected void processAbcSearch() {
		if (Wyvern.input.isKeyHold(Keys.SHIFT_LEFT) || Wyvern.input.isKeyHold(Keys.SHIFT_RIGHT)) {
			return;
		}
		for (int x = 0; x < 26; x++) {
			if (Wyvern.input.isKeyPressed(29+x)) {
				performSearch((char)(65+x), (char)(65+32+x));
			}
		}
		for (int x = 0; x < 9; x++) {
			if (Wyvern.input.isKeyPressed(7+x) || Wyvern.input.isKeyPressed(144+x)) {
				performSearch((char)(48+x), (char)(48+x));
			}
		}
	}
	
	private void performSearch(char c, char orc) {
		for (int x = 0; x < this.getElements().size(); x++) {
			if (this.getElement(x).getOriginalName().length() > 0 &&
					(this.getElement(x).getOriginalName().charAt(0) == c || 
					 this.getElement(x).getOriginalName().charAt(0) == orc) ) {
				this.scrollToThis(x);
				this.selectIndex(x);
				this.processClickedElement(x);
				return;
			}
		}
	}

	public void openDroplist() {
		capSelection();
		droplist.setVisible(true);
		droplist.setRX(Wyvern.input.getX() - getX());
		droplist.setRY(Wyvern.input.getY() - getY());
	}
	
	public boolean droplistShouldOpen() {
		return false;
	}
	
	protected void processSelectAll() {
		if (Wyvern.input.isKeyHold(Keys.CONTROL_LEFT) || Wyvern.input.isKeyHold(Keys.CONTROL_RIGHT)) {
			if (Wyvern.input.isKeyPressed(Keys.A)) {
				selectIndex(0);
				setSelectedEndID(getListSize()-1);
			}
		}
	}
	
	protected void processDoubleClickedElement(int id) {
		this.setSignal(new Signal(Signal.T_DEFAULT, this.getSelected().getData()));
	}
	
	protected void processClickedElement(int id) {
		if (multiSelection && getSelectedID() != -1 
				&& Wyvern.input.isKeyHold(Keys.SHIFT_LEFT) || Wyvern.input.isKeyHold(Keys.SHIFT_RIGHT)) {
			processMultiSelection(id);
		} else {
			// Process the clicked element in the list
			selectIndex(id);
		}
	}

	/**
	 * Process a multiselection 
	 */
	protected void processMultiSelection(int id) {
		// Select only between two points with the same indentation size
		if (this.getSelected().getIndentSize() == getElement(id).getIndentSize()) {
			// Save the original selection
			int ind = getSelected().getIndentSize();
			int sel = getSelectedID();
			int selEnd = getSelectedEndID();
			// If selection is not disallowed in this id.
			if (!disallowSelection(id)) {
				if (id < getSelectedID()) {
					setSelectedID(id);
				} else if (id > getSelectedEndID()) {
					setSelectedEndID(id);
				} else {
					setSelectedEndID(id);
				}
				
				// Now loop in the elements which are selected, to test if this selection is valid.
				for (int i = getSelectedID(); i < getSelectedEndID(); i++) {
					if (getElement(i).getIndentSize() < ind) {
						setSelectedID(sel);
						setSelectedEndID(selEnd);
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Disallow the selection.
	 */
	protected boolean disallowSelection(int newid) {
		return false;
	}

	public void setMultiSelection(boolean value) {
		multiSelection = value;
	}
	
	public void setMouseoverHighlight(boolean value) {
		mouseoverHighlight = value;
	}
	
	public void setPrintCommandString(boolean value) {
		printCommandString = value;
	}
	
	public void setCropping(boolean value) {
		enableCrop = value;
	}
	
	public void resetElements() {
		setElements(this.elements);
	}
	
	public void resetElementsKeepSelection() {
		int oldSelectedID = selectedID;
		int oldSelectedEndID = selectedEndID;
		resetElements();
		selectedID = oldSelectedID;
		selectedEndID = oldSelectedEndID;
	}
	
	public void setElements(ArrayList<ListElement> elements) {
		// Set the elements. If the elements are null, generate an empty array
		this.elements = elements;
		if (this.elements == null) {
			this.elements = new ArrayList<ListElement>();
		}
		// Crop the elements
		cropElements();
		
		selectedID = -1;
		resetMulti();
		
		fullReset(this.elements.size()*elementSize);
	}
	
	public void addElement(int id, ListElement element) {
		// First crop the element
		cropElement(element);
		// Then we add the new element
		elements.add(id, element);
		// Set the new size
		recalculate(this.elements.size()*elementSize);
	}
	
	public void addElement(ListElement element) {
		addElement(getListSize(), element);
	}
	
	public void replaceElement(int id, ListElement element) {
		// Return if invalid element
		if (id >= getListSize()) { return; }
		// Replace this element
		cropElement(element);
		this.elements.set(id, element);
	}
	
	public ListElement removeElement(int id) {
		// Return if invalid element
		if (id >= getListSize()) { return null; }
		// Get the removed element
		ListElement removedElement = elements.get(id);
		// Delete this element
		elements.remove(id);
		// Set the new size
		recalculate(this.elements.size()*elementSize);
		// Cap the selection
		capSelection();
		// Return the removed element
		return removedElement;
	}
	
	/** 
	 * Make the element marked (a small rectangle shows up after it's name) 
	 */
	public void setElementMark(int id, boolean marked) {
		getElement(id).setMarked(marked);
	}
	
	/** 
	 * Set all element marks to false 
	 */
	public void resetMarks() {
		for (ListElement e : elements) {
			e.setMarked(false);
		}
	}
	
	/** 
	 * Set the element in this ID to the top of the list 
	 */
	public void setElementToTop(int id) {
		ListElement e = elements.remove(id);
		elements.add(0, e);
	}
	
	/** 
	 * Get the selected element object 
	 */
	public ListElement getSelected() {
		return getElement(selectedID);
	}
	
	/** 
	 * Get the selected end element object
	 */
	public ListElement getSelectedEnd() {
		return getElement(selectedEndID);
	}
	
	/** 
	 * Get the selected element id 
	 */
	public int getSelectedID() {
		return selectedID;
	}
	
	/** 
	 * Get the selected end element id (when multiselecting) 
	 */
	public int getSelectedEndID() {
		return selectedEndID;
	}
	
	/**
	 * Cap selection inside the list.
	 */
	public void capSelection() {
		if (selectedID < -1) {
			selectedID = -1;
		}
		if (selectedID > this.elements.size()-1) {
			selectedID = this.elements.size()-1;
		}
		if (multiSelection) {
			if (selectedEndID < -1) {
				selectedEndID = -1;
			}
			if (selectedEndID > this.elements.size()-1) {
				selectedEndID = this.elements.size()-1;
			}
		}
	}
	
	/**
	 * Is the selected ID valid? (inside the list)
	 */
	public boolean isValidSelectedID() {
		if (selectedID >= 0 && selectedID < getListSize()) {
			return true;
		}
		return false;
	}
	
	public int getIDbyName(String name) {
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).getOriginalName().equals(name)) {
				return i;
			}
		}
		return -1;
	}
	
	public int getIDbyData(String data) {
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).getData().equals(data)) {
				return i;
			}
		}
		return -1;
	}
	
	public void selectElementByData(String data) {
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).getData().equals(data)) {
				selectIndex(i);
			}
		}
	}
	
	public void selectElementByName(String name) {
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).getOriginalName().equals(name)) {
				selectIndex(i);
			}
		}
	}
	
	public void selectIndex(int id) {
		selectedID = id;
		resetMulti();
	}
	
	public void setSelectedID(int id) {
		selectedID = id;
	}
	
	public void setSelectedEndID(int id) {
		if (multiSelection) {
			selectedEndID = id;
		}
	}
	
	public int getListSize() {
		return elements.size();
	}
	
	public ListElement getElement(int id) {
		if (id == -1) {
			return null;
		}
		return elements.get(id);
	}
	
	public ArrayList<ListElement> getElements() {
		return elements;
	}
	
	public int getElementSize() {
		return elementSize;
	}
	
	/** 
	 * Scroll the list to the given ID 
	 */
	public void scrollToThis(int id) {
		if (id < 0 || id >= elements.size()) {
			return;
		}
		int start = getPixOff() / elementSize;
		int end = start + (getH() / elementSize);
		while (id < start) {
			this.scrollList(-1, 1, elementSize);
			start = getPixOff() / elementSize;
			end = start + (getH() / elementSize);
		}
		while (id >= end) {
			this.scrollList(1, 1, elementSize);
			start = getPixOff() / elementSize;
			end = start + (getH() / elementSize);
		}
	}
	
	public boolean insideMultiSelection(int id) {
		if (id <= selectedEndID && id >= selectedID) {
			return true;
		} else {
			return false;
		}
	}
	
	public void resetMulti() {
		selectedEndID = selectedID;
	}
	
	protected void cropElements() {
		if (enableCrop) {
			for (ListElement e : elements) {
				cropElement(e);
			}
		}
	}
	
	public void cropElement(ListElement element) {
		if (enableCrop) {
			int iconOffset = 0;
			if (element.getType() > -1) {
				iconOffset = 16;
			}
			// Calculate indentation size to crop
			iconOffset += element.getIndentPixSize();
			element.setName(StringUtilities.cropString(element.getOriginalName(), getW()-iconOffset-24));
		}
	}
	
	private boolean getElementMouseover(int id) {
		int yStart = getY()+(id*elementSize)-getPixOff();
		if (Wyvern.input.getX() > getX() && Wyvern.input.getX() < getX()+getW() - getScrollBarWidth() &&
				Wyvern.input.getY() >= yStart && Wyvern.input.getY() < yStart+elementSize) {
			return true;
		}
		return false;
	}
	
	private void drawElementHighlight(SpriteBatch batch, int id) {
		int yStart = getY()+(id*elementSize)-getPixOff();
		batch.setColor(getHighColor());
		batch.draw(Wyvern.cache.getFiller(), getX(), yStart, getW() - getScrollBarWidth(), elementSize);
	}
	
	private void drawElementMark(SpriteBatch batch, int id) {
		int yStart = getY()+(id*elementSize)-getPixOff();
		ShapePainter.drawRectangle(batch, Palette.LIST_MARK, 
				getX() + getW() - getScrollBarWidth() - 12, yStart + 4, 
				8, elementSize - 8);
	}
	
	protected void cursorUpdate() {
		if (Wyvern.input.isKeyHold(Keys.UP) || Wyvern.input.isKeyHold(Keys.DOWN)) {
			// We hold one of the cursors.
			if (arrowDelay == Input.delay1) {
				this.setCursorByButtons();
				arrowDelay -= Wyvern.getDelta();
			} else if (arrowDelay > 0) {
				arrowDelay -= Wyvern.getDelta();
			} else if (arrowDelay <= 0) {
				// Repeat the action
				if (repeatDelay > 0) {
					repeatDelay -= Wyvern.getDelta();
				} else {
					repeatDelay = Input.repeat2;
					this.setCursorByButtons();
				}
			}
		} else {
			// Reset repeating
			arrowDelay = Input.delay1;
			repeatDelay = Input.repeat2;
		}
		// Enter action
		if (Wyvern.input.isKeyPressed(Keys.ENTER)) {
			if (this.isValidSelectedID()) {
				processDoubleClickedElement(getSelectedID());
			}
		}
	}
	
	protected void setCursorByButtons() {
		if (Wyvern.input.isKeyHold(Keys.UP)) {
			cursorUp();
		} else if (Wyvern.input.isKeyHold(Keys.DOWN)) {
			cursorDown();
		}
	}
	
	protected void cursorUp() {
		capSelection();
		if (selectedID == -1 && getListSize() > 0) {
			selectedID = 0;
			// We need to process this immediately!
			processClickedElement(selectedID);
			
		} else if (selectedID > 0) {
			processClickedElement(selectedID - 1);
			scrollToThis(getSelectedID());
		}
	}
	
	protected void cursorDown() {
		capSelection();
		if (selectedID == -1 && getListSize() > 0) {
			selectedID = 0;
			// We need to process this immediately!
			processClickedElement(selectedID);
			
		} else if (selectedID < this.elements.size()-1) {
			processClickedElement(selectedID + 1);
			scrollToThis(getSelectedID());
		}
	}
	
	protected void doClick(boolean doubleEnabled) {
		// Check list elements for clicking... First save the mouseInside check for performance gain
		boolean savedMouseInside = mouseInside();
		// Determine where to start the list checking, and where to end it
		int startID = getPixOff() / elementSize;
		int endID = startID + (getH() / elementSize) + 1;
		// Loop in the elements and check them
		for (int i = startID; (i < elements.size() && i < endID); i++) {
			if (savedMouseInside && getElementMouseover(i)) {
				if (i == getSelectedID() && doubleClickTimer > 0 && doubleEnabled) {
					processDoubleClickedElement(i);
					doubleClickTimer = 0;
				} else {
					processClickedElement(i);
					if (doubleEnabled) {
						doubleClickTimer = Input.doubleClick;
					}
				}
				
				return;
			}
		}
	}
	
	private void elementsClicked() {
		if (Wyvern.input.isButtonPressed(0)) {
			doClick(true);
		}
	}

}
