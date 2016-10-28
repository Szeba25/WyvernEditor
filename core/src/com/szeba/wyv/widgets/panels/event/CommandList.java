package com.szeba.wyv.widgets.panels.event;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Input.Keys;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.data.event.CommandData;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.widgets.List;

/**
 * An extension of the basic list, designed for eventing
 * @author Szeba
 */
public class CommandList extends List {

	public static HashMap<String, String> keyWords = loadKeywords();
	public static ArrayList<ListElement> copyBuffer = new ArrayList<ListElement>();
	
	public CommandList(int ox, int oy, int rx, int ry, int w, int hval, ArrayList<ListElement> elements) {
		super(ox, oy, rx, ry, w, hval, elements, false);
	}
	
	/**
	 * Loads the keywords file, which contains the reserved unclickable commands.
	 */
	private static HashMap<String, String> loadKeywords() {
		HashMap<String, String> words = new HashMap<String, String>();
		TextFile file = new TextFile(Wyvern.INTERPRETER_DIR + "/preferences/events/keywords.ikd");
		for (int i = 0; i < file.getLength(); i++) {
			words.put(file.getValue(i, 0), file.getValue(i, 1));
		}
		return words;
	}

	private int getKeywordColor(String name) {
		String colorString = keyWords.get(name);
		int colorID = 0;
		if (colorString != null) {
			colorID = Integer.parseInt(colorString);
		}
		return colorID;
	}
	
	/**
	 * Add a new event command based on the currently selected command ID.
	 */
	public ListElement addCommand(int ind, String name, CommandData data, int id) {
		
		ListElement addedMainElement = new ListElement(ind, name, 
				data.getParamString(), Integer.parseInt(data.getColor()));
		
		addElementByID(addedMainElement, id);
		// Add additional lines if any.
		int i = 0;
		for (i = 0; i < data.getAdditionalLines().size(); i++) {
			// Add this line
			ArrayList<String> currentLine = data.getAdditionalLines().get(i);
			// Determine color
			int colorID = getKeywordColor(currentLine.get(3));
			// Add the new element
			addElementByID(new ListElement(ind + Integer.parseInt(currentLine.get(1)),
					currentLine.get(3), data.getAdditionalLineString(i), colorID), id+1+i);
		}
		// Add the end line
		if (data.getEndParam() != null) {
			int colorID = getKeywordColor(data.getEndParam());
			addElementByID(new ListElement(ind, data.getEndParam(), data.getEndParam(), 
					colorID), id+1+i);
		}
		// Select this command
		processClickedElement(id);
		
		return addedMainElement;
	}
	
	/**
	 * Adds a new element to the list, specifying the target id.
	 */
	public void addElementByID(ListElement element, int id) {
		cropElement(element);
		getElements().add(id, element);
		recalculate(getListSize()*getElementSize());
	}
	
	/**
	 * Extends the selection point, to cover the whole command
	 */
	private void extendSelection(int id) {
		CommandData cd = CommandChooser.cDatabase.get(this.getElement(id).getOriginalName());
		// If this command data has an endParam, loop until finding that
		if (cd != null && cd.getEndParam() != null) {
			for (int i = id; i < this.getListSize(); i++) {
				// If we find the endParam of this command, and indentation is the same
				if (getElement(i).getOriginalName().equals(cd.getEndParam())) {
					if (getElement(i).getIndentSize() == getElement(id).getIndentSize()) {
						if (i > this.getSelectedEndID()) {
							this.setSelectedEndID(i);
						}
						return;
					}
				}
			}
		}
	}
	
	private void processPaste() {
		if (Wyvern.input.isKeyHold(Keys.CONTROL_LEFT) || Wyvern.input.isKeyHold(Keys.CONTROL_RIGHT)) {
			if (Wyvern.input.isKeyPressed(Keys.V) && copyBuffer.size() > 0) {
				// Paste the elements inside the list, and refactor the indentations.
				int newIndent = getSelected().getIndentSize();
				int minIndent = copyBuffer.get(0).getIndentSize();
				int ind;
				for (ind = 0; ind < copyBuffer.size(); ind++) {
					ListElement added = new ListElement("").setEqualTo(copyBuffer.get(ind));
					this.addElement(getSelectedID()+ind, added);
					added.setIndentSize(added.getIndentSize()-minIndent+newIndent);
					
				}
				setSelectedEndID(getSelectedID()+ind-1);
			}
		}
	}
	
