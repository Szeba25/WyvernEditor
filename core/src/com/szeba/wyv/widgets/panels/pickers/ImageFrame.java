package com.szeba.wyv.widgets.panels.pickers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.cache.TempImage;
import com.szeba.wyv.utilities.TexturePainter;

public class ImageFrame extends BaseFrame {

	private String fileDir;
	private String fileName;
	private TempImage image;
	
	public ImageFrame(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
		fileDir = "";
		fileName = "";
		image = null;
	}
	
	@Override
	public void setFile(String fileDir, String fileName) {
		this.fileDir = fileDir;
		this.fileName = fileName;
		
		if (image != null) {
			image.dispose();
		}
		
		if (fileDir.length() > 0 && fileName.length() > 0) {
			image = new TempImage(fileDir, fileName);
		} else {
			image = null;
		}
	}

	@Override
	public void reset() {
		setFile("", "");
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
	public void mainDraw(SpriteBatch batch) {
		drawBackground(batch);
		if (image != null) {
			TexturePainter.drawGraphics(batch, image.getImage(),
					image.getWidth(),
					image.getHeight(), getX(), getY(), getW());
		}
		drawOutline(batch);
	}
	
}
