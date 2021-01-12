package com.szeba.wyv.widgets.panels.entries;

import java.util.ArrayList;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.DatabaseEntry;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.CommandStringGen;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.dynamic.DynamicPanel;
import com.szeba.wyv.widgets.ext.list.ButtonList;
import com.szeba.wyv.widgets.ext.textfield.IntField;
import com.szeba.wyv.widgets.panels.PromptPanel;

public class DatabaseEntriesPanel extends Widget {

	private ButtonList entryList;
	private ButtonList entryContentList;
	private Button setSize;
	private PromptPanel setSizePrompt;
	private IntField sizeField;
	
	private ArrayList<DynamicPanel> dynPanels;
	
	public DatabaseEntriesPanel(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
		
		setListReferences();
		
		// Create the set size elements ui
		setSize = new Button(getX(), getY(), 320, 25, 70, 17, "set size");
		setSizePrompt = new PromptPanel(0, 0, 0, 0, "");
		sizeField = Wyvern.database.ent.sizeField;
		
		addWidget(setSize);
		addWidget(sizeField);
		addModalWidget(setSizePrompt);
		addWidget(entryList);
		addWidget(entryContentList);
		
		dynPanels = new ArrayList<DynamicPanel>();
		
		for (DatabaseEntry db : Wyvern.database.ent.entryData.values()) {
			addWidget(db.getPanel());
			dynPanels.add(db.getPanel());
		}
		
	}
	
	@Override
	public void setH(int h) {
		super.setH(h);
		// Resize lists...
		entryList.setH((getH()/16)-3);
		entryList.resetElementsKeepSelection();
		entryContentList.setH((getH()/16)-5);
		entryContentList.resetElementsKeepSelection();
	}
	
	@Override
	public void passiveUpdate(int scrolled) {
		if (!this.isFocused()) {
			for (DynamicPanel dbp : this.dynPanels) {
				dbp.passiveUpdate(scrolled);
			}
		}
	}
	
	@Override
	public void setFocused(boolean value) {
		super.setFocused(value);
		if (!value) {
			for (DynamicPanel dbp : this.dynPanels) {
				dbp.setFocused(false);
				dbp.killModalUpdateDelay();
			}
		}
	}
	
	@Override
	public void overDraw(SpriteBatch batch) {
		/*
		 * There is 1 entry delay in mark changed, so this information is useless to the user.
		if (Wyvern.database.ent.currentEntry != null) {
			if (Wyvern.database.ent.entryData.get(Wyvern.database.ent.currentEntry).getChanged()) {
				FontUtilities.print(batch, "*", getX() + 5, getY() + 5);
			}
		}
		*/
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		// Refresh the lists
		Signal sg = entryList.getSignal();
		if (sg != null) {
			updateData();
			refreshContentList(sg.getParam(0));
			
			sizeField.setText(Integer.toString(entryContentList.getListSize()));
			
			Wyvern.database.ent.currentEntry = sg.getParam(0);
			Wyvern.database.ent.currentItem = null;
		}
		
		sg = entryContentList.getSignal();
		if (sg != null) {
			updateData();
			refreshPanel(sg.getParam(0));
			Wyvern.database.ent.currentItem = sg.getParam(0);
		}
		
		// Commit entry name to entry lists
		commitEntryName();
		
		sg = setSize.getSignal();
		if (sg != null && Wyvern.database.ent.currentEntry != null) {
			setSizePrompt.changeText("Set entry size to " + sizeField.getText() + "?");
			setSizePrompt.setVisible(true);
		}
		
		// Resize items
		sg = setSizePrompt.getSignal();
		if (sg != null && sg.getParam(0).equals("yes") && Wyvern.database.ent.currentEntry != null &&
				sizeField.getText().length() > 0) {
			
			// Set the current item to null
			Wyvern.database.ent.currentItem = null; // Set current element to null
			
			// The current database entry
			DatabaseEntry dent = Wyvern.database.ent.entryData.get(Wyvern.database.ent.currentEntry);
			
			// The current default entry
			String defaultItem = Wyvern.database.ent.defaultEntries.get(Wyvern.database.ent.currentEntry);
			
			// Resize the array. This method will mark as changed too.
			dent.resizeItems(Integer.parseInt(sizeField.getText()), defaultItem);
			
			// Update the data changes.
			updateData();
			
			// Refresh the list component
			refreshContentList(Wyvern.database.ent.currentEntry);
			
		}
		
		// Reset one entry with delete
		if (Wyvern.input.isKeyPressed(Keys.FORWARD_DEL) && entryContentList.isFocused()) {
			if (Wyvern.database.ent.currentEntry != null && Wyvern.database.ent.currentItem != null) {
				// Set the entry data string to default value.
				Wyvern.database.ent.entryData.get(Wyvern.database.ent.currentEntry).getItems().set(
						entryContentList.getSelectedID(),
						Wyvern.database.ent.defaultEntries.get(Wyvern.database.ent.currentEntry));
				// Refresh the panel
				refreshPanel(Wyvern.database.ent.currentItem);
			}
		}
		
	}
	
