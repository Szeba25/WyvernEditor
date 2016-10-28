package com.szeba.wyv.widgets.ext.list;

import java.awt.Point;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.event.Event;
import com.szeba.wyv.data.maps.Cell;
import com.szeba.wyv.data.maps.GameMap;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.ShapePainter;

public class EventList extends ButtonList {
	
	private int lastx;
	private int lasty;
	private String lastmap;
	private boolean comev;
	
	public EventList(int ox, int oy, int rx, int ry, int w, int hval, boolean comev) {
		super(ox, oy, rx, ry, w, hval, null, false);
		
		this.comev = comev;
		
		// We must call this to list the events.
		// Now this runs twice with dynamic lists, but better be safe than sorry.
		this.refresh();
	}
	
	@Override
	public void overDraw(SpriteBatch batch) {
		if (!comev) {
			FontUtilities.print(batch, lastmap+" at "+lastx+"/"+lasty, getX()+2, getY()+getH()+1);
		} else {
			FontUtilities.print(batch, "Common event list", getX()+2, getY()+getH()+1);
		}
		ShapePainter.drawRectangle(batch, getPassiveBrdColor(), getX(), getY()+getH(), getW(), 16);
	}
	
	public void refresh() {
		// List events.
		ArrayList<ListElement> elements = new ArrayList<ListElement>();
		
		if (comev) {
			// List common events
			
			if (Wyvern.database.ce == null) {
				this.setElements(elements);
				return;
			}
			
			for (int i = 0; i < Wyvern.database.ce.events.size(); i++) {
				Event ev = Wyvern.database.ce.events.get(i);
				elements.add(new ListElement(ev.getSigID() + ": " + ev.getName(), ev.getSigID()));
			}
			
			this.setElements(elements);
			
		} else {
			// List map events
			
			GameMap map = Wyvern.cache.getCurrentMap();
			Point cc = Wyvern.cache.getCurrentCellCoord();
			
			elements.add(new ListElement("This event"));
			elements.add(new ListElement("Player"));
			
			if (map == null) {
				lastx = 0;
				lasty = 0;
				lastmap = "null";
				this.setElements(elements);
				return;
			}
			
			lastx = cc.x;
			lasty = cc.y;
			lastmap = map.getName();
			
			for (int x = cc.x-1; x < cc.x+2; x++) {
				for (int y = cc.y-1; y< cc.y+2; y++) {
					Cell cell = map.getOrLoadCell(x, y);
					if (cell != null) {
						for (Event ev : cell.getEvents().values()) {
							// Name event entries
							String listed = "";
							if (ev.getReference() != null) {
								listed = ev.getSigID() + "(c): " + ev.getReference().getName();
							} else {
								listed = ev.getSigID() + ": " + ev.getName();
							}
							elements.add(new ListElement(listed, ev.getSigID()));
						}
					}
				}
			}
			
			this.setElements(elements);
		}

	}

}
