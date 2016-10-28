package com.szeba.wyv.widgets.panels.tileset;

import java.awt.Point;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.tiles.Tileset;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.ShapePainter;

public class TilesetPanel_DB extends TilesetPanel {

	private int currentTool;
	private String grabbedType;
	private String terrainTag;
	
	public TilesetPanel_DB(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
		
		currentTool = 0;
		grabbedType = null;
		terrainTag = "";
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		super.mainDraw(batch);
		if (activeTileset != null) {
			drawTools(batch, currentTool);
			drawGrid(batch);
		}
	}
	
	private void drawGrid(SpriteBatch batch) {
		for (int x = 0; x < 8; x++) {
			ShapePainter.drawStraigthLine(batch, Palette.WHITE05, getX() + (32*x), getY() - 128 
					- this.getTilesetOff(), false, 3200);
		}
		for (int y = -4; y < 100; y++) {
			ShapePainter.drawStraigthLine(batch, Palette.WHITE05, getX(), getY() - this.getTilesetOff() + (y*32), true, 256);
		}
	}

	@Override
	public void mainUpdate(int scrolled) {
		super.mainUpdate(scrolled);
		if (activeTileset != null) {
			updateTools(currentTool);
		}
	}
	
	public void setTerrainTag(String value) {
		this.terrainTag = value;
	}
	
	public void changeTool(int tool) {
		currentTool = tool;
	}
	
	private void updateTools(int tool) {
		switch(tool) {
		case 0:
			updateObsTool();
			break;
		case 1:
			updateObs4Tool();
			break;
		case 2:
			updateLayerTool();
			break;
		case 3:
			updateTerrainTool();
			break;
		case 4:
			updateAnimTool();
			break;
		}
	}
	
	/*
	 * Methods for updating the obstruction editor tool
	 */
	
	private void updateObsTool() {
		if (Wyvern.input.isButtonHold(0) && mouseInside() && !getScrollBarGrabbed()) {
			// Get clicked tile's coordinate
			Tileset ts = Wyvern.cache.getTileset(activeTileset);
			int x = getMovX();
			int y = getMovY();
			if (grabbedType == null) {
				grabbedType = "0";
				if (getStartingValue(x, y, ts.getObstructions(0)).equals("0")) {
					grabbedType = "1";
				}
			}
			setTableValue(x, y, ts, ts.getObstructions(0), grabbedType);
			setTableValue(x, y, ts, ts.getObstructions(1), grabbedType);
			setTableValue(x, y, ts, ts.getObstructions(2), grabbedType);
			setTableValue(x, y, ts, ts.getObstructions(3), grabbedType);
			
		} else if (!Wyvern.input.isButtonHold(0)) {
			grabbedType = null;
		}
	}

	/*
	 * Methods for updating the 4 way obstruction tool
	 */
	
	private void updateObs4Tool() {
		if (Wyvern.input.isButtonPressed(0) && mouseInside() && !getScrollBarGrabbed()) {
			// Get clicked tile's coordinate
			Tileset ts = Wyvern.cache.getTileset(activeTileset);
			int x = getMovX();
			int y = getMovY();
			int z = getRectPortion(x, y);
			if (getStartingValue(x, y, ts.getObstructions(z)).equals("1")) {
				setTableValue(x, y, ts, ts.getObstructions(z), "0");
			} else {
				setTableValue(x, y, ts, ts.getObstructions(z), "1");
			}
		}
	}

	private int getRectPortion(int mx, int my) {
		int rmX = Wyvern.input.getX();
		int rmY = Wyvern.input.getY();
		int ctX = getX() + mx*32 + 16;
		int ctY = getY() + (my*32) - getTilesetOff() + 16;
		if (ctY - rmY > 0) {
			if (ctX - rmX > ctY - rmY) {
				return 3;
			} else if (rmX - ctX > ctY - rmY) {
				return 1;
			}
			return 0;
		} else {
			if (ctX - rmX > rmY - ctY) {
				return 3;
			} else if (rmX - ctX > rmY - ctY) {
				return 1;
			}
			return 2;
		}
	}
	
	/*
	 * Methods for updating the layer tool
	 */
	
	private void updateLayerTool() {
		if (Wyvern.input.isButtonHold(0) && mouseInside() && !getScrollBarGrabbed()) {
			// Get clicked tile's coordinate
			Tileset ts = Wyvern.cache.getTileset(activeTileset);
			int x = getMovX();
			int y = getMovY();
			if (grabbedType == null) {
				grabbedType = "0";
				if (getStartingValue(x, y, ts.getLayerData()).equals("0")) {
					grabbedType = "1";
				}
			}
			setTableValue(x, y, ts, ts.getLayerData(), grabbedType);
			
		} else if (!Wyvern.input.isButtonHold(0)) {
			grabbedType = null;
		}
	}

	/*
	 * Methods for updating the terrain editor tool
	 */
	
