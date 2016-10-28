package com.szeba.wyv.widgets.panels.tileset;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.utilities.MathUtilities;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.widgets.ext.ScrollWidget;

public class TilesetPanel extends ScrollWidget {
	
	protected String activeTileset;
	
	public TilesetPanel(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
		
		activeTileset = null;
	}

	@Override
	public Color getBkgColor() {
		return Palette.WIDGET_BKG2;
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		// Draw background
		drawBackground(batch);
		// Draw tileset
		if (activeTileset != null) {
			drawTileset(batch);
		}
		// Draw outlines
		drawOutline(batch);
	}

	@Override
	public void mainUpdate(int scrolled) {
		if (activeTileset != null) {
			scrollList(scrolled, 3, 32);
			scrollBarUpdate();
		}
	}

	public String getActiveTileset() {
		return activeTileset;
	}

	public void setActiveTileset(String activeTileset) {
		this.activeTileset = activeTileset;
	}

	protected int getTilesetOff() {
		return this.getPixOff()-128;
	}
	
	protected int getMovX_un() {
		int dividend = (Wyvern.input.getX() - getX());
		int divisor = 32;
		return MathUtilities.divCorrect(dividend, divisor);
	}
	
	protected int getMovY_un() {
		int dividend = (Wyvern.input.getY() - getY() + getTilesetOff());
		int divisor = 32;
		return MathUtilities.divCorrect(dividend, divisor);
	}
	
	protected int getMovX() {
		int dividend = (Wyvern.input.getX() - getX());
		int divisor = 32;
		return MathUtilities.boundedVariable(MathUtilities.divCorrect(dividend, divisor), 0, 0, 7);
	}
	
	protected int getMovY() {
		int dividend = (Wyvern.input.getY() - getY() + getTilesetOff());
		int divisor = 32;
		return MathUtilities.boundedVariable(MathUtilities.divCorrect(dividend, divisor), 0, -4, 95);
	}
	
	private void drawTileset(SpriteBatch batch) {
		// Draw the autotiles
		for (int i = 0; i < 32; i++) {
			batch.draw(Wyvern.cache.getTileset(activeTileset).getAutotileIcon(i),
					getX() + (i*32) - ((i/8)*256), 
					getY() - 128 + ((i/8) * 32) - getTilesetOff(), 
					32, 32);
		}
		// Draw the main tiles
		batch.draw(Wyvern.cache.getTileset(activeTileset).getTiles1(), getX(), getY()-getTilesetOff(), 256, 1536);
		batch.draw(Wyvern.cache.getTileset(activeTileset).getTiles2(), getX(), getY()-getTilesetOff()+1536, 256, 1536);
	}

}