	private void processCopy() {
		if (Wyvern.input.isKeyHold(Keys.CONTROL_LEFT) || Wyvern.input.isKeyHold(Keys.CONTROL_RIGHT)) {
			if (Wyvern.input.isKeyPressed(Keys.C)) {
				copyElements();
			} else if (Wyvern.input.isKeyPressed(Keys.X)) {
				copyElements();
				deleteElements();
			}
		}
	}
	
	private void copyElements() {
		// Copy the selection!
		// First, get the selection's size.
		int selectionSize = (getSelectedEndID() - getSelectedID())+1;
		// If the selection's size is 1, and the command selected is add command, do nothing.
		if (selectionSize == 1 && getSelected().getOriginalName().equals("add command")) {
			return;
		}
		// Copy elements
		copyBuffer.clear();
		for (int i = getSelectedID(); i <= getSelectedEndID(); i++) {
			copyBuffer.add(new ListElement("").setEqualTo(this.getElement(i)));
		}
	}

	private void processDelete() {
		if (Wyvern.input.isKeyPressed(Keys.FORWARD_DEL)) {
			deleteElements();
		}
	}
	
	private void processSpace() {
		if (Wyvern.input.isKeyPressed(Keys.SPACE)) {
			setSignal(new Signal(Signal.T_COMMAND_CHOOSER));
		}
	}
	
	private void deleteElements() {
		// Delete the selection!
		// First, get the selection's size.
		int selectionSize = (getSelectedEndID() - getSelectedID())+1;
		// If the selection's size is 1, and the command selected is add command, do nothing.
		if (selectionSize == 1 && getSelected().getOriginalName().equals("add command")) {
			return;
		}
		// Delete these elements
		for (int i = 0; i < selectionSize; i++) {
			this.removeElement(getSelectedID());
		}
		// Reset multi selection
		this.resetMulti();
	}
	
	@Override
	protected void processAbcSearch() {
		// We disable abc search, because it bugs with command lists..... ... ...
	}
	
	@Override
	protected void processSelectAll() {
		// Select all rewritten, to ignore the last add command.
		if (Wyvern.input.isKeyHold(Keys.CONTROL_LEFT) || Wyvern.input.isKeyHold(Keys.CONTROL_RIGHT)) {
			if (Wyvern.input.isKeyPressed(Keys.A)) {
				selectIndex(0);
				setSelectedEndID(getListSize()-2);
			}
		}
	}

	@Override
	public void mainUpdate(int scrolled) {
		super.mainUpdate(scrolled);
		processDelete();
		processCopy();
		processPaste();
		processSpace();
	}
	
	@Override
	public void processClickedElement(int id) {
		// Can't click keywords.
		if (keyWords.containsKey(getElement(id).getOriginalName())) {
			return;
		}
		super.processClickedElement(id);
		// Set the end selection to the clicked elements endParam.
		extendSelection(getSelectedID());
		extendSelection(getSelectedEndID());
	}
	
	@Override
	public void processDoubleClickedElement(int id) {
		if (getSelected().getOriginalName().equals("add command")) {
			setSignal(new Signal(Signal.T_COMMAND_CHOOSER));
		} else {
			setSignal(new Signal(Signal.T_COMMAND_EDIT));
		}
	}
	
	@Override
	protected void cursorUp() {
		capSelection();
		if (getSelectedID() == -1 && getListSize() > 0) {
			setSelectedID(0);
		} else if (getSelectedID() > 0) {
			selectIndex(getSelectedID() - 1);
			scrollToThis(getSelectedID());
		}
	}
	
	@Override
	protected void cursorDown() {
		capSelection();
		if (getSelectedID() == -1 && getListSize() > 0) {
			setSelectedID(0);
		} else if (getSelectedID() < getListSize() - 1) {
			selectIndex(getSelectedID() + 1);
			scrollToThis(getSelectedID());
		}
	}
	
	@Override
	protected void cursorUpdate() {
		super.cursorUpdate();
		if (getSelected() != null) {
			while (keyWords.containsKey(this.getSelected().getOriginalName())) {
				super.setCursorByButtons();
			}
			extendSelection(getSelectedID());
		}
	}
	
	@Override
	protected boolean disallowSelection(int newid) {
		return (getSelected().getOriginalName().equals("add command") ||
				getElement(newid).getOriginalName().equals("add command"));
	}
	
}
