package com.szeba.wyv.widgets.panels.map;

import java.io.File;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.data.maps.Cell;
import com.szeba.wyv.data.maps.GameMap;
import com.szeba.wyv.utilities.FileUtilities;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.ShapePainter;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.ext.Warning;
import com.szeba.wyv.widgets.ext.list.DirListTilesets;
import com.szeba.wyv.widgets.ext.textfield.FileNameField;
import com.szeba.wyv.widgets.ext.textfield.IntField;

public class NewMapPanel extends Widget {

	private FileNameField nameField;
	private IntField cellSizeW;
	private IntField cellSizeH;
	private IntField maxCellX;
	private IntField maxCellY;
	private IntField startingCellX;
	private IntField startingCellY;
	private DirListTilesets tilesetPicker;
	
	private String mapPath;
	
	private Button doneButton;
	private Button cancelButton;
	
	public NewMapPanel(int ox, int oy, int rx, int ry) {
		super(ox, oy, rx, ry, 515, 430);
		
		nameField = new FileNameField(getX(), getY(), 10, 30, 300);
		
		cellSizeW = new IntField(getX(), getY(), 80, 70, 40, "Z+", 9999);
		cellSizeH = new IntField(getX(), getY(), 80, 95, 40, "Z+", 9999);
		
		maxCellX = new IntField(getX(), getY(), 80, 135, 40, "Z+", 9999);
		maxCellY = new IntField(getX(), getY(), 80, 160, 40, "Z+", 9999);
		
		startingCellX = new IntField(getX(), getY(), 80, 200, 40, "N", 9998);
		startingCellY = new IntField(getX(), getY(), 80, 225, 40, "N", 9998);
		
		tilesetPicker = new DirListTilesets(getX(), getY(), 150, 60,
				160, 20, Wyvern.INTERPRETER_DIR + "/resources/tilesets", Wyvern.INTERPRETER_DIR + "/resources/tilesets");
		tilesetPicker.selectIndex(0);
		
		mapPath = "/maps";
		
		doneButton = new Button(getX(), getY(), 365, 405, 70, 20, "done");
		cancelButton = new Button(getX(), getY(), 440, 405, 70, 20, "cancel");
		
		this.addWidget(nameField);
		this.addWidget(cellSizeW);
		this.addWidget(cellSizeH);
		this.addWidget(maxCellX);
		this.addWidget(maxCellY);
		this.addWidget(startingCellX);
		this.addWidget(startingCellY);
		this.addWidget(tilesetPicker);
		this.addWidget(doneButton);
		this.addWidget(cancelButton);
		
		this.setEnterFocusDefault(doneButton);
		this.setEnterFocusRestricted(cancelButton);
		
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		if (cancelButton.getSignal() != null) {
			setVisible(false);
		} else if (doneButton.getSignal() != null) {
			createMap();
		}
	}

	@Override
	public void mainDraw(SpriteBatch batch) {
		drawBackground(batch);
		drawTexts(batch);
		drawSelectedTileset(batch);
		drawOutline(batch);
	}
	
	public void set(String path) {
		mapPath = path;
		nameField.setText("");
		cellSizeW.setText("20");
		cellSizeH.setText("15");
		maxCellX.setText("1");
		maxCellY.setText("1");
		startingCellX.setText("0");
		startingCellY.setText("0");
		for (Widget w : getWidgets()) {
			w.setFocused(false);
		}
		nameField.setFocused(true);
	}
	
	private void drawTexts(SpriteBatch batch) {
		FontUtilities.print(batch, "Map name", getX()+10, getY()+5);
		FontUtilities.print(batch, "cell width", getX()+10, 3+getY()+70);
		FontUtilities.print(batch, "cell height", getX()+10, 3+getY()+95);
		FontUtilities.print(batch, "cell no. x", getX()+10, 3+getY()+135);
		FontUtilities.print(batch, "cell no. y", getX()+10, 3+getY()+160);
		FontUtilities.print(batch, "starting x", getX()+10, 3+getY()+200);
		FontUtilities.print(batch, "starting y", getX()+10, 3+getY()+225);
	}

	private void drawSelectedTileset(SpriteBatch batch) {
		if (tilesetPicker.getSelected() != null) {
			// Print tileset image
			batch.draw(Wyvern.cache.getTileset(tilesetPicker.getSelected().getOriginalName()).getTiles1(),
					getX()+330, getY() + 20, 60, 360);
			ShapePainter.drawRectangle(batch, getPassiveBrdColor(), getX()+330, getY()+20, 60, 360);
			batch.draw(Wyvern.cache.getTileset(tilesetPicker.getSelected().getOriginalName()).getTiles2(),
					getX()+400, getY() + 20, 60, 360);
			ShapePainter.drawRectangle(batch, getPassiveBrdColor(), getX()+400, getY()+20, 60, 360);
			for (int i = 0; i < 16 ; i++) {
				batch.draw(Wyvern.cache.getTileset(tilesetPicker.getSelected().getOriginalName()).getAutotileIcon(i),
					getX()+470, getY() + 20 + i*20, 16, 16);
			}
			for (int i = 16; i < 32 ; i++) {
				batch.draw(Wyvern.cache.getTileset(tilesetPicker.getSelected().getOriginalName()).getAutotileIcon(i),
					getX()+490, getY() + 20 + (i-16)*20, 16, 16);
			}
		}
	}

