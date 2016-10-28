package com.szeba.wyv.widgets.panels.map;

import java.awt.Point;
import java.io.File;
import java.util.HashMap;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.geometry.Box;
import com.szeba.wyv.data.maps.Cell;
import com.szeba.wyv.data.maps.GameMap;
import com.szeba.wyv.utilities.FileUtilities;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.MathUtilities;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.ShapePainter;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.Widget;

/** 
 * A Map panel which controls the rendering/updating of map objects. 
 * @author Szebaszti�n
 */
public class MapPanel extends Widget {
	
	private String currentMapPath;
	private GameMap currentMap;
	
	private Box renderBox;
	
	// Mouseover data
	private Point cellCoord;
	private Point mapCoord;
	private Point tileCoord;
	
	// Tile size limits
	private boolean fixTileSize;
	private int minTile;
	private int maxTile;
	
	// Layer index
	private int layerIndex;
	
	// Tileset activation flag
	private boolean tilesetChanged;
	
	// Shade inactive cells
	private boolean shadeCells;
	
	// Shade inactive layers
	private boolean shadeLayer;
	
	// Hide completely
	private boolean completeHide;
	
	// Draw tilegrid
	private boolean gridEnabled;
	
	public MapPanel(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
		
		currentMapPath = null;
		currentMap = null;
		
		renderBox = new Box(getX(), getY(), getX()+getW(), getY()+getH());
		
		cellCoord = new Point(0, 0);
		mapCoord = new Point(0, 0);
		tileCoord = new Point(0, 0);
		
		fixTileSize = false;
		minTile = 8;
		maxTile = 64;
		
		layerIndex = 0;
		
		tilesetChanged = false;
		
		shadeCells = true;
		shadeLayer = true;
		completeHide = false;
		gridEnabled = false;
	}
	
	private void updateRenderBox() {
		renderBox.start.x = getX();
		renderBox.start.y = getY();
		renderBox.end.x = getX()+getW();
		renderBox.end.y = getY()+getH();
	}
	
	@Override
	public void setRX(int x) {
		super.setRX(x);
		updateRenderBox();
	}
	
	@Override
	public void setRY(int y) {
		super.setRY(y);
		updateRenderBox();
	}
	
	@Override
	public void setOX(int x) {
		super.setOX(x);
		updateRenderBox();
	}
	
	@Override
	public void setOY(int y) {
		super.setOY(y);
		updateRenderBox();
	}
	
	@Override
	public void setW(int w) {
		super.setW(w);
		updateRenderBox();
	}
	
	@Override
	public void setH(int h) {
		super.setH(h);
		updateRenderBox();
	}
	
	@Override
	public Color getBkgColor() {
		if (layerIndex == 0 || layerIndex == 5 || !shadeLayer) {
			return Palette.WIDGET_BKG2;
		} else {
			return Palette.BLACK;
		}
	}
	
	// Draw four rectangles around the widget
	public void drawHide(SpriteBatch batch) {
		if (getCurrentMap() != null) {
			ShapePainter.drawFilledRectangle(batch, getBkgColor(), 
					getX()-getCurrentMap().getTileSize(), getY()-getCurrentMap().getTileSize(), 
					getW()+(getCurrentMap().getTileSize()*2), 
					getCurrentMap().getTileSize());
			ShapePainter.drawFilledRectangle(batch, getBkgColor(), 
					getX()-getCurrentMap().getTileSize(), getY()-getCurrentMap().getTileSize(), 
					getCurrentMap().getTileSize(), 
					getH()+(getCurrentMap().getTileSize()*2));
			ShapePainter.drawFilledRectangle(batch, getBkgColor(), 
					getX()-getCurrentMap().getTileSize(), getH()+getY(), 
					getW()+(getCurrentMap().getTileSize()*2), 
					getCurrentMap().getTileSize());
			ShapePainter.drawFilledRectangle(batch, getBkgColor(), 
					getW()+getX(), getY()-getCurrentMap().getTileSize(), 
					getCurrentMap().getTileSize(), 
					getH()+(getCurrentMap().getTileSize()*2));
		}
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		drawBackground(batch);
		if (currentMapPath != null) {
			// Draw the current map, if its available
			if (shadeLayer) {
				currentMap.draw(batch, getW(), getH(), renderBox, shadeCells, completeHide, gridEnabled, layerIndex);
			} else {
				currentMap.draw(batch, getW(), getH(), renderBox, shadeCells, completeHide, gridEnabled, 5);
			}
			// Extended draw on the map (for override)
			extendedDraw(batch);
			// Draw the coordinate data
			drawCoordinateData(batch, 0, cellCoord.x, cellCoord.y, tileCoord.x, tileCoord.y);
			drawZoomData(batch);
			drawActiveCell(batch);
			// Draw the map path
			drawMapPathString(batch);
		}
		drawHide(batch);
		drawOutline(batch);
	}

