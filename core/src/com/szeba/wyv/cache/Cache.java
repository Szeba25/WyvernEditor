package com.szeba.wyv.cache;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.event.Event;
import com.szeba.wyv.data.event.Spriteset;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.data.maps.Cell;
import com.szeba.wyv.data.maps.GameMap;
import com.szeba.wyv.data.texture.Image;
import com.szeba.wyv.data.tiles.Tileset;
import com.szeba.wyv.utilities.FileUtilities;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.ext.Warning;

/** 
 * The main cache. We will refer to this cache each time we need some resource, like music, texture,
 * tileset, etc, or some cached data, like the currently loaded maps.
 * @author Szeba
 */
public class Cache {
	// Main GUI texture
	private Texture mainTexture;
	// Filler texture, used for drawing rectangles.
	private TextureRegion filler;
	// An empty region.
	private TextureRegion empty;
	// Icons
	private ArrayList<TextureRegion> fileIcons;
	// Tool icons
	private ArrayList<TextureRegion> toolIcons;
	// Disabled tool icons
	private ArrayList<TextureRegion> disabledToolIcons;
	// Default font
	private Texture fontTexture;
	private BitmapFont font;
	// Signature
	private String signature;
	// Banners
	private TextureRegion banner1;
	private TextureRegion banner2;
	private TextureRegion banner3;
	// The tileset database textures;
	private TextureRegion tileWalkable;
	private TextureRegion tileObstructed;
	private TextureRegion tileBottom;
	private TextureRegion tileTop;
	private TextureRegion[] tileDot;
	private TextureRegion[] tileArrow;
	// Tilesets
	private HashMap<String, Tileset> tilesets;
	// Loaded maps
	private HashMap<String, GameMap> maps;
	// Currently edited map in the main screen
	private GameMap currentMap;
	private Point currentCellCoord;
	// Starting position
	private StartingPosition startingPosition;
	// Cached sprites
	private HashMap<String, Spriteset> cachedSpritesets;
	// Cached colors
	private HashMap<String, Color> cachedColors;
	
	// Currently edited event inside screen 2
	private Event editedEvent;
	private Cell editedEventCell;
	
	// Currently streamed music
	private Music streamedMusic;
	private boolean unloadStreamedMusic;
	
	public void init() {
		// Load basic resources
		mainTexture = new Texture(Wyvern.DIRECTORY + "/core files/main.png");
		//mainTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		filler = new TextureRegion(mainTexture, 0, 0, 16, 16);
		filler.flip(false, true);
		empty = new TextureRegion(mainTexture, 16, 0, 16, 16);
		empty.flip(false, true);
		fileIcons = new ArrayList<TextureRegion>();
		for (int i = 0; i < 20; i++) {
			fileIcons.add(new TextureRegion(mainTexture, i*16, 16, 16, 16));
			fileIcons.get(i).flip(false, true);
		}
		toolIcons = new ArrayList<TextureRegion>();
		for (int i = 0; i < 34; i++) {
			toolIcons.add(new TextureRegion(mainTexture, i*30, 50, 30, 30));
			toolIcons.get(i).flip(false, true);
		}
		disabledToolIcons = new ArrayList<TextureRegion>();
		disabledToolIcons.add(new TextureRegion(mainTexture, 270, 80, 30, 30));
		disabledToolIcons.get(0).flip(false, true);
		disabledToolIcons.add(new TextureRegion(mainTexture, 300, 80, 30, 30));
		disabledToolIcons.get(1).flip(false, true);
		// Font texture!
		fontTexture = new Texture(Wyvern.DIRECTORY + "/core files/default_0.png");
		fontTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		font = new BitmapFont(new FileHandle(Wyvern.DIRECTORY + "/core files/default.fnt"),
						new TextureRegion(fontTexture, fontTexture.getWidth(), fontTexture.getHeight()), true);
		
		signature = new TextFile(Wyvern.DIRECTORY + "/core files/signature.txt").getValue(0, 0);
		signatureCheck();
		
		banner1 = new TextureRegion(mainTexture, 0, 160, 320, 30);
		banner1.flip(false, true);
		banner2 = new TextureRegion(mainTexture, 0, 190, 320, 30);
		banner2.flip(false, true);
		banner3 = new TextureRegion(mainTexture, 0, 220, 320, 30);
		banner3.flip(false, true);
		tileWalkable = new TextureRegion(mainTexture, 0, 96, 32, 32);
		tileWalkable.flip(false, true);
		tileObstructed = new TextureRegion(mainTexture, 32, 96, 32, 32);
		tileObstructed.flip(false, true);
		tileBottom = new TextureRegion(mainTexture, 64, 96, 32, 32);
		tileBottom.flip(false, true);
		tileTop = new TextureRegion(mainTexture, 96, 96, 32, 32);
		tileTop.flip(false, true);
		
		tileDot = new TextureRegion[4];
		for (int x = 128; x < 256; x += 32) {
			tileDot[(x-128)/32] = new TextureRegion(mainTexture, x, 96, 32, 32);
			tileDot[(x-128)/32].flip(false, true);
		}
		
		tileArrow = new TextureRegion[4];
		for (int x = 128; x < 256; x += 32) {
			tileArrow[(x-128)/32] = new TextureRegion(mainTexture, x, 128, 32, 32);
			tileArrow[(x-128)/32].flip(false, true);
		}
		
		
		tilesets = new HashMap<String, Tileset>();
		maps = new HashMap<String, GameMap>();
		currentMap = null;
		currentCellCoord = new Point();
		
		startingPosition = new StartingPosition();
		
		cachedSpritesets = new HashMap<String, Spriteset>();
		
		cachedColors = new HashMap<String, Color>();
		
		streamedMusic = null;
		unloadStreamedMusic = false;
	}
	
