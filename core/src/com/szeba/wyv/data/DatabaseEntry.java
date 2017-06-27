package com.szeba.wyv.data;

import java.util.ArrayList;

import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.utilities.CommandStringGen;
import com.szeba.wyv.utilities.Separator;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.dynamic.DynamicPanel;

/**
 * This class holds the dynamic panel of the given entry type, and
 * the items it stores.
 * @author Szeba
 */

public class DatabaseEntry {

	private String name;
	private String path;
	private DynamicPanel mainPanel;
	private ArrayList<String> items;
	private boolean changed = false;
	
	public DatabaseEntry(String entryName) {
		
		name = entryName;
		
		path = Wyvern.INTERPRETER_DIR + "/database/entries/" + entryName;
		
		// Get the panel's size
		TextFile file = new TextFile(path + "/widget_meta.wdat");
		int width = Integer.parseInt(file.getValue(0, 1));
		int height = Integer.parseInt(file.getValue(0, 2));
		
		// Load the panel's widgets.
		mainPanel = new DynamicPanel(entryName, 330, 80, 0, 0, width, height);
		mainPanel.loadWidgets(path + "/widget.wdat");
		mainPanel.removeWidget(0);
		mainPanel.removeWidget(0);
		mainPanel.setVisible(false);
		// There is no reason to enable this in the database...
		mainPanel.setEnterFocusDefault(null);
		
		loadItems();
	}
	
	public void loadItems() {
		items = new ArrayList<String>();
		// Load items
		TextFile file = new TextFile(path + "/items.wdat");
		for (int i = 0; i < file.getLength(); i++) {
			items.add(file.getValue(i, 0));
		}
	}
	
	public void resizeItems(int size, String empty) {
		// Resize the items list.
		this.changed = true;
		
		ArrayList<String> newItems = new ArrayList<String>();
		
		for (int z = 0; z < size; z++) {
			if (z < items.size()) {
				newItems.add(items.get(z));
			} else {
				newItems.add(empty);
			}
		}
		
		items = newItems;
		
	}
	
	public boolean getChanged() {
		return changed;
	}
	
	public void markChanged() {
		changed = true;
	}
	
	public void unmarkChanged() {
		changed = false;
	}
	
	public String getName() {
		return name;
	}
	
	public String getItem(int id) {
		return items.get(id);
	}
	
	public ArrayList<String> getItems() {
		return items;
	}
	
	public DynamicPanel getPanel() {
		return mainPanel;
	}
	
	public String getPanelFirstData() {
		return mainPanel.getParam(0);
	}
	
	public ArrayList<ListElement> getItemsAsElementsArray() {
		ArrayList<ListElement> arr = new ArrayList<ListElement>();
		for (int x = 0; x < items.size(); x++) {
			
			// We must handle arrayfields.
			String finalName = StringUtilities.safeSplit(items.get(x), Separator.dataUnit)[0];
			if (CommandStringGen.isArrayText(finalName)) {
				finalName = CommandStringGen.generateArrayText(finalName);
			}
			
			arr.add(new ListElement(Integer.toString(x) + ": " 
					+ finalName,
					Integer.toString(x)));
		}
		return arr;
	}
	
}
