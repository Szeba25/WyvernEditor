package com.szeba.wyv.widgets.panels.pickers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.utilities.ShapePainter;
import com.szeba.wyv.widgets.Widget;

public class ColorFrame extends Widget {

	private float red;
	private float green;
	private float blue;
	private float alpha;
	
	public ColorFrame(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
		setColor(0, 0, 0, 0);
	}
	
	public void setColor(int red, int green, int blue, int alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	public void setRed(float red) {
		this.red = red;
	}
	
	public void setBlue(float blue) {
		this.blue = blue;
	}
	
	public void setGreen(float green) {
		this.green = green;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	
	private void drawColor(SpriteBatch batch) {
		ShapePainter.drawFilledRectangle(batch, 
				0, 0, 0, 1, getX(), getY(), getW(), getH());
		ShapePainter.drawFilledRectangle(batch, 
				red, green, blue, alpha, getX(), getY(), getW(), getH());
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		this.drawBackground(batch);
		drawColor(batch);
		this.drawOutline(batch);
	}
	
}
