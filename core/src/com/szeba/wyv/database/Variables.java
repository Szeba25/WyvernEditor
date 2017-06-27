package com.szeba.wyv.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.utilities.Separator;
import com.szeba.wyv.widgets.TextField;
import com.szeba.wyv.widgets.ext.list.ButtonList;
import com.szeba.wyv.widgets.ext.textfield.IntField;

public class Variables {

	public HashMap<String, ArrayList<String>> entries;
	
	public ButtonList catList;
	public ButtonList varList;
	public IntField sizeField;
	public TextField filterField;
	public TextField nameField1;
	
	public String varFilter;
	
	private boolean changed;
	
	/**
	 * Load variables from a text file to a hashmap.
	 */
	public void load() {
		
		// Build the main hashmap
		entries = new HashMap<String, ArrayList<String>>();
		
		// Build the shared variable widgets!
		catList = new ButtonList(0, 0, 10, 50, 170, 20, new ArrayList<ListElement>(), false);
		varList = new ButtonList(0, 0, 185, 66, 280, 19, new ArrayList<ListElement>(), false);
		// Hope its enough...
		sizeField = new IntField(0, 0, 185, 25, 98, "Z+", 99999);
		
		filterField = new TextField(0, 0, 100, 395, 360, 1);
		nameField1 = new TextField(0, 0, 225, 45, 240, 1);
		nameField1.setVisible(false);
		
		varFilter = "";
		
		changed = false;
		
		// Read data from the variables.wdat file
		TextFile t = new TextFile(Wyvern.INTERPRETER_DIR + "/database/variables.wdat");
		String currentCategory = "";
		for (int i = 0; i < t.getLength(); i++) {
			// We indicate a category with the [SUB] character.
			if (t.getLine(i).size() > 0 && t.getValue(i, 0).length() > 0 && 
					t.getValue(i, 0).charAt(0) == (char)26) {
				currentCategory = t.getValue(i, 1);
				entries.put(currentCategory, new ArrayList<String>());
			} else if (currentCategory.length() > 0) {
				entries.get(currentCategory).add(t.getValue(i, 0));
			}
		}
		
		// Build an alphabetically sorted list from the categories.
		ArrayList<ListElement> ar = new ArrayList<ListElement>();
		for (String str : entries.keySet()) {
			ar.add(new ListElement(str));
		}
		Collections.sort(ar, ListElement.comparator);
		catList.setElements(ar);
		varList.setElements(null);
	}
	
	/**
	 * Save variables to a text file
	 */
	public void save() {
		if (changed) {

			// Create a new blank text file
			TextFile t = new TextFile(Wyvern.INTERPRETER_DIR + "/database/variables.wdat", null);
			
			for (Entry<String, ArrayList<String>> entry : entries.entrySet()) {
			    String key = entry.getKey();
			    
			    // Add category entry.
			    t.addLine();
			    t.addValue(t.getLength()-1, Separator.array);
			    t.addValue(t.getLength()-1, key);
			    
			    // Add variable entries.
			    ArrayList<String> value = entry.getValue();
			    for (String var : value) {
			    	t.addLine();
			    	t.addValue(t.getLength()-1, var);
			    }
			}
			
			t.save();
			System.out.println("Data: Variables saved!");
			changed = false;
			
		}
	}
	
	public boolean getChanged() {
		return changed;
	}
	
	public void markChanged() {
		changed = true;
	}
	
}
