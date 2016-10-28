package com.szeba.wyv.widgets.ext;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.ShapePainter;
import com.szeba.wyv.widgets.Widget;

public class BulletBoard extends Widget {

	private int selected;
	private ArrayList<String> elements;
	
	public BulletBoard(int ox, int oy, int rx, int ry, int w, ArrayList<String> ar) {
		super(ox, oy, rx, ry, w, 0);
		selected = 0;
		elements = ar;
		setH(elements.size()*20);
	}

	@Override
	public void mainDraw(SpriteBatch batch) {
		super.mainDraw(batch);
		drawElements(batch);
		if (mouseInside()) {
			drawBullet(batch, true, getMousePos(), Palette.WIDGET_HIGHLIGHT);
		}
		for (int i = 0; i < elements.size(); i++) {
			drawBullet(batch, false, i, Palette.LIST_MARK);
		}
		drawBullet(batch, true, selected, Palette.LIST_MARK);
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		if (Wyvern.input.isLeftButtonReleased() && mouseInside()) {
			selected = getMousePos();
		}
	}
	
	public int getSelectedID() {
		return selected;
	}
	
	public void setSelectedID(int id) {
		selected = id;
	}
	
	public String getSelected() {
		return elements.get(selected);
	}

	private int getMousePos() {
		return (Wyvern.input.getY()-getY())/20;
	}
	
	private void drawElements(SpriteBatch batch) {
		for (int i = 0; i < elements.size(); i++) {
			FontUtilities.print(batch, elements.get(i), getX() + 18, getY() + 4 + i*20);
		}
	}
	
	private void drawBullet(SpriteBatch batch, boolean filled, int pos, Color color) {
		if (filled) {
			ShapePainter.drawFilledRectangle(batch, color, 
					getX() + 5, getY() + 5 + pos*20, 10, 10);
		} else {
			ShapePainter.drawRectangle(batch, color, 
					getX() + 5, getY() + 5 + pos*20, 10, 10);
		}
	}
	
}
