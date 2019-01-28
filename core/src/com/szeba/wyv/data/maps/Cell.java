package com.szeba.wyv.data.maps;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.event.Event;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.data.geometry.Box;
import com.szeba.wyv.data.tiles.Tile;
import com.szeba.wyv.data.tiles.TileData;
import com.szeba.wyv.data.tiles.Tileset;
import com.szeba.wyv.utilities.*;

/**
 * All the maps are composed of cells. Cells hold the terrain, and event
 * data for maps.
 * @author Szeba
 */
public class Cell {

	// The map path where this cell belongs
	private GameMap map;
	
	// The name of this cell
	private String name;
	// The position of this cell inside the map
	private int posX;
	private int posY;
	// Cell size in tiles
	private int w;
	private int h;
	
	// The tileset for this cell
	private String tileset;
	// Tile data holder
	private Tile[][] tiles;
	// Event data holder
	private HashMap<String, Event> events;
	// A cell becomes valid if a tileset is assigned to it (loading from hdd, painting in the cell)
	private boolean valid;
	private float alpha;
	/* Changed flag. This flag will change to true if any modification happens inside
	 * this cell. New cells are not "changed" so they wont get saved. */
	private boolean changed;
	
	/**
	 * Constructs a new cell.
	 */
	public Cell(GameMap map, String name, int posX, int posY, int w, int h) {
		
		this.map = map;
		
		this.name = name;
		this.posX = posX;
		this.posY = posY;
		this.w = w;
		this.h = h;
		
		tileset = null;
		tiles = new Tile[w][h];
		initializeTiles();
		
		events = new HashMap<String, Event>();
		
		// Load the cell from disc, if exists
		File tempFile = new File(map.getPath()+"/"+name);
		if (tempFile.exists()) {
			loadMetadata();
			TextFile file = new TextFile(map.getPath() + "/" + name + "/layers.wdat");
			loadTileData(file, 0, 1);
			loadTileData(file,1, 1 + (file.getLength()/5) * 1);
			loadTileData(file,2, 1 + (file.getLength()/5) * 2);
			loadTileData(file,3, 1 + (file.getLength()/5) * 3);
			loadTileData(file,4, 1 + (file.getLength()/5) * 4);
			loadEvents();
			loadCommonEvents();
			valid = true;
		}
		alpha = 0;
		changed = false;
	}
	
	/**
	 * Resize this cell. Preserve original tile data if possible, and delete out of cell events.
	 */
	public void resize(int w, int h) {
		
		// Set the new width and height
		this.w = w;
		this.h = h;
		Tile[][] newTiles = new Tile[w][h];
		for (int x1 = 0; x1 < w; x1++) {
			for (int y1 = 0; y1 < h; y1++) {
				if (x1 < tiles.length && y1 < tiles[x1].length) {
					newTiles[x1][y1] = tiles[x1][y1];
				} else {
					newTiles[x1][y1] = new Tile();
				}
			}
		}
		tiles = newTiles;
		
		// Remove invalid events
		Iterator<Map.Entry<String, Event>> iter = events.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Event> entry = iter.next();
			if (entry.getValue().getX() >= w || entry.getValue().getY() >= h) {
				iter.remove();
				System.out.println("Event: removed during cell resize: \"" + 
						entry.getValue().getName() + "\" " +
						entry.getValue().getX() + "/" + entry.getValue().getY());
			}
		}
		
