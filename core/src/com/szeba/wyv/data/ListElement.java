package com.szeba.wyv.data;

import java.util.ArrayList;

import com.szeba.wyv.utilities.CommandStringGen;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.Separator;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.dynamic.Dynamic;

/**
 * Represents an element contained in lists.
 * @author Szeba
 */
public class ListElement {
	// Comparator for sorting
	public static ListElementComparator comparator = new ListElementComparator();
	
	// Indentation
	private String indent;
	// The display name of this element
	private String name;
	// The original name of this element (could not be changed!)
	private String originalName;
	// Special data for this element
	private String data;
	// The command string of this element (only used by event commands)
	private String commandString;
	// Type (folder, picture, etc...)
	private int type;
	// Mark
	private boolean marked;
	// Color value
	private int color;
	
	public ListElement(String value) {
		indent = "";
		name = value;
		originalName = value;
		data = value;
		commandString = "";
		type = -1;
		marked = false;
		color = 0;
	}
	
	public ListElement(String name, String data) {
		indent = "";
		this.name = name;
		originalName = name;
		this.data = data;
		commandString = "";
		type = -1;
		marked = false;
		color = 0;
	}
	
	/** 
	 * Used for event commands, this will also generate a command string from the data!!! 
	 */
	public ListElement(int indentSize, String name, String data, int color) {
		this.setIndentSize(indentSize);
		this.name = name;
		originalName = name;
		this.data = data;
		commandString = CommandStringGen.generate(name, data, null);
		type = -1;
		marked = false;
		this.color = color;
	}
	
	public ListElement(String name, String data, int type) {
		indent = "";
		this.name = name;
		originalName = name;
		this.data = data;
		commandString = "";
		this.type = type;
		marked = false;
		color = 0;
	}
	
	/**
	 * Copy a listelement completely!
	 */
	public ListElement setEqualTo(ListElement reference) {
		indent = reference.indent;
		name = reference.name;
		originalName = reference.originalName;
		data = reference.data;
		commandString = reference.commandString;
		type = reference.type;
		marked = reference.marked;
		color = reference.color;
		return this;
	}
	
	public String getAsString() {
		String generated = "";
		// We use [RS] char 30 as the separator!
		generated += indent + Separator.listElement;
		generated += name + Separator.listElement;
		generated += originalName + Separator.listElement;
		generated += data + Separator.listElement;
		generated += commandString + Separator.listElement;
		generated += Integer.toString(type) + Separator.listElement;
		generated += Integer.toString(color);
		// Return the string
		return generated;
	}
	
	public void rebuildFromString(String dat) {
		String[] datAr = StringUtilities.safeSplit(dat, Separator.listElement);
		indent = datAr[0];
		name = datAr[1];
		originalName = datAr[2];
		data = datAr[3];
		commandString = datAr[4];
		type = Integer.parseInt(datAr[5]);
		color = Integer.parseInt(datAr[6]);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setOriginalName(String name) {
		this.originalName = name;
	}
	
	public String getOriginalName() {
		return originalName;
	}
	
	public String getData() {
		return data;
	}
	
	public String getCommandString() {
		return commandString;
	}

	public void setCommandString(String commandString) {
		this.commandString = commandString;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}

	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean marked) {
		this.marked = marked;
	}
	
	public int getColor() {
		return color;
	}
	
	public void setColor(int color) {
		this.color = color;
	}
	
	public int getIndentPixSize() {
		return (int) FontUtilities.getBounds(indent).width;
	}
	
	public void setIndentSize(int size) {
		indent = "";
		for (int x = 0; x < size; x++) {
			indent += "   ";
		}
	}
	
	public int getIndentSize() {
		return indent.length()/3;
	}
	
	public void setEventCommandData(String data) {
		this.data = data;
		commandString = CommandStringGen.generate(name, data, null);
	}
	
	public void setEventCommandData(String data, ArrayList<Dynamic> panelData) {
		this.data = data;
		commandString = CommandStringGen.generate(name, data, panelData);
	}
}
