package com.szeba.wyv.widgets.panels.map;

import java.awt.Point;
import java.util.ArrayList;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.data.UndoEntry;
import com.szeba.wyv.data.geometry.Box;
import com.szeba.wyv.data.geometry.GrowPoint;
import com.szeba.wyv.data.maps.Cell;
import com.szeba.wyv.data.tiles.TileCopyRegion;
import com.szeba.wyv.data.tiles.TileData;
import com.szeba.wyv.utilities.MathUtilities;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.ShapePainter;

public class EditableMapPanel extends MapPanel {

	private int currentTool;
	private UndoEntry undoEntry;
	private Box selectedTiles;
	
	private GrowPoint selectionPoint;
	private TileCopyRegion copyRegion;
	
	private boolean oldPencil;
	private int oldPencilX;
	private int oldPencilY;
	
	private boolean alt_paste;
	private boolean alt_paste_used;
	
	public EditableMapPanel(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
		
		currentTool = 0;
		undoEntry = new UndoEntry();
		selectedTiles = new Box(0, 0, 0, 0);
		
		selectionPoint = new GrowPoint(new Point(0, 0));
		copyRegion = new TileCopyRegion();
		
		// Pencil variables
		oldPencil = false;
		oldPencilX = 0;
		oldPencilY = 0;
		
		// Alt paste data
		alt_paste = false;
		alt_paste_used = false;
	}

	@Override
	public void extendedDraw(SpriteBatch batch) {
		drawTools(batch);
	}

	@Override
	public void extendedUpdate(int scrolled) {
		// Send signal with spacebar to show the tileset picker
		if (Wyvern.input.isKeyPressed(Keys.SPACE) && mouseInside()) {
			setSignal(new Signal(Signal.T_TPICK, 
					Integer.toString(getCellCoord().x),
					Integer.toString(getCellCoord().y)));
		}
		// Update the tools
		updateTools(scrolled);
	}

	public void setSelectedTiles(Box reference) {
		selectedTiles.start.x = reference.start.x;
		selectedTiles.start.y = reference.start.y;
		selectedTiles.end.x = reference.end.x;
		selectedTiles.end.y = reference.end.y;
	}
	
	public void setActiveTool(int tool) {
		currentTool = tool;
	}
	
	private void drawSelectionPoint(SpriteBatch batch) {
		if (selectionPoint.isVisible()) {
			ShapePainter.drawFilledRectangle(batch, Palette.MAP_SELECTION_RECT, 
					selectionPoint.getStartX()*getCurrentMap().getTileSize() + getX() - getCurrentMap().getOffX(), 
					selectionPoint.getStartY()*getCurrentMap().getTileSize() + getY() - getCurrentMap().getOffY(), 
					getCurrentMap().getTileSize() * (selectionPoint.getWidth()),
					getCurrentMap().getTileSize() * (selectionPoint.getHeight()));
		}
	}
	
	private void drawPaintRect(SpriteBatch batch, int w, int h) {
		ShapePainter.drawRectangle(batch, Palette.MAP_PAINT_RECT, 
				getMapCoord().x*getCurrentMap().getTileSize() + getX() - getCurrentMap().getOffX() + 1, 
				getMapCoord().y*getCurrentMap().getTileSize() + getY() - getCurrentMap().getOffY() + 1, 
				getCurrentMap().getTileSize() * w - 1, 
				getCurrentMap().getTileSize() * h - 1);
	}
	
	private void drawTools(SpriteBatch batch) {
		if (getLayerIndex() < 5) {
			switch(currentTool) {
			case 0:
				drawPaintRect(batch, selectedTiles.getWidth()+1, selectedTiles.getHeight()+1);
				break;
			case 1:
			case 3:
				drawSelectionPoint(batch);
				break;
			case 2:
				drawPaintRect(batch, 1, 1);
				break;
			case 4:
				drawPaintRect(batch, copyRegion.getCopyW(), copyRegion.getCopyH());
				break;
			}
		}
	}
	
	private void updateTools(int scrolled) {
		if (!getTilesetChange() && getLayerIndex() < 5) {
			switch (currentTool) {
			case 0:
				updatePencil();
				break;
			case 1:
				updateRectangle();
				break;
			case 2:
				updateFill();
				break;
			case 3:
				updateCopy();
				break;
			case 4:
				updatePaste();
				break;
			}
		}
	}
	
