package com.szeba.wyv.utilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;

/** 
 * Basic shape painter for the editor
 * @author Szeba
 */
public final class ShapePainter {

	private ShapePainter() {}
	
	public static void drawStraigthLine(SpriteBatch batch, Color color, int x, int y, boolean horizontal, int length) {
		batch.setColor(color);
		if (horizontal) {
			batch.draw(Wyvern.cache.getFiller(), x, y, length, 1);
		} else {
			batch.draw(Wyvern.cache.getFiller(), x, y, 1, length);
		}
		batch.setColor(Palette.BATCH);
	}
	
	public static void drawRectangle(SpriteBatch batch, Color color, int x, int y, int w, int h) {
		batch.setColor(color);
		batch.draw(Wyvern.cache.getFiller(), x, y, 1, h);
		batch.draw(Wyvern.cache.getFiller(), x, y, w, 1);
		batch.draw(Wyvern.cache.getFiller(), x+w-1, y, 1, h);
		batch.draw(Wyvern.cache.getFiller(), x, y+h-1, w, 1);
		batch.setColor(Palette.BATCH);
	}
	
	public static void drawRectangle(SpriteBatch batch, float red, float green, float blue, float alpha, 
			int x, int y, int w, int h) {
		batch.setColor(red, green, blue, alpha);
		batch.draw(Wyvern.cache.getFiller(), x, y, 1, h);
		batch.draw(Wyvern.cache.getFiller(), x, y, w, 1);
		batch.draw(Wyvern.cache.getFiller(), x+w-1, y, 1, h);
		batch.draw(Wyvern.cache.getFiller(), x, y+h-1, w, 1);
		batch.setColor(Palette.BATCH);
	}

	public static void drawFilledRectangle(SpriteBatch batch, Color brdColor,
			int x, int y, int w, int h) {
		batch.setColor(brdColor);
		batch.draw(Wyvern.cache.getFiller(), x, y, w, h);
		batch.setColor(Palette.BATCH);
	}
	
	public static void drawFilledRectangle(SpriteBatch batch, float red, float green, float blue, float alpha,
			int x, int y, int w, int h) {
		batch.setColor(red, green, blue, alpha);
		batch.draw(Wyvern.cache.getFiller(), x, y, w, h);
		batch.setColor(Palette.BATCH);
	}
	
}
