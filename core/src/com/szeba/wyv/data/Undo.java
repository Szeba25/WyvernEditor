package com.szeba.wyv.data;

import java.util.ArrayList;

import com.szeba.wyv.data.maps.GameMap;
import com.szeba.wyv.data.tiles.TileData;

/**
 * A class contained by maps. These undo classes can hold up to 20 undo entries. 
 * @author Szeba
 */
public class Undo {

	private ArrayList<UndoEntry> entries;
	
	public Undo() {
		entries = new ArrayList<UndoEntry>();
	}
	
	public void addEntry(UndoEntry entry) {
		UndoEntry copied = new UndoEntry();
		copied.copyEntry(entry);
		entries.add(copied);
		if (entries.size() > 20) {
			entries.remove(0);
		}
	}

	public boolean isEmpty() {
		return entries.isEmpty();
	}
	
	public void restore(GameMap gameMap) {
		if (entries.size() > 0) {
			// Loop in the last entry, and restore the map by those values
			UndoEntry currentEntry = entries.remove(entries.size()-1);
			for (TileData dat : currentEntry.getOldTileData().values()) {
				// Load the cell from disc if it's unloaded.
				gameMap.getOrLoadCell(dat.cellx, dat.celly).setTileData(dat.tx, dat.ty, 
						dat.layer, dat.type, dat.index, dat.x, dat.y, true);
			}
		}
	}
	
}
