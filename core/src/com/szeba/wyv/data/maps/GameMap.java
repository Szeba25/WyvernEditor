package com.szeba.wyv.data.maps;

import java.awt.Point;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.Undo;
import com.szeba.wyv.data.UndoEntry;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.data.geometry.Box;
import com.szeba.wyv.utilities.FileUtilities;
import com.szeba.wyv.utilities.MathUtilities;
import com.szeba.wyv.widgets.ext.Warning;

/** 
 * Game map, which consist of cells, and is editable, saveable, etc... 
 * @author Szeba
 */
public class GameMap {
	// The name of this map
	private String name;
	// The access path of this map
	private String path;
	// The relative path of this map
	private String relativePath;
	// The signature ID of this map
	private String signature_id;
	// Places
	private HashMap<String, Point> places;
	// Tile size
	private int tileSize;
	// Offsets
	private int offX;
	private int offY;
	// The map is composed of cells
	private HashMap<String, Cell> cells;
	private Cell[][] cellArray;
	// Size of one cell in tiles
	private int cellw;
	private int cellh;
	// Max cell count
	private int maxCellX;
	private int maxCellY;
	// Starting cell X and Y
	private int startingX;
	private int startingY;
	// The currently active tileset on the map
	private String activeTileset;
	// The active tile (eventing)
	private Point activeTile;
	// The undo object
	private Undo undo;
	// Store the cells which were rendered in the last frame
	private Point renderStart;
	private Point renderEnd;
	// Changed flag
	private boolean changed;
	
	/**
	 * Reference a map! 
	 */
	public GameMap(GameMap map) {
		// Pointer to this map!
		name = map.name;
		path = map.path;
		relativePath = map.relativePath;
		signature_id = map.signature_id;
		places = map.places; // Pointer
		tileSize = map.tileSize;
		offX = map.offX;
		offY = map.offY;
		cells = map.cells; // Pointer
		cellArray = map.cellArray; // Pointer
		cellw = map.cellw;
		cellh = map.cellh;
		maxCellX = map.maxCellX;
		maxCellY = map.maxCellY;
		startingX = map.startingX;
		startingY = map.startingY;
		activeTileset = map.activeTileset;
		activeTile = new Point(0, 0);
		undo = new Undo();
		renderStart = new Point(0, 0);
		renderEnd = new Point(0, 0);
		changed = false;
	}
	
	/**
	 * Load a map.
	 * The map metadata file has the following format:
	 * ------------------------------------
	 * 0. cell width
	 * 1. cell height
	 * 2. maximum cell count x
	 * 3. maximum cell count y
	 * 4. starting cell x
	 * 5. starting cell y
	 * 6. offx
	 * 7. offy
	 * 8. tilesize
	 * ------------------------------------
	 */
	public GameMap(String name, String path, String relativePath) {
		// The name and access path of this map
		this.name = name;
		this.path = path;
		this.relativePath = relativePath;
		this.signature_id = "-1";
		
		// Load the ID of the map
		TextFile tid = new TextFile(path + "/map_id.wdat");
		signature_id = tid.getValue(0, 0);
		
		// Load the places file
		TextFile pt = new TextFile(path + "/places.wdat");
		places = new HashMap<String, Point>();
		for (int i = 0; i < pt.getLength(); i++) {
			Point point = new Point(Integer.parseInt(pt.getValue(i, 1)), Integer.parseInt(pt.getValue(i, 2)));
			places.put(pt.getValue(i, 0), point);
		}
		
		// Load the metadata file
		TextFile t = new TextFile(path + "/map_metadata.wdat");
		
		// Set the cell size
		cellw = Integer.parseInt(t.getValue(0, 1));
		cellh = Integer.parseInt(t.getValue(1, 1));
		
		// Set the maximum cell count
		maxCellX = Integer.parseInt(t.getValue(2, 1));
		maxCellY = Integer.parseInt(t.getValue(3, 1));
		
		// Initialize the cell hash
		cells = new HashMap<String, Cell>();
		
		// Initialize the cell array
		cellArray = new Cell[maxCellX][maxCellY];
		
		// Set the default tileset on this map
		startingX = Integer.parseInt(t.getValue(4, 1));
		startingY = Integer.parseInt(t.getValue(5, 1));
		activeTileset = getOrLoadCell(startingX, startingY).getTileset();
		if (activeTileset == null) {
			activeTileset = Wyvern.cache.getDefaultTileset();
		}
		activeTile = new Point(0, 0);
		
		// Set the offsets and tilesize
		offX = Integer.parseInt(t.getValue(6, 1));
		offY = Integer.parseInt(t.getValue(7, 1));
		tileSize = Integer.parseInt(t.getValue(8, 1));
		
		// Initialize the undo entry object
		undo = new Undo();
		
		// Set the render coordinates to 0
		renderStart = new Point(0, 0);
		renderEnd = new Point(0, 0);
		
		// The changed flag... This is true, if any cell inside this map is changed
		changed = false;
	}
	
