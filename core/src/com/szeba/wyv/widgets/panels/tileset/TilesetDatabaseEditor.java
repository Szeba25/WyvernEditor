package com.szeba.wyv.widgets.panels.tileset;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.data.tiles.Tileset;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.ShapePainter;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.ext.BulletBoard;
import com.szeba.wyv.widgets.ext.TextBorderless;
import com.szeba.wyv.widgets.ext.list.DirListTilesets;
import com.szeba.wyv.widgets.ext.textfield.IntField;

public class TilesetDatabaseEditor extends Widget {

	private DirListTilesets tilesetList;
	private TilesetPanel_DB tilesetPanel;
	private BulletBoard toolBullet;
	private TextBorderless terrainText;
	private IntField terrainTag;
	
	public TilesetDatabaseEditor(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
		
		tilesetList = new DirListTilesets(getX(), getY(), 5, 20, 160, (getH()/16)-3,
				Wyvern.INTERPRETER_DIR + "/resources/tilesets", Wyvern.INTERPRETER_DIR + "/resources/tilesets");
		
		tilesetPanel = new TilesetPanel_DB(getX(), getY(), 200, 20, 272, getH()-40);
		tilesetPanel.recalculate(3200, 16);
		tilesetPanel.setScrollFocus(true);
		
		ArrayList<String> ar = new ArrayList<String>();
		ar.add("Obstructions");
		ar.add("Obstructions (4-way)");
		ar.add("Layer data");
		ar.add("Terrain data");
		ar.add("Animations");
		
		toolBullet = new BulletBoard(getX(), getY(), 600, 20, 240, ar);
		
		terrainText = new TextBorderless(getX(), getY(), 600, 160, 80, 16, "Terrain tag");
		terrainTag = new IntField(getX(), getY(), 600, 180, 60, "N", 9999);
		terrainTag.setText("0");
		terrainText.setVisible(false);
		terrainTag.setVisible(false);
		
		addWidget(tilesetList);
		addWidget(tilesetPanel);
		addWidget(toolBullet);
		addWidget(terrainText);
		addWidget(terrainTag);
	}

	@Override
	public void mainUpdate(int scrolled) {
		Signal sg;
		sg = tilesetList.getSignal();
		if (sg != null) {
			tilesetPanel.setActiveTileset(sg.getParam(0));
		}
		
		tilesetPanel.changeTool(toolBullet.getSelectedID());
		
		tilesetPanel.setTerrainTag(this.terrainTag.getValue());
		
		if (toolBullet.getSelectedID() != 3) {
			terrainText.setVisible(false);
			terrainTag.setVisible(false);
		} else {
			terrainText.setVisible(true);
			terrainTag.setVisible(true);
		}
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		this.drawBackground(batch);
	}
	
	@Override
	public void overDraw(SpriteBatch batch) {
		drawTilesetHider(batch);
		this.drawOutline(batch);
	}
	
	@Override
	public void setH(int h) {
		super.setH(h);
		tilesetList.setH((getH()/16)-3);
		tilesetList.resetElementsKeepSelection();
		tilesetPanel.setH(getH()-40);
		tilesetPanel.fullReset(3200);
	}
	
	public void saveTilesets() {
		// Save tilesets
		for (Tileset ts : Wyvern.cache.getTilesets().values()) {
			if (ts.isChanged()) {
				ts.save();
			}
		}
	}
	
	public void reloadCanceledTilesets() {
		for (Tileset ts : Wyvern.cache.getTilesets().values()) {
			if (ts.isChanged()) {
				ts.reloadData();
			}
		}
	}
	
	private void drawTilesetHider(SpriteBatch batch) {
		// Main
		ShapePainter.drawFilledRectangle(batch, getBkgColor(), 
				tilesetPanel.getX(), getY()+1, 272, 19);
		ShapePainter.drawFilledRectangle(batch, getBkgColor(), 
				tilesetPanel.getX(), getY()+getH()-20, 272, 19);
		// OOW
		ShapePainter.drawFilledRectangle(batch, Palette.WIDGET_BKG4, 
				tilesetPanel.getX(), 0, 272, 70);
		ShapePainter.drawFilledRectangle(batch, Palette.WIDGET_BKG4, 
				tilesetPanel.getX(), getY()+getH(), 272, 6);
	}
	
}
