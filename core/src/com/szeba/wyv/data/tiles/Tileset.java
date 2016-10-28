package com.szeba.wyv.data.tiles;

import java.awt.Point;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.utilities.FileUtilities;
import com.szeba.wyv.utilities.StringUtilities;

/** 
 * A tileset represents a preset with 16*48 regular tiles, and 32 autotiles.
 * Each Cell can contain one of these tilesets. The tileset also holds the obstruction
 * terrain, and layer data.
 * @author Szeba
 */
public class Tileset {

	// The access name of this tileset.
	private String name;
	private String realPath;
	private String path;
	
	// Changed state
	private boolean changed;
	
	// The tilesize in pixels. It's calculated by dividing the tileset height by 48
	private int tileSize;
	
	// The reference image dates
	private FileTime timeModified;
	
	// The terrain texture
	private Texture texture;
	
	private TextureRegion wholeTexture;
	private TextureRegion tiles1;
	private TextureRegion tiles2;
	private TextureRegion[][] smallTiles;
	private TextureRegion[] autotileIcons;
	
	private String[][][] obstructions;
	private String[][] terrainData;
	private String[][] layerData;
	private ArrayList<ArrayList<Point>> animations;
	
	public Tileset(String name) {
		// Set the name of this tileset, and the access path
		this.name = name;
		realPath = Wyvern.INTERPRETER_DIR + "/resources/tilesets/" + name + "@tileset";
		path = null;
		
		// If the access path does not exists, load the default tileset!
		checkForAccessPath();
		
		// Load the metadata
		TextFile metadata = new TextFile(path + "/metadata.ikd");
		tileSize = Integer.parseInt(metadata.getValue(0, 1));
		
		createMainTexture();
		
		loadData();
	}

	private void checkForAccessPath() {
		if (!FileUtilities.exists(realPath)) {
			if (!name.equals("default")) {
				// Print a warning if this tileset is not the default one.
				System.err.println("Tileset: \"" + name + "\" is missing!");
			}
			path = Wyvern.INTERPRETER_DIR + "/resources/tilesets/_default";
		} else {
			path = realPath;
		}
	}

	private void createMainTexture() {
		// Get date
		BasicFileAttributes attrs = FileUtilities.getFileAttributes(path + "/terrain.png");
		setFileAttributes(attrs);
		
		// Create the main texture (make nonexisting terrain data magenta coloured)
		Pixmap terrainPixmap = new Pixmap(new FileHandle(path + "/terrain.png"));
		// Create the final image!
		Pixmap finalPixmap = new Pixmap(48*tileSize, 48*tileSize, Pixmap.Format.RGBA8888);
		
		finalPixmap.setColor(1.0f, 0.0f, 1.0f, 1.0f);
		finalPixmap.fill();
		finalPixmap.drawPixmap(terrainPixmap, 0, 0);
		
		texture = new Texture(finalPixmap);
		
		terrainPixmap.dispose();
		finalPixmap.dispose();
		
		// The texture region of the main texture
		wholeTexture = new TextureRegion(texture, 0, 0, 
				texture.getWidth(), texture.getHeight());
		wholeTexture.flip(false, true);
		
		// Create regular tile texture regions
		tiles1 = new TextureRegion(texture, 0, 0, tileSize*8, tileSize*48);
		tiles1.flip(false, true);
		tiles2 = new TextureRegion(texture, tileSize*24, 0, tileSize*8, tileSize*48);
		tiles2.flip(false, true);
		smallTiles = new TextureRegion[48][48];
		initSmallTiles();
		autotileIcons = new TextureRegion[32];
		initAutotileIcons();
	}

	public boolean check() {
		boolean needUpdate = checkForUpdate();
		if (needUpdate) {
			texture.dispose();
			createMainTexture();
			System.out.println("Tileset: \"" + name + "\" was not up to date, and reloaded! (" + 
					"source: " + path);
		}
		return needUpdate;
	}
	
	private boolean checkForUpdate() {
		checkForAccessPath();
		BasicFileAttributes attrs = FileUtilities.getFileAttributes(path + "/terrain.png");
		return !this.isEqualAttrs(attrs);
	}

	public TextureRegion getTexture() {
		return wholeTexture;
	}
	
	public TextureRegion getTiles1() {
		return tiles1;
	}
	
	public TextureRegion getTiles2() {
		return tiles2;
	}
	
	public TextureRegion getSmallTile(int x, int y) {
		return smallTiles[x][y];
	}
	
	public TextureRegion getAutotileIcon(int i) {
		return autotileIcons[i];
	}

	public String[][] getTerrainData() {
		return terrainData;
	}

	public String[][] getLayerData() {
		return layerData;
	}

	public ArrayList<ArrayList<Point>> getAnimations() {
		return animations;
	}
	
	public void addAnimation(ArrayList<Point> frames) {
		this.animations.add(frames);
	}
	
	public String getName() {
		return name;
	}
	
