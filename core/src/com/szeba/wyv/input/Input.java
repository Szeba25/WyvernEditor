package com.szeba.wyv.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

/** 
 * The input manager for the editor. This input manager can handle keypresses, and keys holded down,
 * for the mouse and the keyboard as well. 
 * @author Szeba
 */
public class Input implements InputProcessor {

	// Double click time
	public static double doubleClick = 0.4;
	public static double delay1 = 0.3;
	public static double repeat1 = 0.025;
	public static double repeat2 = 0.075;
	
	// Mouse buttons.
	private boolean leftButtonJustPressed;
	private boolean rightButtonJustPressed;
	private boolean leftButtonReleased;
	// Keyboard buttons.
	private boolean enterReleased;
	private boolean spaceReleased;
	
	// Scrolling values
	private int scrolled;
	private boolean isScrolled;
	
	// Delta values
	private int deltaX;
	private int oldMouseX;
	private int deltaY;
	private int oldMouseY;
	
	// Keyboard input mapping
	private Character typedChar;
	
	/**
	 * Constructs a new input with the default values.
	 */
	public Input() {
		// Initialize the buttons and keys.
		leftButtonJustPressed = false;
		rightButtonJustPressed = false;
		leftButtonReleased = false;
		enterReleased = false;
		spaceReleased = false;
		
		// Scrolling variables
		scrolled = 0;
		isScrolled = false;
		
		// Delta variables
		deltaX = 0;
		oldMouseX = 0;
		deltaY = 0;
		oldMouseY = 0;
		
		// The currently typed character
		typedChar = null;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}
	
	@Override
	public boolean keyTyped(char character) {
		typedChar = character;
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.ENTER) {
			enterReleased = true;
		}
		if (keycode == Keys.SPACE) {
			spaceReleased = true;
		}
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}
	
	@Override
	public boolean scrolled(int amount) {
		scrolled = amount;
		isScrolled = true;
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (button == Buttons.LEFT) {
			leftButtonJustPressed = true;
		} else if (button == Buttons.RIGHT) {
			rightButtonJustPressed = true;
		}
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == Buttons.LEFT) {
			leftButtonReleased = true;
		}
		return false;
	}
	
	public void resetTypedChar() {
		typedChar = null;
	}
	
	public boolean isLeftButtonReleased() {
		return leftButtonReleased;
	}
	
	public boolean isEnterReleased() {
		return enterReleased;
	}
	
	public boolean isSpaceReleased() {
		return spaceReleased;
	}
	
	public boolean isHotkeyRestricted() {
		// We restrict hotkeys when these buttons are pressed down.
		if (isButtonHold(Buttons.LEFT) || isButtonHold(Buttons.RIGHT)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Reset the key released variables.
	 */
	public void update() {
		leftButtonJustPressed = false;
		rightButtonJustPressed = false;
		leftButtonReleased = false;
		enterReleased = false;
		spaceReleased = false;
	}
	
	/**
	 * Update the mouse positions to get the new delta value.
	 */
	public void updateDelta() {
		deltaY = getY()-oldMouseY;
		oldMouseY = getY();
		deltaX = getX()-oldMouseX;
		oldMouseX = getX();
	}

	/** 
	 * This method updates the scrolling value for one frame. Call this in the update loop. 
	 */
	public int updateScrolling() {
		if (isScrolled) {
			int returnVal = scrolled;
			isScrolled = false;
			scrolled = 0;
			return returnVal;
		}
		return 0;
	}
	
	public int getDeltaX() {
		return deltaX;
	}

	public int getDeltaY() {
		return deltaY;
	}

	public String getTypedChar() {
		if (typedChar == null) {
			return null;
		} else {
			int type = Character.getType(typedChar);
			if (type == 15) {
				typedChar = null;
				return null;
			} else {
				String value = Character.toString(typedChar);
				typedChar = null;
				return value;
			}
		}
	}

	public int getX() {
		return (int) Gdx.input.getX();
	}

	public int getY() {
		return (int) Gdx.input.getY();
	}

	public boolean isButtonHold(int button) {
		return Gdx.input.isButtonPressed(button);
	}

	public boolean isButtonPressed(int button) {
		if (button == 0 && leftButtonJustPressed) {
			return true;
		}
		if (button == 1 && rightButtonJustPressed) {
			return true;
		}
		return false;
	}

	public boolean isKeyHold(int key) {
		return Gdx.input.isKeyPressed(key);
	}
	
	public boolean isKeyPressed(int key) {
		if (Gdx.input.isKeyJustPressed(key)) {
			return true;
		}
		return false;
	}
	
}
