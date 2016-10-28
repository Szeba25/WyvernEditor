package com.szeba.wyv.widgets.panels.tileset;

import java.util.HashMap;

import com.badlogic.gdx.Input.Keys;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.data.maps.Cell;
import com.szeba.wyv.data.maps.GameMap;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.ext.Warning;
import com.szeba.wyv.widgets.panels.PromptPanel;
import com.szeba.wyv.widgets.panels.pickers.TilesetPicker;

public class CellEditor extends TilesetPicker {

	private GameMap editedMap;
	private Button okButton;
	private Cell editedCell;
	
	private Button deleteCell;
	private PromptPanel prompt;
	
	private Button exportCell;
	private ExportDialog exportDialog;
	
	public CellEditor(int ox, int oy, int rx, int ry) {
		super(ox, oy, rx, ry);
		
		okButton = new Button(getX(), getY(), getW()-75, getH()-25, 70, 20, "select");
		deleteCell = new Button(getX(), getY(), getW()-170, getH()-25, 90, 20, "delete cell");
		exportCell = new Button(getX(), getY(), getW()-250, getH()-25, 75, 20, "export");
		exportDialog = new ExportDialog(getX(), getY(), 150, 175);
		
		prompt = new PromptPanel(getX(), getY(), 65, 230, "This will save the map (if not saved),"
				+ " delete any undo data, and"
				+ " irreversibly delete this cell from the HDD. Continue?");
		
		removeWidget(0);
		addWidget(deleteCell);
		addWidget(okButton);
		addWidget(exportCell);
		
		addModalWidget(exportDialog);
		addModalWidget(prompt);
		
		// The map where we change the tileset
		editedMap = null;
		editedCell = null;
		
		this.setTabFocus(true);
		this.setEnterFocusDefault(okButton);
		this.setEnterFocusRestricted(deleteCell, exportCell);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			for (Widget w : getWidgets()) {
				w.setFocused(false);
			}
			tilesetList.setFocused(true);
		}
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		super.mainUpdate(scrolled);
		Signal sig = okButton.getSignal();
		if (sig != null || Wyvern.input.isKeyPressed(Keys.SPACE)) {
			setVisible(false);
		}
		sig = deleteCell.getSignal();
		if (sig != null) {
			if (editedMap.getStartingX() == editedCell.getPosX() 
					&& editedMap.getStartingY() == editedCell.getPosY()) {
				Warning.showWarning("This is the starting cell of this map. You cannot delete this cell.");
			} else {
				prompt.setVisible(true);
			}
		}
		sig = prompt.getSignal();
		if (sig != null) {
			if (sig.getParam(0).equals("yes")) {
				this.deleteThisCell();
				this.setVisible(false);
			}
		}
		sig = exportCell.getSignal();
		if (sig != null) {
			this.exportDialog.setVisible(true);
		}
		sig = exportDialog.getSignal();
		if (sig != null) {
			editedCell.exportCell(sig.getParam(0), Integer.parseInt(sig.getParam(1)));
			setVisible(false);
		}
	}
	
	private void deleteThisCell() {
		// Deletes this cell from the hard disc after saving the map. Irreversible.
		editedCell.invalidateCell();
		editedMap.save();
		// Delete undo data
		editedMap.resetUndo();
		// Delete from the disc
		editedCell.deleteCell();
	}

	@Override
	protected void setTileset() {
		if (editedCell != null && tilesetList.getSelectedID() != lastSelectedID && 
				tilesetList.getSelected() != null) {
			
			String tilesetName = tilesetList.getSelected().getOriginalName();
			lastSelectedID = tilesetList.getSelectedID();
			
			Wyvern.cache.getTileset(tilesetName).check();
			
			editedCell.setTileset(tilesetName);
			editedMap.setActiveTileset(tilesetName);
		}
	}
	
	public void setEditedCell(GameMap map ,Cell cell) {
		editedMap = map;
		editedCell = cell;
		tilesetList.selectIndex(0);
		lastSelectedID = -1;
	}
	
	public void setTilesetsToTop() {
		// Reset list
		tilesetList.refreshList();
		tilesetList.selectIndex(0);
		// Count the tilesets around this cell, and place them at the top of the list
		HashMap<String, Integer> temp = new HashMap<String, Integer>();
		for (int x = -1; x < 2; x++) {
			for (int y = -1; y < 2; y++) {
				// If the coordinates are not negative
				int cellX = editedCell.getPosX() + x;
				int cellY = editedCell.getPosY() + y;
				if (cellX >= 0 && cellY >= 0) {
					Cell currentCell = editedMap.getCell(cellX, cellY);
					// If this cell is valid, and valid (loaded from disc, or assigned a tileset)
					if (currentCell != null && currentCell.isValid()) {
						// Add this cell to the hashmap, or increment its value by 1
						if (!temp.containsKey(currentCell.getTileset())) {
							temp.put(currentCell.getTileset(), 1);
						} else {
							int value = temp.get(currentCell.getTileset());
							temp.put(currentCell.getTileset(), value + 1);
						}
					}
				}
			}
		}
		
		/* The hashmap is completed. Loop in the hashmap by value, and set the tilesets to the top of the
		 * list. Also set these elements to be highlighted */
		for (int i = 1; i <= 9; i++) {
			for (String t : temp.keySet()) {
				if (temp.get(t) == i) {
					// Set this element to the top of the list
					int index = tilesetList.getIDbyName(t);
					if (index != -1) {
						tilesetList.getElement(index).setMarked(true);
						tilesetList.setElementToTop(index);
					} else if (!t.equals("default")) {
						Warning.showWarning(t + " no longer exists in the tileset/ directory...");
					} else {
						Warning.showWarning("this cell uses the \"default\" tileset.");
					}
				}
			}
		}
		
	}
	
}
