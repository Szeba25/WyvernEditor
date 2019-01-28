package com.szeba.wyv.data.event;

import java.awt.Point;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.data.texture.Image;
import com.szeba.wyv.utilities.FileUtilities;
import com.szeba.wyv.widgets.ext.Warning;

public class Spriteset {

	public static Point defaultSize = new Point(4, 4);
	
	private String dir;
	private String name;
	private TextFile metadata;
	private ArrayList<Image> images;
	private ArrayList<Point> sizes;
	private int iconW;
	private int iconH;
	private TextureRegion[][] icons;
	
	public Spriteset(String dir, String name) {
		this.dir = dir;
		this.name = name;
		load(dir, name, false);
	}
	
	public int getIconW() {
		return iconW;
	}
	
	public int getIconH() {
		return iconH;
	}
	
	public TextureRegion getIcon(int x, int y) {
		return icons[x][y];
	}
	
	/**
	 * Cut the spriteset into icons for easier drawing.
	 */
	private void getIcons() {
		Point size = getMainSize();
		icons = new TextureRegion[size.x][size.y];
		getMain().flip(false, true);
		for (int x = 0; x < size.x; x++) {
			for (int y = 0; y < size.y; y++) {
				icons[y][x] = new TextureRegion(getMain(), x * iconW, y * iconH, iconW, iconH);
				icons[y][x].flip(false, true);
			}
		}
		getMain().flip(false, true);
	}
	
	private void load(String dir, String name, boolean checkRun) {
		images = new ArrayList<Image>();
		sizes = new ArrayList<Point>();	
		
		String checkedPath = Wyvern.INTERPRETER_DIR + "/" + dir + "/" + name;
		
		if (FileUtilities.isValidSpriteset(checkedPath)) {
			// Load the metadata, and load the images!
			metadata = new TextFile(checkedPath + "/metadata.wdat");
			for (int i = 0; i < metadata.getLength(); i++) {
				images.add(Wyvern.cache.getImage(dir + "/" + name,
						metadata.getValue(i, 0)));
				sizes.add(new Point(
						Integer.parseInt(metadata.getValue(i, 1)),
						Integer.parseInt(metadata.getValue(i, 2)) ));
			}
			
		} else if (FileUtilities.isValidImage(checkedPath)) {
			// Load a simple image or region file!
			images.add(Wyvern.cache.getImage(dir, name));
			sizes.add(defaultSize);
			
		} else {
			// Is not a spriteset...
			if (checkRun) {
				Warning.showWarning("Spriteset (check): " + dir + "/" + name + " is not a valid spriteset");
			} else {
				Warning.showWarning("Spriteset: " + dir + "/" + name + " is not a valid spriteset");
			}
			
		}
		
		// Load the icons
		iconW = getMain().getRegionWidth()/(getMainSize().x);
		iconH = getMain().getRegionHeight()/(getMainSize().y);
		getIcons();
	}

	public void check() {
		load(dir, name, true);
	}
	
	public String getDir() {
		return dir;
	}
	
	public String getName() {
		return name;
	}
	
	public TextureRegion getMain() {
		if (images.size() > 0) {
			return images.get(0);
		} else {
			return Wyvern.cache.getEmpty();
		}
	}
	
	public Point getMainSize() {
		if (sizes.size() > 0) {
			return sizes.get(0);
		} else {
			return defaultSize;
		}
	}
	
}