	@Override
	public void mainUpdate(int scrolled) {
		if (currentMapPath != null) {
			updateScrolling(scrolled);
			// Update the mouseover
			//if (mouseInside()) {
			updateMouseOver();
			//}
			// Update the active tileset
			updateActiveTileset();
			// Update other things (for override)
			extendedUpdate(scrolled);
		}
	}
	
	@Override
	public void passiveUpdate(int scrolled) {
		if (currentMapPath != null) {
			updateMouseOver();
		}
	}
	
	public void extendedDraw(SpriteBatch batch) {
	}
	
	public void extendedUpdate(int scrolled) {
	}
	
	/**
	 * Load or set a map from this hash.
	 */
	public void loadMap(HashMap<String, GameMap> maps, String mapName, String mapPath) {
		if (maps.containsKey(mapPath)) {
			currentMapPath = mapPath;
			currentMap = getCurrentMapFromHash(maps);
		} else {
			maps.put(mapPath, new GameMap(mapName, Wyvern.INTERPRETER_DIR + mapPath, mapPath));
			currentMapPath = mapPath;
			currentMap = getCurrentMapFromHash(maps);
		}
	}

	public void closeMap(HashMap<String, GameMap> maps, String mapPath) {
		if (maps.containsKey(mapPath)) {
			maps.remove(mapPath);
			currentMapPath = null;
			currentMap = null;
		}
	}
	
	public void saveMap(HashMap<String, GameMap> maps, String mapPath) {
		if (maps.containsKey(mapPath)) {
			maps.get(mapPath).save();
		}
	}
	
	/**
	 * Get a map from this hash
	 */
	public GameMap getCurrentMapFromHash(HashMap<String, GameMap> maps) {
		return maps.get(currentMapPath);
	}
	
	/**
	 * Set the current map from the maps hash
	 */
	public GameMap setCurrentMap(HashMap<String, GameMap> maps, String mapPath) {
		if (mapPath == null) { 
			currentMapPath = null;
			currentMap = null;
		} else if (currentMapPath == null || !currentMapPath.equals(mapPath)) {
			currentMapPath = mapPath;
			currentMap = getCurrentMapFromHash(maps);
		}
		return currentMap;
	}
	
	/**
	 * Set a map directly, either loading from disc, or using a shallow copy from maps
	 * Used only by the dynamic widget.
	 * This method will search for the map by the given ID.
	 * IF a search took place, this method will return the new name and path...
	 */
	public void setMap(HashMap<String, GameMap> maps, String sigid, String mapName, String mapPath) {
		// Set the current map path
		currentMapPath = mapPath;
		
		// If map path is null, set an empty map
		if (mapPath == null) {
			currentMapPath = null;
			currentMap = null;
		
		// If map is already loaded, get a shallow copy of it.
		} else if (maps.containsKey(mapPath)) {
			currentMap = new GameMap(getCurrentMapFromHash(maps));
			
		} else {
			
			// Load from hard disc, if exists.
			if (mapPath.length() > 0 && FileUtilities.exists(Wyvern.INTERPRETER_DIR + mapPath)) {
				currentMap = new GameMap(mapName, Wyvern.INTERPRETER_DIR + mapPath, mapPath);
				
			// If not exists, and ID is not -1, search for it by ID.
			} else if (!sigid.equals("-1")) {
				
				// Get the new Path where this signature ID might be.
				String searchPath = Wyvern.mapGameMapIDs().get(sigid);
				
				if (searchPath != null) {
					// Search did found a map!
					String srchName = new File(searchPath).getName();
					// We get the relative path of the search result
					String srchMapPath = StringUtilities.getRelativePath(Wyvern.INTERPRETER_DIR, searchPath);
					currentMapPath = srchMapPath;
					currentMap = new GameMap(srchName, searchPath, srchMapPath);
					// Print info to the console
					System.out.println("Map: opened by search! " + srchMapPath);
				} else {
					// Search did not found the map
					currentMapPath = null;
					currentMap = null;
				}
				
			} else {
				// signature ID was -1
				currentMapPath = null;
				currentMap = null;
			}
		}
	}
	
