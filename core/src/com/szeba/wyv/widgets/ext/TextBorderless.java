package com.szeba.wyv.widgets.ext;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.widgets.Text;

public class TextBorderless extends Text {

	public TextBorderless(int ox, int oy, int rx, int ry, int w, int h, String text) {
		super(ox, oy, rx, ry, w, h, text);
	}
	
	@Override
	public void drawOutline(SpriteBatch batch) {
		
	}

}
