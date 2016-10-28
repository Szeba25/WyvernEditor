package com.szeba.wyv.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Main screen interface.
 * @author Szeba
 */
public interface Screen {

	public void init();
	public void enter();
	public void screenDraw(SpriteBatch batch);
	public void screenUpdate(int scrolled);
	public void resize(int width, int height);
	public void leave();
	
}
