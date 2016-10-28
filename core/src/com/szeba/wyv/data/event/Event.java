package com.szeba.wyv.data.event;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.ShapePainter;

/**
 * An event represents any kind of game object we can make.
 */
public class Event {

	// Reference?
	private Event reference;
	// Name
	private String name;
	private boolean editableName;
	//private int id = -1; // We remove integer id, and replace it with signature id.
	private String signature_id = "-1";
	// Position on the map
	private int x;
	private int y;

	// Event page data
	private ArrayList<Page> pages;
	
	public Event(int x, int y, boolean editableName, boolean requestID) {
		reference = null;
		
		name = "";
		this.editableName = editableName;
		if (requestID) {
			this.signature_id = Wyvern.getNextEventID();
		}
		this.x = x;
		this.y = y;
		
		pages = new ArrayList<Page>();
		pages.add(new Page());
	}
	
	public void setReference(String sigid) {
		// Common event references have no signature in their sigid.
		int id = Integer.parseInt(sigid);
		if (id < Wyvern.database.ce.events.size()) {
			reference = Wyvern.database.ce.events.get(id);
		} else {
			reference = null;
			this.signature_id = "-1";
		}
	}
	
	public Event getReference() {
		return reference;
	}
	
	/** 
	 * Draw spriteset on the map.
	 */
	public void draw(SpriteBatch batch, int page, int bx, int by, int tileSize, boolean fullDraw, float alpha) {
		// Handle references.
		Event ev = this;
		Color c1 = Palette.EVENT_BKG_ACTIVE;
		Color c2 = Palette.EVENT_BKG_PASSIVE;
		if (reference != null) {
			ev = reference;
			c1 = Palette.COMMONEVENT_BKG_ACTIVE;
			c2 = Palette.COMMONEVENT_BKG_PASSIVE;
		} else if (signature_id.equals("-1")) {
			c1 = Palette.LIGHT_RED;
			c2 = Palette.LIGHT_RED05;
		}
		// Draw background
		if (fullDraw) {
			ShapePainter.drawFilledRectangle(batch, c1.r, c1.g, c1.b, c1.a*alpha, 
					bx, by, tileSize, tileSize);
		} else {
			ShapePainter.drawFilledRectangle(batch, c2.r, c2.g, c2.b, c2.a*alpha,
					bx, by, tileSize, tileSize);
		}
		if (!fullDraw) {
			batch.setColor(1, 1, 1, alpha*0.5f);
		} else {
			batch.setColor(1, 1, 1, alpha);
		}
		// Draw the map tile icon on the first page
		ev.firstPage().drawMapTile(batch, bx, by, tileSize);
		
		batch.setColor(Palette.BATCH);
	}

	/**
	 * Copy an event completely!
	 */
	public void setEqualTo(Event ev) {
		if (ev == null) {
			return;
		}
		reference = ev.reference;
		
		name = ev.name;
		x = ev.x;
		y = ev.y;
		
		// We do NOT set the ID to be the same!...
		
		pages = new ArrayList<Page>();
		for (Page p : ev.pages) {
			pages.add(new Page().setEqualTo(p));
		}
	}
	
	public String getName() {
		return name;
	}
	
	public boolean getEditableName() {
		return this.editableName;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	/*
	public int getID() {
		return id;
	}
	*/
	
	public String getSigID() {
		return signature_id;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	/*
	public void setID(int id) {
		this.id = id;
	}
	*/
	
	public void setSigID(String sigid) {
		this.signature_id = sigid;
	}
	
	/**
	 * Refresh the spriteset data on the given page. 
	 */
	public void refreshSprites(int page) {
		pages.get(page).refreshSprites();
	}
	
	/**
	 * Set spriteset data on the given page.
	 */
	public void setSprite(String dir, String filename, int page) {
		pages.get(page).setSprite(dir, filename);
	}
	
	public String getSpriteDir(int page) {
		return pages.get(page).getSpriteDir();
	}
	
	public String getSpriteName(int page) {
		return pages.get(page).getSpriteName();
	}
	
	public String getSpritePath(int page) {
		return pages.get(page).getSpritePath();
	}
	
	public Page firstPage() {
		return pages.get(0);
	}
	
	public int getPageCount() {
		return pages.size();
	}
	
	public boolean isPageExists(int page) {
		if (pages.size()-1 >= page) {
			return true;
		} else {
			return false;
		}
	}
	
	public void addPage() {
		pages.add(new Page());
	}
	
	public void addPage(int position, Page page) {
		pages.add(position, page);
	}
	
	public void addEmptyPage() {
		Page added = new Page();
		added.getCommands().clear();
		pages.add(added);
	}

	public Page getPage(int page) {
		return pages.get(page);
	}

	public void removePages() {
		pages.clear();
	}
	
	public void removePage(int id) {
		pages.remove(id);
	}
	
	public void write(TextFile t2) {
		// First, write the separator
		t2.addLine();
		t2.addValue("*EVENT*");
		t2.addValue(getName());
		t2.addValue(Integer.toString(getX()));
		t2.addValue(Integer.toString(getY()));
		t2.addValue(signature_id);
		// Now write the page data
		for (int i = 0; i < getPageCount(); i++) {
			Page page = getPage(i);
			t2.addLine();
			t2.addValue("*PAGE*");
			t2.addValue(page.getSpriteDir());
			t2.addValue(page.getSpriteName());
			t2.addValue(page.getSpriteCoordStr());
			t2.addLine();
			t2.addValue("*PARAMS*");
			for (String str : page.getParams()) {
				t2.addValue(str);
			}
			t2.addLine();
			t2.addValue("*COMMANDS*");
			for (ListElement e : page.getCommands()) {
				t2.addLine();
				t2.addValue(e.getAsString());
			}
		}
	}
	
	public int load(TextFile t2, int index) {
		if (index > t2.getLength()-1) {
			return -2; // End of file! (no event created, -2)
		} else if (t2.getLine(index).isEmpty()) {
			index++;
			System.out.println("Event: skipped empty line");
			load(t2, index); 
		} else {
			// Load event!
			// Get metadata from the first line
			removePages();
			setName(t2.getLine(index).get(1));
			x = Integer.parseInt(t2.getLine(index).get(2));
			y = Integer.parseInt(t2.getLine(index).get(3));
			signature_id = t2.getLine(index).get(4);
			index++;
			while (true) {
				// Return if End of file (event created, -1), or encountering another event.
				if (index > t2.getLength()-1) {
					return -1;
				} else if (t2.getLine(index).isEmpty()) {
					System.out.println("Event: skipped empty line");
					continue;
				} else if (t2.getValue(index, 0).equals("*EVENT*")) {
					return index;
				} else if (t2.getValue(index, 0).equals("*PAGE*")) {
					addEmptyPage();
					pages.get(pages.size()-1).setSprite(
							t2.getValue(index, 1), t2.getValue(index, 2));
					pages.get(pages.size()-1).setSpriteCoordStr(t2.getValue(index, 3));
				} else if (t2.getValue(index, 0).equals("*PARAMS*")) {
					t2.getLine(index).remove(0);
					pages.get(pages.size()-1).setParamsByString(t2.getLine(index));
				} else if (!t2.getValue(index, 0).equals("*COMMANDS*")) {
					// Add a command!
					ListElement elm = new ListElement("");
					elm.rebuildFromString(t2.getLine(index).get(0));
					pages.get(pages.size()-1).getCommands().add(elm);
				}
				index++;
			}
		}
		// Should never run.
		System.out.println("Event: loading gone wrong...");
		return -100;
	}
	
}
