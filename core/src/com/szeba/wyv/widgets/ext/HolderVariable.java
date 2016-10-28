package com.szeba.wyv.widgets.ext;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.Text;

/**
 * Text designed to hold a variable 
 * @author Szeba
 */

public class HolderVariable extends Text {
	
	String displayedEventString;
	
	public HolderVariable(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h, "");
		displayedEventString = "";
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		// VERY slow and ugly as hell...
		displayedEventString = super.getText() + " (" + getVariableName() + ")";
		
		if (FontUtilities.getBounds(displayedEventString).width > this.getW() - 5) {
			this.setTooltip(displayedEventString);
			displayedEventString = StringUtilities.cropString(
					displayedEventString, 
					this.getW()-5);
		} else {
			this.setTooltip("");
		}
		
		drawBackground(batch);
		FontUtilities.print(batch, displayedEventString, getX()+2, getY()+2);
		drawOutline(batch);
	}
	
	public String getVariableName() {
		String[] str = StringUtilities.safeSplit(super.getText(), ": ");
		if (str.length == 2) {
			int id = Integer.parseInt(str[1]);
			// If this variable exists, draw its name.
			if (variableExists(str[0], id)) {
				return Wyvern.database.var.entries.get(str[0]).get(id);
			}
		}
		return "";
	}
	
	private boolean variableExists(String category, int id) {
		if (Wyvern.database.var.entries.containsKey(category) &&
				id >= 0 && id < Wyvern.database.var.entries.get(category).size()) {
			return true;
		} else {
			return false;
		}
	}

}
