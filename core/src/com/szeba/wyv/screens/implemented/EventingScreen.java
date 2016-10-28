package com.szeba.wyv.screens.implemented;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.data.event.CommandData;
import com.szeba.wyv.data.event.Event;
import com.szeba.wyv.data.event.Page;
import com.szeba.wyv.screens.SubScreen;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.Separator;
import com.szeba.wyv.utilities.ShapePainter;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.dynamic.Dynamic;
import com.szeba.wyv.widgets.ext.Warning;
import com.szeba.wyv.widgets.panels.TabbedBar;
import com.szeba.wyv.widgets.panels.event.CommandChooser;
import com.szeba.wyv.widgets.panels.event.CommandList;
import com.szeba.wyv.widgets.panels.event.EventParamChanger;

/** 
 * Screen for editing events 
 * @author Szeba
 */
public class EventingScreen extends SubScreen {

	private final int PAGE_COUNT = 50;
	
	// Widget objects
	private TabbedBar pageTab;
	private CommandList[] commandList;
	private CommandChooser commandChooser;
	
	// Main buttons
	private Button b_addPage;
	private Button b_deletePage;
	private Button b_copyPage;
	private Button b_pastePage;
	private Button b_shiftLeft;
	private Button b_shiftRight;
	
	// The event page buffer
	private Page bufferPage;
	
	// The event options panel
	private EventParamChanger evParamChanger;
	
	// Other data
	private Event edev; // The currently edited event
	private Event edevBuffer; // The currently edited events original copy
	private int currentPage;
	private CommandData currentDynamicData;
	private int currentDynamicIndex;
	
	@Override
	public void init() {
		super.init();
		
		// Widget objects
		pageTab = new TabbedBar(0, 0, 500, 40, 0, 24);
		
		pageTab.setW(PAGE_COUNT * 65);
		pageTab.setCounted(true);
		pageTab.setButtonWidth(65);
		
		// Add page buttons
		for (int x = 0; x < PAGE_COUNT; x++) {
			pageTab.addTabWithoutX("page", "");
		}
		
		pageTab.setRestricted(true);
		
		pageTab.setGrabbable(false);
		pageTab.setTabFocus(false);
		
		commandList = new CommandList[PAGE_COUNT];
		for (int x = 0; x < PAGE_COUNT; x++) {
			commandList[x] = new CommandList(0, 0, 500, 64, 650, 38, new ArrayList<ListElement>());
			commandList[x].setMultiSelection(true);
			if (x == 0) {
				commandList[x].setVisible(true);
			} else {
				commandList[x].setVisible(false);
			}
			commandList[x].setPrintCommandString(true);
			commandList[x].setCropping(false);
		}
		
		commandChooser = new CommandChooser(0, 0, 0, 0, 790, 500);
		
		// Main buttons
		b_addPage = new Button(0, 0, 500, 5, 80, 30, "add page");
		b_deletePage = new Button(0, 0, 585, 5, 80, 30, "delete page");
		b_copyPage = new Button(0, 0, 670, 5, 80, 30, "copy page");
		b_pastePage = new Button(0, 0, 755, 5, 80, 30, "paste page");
		b_shiftLeft = new Button(0, 0, 840, 5, 40, 30, "<");
		b_shiftRight = new Button(0, 0, 885, 5, 40, 30, ">");
		
		// Buffer page
		bufferPage = new Page();
		
		// Options panel
		evParamChanger = new EventParamChanger(0, 0, 0, 40, 500, 680);
		
		// Page data
		currentPage = 0;
		currentDynamicData = null;
		currentDynamicIndex = 0;
		
		edevBuffer = new Event(0, 0, true, false);
		
		// Add widgets and buttons
		addWidget(b_addPage);
		addWidget(b_deletePage);
		addWidget(b_copyPage);
		addWidget(b_pastePage);
		addWidget(b_shiftLeft);
		addWidget(b_shiftRight);
		
		addWidget(evParamChanger);
		addWidget(pageTab);
		for (int x = 0; x < PAGE_COUNT; x++) {
			addWidget(commandList[x]);
		}
		addModalWidget(commandChooser);
		
		// Add widgets from the commandChooser
		for (CommandData cd : CommandChooser.cDatabase.values()) {
			addModalWidget(cd.getPanel());
		}
		
	}
	
