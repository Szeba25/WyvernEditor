package com.szeba.wyv.widgets.ext;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.utilities.CommandStringGen;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.Separator;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.Text;

/**
 * Text designed to hold a database entry 
 * @author Szeba
 */

public class HolderDBEntry extends Text {
	
	public HolderDBEntry(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h, "");
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		super.mainDraw(batch);
		
		// Get the original strings width for reference
		int width = FontUtilities.getBounds(super.getText()).width;
		
		// Get the database entry name from the database
		FontUtilities.print(batch, 
				"(" + getEntryName() + ")",
				getX()+2+width+4, getY()+2);
	}
	
	public String getEntryName() {
		String[] str = StringUtilities.safeSplit(super.getText(), ": ");
		if (str.length == 2) {
			int id = Integer.parseInt(str[1]);
			// If this entry exists, draw its name.
			if (entryExists(str[0], id)) {
				
				// We must handle non ascii names.
				String name = Wyvern.database.ent.entryData.get(str[0]).getItem(id);
				name = StringUtilities.safeSplit(name, Separator.dataUnit)[0];
				if (CommandStringGen.isArrayText(name)) {
					name = CommandStringGen.generateArrayText(name);
				}
				
				return name;
			}
		}
		return "";
	}
	
	public int getID() {
		if (super.getText().length() > 0) {
			String[] str = StringUtilities.safeSplit(super.getText(), ": ");
			return Integer.parseInt(str[1]);
		}
		return -1;
	}
	
	private boolean entryExists(String category, int id) {
		if (Wyvern.database.ent.entryData.containsKey(category) &&
				id >= 0 && id < Wyvern.database.ent.entryData.get(category).getItems().size()) {
			return true;
		} else {
			return false;
		}
	}

}
