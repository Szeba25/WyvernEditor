package com.szeba.wyv.data;

import java.util.HashMap;

import com.szeba.wyv.data.tiles.TileData;

/**
 * A collection of changed tiles, and their old values
 * @author Szeba
 */
public class UndoEntry {

	private boolean modified;
	private HashMap<String, Boolean> cellsContained;
	private HashMap<String, TileData> oldTileData;
	
	public UndoEntry() {
		modified = false;
		cellsContained = new HashMap<String, Boolean>();
		oldTileData = new HashMap<String, TileData>();
	}
	
	public void addCell(String cellKey) {
		if (!cellsContained.containsKey(cellKey)) {
			cellsContained.put(cellKey, true);
		}
	}
	
	public void addTile(int cellx, int celly, int tx, int ty, int layer, int type, 
			int index, int x, int y) {
		String finalKey = "";
		finalKey = finalKey + cellx + "/" + celly + "@" + tx + "/" + ty + "/" + layer;
		if (!oldTileData.containsKey(finalKey)) {
			modified = true;
			oldTileData.put(finalKey, new TileData(cellx, celly, tx, ty, layer, type, index, x, y));
		}
	}
	
	public boolean isModified() {
		return modified;
	}
	
	public void copyEntry(UndoEntry entry) {
		modified = entry.modified;
		cellsContained = new HashMap<String, Boolean>(entry.cellsContained);
		oldTileData = new HashMap<String, TileData>(entry.oldTileData);
	}
	
	public HashMap<String, TileData> getOldTileData() {
		return this.oldTileData;
	}

	public void clear() {
		modified = false;
		cellsContained.clear();
		oldTileData.clear();
	}

	@SuppressWarnings("unused")
	private void print() {
		System.out.println("------------------");
		System.out.println("The following cells are edited:");
		for (String s : this.cellsContained.keySet()) {
			System.out.println("    " + s);
		}
		System.out.println("The following tiles are edited:");
		for (String s : this.oldTileData.keySet()) {
			System.out.println("    " + s);
		}
	}
	
}
