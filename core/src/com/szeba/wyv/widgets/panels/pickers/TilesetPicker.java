package com.szeba.wyv.widgets.panels.pickers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.ext.list.DirListTilesets;

public class TilesetPicker extends Widget {

	protected DirListTilesets tilesetList;
	protected int lastSelectedID;
	protected Button emptyButton;
	
	public TilesetPicker(int ox, int oy, int rx, int ry) {
		super(ox, oy, rx, ry, 500, 550);
		
		tilesetList = new DirListTilesets(getX(), getY(), 5, 20, 160, (getH()/16)-2, 
				Wyvern.INTERPRETER_DIR + "/resources/tilesets", Wyvern.INTERPRETER_DIR + "/resources/tilesets");
		emptyButton = new Button(getX(), getY(), getW()-100, getH()-30, 95, 25, "set empty");
		
		// Add items to the focuslist
		addWidget(emptyButton);
		addWidget(tilesetList);
		
		lastSelectedID = -1;
	}

	@Override
	public void setFocused(boolean value) {
		super.setFocused(value);
		if (!value) {
			emptyButton.setFocused(value);
			tilesetList.setFocused(value);
		} else {
			tilesetList.setFocused(value);
		}
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		drawBackground(batch);
		drawSelectedTileset(batch);
		drawOutline(batch);
	}

	@Override
	public void mainUpdate(int scrolled) {
		setTileset();
		Signal s = emptyButton.getSignal();
		if (s != null) {
			this.reset();
		}
	}

	public String getTilesetName() {
		if (tilesetList.getSelected() != null) {
			return tilesetList.getSelected().getOriginalName();
		} else {
			return "";
		}
	}
	
	public void setTilesetByName(String name) {
		this.reset();
		if (name.length() > 0) {
			tilesetList.selectElementByName(name);
		}
	}
	
	public void reset() {
		tilesetList.selectIndex(-1);
		lastSelectedID = -1;
	}
	
	protected void setTileset() {
		if (tilesetList.getSelectedID() != lastSelectedID && tilesetList.getSelected() != null) {
			
			String tilesetName = tilesetList.getSelected().getOriginalName();
			lastSelectedID = tilesetList.getSelectedID();
			
			Wyvern.cache.getTileset(tilesetName).check();
		}
	}

	private void drawSelectedTileset(SpriteBatch batch) {
		if (tilesetList.getSelected() != null) {
			// Print tileset image
			batch.draw(Wyvern.cache.getTileset(tilesetList.getSelected().getOriginalName()).getTiles1(),
					getX()+180, getY() + 20, 80, 480);
			batch.draw(Wyvern.cache.getTileset(tilesetList.getSelected().getOriginalName()).getTiles2(),
					getX()+270, getY() + 20, 80, 480);
			for (int i = 0; i < 16 ; i++) {
				batch.draw(Wyvern.cache.getTileset(tilesetList.getSelected().getOriginalName()).getAutotileIcon(i),
					getX()+360, getY() + 20 + i*20, 16, 16);
			}
			for (int i = 16; i < 32 ; i++) {
				batch.draw(Wyvern.cache.getTileset(tilesetList.getSelected().getOriginalName()).getAutotileIcon(i),
					getX()+380, getY() + 20 + (i-16)*20, 16, 16);
			}
		}
	}
	
}
