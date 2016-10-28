package com.szeba.wyv.screens.implemented;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.screens.Screen;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.widgets.ext.Warning;

/** 
 * The first room shown when starting the editor. 
 * @author Szeba
 */
public class SplashScreen implements Screen {

	// The splashscreen is loaded before the cache, so we will load some temporary textures here.
	private Texture fillerTexture;
	private TextureRegion filler;
	private Texture splashTexture;
	private TextureRegion splash;
	private BitmapFont font;
	
	// The splashscreen will initialize other screens
	private ArrayList<Screen> screens;
	
	// The splash images alpha value
	private float alphaValue;
	
	// The fader alpha value
	private float faderValue;
	
	// The wait time in seconds
	private float waitTime;
	
	// The state of this room (0 is fading in, 1 is waiting, 2 is loading, 3 is fading out, and leaving)
	private int splashState;
	
	public SplashScreen() {
		// We load the splash texture, and font
		fillerTexture = new Texture(Wyvern.DIRECTORY+"/core files/splashfiller.png");
		filler = new TextureRegion(fillerTexture, 0, 0, 16, 16);
		filler.flip(false, true);
		splashTexture = new Texture(Wyvern.DIRECTORY+"/core files/splash.png");
		splash = new TextureRegion(splashTexture, 0, 0, 640, 640);
		splash.flip(false, true);
		font = new BitmapFont(true);
		// Set the splash texture's opacity
		alphaValue = 0;
		faderValue = 0;
		waitTime = 0.5f;
		splashState = 0;
	}
	
	@Override
	public void init() {
		// Assign variables
		alphaValue = 0;
		faderValue = 0;
		waitTime = 0.5f;
		splashState = 0;
	}
	
	@Override
	public void enter() {
		
	}

	@Override
	public void screenDraw(SpriteBatch batch) {
		
		batch.setColor(Palette.WHITE);
		batch.draw(filler, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		batch.setColor(1.0f, 1.0f, 1.0f, alphaValue);
		batch.draw(splash, (Gdx.graphics.getWidth()/2)-320, (Gdx.graphics.getHeight()/2)-320);
		
		batch.setColor(0f, 0f, 0f, faderValue);
		batch.draw(filler, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		batch.setColor(Palette.BATCH);
		
		if (splashState < 2) {
			font.draw(batch, "Loading cache...", 5, 5);
		} else {
			font.draw(batch, "Starting editor...", 5, 5);
		}
	}
	
	@Override
	public void screenUpdate(int scrolled) {
		switch (splashState) {
		case 0:
			state0();
			break;
		case 1:
			state1();
			break;
		case 2:
			state2();
			break;
		case 3:
			state3();
			break;
		}
	}
	
	@Override
	public void resize(int width, int height) {
	}
	
	@Override
	public void leave() {
	}
	
	private void state0() {
		if (alphaValue < 1.0f) {
			alphaValue += Wyvern.getDelta();
			if (alphaValue > 1.0f) {
				alphaValue = 1.0f;
			}
		} else {
			splashState = 1;
		}
	}
	
	private void state1() {
		if (waitTime > 0) {
			waitTime -= Wyvern.getDelta();
		} else {
			splashState = 2;
		}
	}
	
	public void setScreens(ArrayList<Screen> screens) {
		this.screens = screens;
	}
	
	private void state2() {
		// We initialize the cache here
		Wyvern.cache.init();
		// And the database...
		Wyvern.database.init();
		// We also initialize other screens
		screens.get(1).init();
		screens.get(2).init();
		screens.get(3).init();
		// Create the warning window
		Warning.initWarning();
		// Switch to state 3
		splashState = 3;
	}
	
	private void state3() {
		if (faderValue < 1.0f) {
			faderValue += Wyvern.getDelta();
			if (faderValue > 1.0f) {
				faderValue = 1.0f;
			}
		} else {
			// Switch out of this room
			Warning.widget.setToCenter();
			Wyvern.screenChanger = 1;
		}
	}

}
