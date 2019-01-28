package com.szeba.wyv.data.texture;

import java.io.File;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.utilities.FileUtilities;
import com.szeba.wyv.utilities.StringUtilities;

/**
 * An advanced texture region, that can be .png file itself, or a .wdat file, that
 * defines a texture region.
 * The object comes with an internal static method, to get Images. (getImage())
 * @author Szeba
 */
public class Image extends TextureRegion {

	private static HashMap<String, ManagedTexture> textures = new HashMap<String, ManagedTexture>();
	private static HashMap<String, Image> images = new HashMap<String, Image>();
	
	private boolean found;
	
	private String imgDir;
	private String imgName;
	private String imgRelativePath;
	private String imgAbsolutePath;
	
	private int reg_x;
	private int reg_y;
	private int reg_w;
	private int reg_h;
	
	private String textureDir;
	private String textureName;
	
	private FileTime timeModified;
	
	/**
	 * Creates a new Image instance on the given path.
	 */
	public static Image getImage(String fileDir, String fileName) {
		if (images.containsKey(fileDir + "/" + fileName)) {
			Image img = images.get(fileDir + "/" + fileName);
			// If the image is already loaded, check for update.
			img.check();
			return img;
		} else {
			Image img = new Image(fileDir, fileName);
			images.put(fileDir + "/" + fileName, img);
			return img;
		}
	}
	
	/**
	 * The main constructor. You must get images with getImage(). 
	 */
	private Image(String fileDir, String fileName) {
		super(Wyvern.cache.getEmpty());
		
		found = false;
		
		imgDir = fileDir;
		imgName = fileName;
		imgRelativePath = fileDir + "/" + fileName;
		imgAbsolutePath = Wyvern.INTERPRETER_DIR + "/" + imgRelativePath;
		
		reg_x = 0;
		reg_y = 0;
		reg_w = 16;
		reg_h = 16;
		
		textureDir = null;
		textureName = null;
		
		timeModified = null;
		
		check();
	}
	
	public String getImageDir() {
		return imgDir;
	}
	
	public String getImageName() {
		return imgName;
	}
	
	/**
	 * Checks if the Image is up to date, or need a reload.
	 */
	public void check() {
		// If the path to the image file exists.
		if (FileUtilities.exists(imgAbsolutePath)) {
			/* 
			 * Reload the Image if it was not found before, or the Images modify, creation and access dates are
			 * different from the file's dates 
			 */
			BasicFileAttributes attrs = FileUtilities.getFileAttributes(imgAbsolutePath);
			if (!found || !isEqualAttrs(attrs)) {
				
				// Reload the Image and check it's new texture!
				
				if (FileUtilities.isValidImage(imgAbsolutePath)) {
					if (StringUtilities.getExtension(imgName).equals("wimg")) {
						// This image is a reference to a texture. Get the texture's name from the file.
						TextFile tf = new TextFile(imgAbsolutePath);
						textureName = tf.getValue(0, 0);
						String parent = tf.getValue(1, 1);
						int x = Integer.parseInt(tf.getValue(2, 1));
						int y = Integer.parseInt(tf.getValue(3, 1));
						int w = Integer.parseInt(tf.getValue(4, 1));
						int h = Integer.parseInt(tf.getValue(5, 1));
						if (parent.equals("true")) {
							textureDir = (new File(imgDir).getParent());
							textureDir = StringUtilities.replaceSlashesInPath(textureDir);
						} else {
							textureDir = imgDir;
						}
						set(textureDir, textureName, false, x, y, w, h);
						
						// Set the found variable to true (image is now up to date)
						found = true;
						setFileAttributes(attrs);
						
					} else {
						// This Image is not a reference to a texture. This is the texture itself.
						textureDir = imgDir;
						textureName = imgName;
						set(imgDir, imgName, true, 0, 0, 0, 0);
						
						// Set the found variable to true (image is now up to date)
						found = true;
						setFileAttributes(attrs);
						
					}
				} else {
					System.out.println("Image: " + imgDir + "/" + imgName + " is not a valid image.");
					// Funky stuff happened here
					// The path points to something else
					setRegion(Wyvern.cache.getEmpty());
					reg_x = 0;
					reg_y = 0;
					reg_w = 16;
					reg_h = 16;
					found = false;
					
				}
				
			} else {
				// Image is up to date. Check texture!
				checkTexture(textureDir, textureName);
			}
		} else {
			if (found) {
				// Image data was found before, but the file requested is not valid anymore.
				System.out.println("Image: " + imgDir + "/" + imgName + " is not a valid image.");
				setRegion(Wyvern.cache.getEmpty());
				reg_x = 0;
				reg_y = 0;
				reg_w = 16;
				reg_h = 16;
				found = false;
			}
		}
	}
	