	private void updateTerrainTool() {
		if (Wyvern.input.isButtonHold(0) && mouseInside() && !getScrollBarGrabbed()) {
			// Get clicked tile's coordinate
			Tileset ts = Wyvern.cache.getTileset(activeTileset);
			int x = getMovX();
			int y = getMovY();
			
			setTableValue(x, y, ts, ts.getTerrainData(), terrainTag);
			
		} else if (!Wyvern.input.isButtonHold(0)) {
			grabbedType = null;
		}
	}

	/*
	 * Methods for updating the animation editor tool
	 */
	
	private void updateAnimTool() {
		if (Wyvern.input.isButtonPressed(0) && mouseInside() && !getScrollBarGrabbed()) {
			// Get clicked tile's coordinate
			Tileset ts = Wyvern.cache.getTileset(activeTileset);
			int x = getMovX();
			int y = getMovY();

			ts.setChanged(true);
			
			int pointCounter = 0;
			boolean delete = false;
			for (ArrayList<Point> points : ts.getAnimations()) {
				if (points.get(0).x == x && points.get(0).y == y) {
					// Delete this animation
					delete = true;
					break;
				}
				pointCounter++;
			}
			if (delete) {
				ts.getAnimations().remove(pointCounter);
				return;
			}
			
			// No matching animation was found, so add one
			ArrayList<Point> frames = new ArrayList<Point>();
			frames.add(new Point(x, y));
			frames.add(new Point(getNewX(x+1), y));
			frames.add(new Point(getNewX(x+2), y));
			frames.add(new Point(getNewX(x+3), y));
			ts.addAnimation(frames);
			
		}
	}

	private int getNewX(int x) {
		if (x > 7) {
			x-=8;
		}
		return x;
	}
	
	private void drawTools(SpriteBatch batch, int tool) {
		switch(tool) {
		case 0:
			drawObsTool(batch);
			break;
		case 1:
			drawObs4Tool(batch);
			break;
		case 2:
			drawLayerTool(batch);
			break;
		case 3:
			drawTerrainTool(batch);
			break;
		case 4:
			drawAnimTool(batch);
			break;
		}
	}

	/*
	 * Methods for drawing the obstruction editor tool
	 */
	
	private void drawObsTool(SpriteBatch batch) {
		Tileset ts = Wyvern.cache.getTileset(activeTileset);
		
		int mx = getMovX_un();
		int my = getMovY_un();
		
		for (int x = 0; x < 8; x++) {
			for (int y = (this.getTilesetOff()-31)/32; y < (this.getTilesetOff()+getH()+31)/32; y++) {
				drawTileObsData(batch, ts, x, y, x, y, mx, my);
			}
		}
	}
	
	private void drawTileObsData(SpriteBatch batch, Tileset ts, int tx, int ty, int bx, int by, int mx, int my) {
		if (bx != mx || by != my) {
			batch.setColor(Palette.WHITE05);
		} else {
			batch.setColor(Palette.WHITE);
		}
		if (getStartingValue(tx, ty, ts.getObstructions(0)).equals("1") || 
				getStartingValue(tx, ty, ts.getObstructions(1)).equals("1") ||
				getStartingValue(tx, ty, ts.getObstructions(2)).equals("1") ||
				getStartingValue(tx, ty, ts.getObstructions(3)).equals("1")) {
			// Obstructed!
			batch.draw(Wyvern.cache.get_tileObstructed(), getX()+(bx*32), getY()+(by*32)-getTilesetOff());
		} else {
			batch.draw(Wyvern.cache.get_tileWalkable(), getX()+(bx*32), getY()+(by*32)-getTilesetOff());
		}
		batch.setColor(Palette.BATCH);
	}
	
	/*
	 * Methods for drawing the 4 way obstructions tool
	 */
	
	private void drawObs4Tool(SpriteBatch batch) {
		Tileset ts = Wyvern.cache.getTileset(activeTileset);
		
		int mx = getMovX_un();
		int my = getMovY_un();
		
		for (int x = 0; x < 8; x++) {
			for (int y = (this.getTilesetOff()-31)/32; y < (this.getTilesetOff()+getH()+31)/32; y++) {
				drawTileObs4Data(batch, ts, 0, x, y, x, y, mx, my);
				drawTileObs4Data(batch, ts, 1, x, y, x, y, mx, my);
				drawTileObs4Data(batch, ts, 2, x, y, x, y, mx, my);
				drawTileObs4Data(batch, ts, 3, x, y, x, y, mx, my);
			}
		}
	}
	
	private void drawTileObs4Data(SpriteBatch batch, Tileset ts, 
			int z, int tx, int ty, int bx, int by, int mx, int my) {
		if (bx != mx || by != my) {
			batch.setColor(Palette.WHITE05);
		} else if (z == getRectPortion(mx, my)) {
			batch.setColor(Palette.WHITE);
		} else {
			batch.setColor(Palette.WHITE05);
		}
		if (getStartingValue(tx, ty, ts.getObstructions(z)).equals("1")) {
			// Obstructed!
			batch.draw(Wyvern.cache.get_tileDot(z), getX()+(bx*32), getY()+(by*32)-getTilesetOff());
		} else {
			batch.draw(Wyvern.cache.get_tileArrow(z), getX()+(bx*32), getY()+(by*32)-getTilesetOff());
		}
		batch.setColor(Palette.BATCH);
	}
	