	/**
	 * Update the pencil tool.
	 */
	private void updatePencil() {
		if (Wyvern.input.isButtonHold(0)) {
			if (mouseInside()) {
				int tileX = getMapCoord().x;
				int tileY = getMapCoord().y;
				
				if (oldPencil == true) {
					pencilLine(oldPencilX, oldPencilY, tileX, tileY);
				} else {
					// We got the tiles where we paint to.
					for (int xi = selectedTiles.start.x; xi <= selectedTiles.end.x; xi++) {
						for (int yi = selectedTiles.start.y; yi <= selectedTiles.end.y; yi++) {
							paintTile(tileX + xi - selectedTiles.start.x, 
									  tileY + yi - selectedTiles.start.y, 
									  xi, yi, getLayerIndex(), true, true);
						}
					}
				}
				// Save this coordinate
				oldPencil = true;
				oldPencilX = tileX;
				oldPencilY = tileY;
			}
		} else {
			// Reset oldPencil variable
			oldPencil = false;
			if (undoEntry.isModified()) {
				getCurrentMap().addUndoEntry(undoEntry);
				undoEntry.clear();
			}
		}
	}
	
	private void pencilLine(int x0, int y0, int x1, int y1) {
		// Initialize variables
		int dx = Math.abs(x1-x0);
		int dy = Math.abs(y1-y0);
		int sx = 0;
		int sy = 0;
		if (x0 < x1) { sx = 1; } else { sx = -1; }
		if (y0 < y1) { sy = 1; } else { sy = -1; }
		int err = dx - dy;
		int e2 = 0;
		// Draw line loop
		while (true) {
			// Draw to x0 y0
			for (int xi = selectedTiles.start.x; xi <= selectedTiles.end.x; xi++) {
				for (int yi = selectedTiles.start.y; yi <= selectedTiles.end.y; yi++) {
					paintTile(x0 + xi - selectedTiles.start.x, 
							  y0 + yi - selectedTiles.start.y, 
							  xi, yi, getLayerIndex(), true, true);
				}
			}
			// Continue
			if (x0 == x1 && y0 == y1) {
				break;
			}
			e2 = 2*err;
			if (e2 > -dy) {
				err -= dy;
				x0 += sx;
			}
			if (e2 < dx) {
				err += dx;
				y0 += sy;
			}
		}
	}
	
	/** 
	 * Update the rectangle tool. 
	 */
	private void updateRectangle() {
		if (Wyvern.input.isButtonPressed(0) && mouseInside()) {
			selectionPoint.setVisible(true);
			selectionPoint.setCenter(getMapCoord().x, getMapCoord().y);
		} 
		if (Wyvern.input.isButtonHold(0) && mouseInside()) {
			selectionPoint.setGrowX(-(selectionPoint.getCenter().x - getMapCoord().x));
			selectionPoint.setGrowY(-(selectionPoint.getCenter().y - getMapCoord().y));
		} else if (!Wyvern.input.isButtonHold(0) && selectionPoint.isVisible()) {
			// Set these tiles
			for (int tix = selectionPoint.getStartX(); tix < selectionPoint.getEndX()+1; tix++) {
				for (int tiy = selectionPoint.getStartY(); tiy < selectionPoint.getEndY()+1; tiy++) {
					paintTile(tix, tiy, selectedTiles.start.x, selectedTiles.start.y, getLayerIndex(), true, true);
				}
			}
			// Add to undo entry
			if (undoEntry.isModified()) {
				getCurrentMap().addUndoEntry(undoEntry);
				undoEntry.clear();
			}
			// Reset the selection point
			selectionPoint.reset();
		}
	}
	
	/**
	 * Update the copy tool
	 */
	private void updateCopy() {
		if (Wyvern.input.isButtonPressed(0) && mouseInside()) {
			selectionPoint.setVisible(true);
			selectionPoint.setCenter(getMapCoord().x, getMapCoord().y);
		} 
		if (Wyvern.input.isButtonHold(0) && mouseInside()) {
			int growX = -(selectionPoint.getCenter().x - getMapCoord().x);
			if (growX < -99) { growX = -99; }
			if (growX > 99) { growX = 99; }
			int growY = -(selectionPoint.getCenter().y - getMapCoord().y);
			if (growY < -99) { growY = -99; }
			if (growY > 99) { growY = 99; }
			selectionPoint.setGrowX(growX);
			selectionPoint.setGrowY(growY);
		} else if (!Wyvern.input.isButtonHold(0) && selectionPoint.isVisible()) {
			// Copy these tiles
			copyRegion.clear();
			copyRegion.setDimensions(selectionPoint.getWidth(), selectionPoint.getHeight());
			for (int tix = selectionPoint.getStartX(), cx = 0; tix < selectionPoint.getEndX()+1; tix++, cx++) {
				for (int tiy = selectionPoint.getStartY(), cy = 0; tiy < selectionPoint.getEndY()+1; tiy++, cy++) {
					copyTile(tix, tiy, copyRegion, cx, cy);
				}
			}
			// Reset the selection point
			selectionPoint.reset();
		}
	}
	