	private void signatureCheck() {
		// Check for the signature folders
		String sigPath = Wyvern.INTERPRETER_DIR + "/preferences/" + getSignature() + "@sig_id";
		File sigFile = new File(sigPath);
		if (!sigFile.exists()) {
			sigFile.mkdir();
			TextFile f1 = new TextFile(sigPath + "/" + "id_counter.txt", null);
			TextFile f2 = new TextFile(sigPath + "/" + "map_id_counter.txt", null);
			f1.addLine();
			f1.addValue("1");
			f1.save();
			f2.addLine();
			f2.addValue("1");
			f2.save();
		}
	}
	
	/**
	 * Play a music from the hard disc.
	 */
	public void playStreamedMusic(String dir, String name) {
		String path = Wyvern.INTERPRETER_DIR + dir + "/" + name;
		if (FileUtilities.exists(path) &&
				!FileUtilities.isFolder(path)) {
			disposeMusic();
			streamedMusic = Gdx.audio.newMusic(new FileHandle(Wyvern.INTERPRETER_DIR + dir + "/" + name));
			streamedMusic.play();
			unloadStreamedMusic = false;
			System.out.println("Music: loaded!");
		} else {
			Warning.showWarning("Music: " + dir + "/" + name + " not found!");
			disposeMusic();
		}
	}
	
	public void refreshStreamedMusic(float volume, float pan) {
		unloadStreamedMusic = false;
		if (streamedMusic != null) {
			streamedMusic.setPan(pan, volume);
		}
	}
	
	public void scheduleMusicUnload() {
		unloadStreamedMusic = true;
	}
	
	public void unloadMusicIfInactive() {
		if (unloadStreamedMusic) {
			disposeMusic();
		}
	}
	
	public void disposeMusic() {
		if (streamedMusic != null) {
			streamedMusic.stop();
			streamedMusic.dispose();
			streamedMusic = null;
			System.out.println("Music: disposed!");
		}
	}
	
	/**
	 * Get the current texture used for drawing rectangles, and outlines.
	 */
	public TextureRegion getFiller() {
		return filler;
	}
	
	public StartingPosition getStartingPosition() {
		return startingPosition;
	}
	
	/**
	 * Get the official "empty" texture. (region) 
	 */
	public TextureRegion getEmpty() {
		return empty;
	}
	
