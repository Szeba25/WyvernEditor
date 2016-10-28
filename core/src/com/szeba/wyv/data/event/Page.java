package com.szeba.wyv.data.event;

import java.awt.Point;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.utilities.TexturePainter;
import com.szeba.wyv.widgets.dynamic.Dynamic;

/** 
 * Represents an event page inside an event.
 */
public class Page {

	private static TextFile paramList = new TextFile(Wyvern.INTERPRETER_DIR + "/preferences/events/event_params.ikd");
	
	// Spriteset data
	private String spriteDir;
	private String spriteName;
	private Point spriteCoord;
	private Spriteset sprite;
	
	// Prevents the loading of sprites at once
	private boolean spriteRequest;
	private String spriteRequestDir;
	private String spriteRequestName;
	
	// Parameter data
	private ArrayList<String> params;
	
	// Event command data
	private ArrayList<ListElement> commands;
	
	/**
	 * Creates an empty page
	 */
	public Page() {
		spriteDir = "";
		spriteName = "";
		spriteCoord = new Point(0, 0);
		sprite = null;
		spriteRequest = false;
		spriteRequestDir = null;
		spriteRequestName = null;
		
		params = new ArrayList<String>();
		for (int i = 0; i < paramList.getLength(); i++) {
			params.add(paramList.getValue(i, 1));
		}
		
		ArrayList<ListElement> ar = new ArrayList<ListElement>();
		ar.add(new ListElement(0, "add command", "add command", 0));
		commands = ar;
	}
	
	/**
	 * Copy a page completely!
	 */
	public Page setEqualTo(Page page) {
		spriteDir = page.spriteDir;
		spriteName = page.spriteName;
		spriteCoord.x = page.spriteCoord.x;
		spriteCoord.y = page.spriteCoord.y;
		sprite = page.sprite;
		spriteRequest = page.spriteRequest;
		spriteRequestDir = page.spriteRequestDir;
		spriteRequestName = page.spriteRequestName;
		
		params = new ArrayList<String>();
		for (int i = 0; i < page.params.size(); i++) {
			params.add(page.params.get(i));
		}
		
		commands = new ArrayList<ListElement>();
		for (int i = 0; i < page.commands.size(); i++) {
			commands.add(new ListElement("").setEqualTo(page.commands.get(i)));
		}
		return this;
	}

	/**
	 * Draw the sprite on the map.
	 */
	public void drawMapTile(SpriteBatch batch, int bx, int by, int tileSize) {
		if (sprite != null) {
			draw(batch, sprite.getIcon(spriteCoord.y , spriteCoord.x), bx, by, tileSize);
		} else if (spriteRequest) {
			setSpriteByRequest();
		}
	}
	
	public void drawSprite(SpriteBatch batch, int bx, int by, int tileSize) {
		if (sprite != null) {
			draw(batch, sprite.getMain(), bx, by, tileSize);
		} else if (spriteRequest) {
			setSpriteByRequest();
		}
	}
	
	public void draw(SpriteBatch batch, TextureRegion region, int bx, int by, int tileSize) {
		if (sprite != null) {
			TexturePainter.drawGraphics(batch, region, sprite.getIconW(), sprite.getIconH(), bx, by, tileSize);
		} else if (spriteRequest) {
			setSpriteByRequest();
		}
	}
	
	private void setSpriteByRequest() {
		sprite = Wyvern.cache.getSpriteset(spriteRequestDir, spriteRequestName);
		spriteRequest = false;
		spriteRequestDir = null;
		spriteRequestName = null;
	}
	
	public String getSpriteDir() {
		return spriteDir;
	}
	
	public String getSpriteName() {
		return spriteName;
	}
	
	public Point getSpriteCoord() {
		return spriteCoord;
	}
	
	public void setSpriteCoord(Point spriteCoord) {
		this.spriteCoord.x = spriteCoord.x;
		this.spriteCoord.y = spriteCoord.y;
	}
	
	public String getSpriteCoordStr() {
		return spriteCoord.x + "x" + spriteCoord.y;
	}
	
	public void setSpriteCoordStr(String coord) {
		String[] spl = StringUtilities.safeSplit(coord, "x");
		spriteCoord.x = Integer.parseInt(spl[0]);
		spriteCoord.y = Integer.parseInt(spl[1]);
	}
	
	public int getIconW() {
		return sprite.getIconW();
	}
	
	public int getIconH() {
		return sprite.getIconH();
	}
	
	public String getSpritePath() {
		return spriteDir + "/" + spriteName;
	}
	
	public void refreshSprites() {
		setSprite(spriteDir, spriteName);
	}
	
	public void checkSprite() {
		if (sprite != null) {
			sprite.check();
		}
	}
	
	/**
	 * Set sprite data from the cache. 
	 */
	public void setSprite(String dir, String filename) {
		if (dir.length() == 0 && filename.length() == 0) {
			spriteDir = "";
			spriteName = "";
			spriteCoord.x = 0;
			spriteCoord.y = 0;
			sprite = null;
			spriteRequest = false;
			spriteRequestDir = null;
			spriteRequestName = null;
		} else {
			spriteDir = dir;
			spriteName = filename;
			spriteCoord.x = 0;
			spriteCoord.y = 0;
			// IMPORTANT! Set sprite to null, to force a refresh.
			sprite = null;
			spriteRequest = true;
			spriteRequestDir = dir;
			spriteRequestName = filename;
		}
	}
	
	public ArrayList<String> getParams() {
		return params;
	}
	
	public void setParamsByString(ArrayList<String> ar) {
		for (int i = 0; i < ar.size(); i++) {
			params.set(i, ar.get(i));
		}
	}
	
	public void setParams(ArrayList<Dynamic> pr) {
		for (int i = 0; i < pr.size(); i++) {
			params.set(i, pr.get(i).dynGetValue());
		}
	}
	
	public ArrayList<ListElement> getCommands() {
		return commands;
	}

}
