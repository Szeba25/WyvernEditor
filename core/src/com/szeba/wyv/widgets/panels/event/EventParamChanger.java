package com.szeba.wyv.widgets.panels.event;

import java.awt.Point;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Text;
import com.szeba.wyv.widgets.TextField;
import com.szeba.wyv.widgets.dynamic.DynamicPanel;
import com.szeba.wyv.widgets.panels.pickers.SpriteFrame;

public class EventParamChanger extends DynamicPanel {

	// Spriteset UI
	private Button changeSprite;
	private SpriteFrame spritePanel;
	private TextField eventName;
	private Text eventID;
	private EventSpriteChanger spriteChanger;
	
	public EventParamChanger(int ox, int oy, int rx, int ry, int w, int h) {
		super(null, ox, oy, rx, ry, w, h);
		
		this.loadWidgets(Wyvern.INTERPRETER_DIR + "/preferences/events/event_params_widget.wdat");
		
		changeSprite = new Button(getX(), getY(), 290, 5, 200, 20, "change sprite");
		spritePanel = new SpriteFrame(getX(), getY(), 290, 30, 200, 200);
		eventName = new TextField(getX(), getY(), 50, 5, 230, 1);
		eventID = new Text(getX(), getY(), 50, 25, 120, 16, "");
		spriteChanger = new EventSpriteChanger(getX(), getY(), 0, 0);
		
		// Remove the done and cancel button
		this.removeWidget(0);
		this.removeWidget(0);
		
		this.addModalWidget(spriteChanger);
		
		this.addWidget(changeSprite);
		this.addWidget(spritePanel);
		this.addWidget(eventName);
		this.addWidget(eventID);
		
		// We dont want enter focus inside this.
		this.setEnterFocusDefault(null);
		
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		drawBackground(batch);
		FontUtilities.print(batch, "Name:", getX()+2, getY()+8);
		FontUtilities.print(batch, "ID:", getX()+2, getY()+28);
		drawOutline(batch);
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		super.mainUpdate(scrolled);
		Signal sg;
		sg = changeSprite.getSignal();
		if (sg != null) {
			spriteChanger.getSpriteFrame().setFile(spritePanel.getFileDir(), spritePanel.getFileName());
			spriteChanger.getSpriteFrame().setSpriteCoord(
					spritePanel.getSpriteCoord().x, spritePanel.getSpriteCoord().y);
			spriteChanger.setName(spritePanel.getFileName());
			spriteChanger.scrollListToFile();
			spriteChanger.setVisible(true);
		}
		sg = spriteChanger.getSignal();
		if (sg != null && sg.getType() != Signal.T_INVALID_DIRLIST) {
			spritePanel.setFile(sg.getParam(0), sg.getParam(1));
			spritePanel.setSpriteCoordStr(sg.getParam(2));
		}
	}

	/**
	 * Update a sprite in an image panel
	 */
	private void updateSprite(SpriteFrame panel, int page) {
		panel.setFile(Wyvern.cache.getEditedEvent().getSpriteDir(page),
				Wyvern.cache.getEditedEvent().getSpriteName(page));
		panel.setSpriteCoord(Wyvern.cache.getEditedEvent().getPage(page).getSpriteCoord());
	}
	
	public void updateSprites(int page) {
		updateSprite(spritePanel, page);
		updateSprite(spriteChanger.getSpriteFrame(), page);
		spriteChanger.setName(Wyvern.cache.getEditedEvent().getSpriteName(page));
		spriteChanger.scrollListToFile();
	}
	
	public String getSpriteDir() {
		return spritePanel.getFileDir();
	}
	
	public String getSpriteName() {
		return spritePanel.getFileName();
	}
	
	public Point getSpriteCoord() {
		return spritePanel.getSpriteCoord();
	}
	
	/**
	 * Update the name field
	 */
	public void updateName() {
		eventName.setText(Wyvern.cache.getEditedEvent().getName());
	}
	
	public void updateID() {
		eventID.setText(Wyvern.cache.getEditedEvent().getSigID());
	}

	public String getEventName() {
		return eventName.getText();
	}
	
	public void disableNamePanel() {
		eventName.setFocused(false);
		eventName.setFocusLocked(true);
	}
	
	public void enableNamePanel() {
		eventName.setFocused(false);
		eventName.setFocusLocked(false);
	}

}