	public int getTileSize() {
		return tileSize;
	}
	
	public void dispose() {
		texture.dispose();
	}
	
	private void initAutotileIcons() {
		int coloumnWidth = tileSize*8;
		int setHeight = tileSize*6;
		// Get autotiles part 1
		for (int i = 0; i < 16; i++) {
			int x = ((i/8) * coloumnWidth) + coloumnWidth;
			int y = i * setHeight - ((i/8) * texture.getHeight());
			autotileIcons[i] = new TextureRegion(texture, x + tileSize*7, y + tileSize*5, tileSize, tileSize);
			autotileIcons[i].flip(false, true);
		}
		// Get autotiles part 2
		for (int i = 0; i < 16; i++) {
			int x = ((i/8) * coloumnWidth) + coloumnWidth*4;
			int y = i * setHeight - ((i/8) * texture.getHeight());
			autotileIcons[i+16] = new TextureRegion(texture, x + tileSize*7, y + tileSize*5, tileSize, tileSize);
			autotileIcons[i+16].flip(false, true);
		}
	}

	/** 
	 * Return a small image for each tile 
	 */
	private void initSmallTiles() {
		for (int x = 0; x < 48; x++) {
			for (int y = 0; y < 48; y++) {
				smallTiles[x][y] = new TextureRegion(texture, 
						x*tileSize, y*tileSize, tileSize, tileSize);
				// Flip the tiles because of the orthographic camera
				smallTiles[x][y].flip(false, true);
			}
		}
	}

	/**
	 * Get a double string array from a text file
	 */
	private String[][] stringArrayConv(TextFile file) {
		String[][] text = new String[48][48];
		for (int y = 0; y < 48; y++) {
			for (int x = 0; x < 48; x++) {
				text[x][y] = file.getValue(y, x);
			}
		}
		return text;
	}
	
	/**
	 * Get a text file from a double string array
	 */
	private TextFile arrayStringConv(String[][] arr, String name) {
		TextFile file = new TextFile(path + name, null);
		for (int y = 0; y < 48; y++) {
			file.addLine();
			for (int x = 0; x < 48; x++) {
				file.addValue(arr[x][y]);
			}
		}
		return file;
	}
	
	public String[][] getObstructions(int z) {
		return obstructions[z];
	}
	
	public void save() {
		setChanged(false);
		arrayStringConv(obstructions[0], "/obstructions/obst_up.ikd").save();
		arrayStringConv(obstructions[1], "/obstructions/obst_right.ikd").save();
		arrayStringConv(obstructions[2], "/obstructions/obst_down.ikd").save();
		arrayStringConv(obstructions[3], "/obstructions/obst_left.ikd").save();
		arrayStringConv(terrainData, "/terrain_data.ikd").save();
		arrayStringConv(layerData, "/layer_data.ikd").save();
		TextFile file = new TextFile(path + "/animations.ikd", null);
		for (ArrayList<Point> tps : animations) {
			file.addLine();
			for (Point p : tps) {
				file.addValue(p.x + "x" + p.y);
			}
		}
		file.save();
		System.out.println("Database: Tileset " + name + " saved!");
	}
	
	public void reloadData() {
		setChanged(false);
		loadData();
		System.out.println("Database: Tileset " + name + " reverted!");
	}

	private void loadData() {
		// Get obstructions, terrain, and layer data.
		obstructions = new String[4][48][48];
		obstructions[0] = stringArrayConv(new TextFile(path + "/obstructions/obst_up.ikd"));
		obstructions[1] = stringArrayConv(new TextFile(path + "/obstructions/obst_right.ikd"));
		obstructions[2] = stringArrayConv(new TextFile(path + "/obstructions/obst_down.ikd"));
		obstructions[3] = stringArrayConv(new TextFile(path + "/obstructions/obst_left.ikd"));
		terrainData = new String[48][48];
		terrainData = stringArrayConv(new TextFile(path + "/terrain_data.ikd"));
		layerData = new String[48][48];
		layerData = stringArrayConv(new TextFile(path + "/layer_data.ikd"));
		animations = new ArrayList<ArrayList<Point>>();
		TextFile animFile = new TextFile(path + "/animations.ikd");
		for (int i = 0; i < animFile.getLength(); i++) {
			ArrayList<Point> list = new ArrayList<Point>();
			animations.add(list);
			for (int p = 0; p < animFile.getLine(i).size(); p++) {
				String[] ts = StringUtilities.safeSplit(animFile.getValue(i, p), "x");
				Point tempPoint = new Point(Integer.parseInt(ts[0]), Integer.parseInt(ts[1]));
				list.add(tempPoint);
			}
		}
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	private void setFileAttributes(BasicFileAttributes attrs) {
		timeModified = attrs.lastModifiedTime();
	}
	
	private boolean isEqualAttrs(BasicFileAttributes attrs) {
		return timeModified.compareTo(attrs.lastModifiedTime()) == 0;
	}

}
