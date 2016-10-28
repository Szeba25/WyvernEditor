package com.szeba.wyv.screens.implemented;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.screens.SubScreen;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.ShapePainter;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.panels.TabbedBar;
import com.szeba.wyv.widgets.panels.entries.DatabaseEntriesPanel;
import com.szeba.wyv.widgets.panels.tileset.TilesetDatabaseEditor;

public class DatabaseScreen extends SubScreen {
	
	private TabbedBar tabbedBar;
	private HashMap<String, Widget> panels;
	
	private DatabaseEntriesPanel entriesdb;
	private TilesetDatabaseEditor tiledb;
	
	@Override
	public void init() {
		super.init();
		
		tabbedBar = new TabbedBar(0, 0, 5, 40, 0, 24);
		tabbedBar.setGrabbable(false);
		tabbedBar.setFocused(true);
		tabbedBar.setFocusLocked(true);
		
		tabbedBar.setButtonWidth(240);
		
		tabbedBar.addTabWithoutX("Entries", "Entries");
		tabbedBar.addTabWithoutX("Tilesets", "Tilesets");
		
		entriesdb = new DatabaseEntriesPanel(0, 0, 5, 70, 922, 525);
		entriesdb.setVisible(false);
		entriesdb.setModalsToCenter();
		
		tiledb = new TilesetDatabaseEditor(0, 0, 5, 70, 922, 525);
		tiledb.setVisible(false);
		
		addWidget(tabbedBar);
		addWidget(entriesdb);
		addWidget(tiledb);
		
		panels = new HashMap<String, Widget>();
		panels.put("Entries", entriesdb);
		panels.put("Tilesets", tiledb);
	}
	
	@Override
	public void screenDraw(SpriteBatch batch) {
		ShapePainter.drawFilledRectangle(batch, Palette.WIDGET_BKG4, 
				0, 0, 
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		super.screenDraw(batch);
	}
	
	@Override
	public void screenUpdate(int scrolled) {
		super.screenUpdate(scrolled);
		updateTabbedBar();
	}
	
	@Override
	public void resize(int width, int height) {
		tabbedBar.setW(width - 10);
		entriesdb.setH(height - 75);
		entriesdb.setW(width - 10);
		tiledb.setH(height - 75);
		tiledb.setW(width - 10);
		entriesdb.setModalsToCenter();
	}
	
	@Override
	public void cancelButtonEvent() {
		// Save variables
		Wyvern.database.var.save();
		// Revert the database from the hard disc
		System.out.println("Database: Reverting...");
		tiledb.reloadCanceledTilesets();
		entriesdb.updateData();
		entriesdb.closeAll();
		Wyvern.database.ent.reload();
	}
	
	@Override
	public void doneButtonEvent() {
		// Save variables
		Wyvern.database.var.save();
		// Save database...
		System.out.println("Database: Saving...");
		tiledb.saveTilesets();
		entriesdb.updateData();
		entriesdb.closeAll();
		Wyvern.database.ent.save();
	}
	
	private void updateTabbedBar() {
		if (tabbedBar.getActiveButton() != null) {
			if (!panels.get(tabbedBar.getActiveButton()).isVisible()) {
				for (Widget w : panels.values()) {
					w.setVisible(false);
				}
				panels.get(tabbedBar.getActiveButton()).setVisible(true);
			}
		}
	}
	
}
