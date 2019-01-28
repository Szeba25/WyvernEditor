package com.szeba.wyv.cache;

import java.io.File;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.utilities.FileUtilities;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.ext.Warning;

public class TempImage {

	private TextureRegion image;
	private Texture texture;
	private String imageDir;
	private String imageName;
	
	public TempImage(String dir, String name) {
		load(dir, name);
	}

	public int getWidth() {
		return getImage().getRegionWidth();
	}
	
	public int getHeight() {
		return getImage().getRegionHeight();
	}
	
	public void dispose() {
		if (texture != null && texture != Wyvern.cache.getEmpty().getTexture()) {
			texture.dispose();
			System.out.println("TempImage: (d) image disposed " + imageDir + "/" + imageName);
		}
	}
	
	public void load(String dir, String name) {
		
		dispose();
		
		image = null;
		texture = null;
		imageDir = dir;
		imageName = name;
		
		boolean nullImage = false;
		String finalPath = null;
		
		if (dir == null || name == null) {
			nullImage = true;
		} else {
			finalPath = Wyvern.INTERPRETER_DIR + "/" + dir + "/" + name;
		}
		
		if (!nullImage && FileUtilities.exists(finalPath)) {
			
			System.out.println("TempImage: (n) image loaded " + imageDir + "/" + imageName);
			
			if (FileUtilities.isValidImage(finalPath)) {
				if (StringUtilities.getExtension(imageName).equals("wdat")) {
					// This image is a reference to a texture. Get the texture's name from the file.
					TextFile tf = new TextFile(finalPath);
					String textureDir = "";
					String textureName = tf.getValue(0, 0);
					String parent = tf.getValue(1, 1);
					int x = Integer.parseInt(tf.getValue(2, 1));
					int y = Integer.parseInt(tf.getValue(3, 1));
					int w = Integer.parseInt(tf.getValue(4, 1));
					int h = Integer.parseInt(tf.getValue(5, 1));
					if (parent.equals("true")) {
						textureDir = (new File(imageDir).getParent());
						textureDir = StringUtilities.replaceSlashesInPath(textureDir);
					} else {
						textureDir = imageDir;
					}
					texture = new Texture(Wyvern.INTERPRETER_DIR + "/" + textureDir + "/" + textureName);
					image = new TextureRegion(texture, x, y, w, h);
					image.flip(false, true);
					
				} else {
					// This image is the texture itself.
					texture = new Texture(finalPath);
					image = new TextureRegion(texture, texture.getWidth(), texture.getHeight());
					image.flip(false, true);
				}
			} else {
				Warning.showWarning("TempImage: (e) " + imageDir + "/" + imageName + " is not a valid image!");
				texture = Wyvern.cache.getEmpty().getTexture();
				image = Wyvern.cache.getEmpty();
			}
		} else {
			// Image not found
			if (nullImage) {
				Warning.showWarning("TempImage: (e) null image loaded.");
			} else {
				Warning.showWarning("TempImage: (e) " + imageDir + "/" + imageName + " is missing!");
			}
			texture = Wyvern.cache.getEmpty().getTexture();
			image = Wyvern.cache.getEmpty();
		}
	}
	
	public TextureRegion getImage() {
		if (image == null) {
			return Wyvern.cache.getEmpty();
		} else {
			return image;
		}
	}
	
}
