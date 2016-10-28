package com.szeba.wyv.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.container.Container;

/**
 * Base of all screens
 * @author Szeba
 */
public class GeneralScreen extends Container implements Screen {

	@Override
	public void init() {
		// If we initialize a screen, we must make it focused
		setFocused(true);
	}

	@Override
	public void enter() {
		resize();
	}

	@Override
	public void screenDraw(SpriteBatch batch) {
		draw(batch);
		drawAllTooltip(batch);
	}

	@Override
	public void screenUpdate(int scrolled) {
		update(scrolled);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void leave() {
	}

	@Override
	public void mainDraw(SpriteBatch batch) {
	}

	@Override
	public void mainUpdate(int scrolled) {
	}

	/**
	 * Resize the screen to fit the whole window.
	 */
	private void resize() {
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
}