	@Override
	public void doneButtonEvent() {
		// Save variables
		Wyvern.database.var.save();
		// Set all the unset data
		applyPageChanges();
		edev.setName(evParamChanger.getEventName());
		
		// Make the edited map changed, if not null.
		if (Wyvern.cache.getEditedEventCell() != null) {
			Wyvern.cache.getEditedEventCell().setChanged(true);
			Wyvern.cache.setEditedEventCell(null);
		}
		// Make common events changed, if the events name was uneditable.
		if (!edev.getEditableName()) {
			Wyvern.database.ce.markChanged();
		}
	}
	
	@Override
	public void cancelButtonEvent() {
		// Save variables
		Wyvern.database.var.save();
		// Reset all changes!
		edev.setEqualTo(edevBuffer);
	}
	
	@Override
	public void enter() {
		super.enter();
		setup();
	}

	@Override
	public void screenDraw(SpriteBatch batch) {
		ShapePainter.drawFilledRectangle(batch, Palette.WIDGET_BKG4, 
				0, 0, 
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		super.screenDraw(batch);
	}
	
	@Override
	public void screenUpdate(int scrolled) {
		update(scrolled);
		processReturnButtons(Wyvern.returnToFromEventing);
		
		Signal s = null;
		
		updatePageChange();
		
		updateMainButtons();
		
		// Get signal from the commandChooser, to open the command chooser modal widget.
		s = commandList[currentPage].getSignal();
		if (s != null) {
			if (s.getType() == Signal.T_COMMAND_CHOOSER) {
				commandChooser.setVisible(true);
			} else if (s.getType() == Signal.T_COMMAND_EDIT) {
				CommandList list = commandList[currentPage];
				String name = list.getSelected().getOriginalName();
				CommandData commandData = CommandChooser.cDatabase.get(name);
				int index = list.getSelectedID();
				ListElement element = list.getElement(index);
				prepareDynamicPanel(element, commandData, index);
			}
		}
		
		// Insert a command selected in the command chooser widget
		s = commandChooser.getSignal();
		if (s != null) {
			CommandList list = commandList[currentPage];
			String name = s.getParam(0);
			CommandData commandData = CommandChooser.cDatabase.get(name);
			addCommand(name, commandData, list);
		}
		
		// Get the signal from the dynamic panel, to set the attributes of the current index
		if (currentDynamicData != null && currentDynamicData.getPanel().getSignal() != null) {
			modifyCommand(commandList[currentPage], currentDynamicData, currentDynamicIndex);
		}
		
	}
	
	@Override
	public void resize(int width, int height) {
		
		// Set the new restrict coordinates to match the new screen size
		pageTab.setRestrictCoords(500, width, 40, 64);
		
		evParamChanger.setW(500);
		evParamChanger.setH(height-40);
		evParamChanger.setModalsToCenter();
		
		for (int x = 0; x < PAGE_COUNT; x++) {
			commandList[x].setH((height-72)/16);
			commandList[x].setW(width - 500);
			commandList[x].setRX(500);
			commandList[x].resetElementsKeepSelection();
		}
		
		for (CommandData cd : CommandChooser.cDatabase.values()) {
			if (cd.getPanel().isVisible()) {
				cd.getPanel().setToCenter();
				cd.getPanel().setModalsToCenter();
			}
		}
		commandChooser.setToCenter();
	}
	
	private void prepareDynamicPanel(ListElement element, CommandData commandData, int index) {
		if (commandData.opens) {
			
			commandData.getPanel().setVisible(true);
			commandData.getPanel().setParams(element.getData());
			
			// Set to center
			commandData.getPanel().setToCenter();
			commandData.getPanel().setModalsToCenter();
			
			// Set the dynamic panel listener, and the index.
			currentDynamicData = commandData;
			currentDynamicIndex = index;
			
		}
	}
	
	/**
	 * Adds a command based on a signal.
	 */
	private void addCommand(String name, CommandData commandData, CommandList list) {
		int index = list.getSelectedID();
		int indentation = list.getElement(index).getIndentSize();
		ListElement element = list.addCommand(indentation, name, commandData, index);
		prepareDynamicPanel(element, commandData, index);
	}

	/**
	 * Modifies the event command inside "list" at "dynIndex" position with the data from "commandData".
	 */
	private void modifyCommand(CommandList list, CommandData commandData, int dynIndex) {
		// Set the attributes of this index
		String data = commandData.getPanel().buildParams();
		
		// The dynamic widgets used to build the params
		ArrayList<Dynamic> panelTypeData = commandData.getPanel().buildPanelTypeData();
		
		list.getElement(dynIndex).setEventCommandData(data, panelTypeData);
		
		// Get the indent of the main edited line
		int minIndent = list.getElement(dynIndex).getIndentSize();
		
		// Set additional lines data!
		
		// Get how much parameters this command has
		int paramSize = commandData.getParamLines().size();
		int currentParam = 0;
		// If this command has parameters, loop
		if (paramSize > 0) {
			for (int i = dynIndex; i < list.getListSize(); i++) {
				// If this list elements name equals to the currently checked paramline's name
				if (list.getElement(i).getOriginalName().equals(
						commandData.getParamLines().get(currentParam).get(3))) {
					// Name equals! check indentation too. If matches, set the data.
					if (list.getElement(i).getIndentSize()-minIndent == 
							Integer.parseInt(commandData.getParamLines().get(currentParam).get(1))) {
						// Set..... Preserve the original name of this command.
						list.getElement(i).setEventCommandData(
								list.getElement(i).getOriginalName() + 
								Separator.dataUnit + commandData.getPanel().getParam(currentParam));
						// Move on to the next parameter, if any. If there is none, break this loop.
						currentParam++;
						if (currentParam >= paramSize) {
							break;
						}
					}
				}
			}
		}
	}

	/** 
	 * Setup the screen every time you enter with a different editable event. 
	 */
	private void setup() {
		// Set the shortcut to the edited event!
		edev = Wyvern.cache.getEditedEvent();
		edevBuffer.setEqualTo(edev);
		
		if (!edev.getEditableName()) {
			evParamChanger.disableNamePanel();
		} else {
			evParamChanger.enableNamePanel();
		}
		
		currentPage = 0;
		edev.getPage(currentPage).checkSprite();
		
		evParamChanger.reset(); // Reset the dynamic widgets before setting the params
		
		evParamChanger.updateSprites(0);
		evParamChanger.updateName();
		evParamChanger.updateID();
		evParamChanger.setParams(edev.getPage(0).getParams());
		
		// Set the number of tab widgets, and make the first one focused
		pageTab.setRX(500); // reset pageTab position
		for (int x = 0; x < PAGE_COUNT; x++) {
			if (x == 0) {
				pageTab.getWidget(0).setFocused(true);
			} else {
				pageTab.getWidget(x).setFocused(false);
				// Make existing pages visible.
				if (!edev.isPageExists(x)) {
					pageTab.getWidget(x).setVisible(false);
				} else {
					pageTab.getWidget(x).setVisible(true);
				}
			}
		}
		refreshCommandLists(0);
	}
	
	/**
	 * Reset the command lists completely
	 */
	private void refreshCommandLists(int showID) {
		for (int x = 0; x < PAGE_COUNT; x++) {
			if (x == showID) {
				commandList[x].setElements(edev.getPage(x).getCommands());
				commandList[x].setVisible(true);
			} else {
				if (edev.isPageExists(x)) {
					commandList[x].setElements(edev.getPage(x).getCommands());
				}
				commandList[x].setVisible(false);
			}
		}
	}

	/** 
	 * Run constantly to catch page changes 
	 */
	private void updatePageChange() {
		if (currentPage != getActiveTabPage()) {
			// Page number changed
			// We update the event with these values
			
			int newPage = getActiveTabPage();
			
			commandList[currentPage].setVisible(false);
			commandList[newPage].setVisible(true);
			
			applyPageChanges();
			
			evParamChanger.setParams(edev.getPage(newPage).getParams());
			evParamChanger.updateSprites(newPage);
			
			// Update the currentPage variable
			currentPage = newPage;
		}
	}
	
	private void applyPageChanges() {
		edev.getPage(currentPage).setParams(evParamChanger.getParams());
		edev.getPage(currentPage).setSprite(evParamChanger.getSpriteDir(), evParamChanger.getSpriteName());
		edev.getPage(currentPage).setSpriteCoord(evParamChanger.getSpriteCoord());
		edev.getPage(currentPage).checkSprite();
	}
	
	/** 
	 * Get the currently active tab page index 
	 */
	private int getActiveTabPage() {
		int x = 0;
		for (Widget e : pageTab.getWidgets()) {
			if (e.isFocused()) {
				return x;
			} else {
				x++;
			}
		}
		Warning.showWarning("Active tab page returned -1 ...?");
		return -1;
	}
	
	private void setActiveTabPage(int id) {
		int x = 0;
		for (Widget e : pageTab.getWidgets()) {
			if (x == id) {
				e.setFocused(true);
			} else {
				e.setFocused(false);
			}
			x++;
		}
	}
	
	private void updateMainButtons() {
		
		// The signal
		Signal bs = null;
		// Get pageTab shift left signal
		bs = b_shiftLeft.getSignal();
		if (bs != null && pageTab.getRX() < 500) {
			pageTab.setRX(pageTab.getRX()+65);
		}
		// Get pageTab shift right signal
		bs = b_shiftRight.getSignal();
		if (bs != null && (pageTab.getRX() + edev.getPageCount()*65) > Gdx.graphics.getWidth()) {
			pageTab.setRX(pageTab.getRX()-65);
		}
		
		// Add page button
		bs = b_addPage.getSignal();
		if (bs != null) {
			addEventPage(new Page());
		}
		
		// Delete page button
		bs = b_deletePage.getSignal();
		if (bs != null && edev.getPageCount() > 1) {
			
			// Remove the page from the event
			int removedPageIndex = getActiveTabPage();
			//System.out.println("Removed page index is: " + removedPageIndex);
			if (removedPageIndex == edev.getPageCount()-1) {
				setActiveTabPage(currentPage-1);
				//System.out.println("Activated new tab: " + Integer.toString(currentPage-1));
			} else {
				setActiveTabPage(currentPage+1);
				//System.out.println("Activated new tab: " + Integer.toString(currentPage+1));
			}
			updatePageChange();
			//System.out.println("Current page ID before delete: " + currentPage);
			edev.removePage(removedPageIndex);
			currentPage = removedPageIndex;
			// We deleted the last page, so the ID must not be higher than the page count!
			if (currentPage > edev.getPageCount()-1) {
				currentPage = edev.getPageCount()-1;
			}
			int newHighlightedPage = currentPage;
			//System.out.println("Current page ID after delete: " + currentPage);
			
			// Delete one page button
			Widget eLast = null;
			for (Widget e : pageTab.getWidgets()) {
				if (!e.isVisible()) {
					eLast.setVisible(false);
					break;
				} else {
					eLast = e;
				}
			}
			setActiveTabPage(newHighlightedPage);
			refreshCommandLists(newHighlightedPage);
		}
		
		// Copy page button
		bs = b_copyPage.getSignal();
		if (bs != null) {
			applyPageChanges(); // Important, as without this it will copy an older version
			bufferPage.setEqualTo(edev.getPage(currentPage));
		}
		
		// Paste page button
		bs = b_pastePage.getSignal();
		if (bs != null) {
			Page pasted = new Page();
			pasted.setEqualTo(bufferPage);
			addEventPage(pasted);
		}
		
	}
	
	private void addEventPage(Page page) {
		// Make one button visible, and add one page to the edited event
		for (Widget e : pageTab.getWidgets()) {
			if (!e.isVisible()) {
				e.setVisible(true);
				break;
			}
		}
		edev.addPage(currentPage+1, page);
		refreshCommandLists(currentPage);
	}

}
