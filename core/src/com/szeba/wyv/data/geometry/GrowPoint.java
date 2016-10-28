package com.szeba.wyv.data.geometry;

import java.awt.Point;

/**
 * A growable box, used by the rectangle and copy tools. 
 * @author Szeba
 */
public class GrowPoint {

	private int growX;
	private int growY;
	private Point center;
	private boolean visible;
	
	public GrowPoint(Point center) {
		this.center = center;
		visible = false;
	}
	
	public int getStartX() {
		if (growX < 0) {
			return center.x + growX;
		} else {
			return center.x;
		}
	}
	
	public int getStartY() {
		if (growY < 0) {
			return center.y + growY;
		} else {
			return center.y;
		}
	}
	
	public int getEndX() {
		if (growX > 0) {
			return center.x + growX;
		} else {
			return center.x;
		}
	}
	
	public int getEndY() {
		if (growY > 0) {
			return center.y + growY;
		} else {
			return center.y;
		}
	}
	
	public int getWidth() {
		return getEndX() - getStartX() + 1;
	}
	
	public int getHeight() {
		return getEndY() - getStartY() + 1;
	}

	public Point getCenter() {
		return center;
	}
	
	public void setCenter(int x, int y) {
		center.x = x;
		center.y = y;
	}
	
	public void setGrowX(int growX) {
		this.growX = growX;
	}
	
	public void setGrowY(int growY) {
		this.growY = growY;
	}

	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public void reset() {
		growX = 0;
		growY = 0;
		center.x = 0;
		center.y = 0;
		setVisible(false);
	}
	
}