		// Set the cell to be changed
		this.setChanged(true);
		
	}
	
	/**
	 * Draws the cell on the spritebatch.
	 */
	public void draw(SpriteBatch batch, int blitX, int blitY, Box renderBox, 
			int tileSize, String activeTileset, boolean shade, boolean hide, boolean grid, int layerIndex) {
		if (valid) {
		// Reset color to white (no coloring)
			batch.setColor(Palette.BATCH);
			drawTiles(batch, blitX, blitY, renderBox, tileSize, hide, layerIndex);
			drawEvents(batch, blitX, blitY, renderBox, tileSize, layerIndex);
			drawStartingPosition(batch, blitX, blitY, renderBox, tileSize);
			if (shade) {
				drawShading(batch, blitX, blitY, tileSize, activeTileset);
			}
			if (grid) {
				drawGrid(batch, blitX, blitY, tileSize);
			}
			drawOutline(batch, blitX, blitY, tileSize, renderBox);
			// Set alpha!
			alpha = MathUtilities.boundedVariable(alpha, Wyvern.getDelta()*2, 0.0f, 1.0f);
			batch.setColor(Palette.BATCH);
		}
	}

	/**
	 * Exports this cell as a PNG image.
	 */
	public void exportCell(String magenta, int ignored) {
		// Enable alpha channel here
		Pixmap.setBlending(Blending.SourceOver);

		// Process the cell.
		Tileset currentTileset = Wyvern.cache.getTileset(tileset);
		int tsize = currentTileset.getTileSize();
		
		Pixmap finalImage = new Pixmap(this.w * tsize, this.h * tsize, Format.RGBA8888);
		
		Pixmap tilesetImage = new Pixmap(new FileHandle(
				Wyvern.INTERPRETER_DIR + "/resources/tilesets/" + tileset + "@tileset/terrain.png"));
		
		Pixmap rect = new Pixmap(tsize, tsize, Format.RGBA8888);
		rect.setColor(Color.MAGENTA);
		rect.fill();
		
		// Magenta bottoms
		boolean[][] colored = new boolean[this.w][this.h];
		
		// Loop in all the tiles.
		for (int x = 0; x < this.w; x++) {
			for (int y = 0; y < this.h; y++) {
				for (int z = 0; z < 5; z++) {
					
					// Get the current tile positions on this layer.
					int tileX = this.getTile(x, y).getX(z);
					int tileY = this.getTile(x, y).getY(z);
					
					// If this tile is empty, dont blit anything.
					if (tileX == 0 && tileY == 0) {
						continue;
					} else {
						
						// This tile is not empty, draw the tile here.
						finalImage.drawPixmap(tilesetImage, 
								tileX*tsize, tileY*tsize, 
								tsize, tsize, 
								x*tsize, y*tsize, 
								tsize, tsize);
						
						// Color flagged tiles to magenta.
						if (currentTileset.getTerrainData()[tileX][tileY].equals(magenta)) {
							
							// Draw magenta texture, as this tile is flagged.
							
							// Draw with 0 ignored, if this is the last tile.
							int reallyIgnored = ignored;
							if (y == this.h-1) {
								reallyIgnored = 0;
							}
							
							finalImage.drawPixmap(rect, 
									x*tsize, y*tsize, 
									0, 0,
									tsize, tsize-reallyIgnored);
							
							// We use the simple X and Y values...
							colored[x][y] = true;
							
							// If a tile above this tile is already colored, color it again.
							// We need to color again, as it contains the ignored parts.
							if (y-1 >= 0) {
								if (colored[x][y-1]) {
									finalImage.drawPixmap(rect, 
											x*tsize, 
											(y-1)*tsize, 
											0, 0,
											tsize, 
											tsize);
								}
							}
						}
					}
				}
			}
		}
		
		// We finally export.
		String exportPath = Wyvern.INTERPRETER_DIR + "/" +
				this.map.getRelativePath() + "/" + this.getPosX() + "x" + this.getPosY();
		PixmapIO.writePNG(new FileHandle(exportPath + "/export.png"), 
				finalImage);
		// Open the export location
		try {
			Desktop.getDesktop().open(new File(exportPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Print dialog
		System.out.println("Export: PNG successfully exported to " + exportPath + "/export.png");
		// Dispose all.
		finalImage.dispose();
		tilesetImage.dispose();
		rect.dispose();

		// Set back blending to None
		Pixmap.setBlending(Blending.None);
	}
	
	/**
	 * Save this cell to hdd
	 */
	public void saveCell() {
		
		// Check for the cell files
		File f = new File(map.getPath()+"/"+name);
		if (!f.exists()) {
			FileUtilities.createFolders(map.getPath()+"/"+name);
		}

		// Write terrain data
		TextFile td = new TextFile(map.getPath()+"/"+name+"/"+"layers.wdat",null);
		for (int layer = 0; layer < 5; layer++) {
			td.addLine();
			td.addValue("@"+layer);
			for (int y = 0; y < h; y++) {
				td.addLine();
				for (int x = 0; x < w; x++) {
					Tile tile = tiles[x][y];
					td.addValue(tile.getAsDataString(layer));
				}
			}
		}
		td.save();
		
		// Write other data
		TextFile t = new TextFile(map.getPath()+"/"+name+"/metadata.wdat", null);
		t = constructMetadata(t, name, "default", getTileset());
		t.save();
		
		// Write event data
		TextFile t2 = new TextFile(map.getPath()+"/"+name+"/events.wdat", null);
		TextFile t3 = new TextFile(map.getPath()+"/"+name+"/common_events.wdat", null);
		for (Event ev : events.values()) {
			if (ev.getReference() == null) {
				ev.write(t2);
			} else {
				t3.addLine();
				t3.addValue(ev.getReference().getSigID());
				t3.addValue(Integer.toString(ev.getX()));
				t3.addValue(Integer.toString(ev.getY()));
				t3.addValue(ev.getSigID());
			}
		}
		t2.save();
		t3.save();
		
		setChanged(false);
	}
	
	private void loadEvents() {
		TextFile evf = new TextFile(map.getPath() + "/" + name + "/events.wdat");
		int index = 0;
		while(true) {
			Event event = new Event(0, 0, true, false);
			index = event.load(evf, index);
			if (index == -2) {
				break;
			} else if (index == -1) {
				putEvent(event);
				break;
			} else {
				putEvent(event);
			}
		}
	}
	
	private void loadCommonEvents() {
		TextFile evf = new TextFile(map.getPath() + "/" + name + "/common_events.wdat");
		for (int i = 0; i < evf.getLength(); i++) {
			Event event = new Event(0, 0, true, false);
			int x = Integer.parseInt(evf.getValue(i, 1));
			int y = Integer.parseInt(evf.getValue(i, 2));
			String sigid = evf.getValue(i, 3);
			event.setX(x);
			event.setY(y);
			event.setSigID(sigid);
			// We set the reference after the ID in case the reference is broken...
			event.setReference(evf.getValue(i, 0));
			putEvent(event);
		}
	}
	
	/**
	 * Put an event inside this cell. Will ignore out of range events.
	 */
	private void putEvent(Event event) {
		if (event.getX() < w && event.getY() < h) {
			events.put(event.getX() + "x" + event.getY(), event);
		} else {
			System.out.println("Event: cannot load out of range event: \"" + 
					event.getName() + "\" " + event.getX() + "/" + event.getY());
		}
	}
	
	/**
	 * Set a new tileset for this cell.
	 * @param tileset
	 */
	public void setTileset(String tileset) {
		if (this.tileset == null || !this.tileset.equals(tileset)) {
			setChanged(true);
			this.tileset = tileset;
		}
		Wyvern.cache.getTileset(tileset).check();
	}
	
	public void invalidateCell() {
		initializeTiles();
		tileset = null;
		events.clear();
		valid = false;
		setChanged(true);
	}
	
	public void deleteCell() {
		File f = new File(map.getPath() + "/" + name);
		if (f.exists()) {
			FileUtilities.deleteDirectory(f);
		}
	}
	
	/**
	 * Set the tile data inside this cell, and update the autotiling around the changed tile.
	 */
	public void setTileData(int tx, int ty, int layer, int type, int index, int x, int y, boolean updateAuto) {
		tiles[tx][ty].setData(layer, type, index, x, y);
		if (updateAuto) {
			updateAutotiles(tx, ty, layer);
		}
		setChanged(true);
	}
	
	/**
	 * Set the tile data inside this cell, and update the autotiling around the changed tile if requested.
	 */
	public void setTileData(int tx, int ty, int layer, int type, boolean updateAuto) {
		tiles[tx][ty].setData(layer, type);
		if (updateAuto) {
			updateAutotiles(tx, ty, layer);
		}
		setChanged(true);
	}
	
	/**
	 * Set the tile data inside this cell, and update the autotiling around the changed tile if requested.
	 */
	public void setTileData(int tx, int ty, int layer, int x, int y, boolean updateAuto) {
		tiles[tx][ty].setData(layer, x, y);
		if (updateAuto) {
			updateAutotiles(tx, ty, layer);
		}
		setChanged(true);
	}
	
	/**
	 * Set the tile data inside this cell based on a "tiledata".
	 */
	public void setTileData(int tx, int ty, TileData data) {
		tiles[tx][ty].setData(data.layer, data.type, data.index, data.x, data.y);
		updateAutotiles(tx, ty, data.layer);
		setChanged(true);
	}
	
	public Tile getTile(int tx, int ty) {
		return tiles[tx][ty];
	}
	
	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
		// If the cell is changed, set the map to changed too
		if (changed) {
			map.setChanged(changed);
		}
	}
	
	public String getTileset() {
		return tileset;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	public HashMap<String, Event> getEvents() {
		return events;
	}
	
	/**
	 * Draw the cell tiles on screen. Crop the non-visible tiles.
	 */
	private void drawTiles(SpriteBatch batch, int blitX, int blitY, Box renderBox, int tileSize, boolean hide, int layerIndex) {
		if (valid) {
			// Calculate starting tile coordinates
			int startX = (renderBox.start.x - blitX) / tileSize;
			if (startX < 0) { startX = 0; }
			int startY = (renderBox.start.y - blitY) / tileSize;
			if (startY < 0) { startY = 0; }
			
			// Calculate ending tile coordinates
			int endX = ((renderBox.end.x - blitX) / tileSize) +1;
			if (endX > w) { endX = w; }
			int endY = ((renderBox.end.y - blitY) / tileSize) +1;
			if (endY > h) { endY = h; }
			
			Tileset currentTileset = Wyvern.cache.getTileset(tileset);
			
			// Loop by these coordinates, and draw the tiles
			float c_alpha = alpha;
			float c_alpha05 = alpha*0.5f;
			for (int x = startX; x < endX; x++ ) {
				for (int y = startY; y < endY; y++) {
					tiles[x][y].draw(batch, currentTileset, 
							blitX + x*tileSize, blitY + y*tileSize, tileSize, hide, layerIndex,
							c_alpha, c_alpha05); 
				}
			}
		}
	}
	
	private void drawEvents(SpriteBatch batch, int blitX, int blitY, Box renderBox, int tileSize, int layerIndex) {
		int bx = 0;
		int by = 0;
		for (Event e : events.values()) {
			bx = blitX + e.getX()*tileSize;
			by = blitY + e.getY()*tileSize;
			if (bx < renderBox.start.x-tileSize || bx > renderBox.end.x || 
					by < renderBox.start.y-tileSize || by > renderBox.end.y) {
				continue;
			}
			if (layerIndex == 5) {
				e.draw(batch, 0, bx, by, tileSize, true, alpha);
			} else {
				e.draw(batch, 0, bx, by, tileSize, false, alpha);
			}
		}
	}
	
	private void drawStartingPosition(SpriteBatch batch, int blitX, int blitY, Box renderBox, int tileSize) {
		if (map.getRelativePath().equals(Wyvern.cache.getStartingPosition().mapPath)) {
			if (posX == Wyvern.cache.getStartingPosition().cellX && posY == Wyvern.cache.getStartingPosition().cellY) {
				int x = Wyvern.cache.getStartingPosition().x;
				int y = Wyvern.cache.getStartingPosition().y;
				int bx = blitX + x*tileSize;
				int by = blitY + y*tileSize;
				if (bx < renderBox.start.x-tileSize || bx > renderBox.end.x || 
						by < renderBox.start.y-tileSize || by > renderBox.end.y) {
					return;
				}
				ShapePainter.drawFilledRectangle(batch, 0.1f, 0.1f, 0.7f, 0.4f, bx, by, tileSize+1, tileSize+1);
				ShapePainter.drawRectangle(batch, 0f, 0f, 0f, 1f, bx, by, tileSize+1, tileSize+1);
			}
		}
	}
	
	/** 
	 * Draw the shading of the cell, if the tilesets differ 
	 */
	private void drawShading(SpriteBatch batch, int blitX, int blitY, int tileSize, String activeTileset) {
		if (!tileset.equals(activeTileset)) {
			ShapePainter.drawFilledRectangle(batch, Palette.BLACK05, blitX, blitY, 
					w*tileSize+1, h*tileSize+1);
		}
	}
	
	private void drawGrid(SpriteBatch batch, int blitX, int blitY, int tileSize) {
		for (int x = 0; x < w; x++) {
			ShapePainter.drawStraigthLine(batch, Palette.MAP_TILEGRID, blitX + (x*tileSize), blitY, false, h*tileSize);
		}
		for (int y = 0; y < h; y++) {
			ShapePainter.drawStraigthLine(batch, Palette.MAP_TILEGRID, blitX, blitY + (y*tileSize), true, w*tileSize);
		}
	}
	
	/**
	 * Draws a capped horizontal line
	 */
	private void drawHorizontalLine(SpriteBatch batch, int blitX, int blitY, int tileSize, 
			int cellSize, Box renderBox) {
		
		int x = MathUtilities.boundedVariable(blitX, 0, renderBox.start.x, renderBox.end.x);
		int y = MathUtilities.boundedVariable(blitY, 0, renderBox.start.y, renderBox.end.y);
		int len = MathUtilities.boundedVariable(cellSize, 0, 0, renderBox.getWidth());
		
		if (blitX >= renderBox.end.x) {
			len = 0;
		}
		else if (blitX > renderBox.start.x && blitX + cellSize < renderBox.end.x) {
			// Do nothing
		}
		else if (blitX > renderBox.start.x && blitX < renderBox.end.x) {
			len = renderBox.getWidth() - (blitX - renderBox.start.x);
		} 
		else if (blitX + cellSize > renderBox.start.x && blitX + cellSize < renderBox.end.x) {
			len = cellSize - (renderBox.start.x - blitX);
		}
		
		ShapePainter.drawStraigthLine(batch, Palette.MAP_CELLGRID, 
				x, y, true, len);
	}
	
	/**
	 * Draws a capped vertical line
	 */
	private void drawVerticalLine(SpriteBatch batch, int blitX, int blitY, int tileSize, 
			int cellSize, Box renderBox) {
		
		int x = MathUtilities.boundedVariable(blitX, 0, renderBox.start.x, renderBox.end.x);
		int y = MathUtilities.boundedVariable(blitY, 0, renderBox.start.y, renderBox.end.y);
		int len = MathUtilities.boundedVariable(cellSize, 0, 0, renderBox.getHeight());
		
		if (blitY >= renderBox.end.y) {
			len = 0;
		}
		else if (blitY > renderBox.start.y && blitY + cellSize < renderBox.end.y) {
			// Do nothing
		}
		else if (blitY > renderBox.start.y && blitY < renderBox.end.y) {
			len = renderBox.getHeight() - (blitY - renderBox.start.y);
		} 
		else if (blitY + cellSize > renderBox.start.y && blitY + cellSize < renderBox.end.y) {
			len = cellSize - (renderBox.start.y - blitY);
		}
		
		ShapePainter.drawStraigthLine(batch, Palette.MAP_CELLGRID, 
				x, y, false, len);
	}
	
	private void drawOutline(SpriteBatch batch, int blitX, int blitY, int tileSize, Box renderBox) {
		
		int cellWidth = w*tileSize;
		int cellHeight = h*tileSize;
		this.drawHorizontalLine(batch, blitX, blitY, tileSize, cellWidth, renderBox);
		this.drawHorizontalLine(batch, blitX, blitY+cellHeight, tileSize, cellWidth, renderBox);
		this.drawVerticalLine(batch, blitX, blitY, tileSize, cellHeight, renderBox);
		this.drawVerticalLine(batch, blitX+cellWidth, blitY, tileSize, cellHeight, renderBox);
		
		/*
		 * Old version.
		ShapePainter.drawRectangle(batch, Palette.MAP_CELLGRID, blitX, blitY, 
				w*tileSize+1, h*tileSize+1);
		*/
	}
	
	/** 
	 * Update the autotile data around the tile (cx - center x and cy - center y)
	 * If needed, search for tiles in adjacent cells too.
	 */
	private void updateAutotiles(int cx, int cy, int layer) {
		for (int x = cx-1; x < cx+2; x++) {
			for (int y = cy-1; y < cy+2; y++) {
				int displacementX = MathUtilities.divCorrect(x, map.getCellW());
				int displacementY = MathUtilities.divCorrect(y, map.getCellH());
				Cell cell = map.getCell(getPosX() + displacementX, getPosY() + displacementY);
				if (cell != null && cell.isValid()) {
					int finalX = x - displacementX*map.getCellW();
					int finalY = y - displacementY*map.getCellH();
					Tile tile = cell.getTile(finalX, finalY);
					if (tile.getType(layer) != -1) {
						tile.setIndex(layer, tile.getBinIndex(getBinary(x, y, layer, tile.getType(layer))));
					}
				}
			}
		}
	}
	
	/** 
	 * Get an integer binary number data about the sorrounding autotiles. We will use this
	 * value for determining the current tiles autotile index. 
	 */
	private int getBinary(int cx, int cy, int layer, int type) {
		int binary = 0;
		int power = 8;
		for (int x = cx-1; x < cx+2; x++) {
			for (int y = cy-1; y < cy+2; y++) {
				int displacementX = MathUtilities.divCorrect(x, map.getCellW());
				int displacementY = MathUtilities.divCorrect(y, map.getCellH());
				Cell cell = map.getCell(getPosX() + displacementX, getPosY() + displacementY);
				if (cell != null && cell.isValid()) {
					Tile tile = cell.getTile(x - displacementX*map.getCellW(), y - displacementY*map.getCellH());
					if (type == tile.getType(layer)) {
						binary += Math.pow(2, power);
					}
				} else {
					binary += Math.pow(2, power);
				}
				power--;
			}
		}
		return binary;
	}
	
	private void loadMetadata() {
		TextFile meta = new TextFile(map.getPath()+"/"+name+"/metadata.wdat");
		tileset = meta.getValue(2, 1);
		Wyvern.cache.getTileset(tileset).check();
	}
	
	private void initializeTiles() {
		// Initialize tiles by these values
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				tiles[x][y] = new Tile();
			}
		}
	}

	private int calculateAutoIndex(int x, int y) {
		// We must determine the index by the x and y coordinates.
		return ((y%6)*8) + (x%8);
	}

	private void loadTileData(TextFile file, int id, int filePos) {
		// Iterate over the data, and manipulate the cell's data.
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {

				String data[] = StringUtilities.safeSplit(file.getValue(filePos + y, x), "x");

				int merged = Integer.parseInt(data[0]);
				int cr = merged % 48;
				int cg = merged / 48;
				int cb = Integer.parseInt(data[1]);
				int ca = calculateAutoIndex(cr, cg);

				// cb contains the autotile data
				if (cb > 0) {
					tiles[x][y].setData(id, cb-1, ca, cr, cg);
				} else {
					// Set this tile (x,y) to this tile index (r,g)
					tiles[x][y].setData(id, cr, cg);	
				}
			}
		}
	}
	
	/** 
	 * Construct a cell metadata file 
	 */
	public static TextFile constructMetadata(TextFile t, String name, String region, String tileset) {
		t.clear();
		t.addLine();
		t.addValue(0, "name");
		t.addValue(0, name);
		t.addLine();
		t.addValue(1, "region");
		t.addValue(1, region);
		t.addLine();
		t.addValue(2, "tileset");
		t.addValue(2, tileset);
		return t;
	}
	
}