	/**
	 * Get a tool icon for the main toolbar.
	 */
	public TextureRegion getToolIcon(int i) {
		return toolIcons.get(i);
	}
	
	public TextureRegion getDisabledToolIcon(int i) {
		return disabledToolIcons.get(i);
	}
	
	/**
	 * Get the file icon by index (file type)
	 */
	public TextureRegion getFileIcon(int index) {
		return fileIcons.get(index);
	}
	
	/**
	 * Get the currently used font
	 */
	public BitmapFont getFont() {
		return font;
	}
	
	public String getSignature() {
		return signature;
	}
	
	public TextureRegion getBanner1() {
		return banner1;
	}
	
	public TextureRegion getBanner2() {
		return banner2;
	}
	
	public TextureRegion getBanner3() {
		return banner3;
	}
	
	public TextureRegion get_tileWalkable() {
		return this.tileWalkable;
	}
	
	public TextureRegion get_tileObstructed() {
		return this.tileObstructed;
	}
	
	public TextureRegion get_tileBottom() {
		return this.tileBottom;
	}
	
	public TextureRegion get_tileTop() {
		return this.tileTop;
	}
	
	public TextureRegion get_tileDot(int dir) {
		return tileDot[dir];
	}
	
	public TextureRegion get_tileArrow(int dir) {
		return tileArrow[dir];
	}
	
	/**
	 * Get a tileset from the cache. If it does not exists, try to load it from hdd.
	 */
	public Tileset getTileset(String name) {
		Tileset tileset = tilesets.get(name);
		if (tileset == null) {
			tileset = new Tileset(name);
			tilesets.put(name, tileset);
		}
		return tileset;
	}
	
	public HashMap<String, Tileset> getTilesets() {
		return tilesets;
	}
	
	/**
	 * Get the currently loaded maps.
	 */
	public HashMap<String, GameMap> getMaps() {
		return maps;
	}

	public String getDefaultTileset() {
		return "default";
	}
	
	/** 
	 * Get or load an image
	 */
	public Image getImage(String dir, String name) {
		return Image.getImage(dir, name);
	}
	
	/**
	 * Get or load a spriteset
	 */
	public Spriteset getSpriteset(String dir, String filename) {
		if (dir != null && filename != null) {
			if (this.cachedSpritesets.containsKey(dir + "/" + filename)) {
				return cachedSpritesets.get(dir + "/" + filename);
			} else {
				Spriteset spr = new Spriteset(dir, filename);
				cachedSpritesets.put(dir + "/" + filename, spr);
				return spr;
			}
		}
		return null;
	}
	
	public void setEditedEvent(Event e) {
		this.editedEvent = e;
	}
	
	public void setEditedEventCell(Cell c) {
		this.editedEventCell = c;
	}
	
	public Event getEditedEvent() {
		return this.editedEvent;
	}
	
	public Cell getEditedEventCell() {
		return this.editedEventCell;
	}
	
	public Color getCachedColor(String color) {
		if (color != null) {
			if (cachedColors.containsKey(color)) {
				return cachedColors.get(color);
			} else {
				// Create this color
				String[] c = StringUtilities.safeSplit(color, "x");
				Color constructed = new Color(
						Float.parseFloat(c[0]), Float.parseFloat(c[1]),
						Float.parseFloat(c[2]), Float.parseFloat(c[3]));
				cachedColors.put(color, constructed);
				return constructed;
			}
		} else {
			return null;
		}
	}
	
	public GameMap getCurrentMap() {
		return currentMap;
	}
	
	public Point getCurrentCellCoord() {
		return currentCellCoord;
	}
	
	public void setCurrentMap(GameMap map) {
		this.currentMap = map;
	}
	
	public void setCurrentCellCoord(Point cellCoord) {
		this.currentCellCoord.x = cellCoord.x;
		this.currentCellCoord.y = cellCoord.y;
	}
	
	public void setCurrentCellCoord(int x, int y) {
		this.currentCellCoord.x = x;
		this.currentCellCoord.y = y;
	}

}
