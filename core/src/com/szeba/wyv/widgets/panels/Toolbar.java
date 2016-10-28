package com.szeba.wyv.widgets.panels;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.ShapePainter;
import com.szeba.wyv.widgets.Widget;

public class Toolbar extends Widget {

	private boolean clickHappened;
	private ArrayList<String> buttonList;
	private ArrayList<String> tooltipList;
	private ArrayList<Boolean> markList;
	private ArrayList<Boolean> disabledList;
	
	private HashMap<Integer, Integer> disabledIcons;
	
	private int lastButton;
	
	public Toolbar(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
		
		clickHappened = false;
		buttonList = new ArrayList<String>();
		tooltipList = new ArrayList<String>();
		markList = new ArrayList<Boolean>();
		disabledList = new ArrayList<Boolean>();
		
		disabledIcons = new HashMap<Integer, Integer>();
		
		lastButton = -1;
	}

	@Override
	public void mainDraw(SpriteBatch batch) {
		drawBackground(batch);
		
		for (int i = 0; i < buttonList.size(); i++) {
			
			TextureRegion icon = Wyvern.cache.getToolIcon(i);
			if (disabledList.get(i)) {
				icon = Wyvern.cache.getDisabledToolIcon(this.disabledIcons.get(i));
			}
			
			if (getIndexByX() == i && Wyvern.input.isButtonHold(0) && !disabledList.get(i)) {
				// Draw by 1 pixel offset!
				batch.draw(icon, getX()+(i*30)+1, getY()+1);
			} else {
				batch.draw(icon, getX()+(i*30), getY());
			}
			if (markList.get(i)) {
				ShapePainter.drawFilledRectangle(batch, Palette.TOOLBAR_MARK, getX()+i*30, getY(), 30, 30);
			}
		}
		ShapePainter.drawFilledRectangle(batch, getHighColor(), getX()+getIndexByX()*30, 
				getY(), 30, 30);
		
		drawOutline(batch);
	}

	@Override
	public void mainUpdate(int scrolled) {
		if (Wyvern.input.isButtonPressed(Buttons.LEFT) && this.mouseInside()) {
			clickHappened = true;
		}
		if (Wyvern.input.isLeftButtonReleased() && clickHappened) {
			clickHappened = false;
			int index = getIndexByX();
			if (index >= 0 && index < buttonList.size()) {
				setSignal(new Signal(Signal.T_DEFAULT, buttonList.get(index)));
				this.setTooltip("");
				resetTooltipVisibility();
			}
		}
		if (lastButton != getIndexByX()) {
			lastButton = getIndexByX();
			if (lastButton >= 0 && lastButton < tooltipList.size()) {
				this.setTooltip(tooltipList.get(lastButton));
			} else {
				this.setTooltip("");
			}
			resetTooltipVisibility();
		}
	}
	
	@Override
	public Color getActiveBrdColor() {
		return Palette.WIDGET_PASSIVE_BRD;
	}

	public void addButton(String text, String tooltip, int icon) {
		buttonList.add(text);
		tooltipList.add(tooltip);
		markList.add(false);
		disabledList.add(false);
	}

	public boolean getMarked(int index) {
		return markList.get(index);
	}
	
	public void setMarked(int index, boolean b) {
		markList.set(index, b);
	}
	
	public boolean getDisabled(int index) {
		return disabledList.get(index);
	}
	
	public void setDisabled(int index, boolean b) {
		disabledList.set(index, b);
	}
	
	public int getSize() {
		return buttonList.size();
	}

	private int getIndexByX() {
		if (mouseInside()) {
			return ((Wyvern.input.getX()-getX())/30);
		} else {
			return -1;
		}
	}

	public void setDisabledIcon(int i, int j) {
		this.disabledIcons.put(i, j);
	}

}