	/**
	 * Draw this map
	 */
	public void draw(SpriteBatch batch, int loopW, int loopH, Box renderBox, 
			boolean shadeCells, boolean completeHide, boolean gridEnabled, int layerIndex) {
		// Initialize current cell variable
		Cell current;
		// Draw the visible cells
		renderStart.x = offX/getCellPixelX();
		renderStart.y = offY/getCellPixelY();
		renderEnd.x = (offX+loopW)/getCellPixelX();
		renderEnd.y = (offY+loopH)/getCellPixelY();
		
		// Rendering is totally offscreen and negative.
		if (offX+loopW < 0 || offY+loopH < 0) {
			renderEnd.x = -1;
			renderEnd.y = -1;
			return;
		}
		
		for (int x = renderStart.x; x <= renderEnd.x; x++) {
			for (int y = renderStart.y; y <= renderEnd.y; y++) {
				// Get this cell
				current = getOrLoadCell(x, y);
				// If this cell is not null, render it
				if (current != null) {
					drawCell(current, batch, x, y, renderBox, shadeCells, completeHide, gridEnabled, layerIndex);
				}
			}
		}
	}
	
	public boolean jumpToPlace(String placeName) {
		if (places.containsKey(placeName)) {
			Point pt = places.get(placeName);
			setCameraToCell(pt.x, pt.y);
			return true;
		}
		return false;
	}
	
	public HashMap<String, Point> getPlaces() {
		return places;
	}
	
	public void setPlaces(HashMap<String, Point> newPlaces) {
		places = newPlaces;
		this.changed = true;
	}
	
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	public boolean isChanged() {
		return changed;
	}
	
	public void subFromTileSize(int value, int min, int max) {
		tileSize = MathUtilities.boundedVariable(tileSize, -value, min, max);
	}
	
	public void subFromOffX(int value) {
		offX -= value;
	}
	
	public void subFromOffY(int value) {
		offY -= value;
	}
	
	public void addUndoEntry(UndoEntry entry) {
		undo.addEntry(entry);
	}
	
	public void restoreLastState() {
		undo.restore(this);
	}
	
	public void resetUndo() {
		undo = new Undo();
	}
	
	public int getCellW() {
		return cellw;
	}
	
	public void setCellW(int w) {
		cellw = w;
	}
	
	public int getCellH() {
		return cellh;
	}
	
	public void setCellH(int h) {
		cellh = h;
	}
	
	public int getCellPixelX() {
		return cellw*tileSize;
	}
	
	public int getCellPixelY() {
		return cellh*tileSize;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getRelativePath() {
		return relativePath;
	}
	
	public String getSignatureID() {
		return signature_id;
	}
	
	public int getStartingX() {
		return startingX;
	}
	
	public int getStartingY() {
		return startingY;
	}
	
	public int getOffX() {
		return offX;
	}
	
	public int getOffY() {
		return offY;
	}
	
	public int getTileSize() {
		return tileSize;
	}
	
	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}

	public String getActiveTileset() {
		return activeTileset;
	}

	public void setActiveTileset(String activeTileset) {
		this.activeTileset = activeTileset;
	}

	public Cell getCell(int x, int y) {
		if (x < 0 || y < 0 || x >= maxCellX || y >= maxCellY) {
			return null;
		}
		return cellArray[x][y];
	}
	
	public Point getActiveTile() {
		return activeTile;
	}
	
	public HashMap<String, Cell> getCells() {
		return cells;
	}
	
	/** 
	 * Same as getCell, but will load the cell from hdd if possible
	 */
	public Cell getOrLoadCell(int x, int y) {
		// If invalid cell, return null
		if (x < 0 || y < 0 || x >= maxCellX || y >= maxCellY) {
			return null;
		}
		Cell cell = cellArray[x][y];
		if (cell == null) {
			String getKey = x+"x"+y;
			cell = new Cell(this, getKey, x, y, cellw, cellh);
			cells.put(getKey, cell);
			cellArray[x][y] = cell;
		}
		return cell;
	}
	
	public void setOffX(int x) {
		offX = x;
	}
	
	public void setOffY(int y) {
		offY = y;
	}
	
	public void setCameraToCell(int cx, int cy) {
		offX = cx*getCellPixelX();
		offY = cy*getCellPixelY();
	}
	
