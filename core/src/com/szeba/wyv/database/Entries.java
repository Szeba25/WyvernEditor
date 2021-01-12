package com.szeba.wyv.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.DatabaseEntry;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.utilities.FileUtilities;
import com.szeba.wyv.widgets.ext.list.ButtonList;
import com.szeba.wyv.widgets.ext.list.EntryContentList;
import com.szeba.wyv.widgets.ext.textfield.IntField;

/**
 * The main database entries holder
 * @author Szeba
 */
public class Entries {

	// Represent the entry category list
	public ButtonList entryList;
	
	// Represent the items in each entry category
	public EntryContentList entryContentList;
	
	// Size box
	public IntField sizeField;
	
	// The entry data
	public HashMap<String, DatabaseEntry> entryData;
	// The default entry data
	public HashMap<String, String> defaultEntries;
	
	public String currentEntry;
	public String currentItem;
	
	/** Loads the database entries from the hard disc */
	public void load() {
		// Create two empty lists
		entryList = new ButtonList(0, 0, 10, 90, 160, 30, new ArrayList<ListElement>(), false);
		entryContentList = new EntryContentList(0, 0, 175, 122, 220, 28, new ArrayList<ListElement>());
		
		// Create size box, hope its enough...
		sizeField = new IntField(0, 0, 175, 95, 148, "Z+", 99999);
		
		// Initialize the main storage
		entryData = new HashMap<String, DatabaseEntry>();
		// Initialize the default storage
		defaultEntries = new HashMap<String, String>();
		
		// Create a list of entry types
		ArrayList<String> entryTypes = FileUtilities.listFolderContents(Wyvern.INTERPRETER_DIR + "/database/entries");
		Collections.sort(entryTypes);
		for (String s : entryTypes) {
			entryData.put(s, new DatabaseEntry(s));
		}
		
		// Populate the database entries
		
		// Construct the final lists
		for (String s : entryTypes) {
			entryList.addElement(new ListElement(s));
		}
		
		// Load default entries
		for (String s : entryTypes) {
			TextFile tf = new TextFile(Wyvern.INTERPRETER_DIR + "/database/entries/" + s + "/default_item.wdat");
			defaultEntries.put(s, tf.getValue(0, 0));
		}
		
		currentEntry = null;
		currentItem = null;
	}
	
	public void reload() {
		for (String db : entryData.keySet()) {
			if (entryData.get(db).getChanged()) {
				entryData.get(db).unmarkChanged();
				System.out.println("Database: entries in \"" + db + "\" reverted!");
				entryData.get(db).loadItems();
			}
		}
	}
	
	/** Save database entries */
	public void save() {
		for (String db : entryData.keySet()) {
			if (entryData.get(db).getChanged()) {
				entryData.get(db).unmarkChanged();
				System.out.println("Database: entries in \"" + db + "\" saved!");
				TextFile file = new TextFile(Wyvern.INTERPRETER_DIR + "/database/entries/" + db + "/items.wdat", null);
				for (String data : entryData.get(db).getItems()) {
					file.addLine();
					file.addValue(data);
				}
				file.save();
			}
		}
	}
	
	public void refreshContentList(String param) {
		entryContentList.setElements(entryData.get(param).getItemsAsElementsArray());
	}

}
