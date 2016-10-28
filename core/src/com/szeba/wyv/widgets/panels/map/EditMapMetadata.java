package com.szeba.wyv.widgets.panels.map;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.data.maps.Cell;
import com.szeba.wyv.data.maps.GameMap;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.List;
import com.szeba.wyv.widgets.TextField;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.ext.textfield.FileNameField;
import com.szeba.wyv.widgets.ext.textfield.IntField;

public class EditMapMetadata extends Widget {

	private GameMap currentMap;
	private HashMap<String, Point> copyOfPlacesHash;
	
	private List placesList;
	private TextField filterField;
	private String appliedFilter;
	
	private TextField newPlaceField;
	private IntField newx;
	private IntField newy;
	private Button addPlaceButton;
	
	private FileNameField renameMapField;
	
	private IntField cellw;
	private IntField cellh;
	
	private Button doneButton;
	private Button cancelButton;
	
	public EditMapMetadata(int ox, int oy, int rx, int ry) {
		super(ox, oy, rx, ry, 500, 500);
		
		this.setTabFocus(true);
		
		currentMap = null;
		copyOfPlacesHash = null;
		
		placesList = new List(getX(), getY(), 5, 45, 180, 23, new ArrayList<ListElement>(), false);
		filterField = new TextField(getX(), getY(), 40, 430, 145, 1);
		appliedFilter = "";
		
		newPlaceField = new TextField(getX(), getY(), 195, 95, 130, 1);
		newx = new IntField(getX(), getY(), 240, 115, 55, "N", 9999);
		newy = new IntField(getX(), getY(), 240, 135, 55, "N", 9999);
		addPlaceButton = new Button(getX(), getY(), 330, 95, 35, 17, "add");
		
		renameMapField = new FileNameField(getX(), getY(), 195, 45, 160);
		
		cellw = new IntField(getX(), getY(), 195, 195, 55, "Z+", 9999);
		cellh = new IntField(getX(), getY(), 255, 195, 55, "Z+", 9999);
		
		doneButton = new Button(getX(), getY(), 370, 475, 60, 20, "done");
		cancelButton = new Button(getX(), getY(), 435, 475, 60, 20, "cancel");
		
		addWidget(placesList);
		addWidget(filterField);
		
		addWidget(renameMapField);
		
		addWidget(newPlaceField);
		addWidget(newx);
		addWidget(newy);
		addWidget(addPlaceButton);
		
		addWidget(cellw);
		addWidget(cellh);
		
		addWidget(doneButton);
		addWidget(cancelButton);
		
		// Enter focus
		this.setEnterFocusDefault(doneButton);
		this.setEnterFocusRestricted(cancelButton, addPlaceButton);
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		super.mainDraw(batch);
		FontUtilities.print(batch, "filter:", getX()+5, getY()+430);
		FontUtilities.print(batch, "new place:", getX()+195, getY()+75);
		FontUtilities.print(batch, "cell X:", getX()+195, getY()+115);
		FontUtilities.print(batch, "cell Y:", getX()+195, getY()+135);
		FontUtilities.print(batch, "map name:", getX()+195, getY()+25);
		FontUtilities.print(batch, "map id:", getX()+370, getY()+25);
		FontUtilities.print(batch, currentMap.getSignatureID(), getX()+370, getY()+45);
		FontUtilities.print(batch, "cell size:", getX()+195, getY()+175);
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		for (Widget w : this.getWidgets()) {
			w.setFocused(false);
		}
		this.renameMapField.setFocused(true);
	}
	
