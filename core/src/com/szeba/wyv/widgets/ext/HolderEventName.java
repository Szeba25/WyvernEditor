package com.szeba.wyv.widgets.ext;

import java.awt.Point;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.event.Event;
import com.szeba.wyv.data.maps.Cell;
import com.szeba.wyv.data.maps.GameMap;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.widgets.Text;

/**
 * Text holder designed to hold event IDs
 * @author Szeba
 */

public class HolderEventName extends Text {

	public HolderEventName(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h, "");
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		super.mainDraw(batch);
		// Get text width for reference
		int width = FontUtilities.getBounds(super.getText()).width;
		
		GameMap map = Wyvern.cache.getCurrentMap();
		Point cc = Wyvern.cache.getCurrentCellCoord();
		
		if (map == null) {
			return;
		}
		
		if (super.getText().equals("This event") || super.getText().equals("Player") || super.getText().length() == 0) {
			return;
		}
		
		String id = super.getText();
		
		for (int x = cc.x-1; x < cc.x+2; x++) {
			for (int y = cc.y-1; y< cc.y+2; y++) {
				Cell cell = map.getOrLoadCell(x, y);
				if (cell != null) {
					for (Event ev : cell.getEvents().values()) {
						// Name event entries
						if (ev.getSigID().equals(id)) {
							if (ev.getReference() != null) {
								FontUtilities.print(batch, Palette.LIGHT_RED, ev.getReference().getName(), 
										getX()+2+4+width, getY()+2);
							} else {
								FontUtilities.print(batch, Palette.LIGHT_RED, ev.getName(), 
										getX()+2+4+width, getY()+2);
							}
							return;
						}
					}
				}
			}
		}
		
	}

}