	public GameMap getCurrentMap() {
		return currentMap;
	}
	
	public Point getCellCoord() {
		return this.cellCoord;
	}
	
	public Point getMapCoord() {
		return this.mapCoord;
	}
	
	public Point getTileCoord() {
		return this.tileCoord;
	}
	
	public boolean getTilesetChange() {
		return this.tilesetChanged;
	}
	
	public void setLayerIndex(int layerIndex) {
		this.layerIndex = layerIndex;
	}
	
	public int getLayerIndex() {
		return layerIndex;
	}
	
	public void setGridEnabled(boolean value) {
		this.gridEnabled = value;
	}
	
	public boolean isGridEnabled() {
		return gridEnabled;
	}
	
	public void setShadeCells(boolean shadeCells) {
		this.shadeCells = shadeCells;
	}
	
	public void setCompleteHide(boolean completeHide) {
		this.completeHide = completeHide;
	}
	
	public void setShadeLayer(boolean shadeLayer) {
		this.shadeLayer = shadeLayer;
	}
	
	public Point getActiveTile() {
		return getCurrentMap().getActiveTile();
	}
	
	private void updateMouseOver() {
		if (currentMap != null) {
			// Calculate cell coordinate
			int mousePosX = (Wyvern.input.getX() - getX() + currentMap.getOffX());
			int mousePosY = (Wyvern.input.getY() - getY() + currentMap.getOffY());
			int cellX = MathUtilities.divCorrect(mousePosX, currentMap.getCellPixelX());
			int cellY = MathUtilities.divCorrect(mousePosY, currentMap.getCellPixelY());
			cellCoord.x = cellX;
			cellCoord.y = cellY;
			// Calculate map coordinate
			mapCoord.x = MathUtilities.divCorrect(mousePosX, currentMap.getTileSize());
			mapCoord.y = MathUtilities.divCorrect(mousePosY, currentMap.getTileSize());
			// Calculate tile coordinate
			tileCoord.x = mapCoord.x - (cellCoord.x*currentMap.getCellW());
			tileCoord.y = mapCoord.y - (cellCoord.y*currentMap.getCellH());
		}
	}
	
	private void updateActiveTileset() {
		// Change the active tileset even on mouse hold, if shading is disabled.
		if (((Wyvern.input.isButtonHold(0) && !shadeCells) ||
				(Wyvern.input.isButtonPressed(0) && shadeCells)) &&
				mouseInside()) {
			// Get the tileset here
			Cell clickedCell = currentMap.getCell(cellCoord.x, cellCoord.y);
			// If the clicked cell is valid, get its tileset
			if (clickedCell != null && clickedCell.isValid()) {
				String activeTileset = clickedCell.getTileset();
				if (!currentMap.getActiveTileset().equals(activeTileset)) {
					currentMap.setActiveTileset(activeTileset);
					if (shadeCells) {
						tilesetChanged = true;
					}
				}
			}
		}
		if (!Wyvern.input.isButtonHold(0)) {
			tilesetChanged = false;
		}
	}
	
	public void setFixTileSize(boolean value) {
		fixTileSize = value;
	}
	
	private void updateMapZoom(int scrolled) {
		if (scrolled != 0 && !fixTileSize) {
			int oldTileSize = currentMap.getTileSize();
			int amount = getZoomAmount(scrolled, oldTileSize);
			currentMap.subFromTileSize(amount, minTile, maxTile);
			if (oldTileSize != currentMap.getTileSize()) {
				currentMap.subFromOffX(amount * ((currentMap.getOffX()+ Wyvern.input.getX()-getX() )/oldTileSize));
				currentMap.subFromOffY(amount * ((currentMap.getOffY()+ Wyvern.input.getY()-getY() )/oldTileSize));
				//currentMap.subFromOffX(amount * ((currentMap.getOffX()+ (getW()/2) )/oldTileSize));
				//currentMap.subFromOffY(amount * ((currentMap.getOffY()+ (getH()/2) )/oldTileSize));
			}
		}
	}
	