	/**
	 * Creates a new managed texture.
	 */
	private ManagedTexture createTexture(String dir, String name) {
		
		String texRelativePath = dir + "/" + name;
		String texAbsolutePath = Wyvern.INTERPRETER_DIR + "/" + texRelativePath;
		ManagedTexture tex = null;
		
		// We must create this texture. Check for the file if its valid.
		if (FileUtilities.isValidTexture(texAbsolutePath)) {
			
			// This texture is valid.
			tex = new ManagedTexture(texAbsolutePath, true, dir, name);
			tex.setFileAttributes(FileUtilities.getFileAttributes(texAbsolutePath));
			
			System.out.println("Texture: (n) " + texRelativePath + " is created!");
			
		} else {
			
			// This texture is not valid.
			tex = new ManagedTexture(Wyvern.DIRECTORY + "/core files/empty.png", false, dir, name);
			
			System.out.println("Texture: (n) " + texRelativePath + " is invalid. Empty texture created.");
		}
		// Put the texture inside the hashmap for easy access
		textures.put(texRelativePath, tex);
		return tex;
	}
	
	/**
	 * Check for this managed texture if it's up to date. 
	 */
	private void checkTexture(String dir, String name) {
		
		String texRelativePath = dir + "/" + name;
		String texAbsolutePath = Wyvern.INTERPRETER_DIR + "/" + texRelativePath;
		ManagedTexture oldTexture = textures.get(texRelativePath);
		
		// Check, if the textures file is valid.
		if (FileUtilities.isValidTexture(texAbsolutePath)) {
			
			BasicFileAttributes attrs = FileUtilities.getFileAttributes(texAbsolutePath);
			if (!oldTexture.getFound() || !oldTexture.isEqualAttrs(attrs)) {
				
				ManagedTexture newTexture = new ManagedTexture(texAbsolutePath, true, dir, name);
				textures.put(texRelativePath, newTexture);
				updateImages(oldTexture, newTexture);
				oldTexture.dispose();
				
				// Update the new textures attributes.
				newTexture.setFileAttributes(attrs);
				
				System.out.println("Texture: (o) " + texRelativePath + " was not up to date, and reloaded.");
				
			}
		} else {
			// The texture was previously loaded, but now, its invalid.
			if (oldTexture.getFound()) {
				
				ManagedTexture newTexture = new ManagedTexture(Wyvern.DIRECTORY + "/core files/empty.png",
						false, dir, name);
				textures.put(texRelativePath, newTexture);
				updateImages(oldTexture, newTexture);
				oldTexture.dispose();
				
				System.out.println("Texture: (o) " + texRelativePath + " is invalid. Empty texture created.");
				
			}
		}
	}
	
	/**
	 * Update the texture object change inside the Image objects.
	 */
	private void updateImages(ManagedTexture oldTexture, ManagedTexture newTexture) {
		for (Image img : images.values()) {
			if (img.getTexture() == oldTexture) {
				// LoL... shorten than I thought.
				img.setPartialTexture(newTexture, img.reg_x, img.reg_y, img.reg_w, img.reg_h);
			}
		}
	}
	
	private void set(String dir, String name, boolean whole, int x, int y, int w, int h) {
		ManagedTexture tex;
		if (textures.containsKey(dir + "/" + name)) {
			checkTexture(dir, name);
			tex = textures.get(dir + "/" + name);
		} else {
			tex = createTexture(dir, name);
		}
		if (whole) {
			setWholeTexture(tex);
		} else {
			setPartialTexture(tex, x, y, w, h);
		}
	}
	
	private void setWholeTexture(Texture tex) {
		super.setTexture(tex);
		
		this.reg_x = 0;
		this.reg_y = 0;
		this.reg_w = tex.getWidth();
		this.reg_h = tex.getHeight();
		
		this.setRegion(0, 0, tex.getWidth(), tex.getHeight());
		if (!this.isFlipY()) { 
			this.flip(false, true); 
		}
	}
	
	private void setPartialTexture(Texture tex, int x, int y, int w, int h) {
		super.setTexture(tex);
		
		this.reg_x = x;
		this.reg_y = y;
		this.reg_w = w;
		this.reg_h = h;
		
		this.setRegion(x, y, w, h);
		if (!this.isFlipY()) { 
			this.flip(false, true); 
		}
	}
	
	private void setFileAttributes(BasicFileAttributes attrs) {
		timeModified = attrs.lastModifiedTime();
	}
	
	private boolean isEqualAttrs(BasicFileAttributes attrs) {
		return timeModified.compareTo(attrs.lastModifiedTime()) == 0;
	}
	
}