	/*
	 * Methods for drawing the layer editor tool
	 */
	
	private void drawLayerTool(SpriteBatch batch) {
		Tileset ts = Wyvern.cache.getTileset(activeTileset);
		
		int mx = getMovX_un();
		int my = getMovY_un();
		
		for (int x = 0; x < 8; x++) {
			for (int y = (this.getTilesetOff()-31)/32; y < (this.getTilesetOff()+getH()+31)/32; y++) {
				drawTileLayerData(batch, ts, x, y, x, y, mx, my);
			}
		}
	}
	
	private void drawTileLayerData(SpriteBatch batch, Tileset ts, int tx, int ty, int bx, int by, int mx, int my) {
		
		String layerValue = getStartingValue(tx, ty, ts.getLayerData());
		String obstValue0 = getStartingValue(tx, ty, ts.getObstructions(0));
		String obstValue1 = getStartingValue(tx, ty, ts.getObstructions(1));
		String obstValue2 = getStartingValue(tx, ty, ts.getObstructions(2));
		String obstValue3 = getStartingValue(tx, ty, ts.getObstructions(3));
		boolean faded = false;
		
		if (bx != mx || by != my) {
			faded = true;
		} else {
			faded = false;
		}
		
		// Determine color
		if (obstValue0.equals("1") || obstValue1.equals("1") || obstValue2.equals("1") || obstValue3.equals("1")) {
			if (faded) {
				batch.setColor(Palette.LIGHT_RED05);
			} else {
				batch.setColor(Palette.LIGHT_RED);
			}
		} else {
			if (faded) {
				batch.setColor(Palette.WHITE05);
			} else {
				batch.setColor(Palette.WHITE);
			}
		}
		
		// Draw the icon
		if (layerValue.equals("1")) {
			// Top!
			batch.draw(Wyvern.cache.get_tileTop(), getX()+(bx*32), getY()+(by*32)-getTilesetOff());
		} else {
			batch.draw(Wyvern.cache.get_tileBottom(), getX()+(bx*32), getY()+(by*32)-getTilesetOff());
		}
		batch.setColor(Palette.BATCH);
	}

	/*
	 * Methods for drawing the terrain editor tool
	 */
	
	private void drawTerrainTool(SpriteBatch batch) {
		Tileset ts = Wyvern.cache.getTileset(activeTileset);
		
		int mx = getMovX_un();
		int my = getMovY_un();
		
		for (int x = 0; x < 8; x++) {
			for (int y = (this.getTilesetOff()-31)/32; y < (this.getTilesetOff()+getH()+31)/32; y++) {
				
				if (mx == x && my == y) {
					batch.setColor(0.1f, 0.1f, 0.1f, 0.75f);
					batch.draw(Wyvern.cache.getFiller(), getX()+(x*32), getY()+(y*32)-getTilesetOff(), 32, 32);
					batch.setColor(Palette.BATCH);
				}
				
				FontUtilities.print(batch, getStartingValue(x, y, ts.getTerrainData()), 
						getX()+(x*32), getY()+(y*32)-getTilesetOff());
			}
		}
	}
	
	/*
	 * Methods for drawing the animation editor tool
	 */
	
	private void drawAnimTool(SpriteBatch batch) {
		Tileset ts = Wyvern.cache.getTileset(activeTileset);
		
		int x = 0;
		int y = 0;
		
		for (ArrayList<Point> points : ts.getAnimations()) {
			x = points.get(0).x;
			y = points.get(0).y;
			FontUtilities.print(batch, "ANM ----------------->",
					getX()+(x*32)+1, getY()+(y*32)+9-getTilesetOff());
		}
		
	}
	
	/**
	 * Get the value of the given tile from the requested table.
	 */
	public String getStartingValue(int x, int y, String[][] table) {
		if (y < 0) {
			switch (y) {
			case -4:
				return table[8][x*6];
			case -3:
				return table[16][x*6];
			case -2:
				return table[32][x*6];
			case -1:
				return table[40][x*6];
			}
		} else if (y > 47) {
			return table[x+24][y-48];
		} else {
			return table[x][y];
		}
		System.out.println("We got *ehh* from a tileset entry... " + x +"/" + y);
		return "ehh...";
	}
	
	/**
	 * Edit a value in a tileset table, based on the panel coordinates
	 */
	public void setTableValue(int x, int y, Tileset ts, String[][] table, String value) {
		ts.setChanged(true);
		if (y < 0) {
			// Autotile!
			int start = 0;
			switch (y) {
			case -4:
				start = 8;
				break;
			case -3:
				start = 16;
				break;
			case -2:
				start = 32;
				break;
			case -1:
				start = 40;
				break;
			}
			for (int tx = start; tx < start+8; tx++) {
				for (int ty = x*6; ty < (x*6)+6; ty++) {
					table[tx][ty] = value;
				}
			}
		} else if (y > 47) {
			x += 24;
			y -= 48;
			table[x][y] = value;
		} else {
			table[x][y] = value;
		}
	}
	
}