	private boolean anythingEmpty() {
		if (nameField.getText().length() > 0 &&
				cellSizeW.getText().length() > 0 &&
				cellSizeH.getText().length() > 0 &&
				maxCellX.getText().length() > 0 &&
				maxCellY.getText().length() > 0 &&
				startingCellX.getText().length() > 0 &&
				startingCellY.getText().length() > 0 &&
				tilesetPicker.getSelected() != null) {
			return false;
		}
		return true;
	}
	
	private void createMap() {
		if (anythingEmpty()) {
			Warning.showWarning("Please fill all the required fields.");
			// Go back to the namefield
			this.doneButton.setFocused(false);
			this.nameField.setFocused(true);
		} else if (invalidStarting()) {
			Warning.showWarning("Starting cell coordinate refers to an invalid cell. (greater than maximum cell count)");
			// Go back to the starting cell field
			this.doneButton.setFocused(false);
			this.startingCellX.setFocused(true);
		} else {
			String finalPath = Wyvern.INTERPRETER_DIR + "/" + mapPath + "/" + nameField.getText() + "@map";
			
			if (nameField.getFileName().length() == 0) {
				nameField.showWarning();
				return;
			}
			
			File f = new File(finalPath);
			if (!f.exists()) {
				
				// Create folder for the map
				String startingCellName = startingCellX.getText() + "x" + startingCellY.getText();
				FileUtilities.createFolders(finalPath + "/" + startingCellName);
				
				// Create metadata
				TextFile t = new TextFile(finalPath + "/map_metadata.wdat", null);
				t = GameMap.constructMetadata(t, cellSizeW.getText(), cellSizeH.getText(), 
						maxCellX.getText(), maxCellY.getText(), 
						startingCellX.getText(), startingCellY.getText(), 
						Integer.toString(Integer.parseInt(startingCellX.getText()) * Integer.parseInt(cellSizeW.getText()) * 24), 
						Integer.toString(Integer.parseInt(startingCellY.getText()) * Integer.parseInt(cellSizeH.getText()) * 24), 
						"24");
				t.save();
				
				// Create map ID and append counter by one
				TextFile tid = new TextFile(finalPath + "/map_id.wdat", null);
				tid.addLine();
				tid.addValue(Wyvern.getNextMapID());
				tid.save();

				// Create cell data
				TextFile td = new TextFile(finalPath + "/" + startingCellName + "/layers.wdat", null);

				for (int z = 0; z < 5; z++) {
					td.addLine();
					td.addValue("@"+z);
					for (int y = 0; y < Integer.parseInt(cellSizeH.getText()); y++) {
						td.addLine();
						for (int x = 0; x < Integer.parseInt(cellSizeW.getText()); x++) {
							td.addValue("0a0");
						}
					}
				}

				td.save();
				TextFile t2 = new TextFile(finalPath + "/" + startingCellName + "/events.wdat", null);
				t2.save();
				TextFile t5 = new TextFile(finalPath + "/" + startingCellName + "/common_events.wdat", null);
				t5.save();
				TextFile t3 = new TextFile(finalPath + "/" + startingCellName + "/metadata.wdat", null);
				t3 = Cell.constructMetadata(t3, startingCellName, "default", 
						this.tilesetPicker.getSelected().getOriginalName());
				t3.save();
				TextFile t4 = new TextFile(finalPath + "/places.wdat", null);
				t4.addLine();
				t4.addValue(0, "start");
				t4.addValue(0, startingCellX.getText());
				t4.addValue(0, startingCellY.getText());
				t4.save();
				// Set the created maps data as signal
				setSignal(new Signal(Signal.T_NEWMAP, nameField.getText() + "@map", mapPath));
				setVisible(false);	
			} else {
				Warning.showWarning("Map with this name already exists in this folder!");
			}
		
		}
	}

	private boolean invalidStarting() {
		// Correct the max and starting cell coordinates
		if (Integer.parseInt(startingCellX.getText()) >= Integer.parseInt(maxCellX.getText())) {
			return true;
		} else if (Integer.parseInt(startingCellY.getText()) >= Integer.parseInt(maxCellY.getText())) {
			return true;
		} else {
			return false;
		}
	}
	
}