	/**
	 * Update the paste tool
	 */
	private void updatePaste() {
		
		// Determine alt paste tool
		alt_paste = false;
		alt_paste_used = false;
		if (Wyvern.input.isKeyHold(Keys.ALT_LEFT)) {
			// Alt button is pressed, we might use the alt paste tool.
			alt_paste = true;
		}
		
		// Determine action
		if (Wyvern.input.isButtonPressed(0)) {
			if (mouseInside()) {
				int tileX = getMapCoord().x;
				int tileY = getMapCoord().y;
				
				// If we click while we hold down the alt paste button, set the used to true
				// so we can successfully delete the undo entries
				if (alt_paste) {
					alt_paste_used = true;
				}
				
				// We got the tiles where we paste to.
				for (int tx = tileX, px = 0; px < copyRegion.getCopyW(); tx++, px++) {
					for (int ty = tileY, py = 0; py < copyRegion.getCopyH(); ty++, py++) {
						for (int layer = 0; layer < 5; layer++) {
							if (copyRegion.getCopiedTiles()[px][py][layer] != null) {
								if (alt_paste) {
									// Undoless direct paste!
									paintTile(tx, ty, copyRegion.getCopiedTiles()[px][py][layer].x, 
											copyRegion.getCopiedTiles()[px][py][layer].y, 
											copyRegion.getCopiedTiles()[px][py][layer].type,
											copyRegion.getCopiedTiles()[px][py][layer].index,
											layer, false, false);
								} else {
									// Regular paste!
									paintTile(tx, ty, copyRegion.getCopiedTiles()[px][py][layer].x, 
											copyRegion.getCopiedTiles()[px][py][layer].y, 
											copyRegion.getCopiedTiles()[px][py][layer].type,
											-1, 
											layer, true, true);
								}
							}
						}
					}
				}
				
				// We are done with pasting, determine if we have to reset the undo data, or add to it.
				if (alt_paste_used) {
					System.out.println("Undo: data reset by a direct paste!");
					this.getCurrentMap().resetUndo();
				} else {
					if (undoEntry.isModified()) {
						getCurrentMap().addUndoEntry(undoEntry);
						undoEntry.clear();
					}
				}
				
			} // End of mouse inside check
		} // End of click check
		
	}
	
	/**
	 * Update the floodfill tool.
	 */
	private void updateFill() {
		if (Wyvern.input.isButtonPressed(0) && mouseInside()) {
			// Get the starting tile and cell
			int paintedX = getMapCoord().x;
			int paintedY = getMapCoord().y;
			int cellX = MathUtilities.divCorrect(paintedX, getCurrentMap().getCellW());
			int cellY = MathUtilities.divCorrect(paintedY, getCurrentMap().getCellH());
			int finalX = paintedX - cellX*getCurrentMap().getCellW();
			int finalY = paintedY - cellY*getCurrentMap().getCellH();
			Cell currentCell = getCurrentMap().getCell(cellX, cellY);
			
			// If we clicked an invalid cell, return
			if (currentCell == null) {
				return;
			}
			
			// Validate the clicked cell
			if (!currentCell.isValid()) {
				currentCell.setTileset(getCurrentMap().getActiveTileset());
				currentCell.setValid(true);
			}
			
			// The target tile data (this is the tile which we clicked)
			TileData target = new TileData(cellX, cellY, finalX, finalY, 
					getLayerIndex(), currentCell.getTile(finalX, finalY));
			
			// The painted tile data (the selected tile in the tile picker panel)
			int autoType; // Get the autotile type of the selected tile
			if (selectedTiles.start.y < 0) {
				autoType = selectedTiles.start.x + ((selectedTiles.start.y+4)*8);
			} else {
				autoType = -1;
			}
			// We generate a pseudo tile data of this tile
			TileData painted = new TileData(-1, -1, -1, -1, getLayerIndex(),
					autoType, -1, selectedTiles.start.x, selectedTiles.start.y);
			
			// If the target and the painted is the same kind of tile, return
			if (painted.sameTypeOfTileAs(target)) {
				System.out.println("Same type of tile is clicked.");
				return;
			}
			
			// The queue of tiles we need to check, and a table to mark already checked tiles
			ArrayList<TileData> queue = new ArrayList<TileData>();
			queue.add(target);
			boolean[][] markedTiles = new boolean[getCurrentMap().getCellW()][getCurrentMap().getCellH()];
			
			int iterations = 0;
			
			// Loop until the queue is empty
			while (!queue.isEmpty()) {
				iterations++;
				// Set this tile to the selected tile
				TileData current = queue.remove(0);
				this.addToEntry(currentCell, cellX, cellY, current.tx, current.ty, getLayerIndex());
				currentCell.setTileData(current.tx, current.ty, painted);
				// Add adjacent tiles to queue
				int chkX; // The currently checked tile X coordinate inside the cell
				int chkY; // The currently checked tile Y coordinate inside the cell
				for (int x = -1; x < 2; x++) {
					for (int y = -1; y < 2; y++) {
						// Skip this iteration if its diagonal
						if (x != 0 && y != 0) { continue; }
						// Set the checked tile coordinates
						chkX = current.tx + x;
						chkY = current.ty + y;
						// If the checked tile is inside the cell bounds, and not marked
						if (chkX >= 0 && chkY >= 0 &&
								chkX < getCurrentMap().getCellW() &&
								chkY < getCurrentMap().getCellH() &&
								!markedTiles[chkX][chkY]) {
							/* Generate a tiledata from this tile and check if this tile has the same attributes
							 * as the clicked tile */
							TileData added = new TileData(cellX, cellY, current.tx + x, current.ty + y, current.layer,
									currentCell.getTile(current.tx + x, current.ty + y));
							// Add this tile to the queue, if its the same type of tile.
							if (added.sameTypeOfTileAs(target)) {
								queue.add(added);
							}
							// Mark this tile
							markedTiles[current.tx + x][current.ty + y] = true;
						}
						
					}
				}
			}
			// Add changes to the undoEntry
			if (undoEntry.isModified()) {
				getCurrentMap().addUndoEntry(undoEntry);
				undoEntry.clear();
			}
			System.out.println("Fill took " + iterations + " iterations to complete.");
		}
	}
	
