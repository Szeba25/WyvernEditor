package com.szeba.wyv.data.geometry;

import java.awt.Point;

/**
 * A box representing start, and end coordinates.
 * @author Szeba
 */
public class Box {

	public Point start;
	public Point end;
	
	public Box(int x1, int y1, int x2, int y2) {
		start = new Point(x1, y1);
		end = new Point(x2, y2);
	}
	
	public int getWidth() {
		return end.x - start.x;
	}
	
	public int getHeight() {
		return end.y - start.y;
	}
	
}