	private void commitEntryName() {
		if (Wyvern.database.ent.currentEntry != null && Wyvern.database.ent.currentItem != null) {
			String newname = Wyvern.database.ent.entryData.get(Wyvern.database.ent.currentEntry).getPanelFirstData();
			// If this is an arrayfield, we generate array text.
			if (CommandStringGen.isArrayText(newname)) {
				newname = CommandStringGen.generateArrayText(newname);
			}
			ListElement listel = entryContentList.getElement(Integer.parseInt(Wyvern.database.ent.currentItem));
			listel.setName(listel.getData() + ": " + newname);
		}
	}
	
	private void setListReferences() {
		entryList = Wyvern.database.ent.entryList;
		entryContentList = Wyvern.database.ent.entryContentList;
	}
	
	private void refreshContentList(String param) {
		if (Wyvern.database.ent.currentEntry != null) {
			Wyvern.database.ent.entryData.get(Wyvern.database.ent.currentEntry).getPanel().setVisible(false);
		}
		Wyvern.database.ent.refreshContentList(param);
	}
	
	private void refreshPanel(String param) {
		for (DatabaseEntry db : Wyvern.database.ent.entryData.values()) {
			db.getPanel().setVisible(false);
			if (db.getPanel().getName().equals(entryList.getSelected().getOriginalName())) {
				db.getPanel().setVisible(true);
				// Refresh this panels content.
				db.getPanel().setParamsWithoutName(
					Wyvern.database.ent.entryData.get(db.getPanel().getName()).getItem(Integer.parseInt(param)));
			}
		}
	}

	public void updateData() {
		if (Wyvern.database.ent.currentEntry != null && Wyvern.database.ent.currentItem != null) {
			Wyvern.database.ent.entryData.get(Wyvern.database.ent.currentEntry).getItems().set(
					Integer.parseInt(Wyvern.database.ent.currentItem),
					// Build parameters, and update the entry data.
					Wyvern.database.ent.entryData.get(
							Wyvern.database.ent.currentEntry).getPanel().buildParamsWithoutName());
			
			Wyvern.database.ent.entryData.get(Wyvern.database.ent.currentEntry).markChanged();
		}
	}
	
	public void setModalsToCenter() { 
		this.setSizePrompt.setToCenter();
	}
	
	public void closeAll() {
		
		for (DatabaseEntry db : Wyvern.database.ent.entryData.values()) {
			db.getPanel().setVisible(false);
		}
		
		// Clear the entry content list.
		entryList.selectIndex(-1);
		entryContentList.setElements(new ArrayList<ListElement>());
		entryList.setFocused(false);
		entryContentList.setFocused(false);
		
		setSizePrompt.setVisible(false);
		sizeField.setText("");
		setSize.setFocused(false);
		
		Wyvern.database.ent.currentEntry = null;
		Wyvern.database.ent.currentItem = null;
	}

}
