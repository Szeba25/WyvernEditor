package com.szeba.wyv.widgets.panels.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.ShapePainter;

public class CoordinateMapPanel extends MapPanel {

	private int cellX;
	private int cellY;
	private int x;
	private int y;
	private int mx;
	private int my;
	
	public CoordinateMapPanel(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
		cellX = 0;
		cellY = 0;
		x = 0;
		y = 0;
		mx = 0;
		my = 0;
		this.setLayerIndex(5);
	}
	
	@Override
	public void drawActiveCell(SpriteBatch batch) {
		// Nope.
	}
	
	@Override
	public void extendedUpdate(int scrolled) {
		if (Wyvern.input.isButtonPressed(0)) {
			cellX = this.getCellCoord().x;
			cellY = this.getCellCoord().y;
			x = this.getTileCoord().x;
			y = this.getTileCoord().y;
			mx = this.getMapCoord().x;
			my = this.getMapCoord().y;
		}
	}
	
	@Override
	public void extendedDraw(SpriteBatch batch) {
		int blitX = getX()+(mx*getCurrentMap().getTileSize())-getCurrentMap().getOffX();
		int blitY = getY()+(my*getCurrentMap().getTileSize())-getCurrentMap().getOffY();
		int tileSize = getCurrentMap().getTileSize();
		if (blitX > getX()-tileSize && blitX < getX()+getW() 
				&& blitY > getY()-tileSize && blitY < getY()+getH()) {
			ShapePainter.drawFilledRectangle(batch, Palette.LIGHT_RED, blitX, blitY, tileSize, tileSize);
			ShapePainter.drawRectangle(batch, Palette.BLACK, blitX, blitY, tileSize, tileSize);
		}
		this.drawCoordinateData(batch, 1, cellX, cellY, x, y);
	}
	
	public void setCoordinates(int cellX, int cellY, int x, int y) {
		this.cellX = cellX;
		this.cellY = cellY;
		this.x = x;
		this.y = y;
		this.mx = (cellX * getCurrentMap().getCellW()) + x;
		this.my = (cellY * getCurrentMap().getCellH()) + y;
	}
	
	public int getCoordCellX() {
		return cellX;
	}
	
	public int getCoordCellY() {
		return cellY;
	}
	
	public int getCoordX() {
		return x;
	}
	
	public int getCoordY() {
		return y;
	}

}
