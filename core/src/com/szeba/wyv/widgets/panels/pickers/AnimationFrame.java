package com.szeba.wyv.widgets.panels.pickers;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.cache.TempImage;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.utilities.FileUtilities;
import com.szeba.wyv.utilities.TexturePainter;
import com.szeba.wyv.widgets.ext.Warning;

public class AnimationFrame extends BaseFrame {
	
	private String fileDir;
	private String fileName;
	
	private ArrayList<TempImage> frames;
	private int currentFrame;
	private float untilNextFrame;
	
	public AnimationFrame(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
		
		fileDir = "";
		fileName = "";
		
		frames = new ArrayList<TempImage>();
		currentFrame = 0;
		untilNextFrame = 0;
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		this.drawBackground(batch);
		
		if (frames.size() > 0) {
			untilNextFrame -= Wyvern.getDelta();
			if (untilNextFrame <= 0) {
				untilNextFrame = getFrameTime();
				currentFrame++;
				if (currentFrame > frames.size()-1) {
					currentFrame = 0;
				}
			}
			
			TexturePainter.drawGraphics(batch, frames.get(currentFrame).getImage(), 
					frames.get(currentFrame).getWidth(), 
					frames.get(currentFrame).getHeight(), getX(), getY(), getW());
		}
		
		this.drawOutline(batch);
	}
	
	@Override
	public String getFileDir() {
		return fileDir;
	}
	
	@Override
	public String getFileName() {
		return fileName;
	}
	
	@Override
	public void setFile(String dir, String name) {
		
		reset();
		
		String finalAccessPath = Wyvern.INTERPRETER_DIR + "/" + dir + "/" + name;
		
		if (dir.length() > 0 && name.length() > 0) {
			
			fileDir = dir;
			fileName = name;
			
			if (FileUtilities.exists(finalAccessPath)) {
				TextFile file = new TextFile(finalAccessPath + "/metadata.wdat");
				
				frames = new ArrayList<TempImage>();
				
				for (int f = 0; f < file.getLine(0).size(); f++) {
					frames.add(new TempImage(fileDir + "/" + fileName, file.getValue(0, f)));
				}
				
				currentFrame = 0;
				untilNextFrame = getFrameTime();
			} else {
				// Add a null image
				Warning.showWarning("Animation: " + dir + "/" + name + " is missing.");
				frames = new ArrayList<TempImage>();
				frames.add(new TempImage(null, null));
				
				currentFrame = 0;
				untilNextFrame = getFrameTime();
			}
			
		}
	}
	
	private float getFrameTime() {
		return 0.2f;
	}

	@Override
	public void reset() {
		fileDir = "";
		fileName = "";
		unloadImages();
		frames = new ArrayList<TempImage>();
		currentFrame = 0;
		untilNextFrame = 0;
	}

	private void unloadImages() {
		// Unloads not used image files from memory.
		for (TempImage i : frames) {
			i.dispose();
		}
	}
	
}