	/** 
	 * Save the current map 
	 */
	public void save() {
		double time = System.nanoTime();
		// Check for the map files
		
		System.out.println("Map: Saving " + name + "...");
		
		boolean saveAll = false;
		File f = new File(path);
		
		if (!f.exists()) {
			FileUtilities.createFolders(path);
			Warning.showWarning("The directory of this map (" + name + ") was missing." + 
					" (All cells currently loaded in memory are saved, but some content may be lost...)");
			saveAll = true;
		}
		
		// Save the metadata
		TextFile t = new TextFile(this.path + "/map_metadata.wdat", null);
		t = constructMetadata(t, Integer.toString(cellw), Integer.toString(cellh),
				Integer.toString(maxCellX), Integer.toString(maxCellY),
				Integer.toString(startingX), Integer.toString(startingY), 
				Integer.toString(offX), Integer.toString(offY), Integer.toString(tileSize));
		t.save();
		
		// Save the map ID
		TextFile t3 = new TextFile(this.path + "/map_id.wdat", null);
		t3.addLine();
		t3.addValue(this.signature_id);
		t3.save();
		
		// Save the places
		TextFile t2 = new TextFile(this.path + "/places.wdat", null);
		for (Map.Entry<String, Point> place : places.entrySet()) {
			t2.addLine();
			t2.addValue(place.getKey());
			t2.addValue(Integer.toString(place.getValue().x));
			t2.addValue(Integer.toString(place.getValue().y));
		}
		t2.save();
		
		// Loop in the cells
		// Use an iterator, so we can delete cell right away.
		Iterator<Entry<String, Cell>> iter = cells.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Cell> cell = iter.next();
			if (!cell.getValue().isValid()) {
				cell.getValue().deleteCell();
			} else if (cell.getValue().isValid() && (saveAll || cell.getValue().isChanged())) {
				cell.getValue().saveCell();
				System.out.println(" * " + cell.getValue().getPosX() + "x" + 
						cell.getValue().getPosY() + " saved.");
			}
			if (cell.getValue().getPosX() < renderStart.x || cell.getValue().getPosX() > renderEnd.x ||
				cell.getValue().getPosY() < renderStart.y || cell.getValue().getPosY() > renderEnd.y) {
				// The cell is out of screen, unload it.
				iter.remove();
				cellArray[cell.getValue().getPosX()][cell.getValue().getPosY()] = null;
			}
		}
		// Set map changed flag to false
		setChanged(false);
		System.out.println("Map: " + name + " succesfully saved! (" + 
				((System.nanoTime() - time) / 1000000000.0) + " seconds)");
	}
	
	/** 
	 * Draws a cell 
	 */
	private void drawCell(Cell cell, SpriteBatch batch, int x, int y, Box renderBox, 
			boolean shadeCells, boolean completeHide, boolean gridEnabled, int layerIndex) {
		cell.draw(batch, renderBox.start.x - offX + (x*getCellPixelX()), 
				renderBox.start.y - offY + (y*getCellPixelY()), renderBox, tileSize, activeTileset, 
				shadeCells, completeHide, gridEnabled, layerIndex);
	}
	
	/** 
	 * Fill a text file to contain the map metadata 
	 */
	public static TextFile constructMetadata(TextFile t,
			String cellw, String cellh, String maxcellx, String maxcelly,
			String startingcellx, String startingcelly, String offx, String offy, String tilesize) {
		// Set these values to the text file
		t.clear();
		t.addLine();
		t.addValue(0, "cellw");
		t.addValue(0, cellw);
		t.addLine();
		t.addValue(1, "cellh");
		t.addValue(1, cellh);
		t.addLine();
		t.addValue(2, "maxcellx");
		t.addValue(2, maxcellx);
		t.addLine();
		t.addValue(3, "maxcelly");
		t.addValue(3, maxcelly);
		t.addLine();
		t.addValue(4, "startingcellx");
		t.addValue(4, startingcellx);
		t.addLine();
		t.addValue(5, "startingcelly");
		t.addValue(5, startingcelly);
		t.addLine();
		t.addValue(6, "offx");
		t.addValue(6, offx);
		t.addLine();
		t.addValue(7, "offy");
		t.addValue(7, offy);
		t.addLine();
		t.addValue(8, "tilesize");
		t.addValue(8, tilesize);
		return t;
	}

	public void renameMap(String param) {
		this.path = this.path.substring(0, this.path.length()-name.length()) + param;
		this.relativePath = this.relativePath.substring(0, this.relativePath.length()-name.length()) + param;
		this.name = param;
	}

	public boolean undoIsEmpty() {
		return undo.isEmpty();
	}
	
}