	private void copyTile(int copyX, int copyY, TileCopyRegion copyTo, int cx, int cy) {
		int cellX = MathUtilities.divCorrect(copyX, getCurrentMap().getCellW());
		int cellY = MathUtilities.divCorrect(copyY, getCurrentMap().getCellH());
		int finalX = copyX - cellX*getCurrentMap().getCellW();
		int finalY = copyY - cellY*getCurrentMap().getCellH();
		Cell currentCell = getCurrentMap().getCell(cellX, cellY);
		if (currentCell != null) {
			for (int layer = 0; layer < 5; layer++) {
				copyTo.setCopiedTile(cx, cy, layer, 
						currentCell.getTile(finalX, finalY).constructTileData(layer));
			}
		}
	}
	
	private void paintTile(int paintedX, int paintedY, int tileX, int tileY, 
			int autoType, int index, int layer, boolean addToUndo, boolean updateAuto) {
		int cellX = MathUtilities.divCorrect(paintedX, getCurrentMap().getCellW());
		int cellY = MathUtilities.divCorrect(paintedY, getCurrentMap().getCellH());
		int finalX = paintedX - cellX*getCurrentMap().getCellW();
		int finalY = paintedY - cellY*getCurrentMap().getCellH();
		Cell currentCell = getCurrentMap().getCell(cellX, cellY);
		if (currentCell != null) {
			if (autoType > -1) {
				// Add this tile to the undo entry
				if (addToUndo) {
					addToEntry(currentCell, cellX, cellY, finalX, finalY, layer);
				}
				// Set this tile
				if (!updateAuto) {
					currentCell.setTileData(finalX, finalY, layer, autoType, index, tileX, tileY, false);
				} else {
					currentCell.setTileData(finalX, finalY, layer, autoType, true);
				}
			} else {
				// Add this tile to the undo entry
				if (addToUndo) {
					addToEntry(currentCell, cellX, cellY, finalX, finalY, layer);
				}
				// Set this tile
				currentCell.setTileData(finalX, finalY, layer, tileX, tileY, updateAuto);
			}
			
			if (!currentCell.isValid()) {
				currentCell.setTileset(getCurrentMap().getActiveTileset());
				currentCell.setValid(true);
			}
		}
	}
	
	private void paintTile(int paintedX, int paintedY, int tileX, int tileY, 
			int layer, boolean addToUndo, boolean updateAuto) {
		// Get the current autotile index
		int autoType = -1;
		if (tileY < 0) {
			autoType = tileX + ((tileY+4)*8);
		}
		paintTile(paintedX, paintedY, tileX, tileY, autoType, -1, layer, addToUndo, updateAuto);
	}
	
	private void addToEntry(Cell currentCell, int cellX, int cellY, int tileX, int tileY, int layer) {
		undoEntry.addCell(cellX + "x" + cellY);
		undoEntry.addTile(cellX, cellY, tileX, tileY, layer, 
				currentCell.getTile(tileX, tileY).getType(layer), 
				currentCell.getTile(tileX, tileY).getIndex(layer), 
				currentCell.getTile(tileX, tileY).getX(layer), 
				currentCell.getTile(tileX, tileY).getY(layer));
	}
	
}