	@Override
	public void overDraw(SpriteBatch batch) {
		FontUtilities.print(batch, "places:", getX()+5, getY()+25);
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		
		if (!appliedFilter.equals(filterField.getText())) {
			appliedFilter = filterField.getText();
			setPlacesList();
		}
		
		Signal sg = doneButton.getSignal();
		if (sg != null) {
			this.currentMap.setPlaces(copyOfPlacesHash);
			this.setSignal(new Signal(Signal.T_RENAME_MAP, renameMapField.getFileName(), 
					renameMapField.getText()));
			this.doCellResize();
			this.setVisible(false);
		}
		
		sg = cancelButton.getSignal();
		if (sg != null) {
			this.setVisible(false);
		}
		
		sg = addPlaceButton.getSignal();
		if (sg != null && !newPlaceField.getText().isEmpty() && !newx.getText().isEmpty() &&
				!newy.getText().isEmpty() && !copyOfPlacesHash.containsKey(newPlaceField)) {
			placesList.addElement(new ListElement(
					newPlaceField.getText() + " " + newx.getText() + "/" + newy.getText()));
			copyOfPlacesHash.put(newPlaceField.getText(),
					new Point(Integer.parseInt(newx.getText()), Integer.parseInt(newy.getText())));
			newPlaceField.setText("");
			newx.setText("");
			newy.setText("");
			
			for (Widget w : this.getWidgets()) {
				w.setFocused(false);
			}
			newPlaceField.setFocused(true);
			
		}
		
		if (Wyvern.input.isKeyPressed(Keys.FORWARD_DEL)) {
			if (placesList.isValidSelectedID()) {
				ListElement element = placesList.getSelected();
				placesList.removeElement(placesList.getSelectedID());
				String[] tempstrArr = StringUtilities.safeSplit(element.getOriginalName(), " ");
				String keystr = "";
				for (int x = 0; x < tempstrArr.length-1; x++) {
					keystr += tempstrArr[x];
					if (x < tempstrArr.length-2) {
						keystr += " ";
					}
				}
				//Warning.showWarning(keystr);
				copyOfPlacesHash.remove(keystr);
			}
		}
		
		if (Wyvern.input.isKeyPressed(Keys.ENTER) &&
				(newPlaceField.isFocused() || newx.isFocused() || newy.isFocused())) {
			for (Widget w : this.getWidgets()) {
				w.setFocused(false);
			}
			addPlaceButton.setFocused(true);
		}
		
	}

	private void doCellResize() {
		if (cellw.getText().length() > 0 && cellh.getText().length() > 0) {
			
			int newW = Integer.parseInt(cellw.getText());
			int newH = Integer.parseInt(cellh.getText());
			
			if (newW != currentMap.getCellW() || newH != currentMap.getCellH()) {
				// Set the new cell data
				currentMap.setCellW(newW);
				currentMap.setCellH(newH);
				// Resize cell data
				for (Cell cell : currentMap.getCells().values()) {
					cell.resize(currentMap.getCellW(), currentMap.getCellH());
				}
				currentMap.setOffX(0);
				currentMap.setOffY(0);
			}
		}
	}

	public void setCurrentMap(GameMap currentMap) {
		this.currentMap = currentMap;
		
		this.renameMapField.setText(StringUtilities.getSpecialFileName(currentMap.getName()));
		
		cellw.setText(Integer.toString(currentMap.getCellW()));
		cellh.setText(Integer.toString(currentMap.getCellH()));
		
		// Copy the places hash!
		copyOfPlacesHash = new HashMap<String, Point>();
		for (Map.Entry<String, Point> entry : currentMap.getPlaces().entrySet()) {
			copyOfPlacesHash.put(entry.getKey(), 
					new Point(entry.getValue().x, entry.getValue().y));
		}
				
		setPlacesList();
	}
	
	private void setPlacesList() {
		ArrayList<String> tempAr = new ArrayList<String>();
		ArrayList<ListElement> ar = new ArrayList<ListElement>();
		for (Map.Entry<String, Point> entry : copyOfPlacesHash.entrySet()) {
			String tmp = entry.getKey() + " " +
					(int)entry.getValue().getX() + "/" + (int)entry.getValue().getY();
			if (tmp.contains(appliedFilter)) {
				tempAr.add(tmp);
			}
		}
		Collections.sort(tempAr);
		for (String str : tempAr) {
			ar.add(new ListElement(str));
		}
		placesList.setElements(ar);
	}

}
