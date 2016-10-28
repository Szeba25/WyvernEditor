package com.szeba.wyv.utilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.DisplayedText;

public final class FontUtilities {

	private static GlyphLayout layout = new GlyphLayout();
	private static TextBounds bounds = new TextBounds(0, 0);
	
	private FontUtilities() { }
	
	public static void print(SpriteBatch batch, String text, int x, int y) {
		Wyvern.cache.getFont().setColor(Palette.FONT);
		Wyvern.cache.getFont().draw(batch, text, x, y-5);
	}
	
	public static void print(SpriteBatch batch, Color color, float alpha, String text, int x, int y) {
		if (color != null) {
			Wyvern.cache.getFont().setColor(color.r, color.g, color.b, alpha);
		} else {
			Wyvern.cache.getFont().setColor(Palette.FONT.r, Palette.FONT.g, Palette.FONT.b, alpha);
		}
		Wyvern.cache.getFont().draw(batch, text, x, y-5);
	}
	
	public static void print(SpriteBatch batch, Color color, String text, int x, int y) {
		if (color != null) {
			Wyvern.cache.getFont().setColor(color);
		} else {
			Wyvern.cache.getFont().setColor(Palette.FONT);
		}
		Wyvern.cache.getFont().draw(batch, text, x, y-5);
	}
	
	public static void displayedTextPrint(SpriteBatch batch, DisplayedText text, int x, int y) {
		Wyvern.cache.getFont().setColor(Palette.FONT);
		for (int z = 0; z < text.lines.size(); z++) {
			Wyvern.cache.getFont().draw(batch, text.lines.get(z), x, y+(z*17));
		}
	}
	
	public static void wrappedPrint(SpriteBatch batch, String text, int x, int y, int width) {
		Wyvern.cache.getFont().setColor(Palette.FONT);
		Wyvern.cache.getFont().draw(batch, text, x, y-5, width, Align.left, true);
	}
	
	public static void wrappedPrint(SpriteBatch batch, Color color, String text, int x, int y, int width) {
		if (color != null) {
			Wyvern.cache.getFont().setColor(color);
		} else {
			Wyvern.cache.getFont().setColor(Palette.FONT);
		}
		Wyvern.cache.getFont().draw(batch, text, x, y-5, width, Align.left, true);
	}
	
	public static TextBounds getBounds(String text) {
		layout.setText(Wyvern.cache.getFont(), text);
		bounds.width = (int) layout.width;
		bounds.height = (int) layout.height;
		return bounds;
	}
	
	public static TextBounds getBounds(String text, int start, int end) {
		layout.setText(Wyvern.cache.getFont(), text.subSequence(start, end));
		bounds.width = (int) layout.width;
		bounds.height = (int) layout.height;
		return bounds;
	}
	
}
