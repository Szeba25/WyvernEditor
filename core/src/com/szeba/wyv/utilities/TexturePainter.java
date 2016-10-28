package com.szeba.wyv.utilities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/** 
 * Basic texture painter for the editor
 * @author Szeba
 */
public final class TexturePainter {

	private TexturePainter() {}
	
	public static void drawGraphics(SpriteBatch batch, TextureRegion icon, int iconW, int iconH, 
			int bx, int by, int tileSize) {
		if (iconW > iconH) {
			int shorterSide = resizeValue(tileSize, (double)iconH, (double)iconW);
			batch.draw(icon, 
					bx, by + ((tileSize - shorterSide)/2), 
					tileSize, shorterSide);
		} else {
			int shorterSide = resizeValue(tileSize, (double)iconW, (double)iconH);
			batch.draw(icon,
					bx + ((tileSize - shorterSide)/2), by, 
					shorterSide, tileSize);
		}
	}
	
	/** 
	 * Gets the real blit X coordinate, when using drawTexture. (This method helps setting sprite directions)
	 */
	public static int getRealBlitX(int iconW, int iconH, int bx, int by, int tileSize) {
		if (iconW > iconH) {
			return bx;
		} else {
			int shorterSide = resizeValue(tileSize, (double)iconW, (double)iconH);
			return bx + ((tileSize - shorterSide)/2);
		}
	}
	
	/** 
	 * Gets the real blit Y coordinate, when using drawTexture. (This method helps setting sprite directions)
	 */
	public static int getRealBlitY(int iconW, int iconH, int bx, int by, int tileSize) {
		if (iconW > iconH) {
			int shorterSide = resizeValue(tileSize, (double)iconH, (double)iconW);
			return by + ((tileSize - shorterSide)/2);
		} else {
			return by;
		}
	}
	
	/** 
	 * Gets the real width, when using drawTexture. (This method helps setting sprite directions)
	 */
	public static int getRealW(int iconW, int iconH, int tileSize) {
		if (iconW > iconH) {
			return tileSize;
		} else {
			return resizeValue(tileSize, (double)iconW, (double)iconH);
		}
	}
	
	/** 
	 * Gets the real height, when using drawTexture. (This method helps setting sprite directions)
	 */
	public static int getRealH(int iconW, int iconH, int tileSize) {
		if (iconW > iconH) {
			return resizeValue(tileSize, (double)iconH, (double)iconW);
		} else {
			return tileSize;
		}
	}
	
	public static int resizeValue(int value, double dividend, double divisor) {
		return (int) (value * ((double)dividend / (double)divisor));
	}
	
}
