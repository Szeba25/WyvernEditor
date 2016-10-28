package com.szeba.wyv.viewport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CustomViewport extends Viewport {

	public CustomViewport (int width, int height, Camera camera) {
		setWorldSize(width, height);
		setCamera(camera);
	}

	@Override
	public void update (int screenWidth, int screenHeight, boolean centerCamera) {
		// Fix "scaling" bug.
		if (screenWidth % 2 > 0) {
			screenWidth++;
		}
		if (screenHeight % 2 > 0) {
			screenHeight++;
		}
		
		// Continue...
		int bonusX = (screenWidth - 1124)/2;
		int bonusY = (screenHeight - 680)/2;
		
		setScreenSize(screenWidth, screenHeight);
		setWorldSize(screenWidth, screenHeight);
		
		setCamera(562+bonusX, 340+bonusY);
		
		apply(centerCamera);
		
	}

	private void setCamera(int x, int y) {
		getCamera().position.set(x, y, 0);
	}
	
	@Override
	public void apply(boolean centerCamera) {
		Gdx.gl.glViewport(getScreenX(), getScreenY(), getScreenWidth(), getScreenHeight());
		getCamera().viewportWidth = getWorldWidth();
		getCamera().viewportHeight = getWorldHeight();
		getCamera().update();
	}
	
}