	private int getZoomAmount(int dir, int val) {
		if (val < 14) {
			return dir;
		} else if (val < 24) {
			return dir * 2;
		} else {
			return dir * 4;
		}
	}
	
	private void updateScrolling(int scrolled) {
		if (Wyvern.input.isButtonHold(1)) {
			currentMap.subFromOffX(Wyvern.input.getDeltaX());
			currentMap.subFromOffY(Wyvern.input.getDeltaY());
		} else if (Wyvern.input.isKeyHold(Keys.CONTROL_LEFT) ||
				Wyvern.input.isKeyHold(Keys.CONTROL_RIGHT)) {
			updateMapZoom(scrolled);
		} else if (Wyvern.input.isKeyHold(Keys.SHIFT_LEFT) ||
				   Wyvern.input.isKeyHold(Keys.SHIFT_RIGHT)) {
			if (scrolled == 1) {
				currentMap.subFromOffX(-40);
			} else if (scrolled == -1) {
				currentMap.subFromOffX(40);
			}
		} else {
			if (scrolled == 1) {
				currentMap.subFromOffY(-40);
			} else if (scrolled == -1) {
				currentMap.subFromOffY(40);
			}
		}
	}
	
	protected void drawCoordinateData(SpriteBatch batch, int row, int cx, int cy, int tx, int ty) {
		// Draw a rectangle
		row *= 17;
		ShapePainter.drawFilledRectangle(batch, Palette.WIDGET_BKG, getX()+(getW()-240), getY()+row, 240, 18);
		// Draw the outline
		if (isFocused()) {
			ShapePainter.drawRectangle(batch, getActiveBrdColor(), getX()+(getW()-240), getY()+row, 240, 18);
		} else {
			ShapePainter.drawRectangle(batch, getPassiveBrdColor(), getX()+(getW()-240), getY()+row, 240, 18);
		}
		// Draw the texts
		FontUtilities.print(batch, "cell: " + cx + "/" + cy, 
				getX()+(getW()-240) + 3, getY()+ row + 2);
		FontUtilities.print(batch, tx + "/" + ty, 
				getX()+(getW()-240) + 163, getY()+ row + 2);
		batch.setColor(Palette.BATCH);
	}
	
	protected void drawZoomData(SpriteBatch batch) {
		ShapePainter.drawFilledRectangle(batch, Palette.WIDGET_BKG, getX(), getY(), 20, 18);
		if (isFocused()) {
			ShapePainter.drawRectangle(batch, getActiveBrdColor(), getX(), getY(), 20, 18);
		} else {
			ShapePainter.drawRectangle(batch, getPassiveBrdColor(), getX(), getY(), 20, 18);
		}
		// Draw the zoom amount
		FontUtilities.print(batch, Integer.toString(currentMap.getTileSize()), getX() + 2, getY() + 2);
		batch.setColor(Palette.BATCH);
	}
	
	protected void drawActiveCell(SpriteBatch batch) {
		ShapePainter.drawFilledRectangle(batch, Palette.WIDGET_BKG, getW()-200, getY()+getH()-18, 200, 18);
		if (isFocused()) {
			ShapePainter.drawRectangle(batch, getActiveBrdColor(), getW()-200, getY()+getH()-18, 200, 18);
		} else {
			ShapePainter.drawRectangle(batch, getPassiveBrdColor(), getW()-200, getY()+getH()-18, 200, 18);
		}
		FontUtilities.print(batch, "Active cell: " +
				Wyvern.cache.getCurrentCellCoord().x + " / " + Wyvern.cache.getCurrentCellCoord().y,
				(getW()-200)+2, getY()+(getH()-18)+2);
		batch.setColor(Palette.BATCH);
	}
	
	private void drawMapPathString(SpriteBatch batch) {
		ShapePainter.drawFilledRectangle(batch, Palette.WIDGET_BKG, 
				getX()+1, getY()+(getH()-18), 
				(int) FontUtilities.getBounds(currentMapPath).width + 2, 18);
		batch.setColor(Palette.BATCH);
		FontUtilities.print(batch, currentMapPath, getX(), getY()+(getH()-18)+2);
	}
	
}
