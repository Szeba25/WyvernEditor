package com.szeba.wyv.widgets.panels.tileset;

import java.awt.Point;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.geometry.Box;
import com.szeba.wyv.utilities.MathUtilities;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.ShapePainter;

public class TilesetPanel_Map extends TilesetPanel {

	private Point mainTile;
	private Point grabbedTile;
	private Box selectedTiles;
	private boolean grabSelection;
	private final double orgGrabDelay;
	private double grabDelay;
	
	public TilesetPanel_Map(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
		
		mainTile = new Point(0, 0);
		grabbedTile = new Point(0, 0);
		selectedTiles = new Box(0, 0, 0, 0);
		grabSelection = false;
		orgGrabDelay = 0.10;
		grabDelay = orgGrabDelay;
	}

	@Override
	public void mainUpdate(int scrolled) {
		super.mainUpdate(scrolled);
		if (activeTileset != null) {
			updateSelection();
		}
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		super.mainDraw(batch);
		if (activeTileset != null) {
			drawTilesetSelection(batch);
		}
	}
	
	public int getSelectionWidth() {
		return selectedTiles.end.x - selectedTiles.start.x + 1;
	}

	public int getSelectionHeight() {
		return selectedTiles.end.y - selectedTiles.start.y + 1;
	}
	
	public Box getSelectedTiles() {
		return selectedTiles;
	}
	
	private void drawTilesetSelection(SpriteBatch batch) {
		if (grabSelection) {
			drawActiveSelection(batch);
		} else {
			ShapePainter.drawFilledRectangle(batch, getHighColor(), 
					getX() + selectedTiles.start.x*32, getY() + selectedTiles.start.y*32 - getTilesetOff(), 
					(selectedTiles.end.x - selectedTiles.start.x + 1)*32, 
					(selectedTiles.end.y - selectedTiles.start.y + 1)*32);
		}
		ShapePainter.drawRectangle(batch, Palette.TILES_MAINTILE,
				getX() + mainTile.x*32, getY() + (mainTile.y*32) - getTilesetOff(), 32, 32);
	}
	
	private void drawActiveSelection(SpriteBatch batch) {
		// Get new tile X and tile Y values
		int currentX = getMovX();
		int currentY = getMovY();
		
		int startX = MathUtilities.getSmaller(currentX, grabbedTile.x);
		int startY = MathUtilities.getSmaller(currentY, grabbedTile.y);
		int endX = MathUtilities.getBigger(currentX, grabbedTile.x);
		int endY = MathUtilities.getBigger(currentY, grabbedTile.y);
		
		ShapePainter.drawFilledRectangle(batch, getHighColor(), 
				getX() + startX*32, getY() + startY*32 - getTilesetOff(), 
				(endX - startX + 1)*32, 
				(endY - startY + 1)*32);
	}
	
	private void updateSelection() {
		if (Wyvern.input.isButtonPressed(0) && mouseInside() && !getScrollBarGrabbed()) {
			// Get the x and y coordinate value
			mainTile.x = getMovX();
			mainTile.y = getMovY();
			// Reset the box
			generateSelectionBox();
		} else if (Wyvern.input.isButtonHold(0) && mouseInside() && !getScrollBarGrabbed()) {
			if (grabDelay > 0.0) {
				grabDelay -= Wyvern.getDelta();
			} else {
				// Grab the selection
				grabbedTile.x = mainTile.x;
				grabbedTile.y = mainTile.y;
				grabSelection = true;
			}
		} else if (!Wyvern.input.isButtonHold(0) && grabSelection) {
			// Release
			generateSelectionBox();
			grabSelection = false;
		} else {
			grabDelay = orgGrabDelay;
		}
	}

	private void generateSelectionBox() {
		// Get new tile X and tile Y values
		int dropX = getMovX();
		int dropY = getMovY();

		selectedTiles.start.x = MathUtilities.getSmaller(dropX, mainTile.x);
		selectedTiles.start.y = MathUtilities.getSmaller(dropY, mainTile.y);
		selectedTiles.end.x = MathUtilities.getBigger(dropX, mainTile.x);
		selectedTiles.end.y = MathUtilities.getBigger(dropY, mainTile.y);
		
		mainTile.x = selectedTiles.start.x;
		mainTile.y = selectedTiles.start.y;
	}
	
}
