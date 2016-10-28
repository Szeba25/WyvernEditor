package com.szeba.wyv.widgets.panels.pickers;

import java.awt.Point;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.event.Spriteset;
import com.szeba.wyv.data.geometry.Box;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.ShapePainter;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.utilities.TexturePainter;

public class SpriteFrame extends BaseFrame {

	private String fileDir;
	private String fileName;
	private Point spriteCoord;
	private Box spriteHigh;
	private Spriteset spriteset;
	
	private static Box spriteBox = new Box(0, 0, 0, 0);
	
	public SpriteFrame(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
		fileDir = "";
		fileName = "";
		spriteCoord = new Point(0, 0);
		spriteHigh = new Box(0, 0, 0, 0);
		spriteset = null;
	}
	
	@Override
	public void setFile(String fileDir, String fileName) {
		this.fileDir = fileDir;
		this.fileName = fileName;
		if (fileDir.length() > 0 && fileName.length() > 0) {
			spriteset = Wyvern.cache.getSpriteset(fileDir, fileName);
			spriteset.check(); // Important!
		} else {
			spriteset = null;
		}
		setSpriteCoord(0, 0);
	}
	
	@Override
	public String getFileDir() {
		return fileDir;
	}
	
	@Override
	public String getFileName() {
		return fileName;
	}
	
	public void setSpriteCoord(int x, int y) {
		spriteCoord.x = x;
		spriteCoord.y = y;
		refreshSpriteHigh(x, y);
	}
	
	public void refreshSpriteHigh() {
		refreshSpriteHigh(spriteCoord.x, spriteCoord.y);
	}
	
	private void refreshSpriteHigh(int x, int y) {
		if (spriteset != null) {
			refreshSpriteBox();
			
			int width = spriteBox.end.x / (spriteset.getMainSize().x);
			int height = spriteBox.end.y / (spriteset.getMainSize().y);
			
			spriteHigh.start.x = spriteBox.start.x + x*width;
			spriteHigh.start.y = spriteBox.start.y + y*height;
			spriteHigh.end.x = spriteHigh.start.x + width;
			spriteHigh.end.y = spriteHigh.start.y + height;
		}
	}

	public Point getSpriteCoord() {
		return spriteCoord;
	}
	
	public void setSpriteCoord(Point spriteCoord) {
		setSpriteCoord(spriteCoord.x, spriteCoord.y);
	}
	
	public String getSpriteCoordStr() {
		return spriteCoord.x + "x" + spriteCoord.y;
	}

	public void setSpriteCoordStr(String coord) {
		String[] splitted = StringUtilities.safeSplit(coord, "x");
		this.setSpriteCoord(Integer.parseInt(splitted[0]), Integer.parseInt(splitted[1]));
	}
	
	private void refreshSpriteBox() {
		if (spriteset != null) {
			spriteBox.start.x = TexturePainter.getRealBlitX(spriteset.getMain().getRegionWidth(), 
					spriteset.getMain().getRegionHeight(), getX(), getY(), getW());
			spriteBox.start.y = TexturePainter.getRealBlitY(spriteset.getMain().getRegionWidth(), 
					spriteset.getMain().getRegionHeight(), getX(), getY(), getW());
			spriteBox.end.x = TexturePainter.getRealW(spriteset.getMain().getRegionWidth(), 
					spriteset.getMain().getRegionHeight(), getW());
			spriteBox.end.y = TexturePainter.getRealH(spriteset.getMain().getRegionWidth(), 
					spriteset.getMain().getRegionHeight(), getW());
		}
	}
	
	private void setNewCoord() {
		int newx = 0;
		int newy = 0;
		
		refreshSpriteBox();
		int realX = spriteBox.start.x;
		int realY = spriteBox.start.y;
		int realW = spriteBox.end.x;
		int realH = spriteBox.end.y;
		
		int relativeMX = Wyvern.input.getX() - realX;
		int relativeMY = Wyvern.input.getY() - realY;
		
		newx = relativeMX / (realW/(spriteset.getMainSize().x));
		newy = relativeMY / (realH/(spriteset.getMainSize().y));
		
		if (newx > spriteset.getMainSize().x-1) {
			newx = spriteset.getMainSize().x-1;
		} else if (newx < 0) {
			newx = 0;
		}
		
		if (newy > spriteset.getMainSize().y-1) {
			newy = spriteset.getMainSize().y-1;
		} else if (newy < 0) {
			newy = 0;
		}
		
		setSpriteCoord(newx, newy);
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		if (Wyvern.input.isButtonPressed(0) && this.mouseInside() && spriteset != null) {
			this.setNewCoord();
		}
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		drawBackground(batch);
		if (spriteset != null) {
			TexturePainter.drawGraphics(batch, spriteset.getMain(), 
					spriteset.getMain().getRegionWidth(), 
					spriteset.getMain().getRegionHeight(), getX(), getY(), getW());
			
			FontUtilities.print(batch, spriteCoord.x + "/" + spriteCoord.y, getX(), getY());
			ShapePainter.drawRectangle(batch, getActiveBrdColor(), 
					spriteHigh.start.x, spriteHigh.start.y, spriteHigh.getWidth(), spriteHigh.getHeight());
		}
		drawOutline(batch);
	}
	
	@Override
	public void reset() {
		setFile("", "");
	}

}
