package com.szeba.wyv.widgets.dynamic;

import java.util.ArrayList;
import java.util.HashMap;

import com.szeba.wyv.Wyvern;
import org.apache.commons.lang3.math.NumberUtils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.utilities.Separator;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Widget;

public class DynamicPanel extends Widget {

	// This name equals to the command's name it references to.
	private String name;
	
	private HashMap<String, Dynamic> widgets;
	private HashMap<String, Dynamic> modalWidgets;
	private HashMap<String, Dynamic> allWidgets;
	
	private ArrayList<Dynamic> params;
	
	private Button done;
	private Button cancel;
	
	private int defaultFocus = -1;
	
	public DynamicPanel(String name, int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
		
		this.name = name;
		widgets = new HashMap<String, Dynamic>();
		modalWidgets = new HashMap<String, Dynamic>();
		allWidgets = new HashMap<String, Dynamic>();
		params = new ArrayList<Dynamic>();
		
		done = new Button(getX(), getY(), getW()-150, getH()-25, 70, 20, "done");
		cancel = new Button(getX(), getY(), getW()-75, getH()-25, 70, 20, "cancel");
		
		addWidget(done);
		addWidget(cancel);
		
		this.setTabFocus(true);
		this.setEnterFocusDefault(done);
		this.setEnterFocusRestricted(cancel);
	}

	@Override
	public void setW(int w) {
		super.setW(w);
		done.setRX(getW()-150);
		cancel.setRX(getW()-75);
	}
	
	@Override
	public void setH(int h) {
		super.setH(h);
		done.setRY(getH()-25);
		cancel.setRY(getH()-25);
	}
	
	@Override
	public void setFocused(boolean value) {
		super.setFocused(value);
		if (!value) {
			// Defocus all widgets
			for (Widget w : this.getWidgets()) {
				w.setFocused(false);
			}
			// Close modal widgets
			this.closeModalWidgets();
		}
	}
	
	@Override
	public void setVisible(boolean value) {
		super.setVisible(value);
		if (value) {
			// Reset the widgets
			reset();
			// Set the default focused item.
			if (defaultFocus >= 0) {
				for (int w = 0; w < this.getWidgets().size(); w++) {
					if (defaultFocus+1 == w) {
						this.getWidget(w).setFocused(true);
					} else {
						this.getWidget(w).setFocused(false);
					}
				}
			}
		} else {
			// Defocus all widgets
			for (Widget w : this.getWidgets()) {
				w.setFocused(false);
			}
			// Close modal widgets
			this.closeModalWidgets();
		}
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		drawBackground(batch);
		drawOutline(batch);
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		for (Dynamic w : allWidgets.values()) {
			// Loop in the dynamic widgets.
			Signal sg = ((Widget) w).getSignal();
			if (sg != null) {
				if (allWidgets.containsKey(w.dynGetReceiver())) {
					allWidgets.get(w.dynGetReceiver()).dynProcessSignal(sg);
				}
			}
		}
		if (done.getSignal() != null) {
			this.setVisible(false);
			this.setSignal(new Signal(Signal.T_DYN_DONE));
		} else if (cancel.getSignal() != null) {
			this.setVisible(false);
		}
	}
	
	@Override
	public void passiveUpdate(int scrolled) {
		killModalUpdateDelay();
	}
	
	public void closeModalWidgets() {
		for (Widget w : getModalWidgets()) {
			w.setVisible(false);
		}
	}
	
	/**
	 * Load widgets from a text file specified by path. 
	 */
	public void loadWidgets(String path) {
		TextFile file = new TextFile(path);
		
		for (int i = 0; i < file.getLength(); i++) {
			buildWidget(file.getLine(i));
		}
	}

	/**
	 * Build a widget from a line of data.
	 */
	public void buildWidget(ArrayList<String> line) {
		// The builded widget.
		Dynamic wid = null;
		
		boolean addToEnterFocusRestricted = false;
		
		/*
		 * 0 -> The line should start with a "." symbol, or a number. A "." will indicate, that
		 * this widget will hold no parameter. 
		 * # -> An opt symbol will set something in the dynamic panel.
		 * 1 -> After that, "w" or "mw" specifies if the widget is a simple, or modal widget.
		 * 2 -> The identifier string of this widget
		 * 3 -> The identifier of the widget which will get a signal from this widget.
		 * 4 -> The type of the widget. Currently available types:
		 */
		
		// Set an option, if prompted
		if (line.get(0).equals("opt")) {
			if (line.get(1).equals("defaultfocus")) {
				this.defaultFocus = Integer.parseInt(line.get(2));
			}
			return;
		}
		
		// Return if this line is not a dynamic panel element
		if (line.size() < 2) {
			return;
		}
		
		String name = line.get(4);
		
		switch(name) {
		
		// Build a new button widget
		// x, y, w, h, text
		// (def build added)
		case "button":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			int h = Integer.parseInt(line.get(8));
			String text = line.get(9);
			wid = new DynButton(getX(), getY(), x, y, w, h, text);
			addToEnterFocusRestricted = true;
		}
		break;
		
		// Build a new switchbutton
		// x, y, w, h, text1, text2
		// (def build added)
		case "switchbutton":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			int h = Integer.parseInt(line.get(8));
			String text1 = line.get(9);
			String text2 = line.get(10);
			wid = new DynSwitchButton(getX(), getY(), x, y, w, h, text1, text2);
			addToEnterFocusRestricted = true;
		}
		break;
		
		// Build a new bullet board
		// x, y, width, array
		// (def build added)
		case "bulletboard":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			ArrayList<String> ar = StringUtilities.buildStringList(line.get(8));
			wid = new DynBulletBoard(getX(), getY(), x, y, w, ar);
		}
		break;
		
		// Build a new text
		// x, y, w, h, text
		// (def build added)
		case "text":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			int h = Integer.parseInt(line.get(8));
			String text = line.get(9);
			wid = new DynText(getX(), getY(), x, y, w, h, text);
		}
		break;
		
		// Build a new textfield widget
		// x, y, w, linecount
		// (def build added)
		case "textfield":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			int count = Integer.parseInt(line.get(8));
			DynTextField tf = new DynTextField(getX(), getY(), x, y, w, count);
			wid = tf;
			if (count > 1) {
				addToEnterFocusRestricted = true;
			}
		}
		break;
		
		// Build a new intfield widget
		// x, y, w, mode, max
		// (def build added)
		case "intfield":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			String mode = line.get(8);
			int max = Integer.parseInt(line.get(9));
			DynIntField ifp = new DynIntField(getX(), getY(), x, y, w, mode, max);
			wid = ifp;
		}
		break;
		
		// Build a new doublefield widget
		// x, y, w, mode, max
		// (def build added)
		case "doublefield":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			String mode = line.get(8);
			int max = Integer.parseInt(line.get(9));
			DynDoubleField ifp = new DynDoubleField(getX(), getY(), x, y, w, mode, max);
			wid = ifp;
		}
		break;
		
		// Build a new holder variable
		// x, y, w, h
		// (def build added)
		case "holdervariable":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			int h = Integer.parseInt(line.get(8));
			wid = new DynHolderVariable(getX(), getY(), x, y, w, h);
		}
		break;
		
		// Build a new holder event name
		// x, y, w, h
		// (def build added)
		case "holdereventname":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			int h = Integer.parseInt(line.get(8));
			wid = new DynHolderEventName(getX(), getY(), x, y, w, h);
		}
		break;
		
		// Build a new holder database entry
		// x, y, w, h
		// (def build added)
		case "holderdbentry":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			int h = Integer.parseInt(line.get(8));
			wid = new DynHolderDBEntry(getX(), getY(), x, y, w, h);
		}
		break;
		
		// Build a new arrayfield widget
		// x, y, w, linecount
		// (def build added)
		case "arrayfield":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			int count = Integer.parseInt(line.get(8));
			DynArrayField af = new DynArrayField(getX(), getY(), x, y, w, count);
			wid = af;
			if (count > 1) {
				addToEnterFocusRestricted = true;
			}
		}
		break;
		
		// Build a new list
		// x, y, w, linecount, array
		// (def build added)
		case "list":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			int h = Integer.parseInt(line.get(8));
			ArrayList<ListElement> ar = StringUtilities.buildElementList(line.get(9));
			wid = new DynList(getX(), getY(), x, y, w, h, ar, false);
			addToEnterFocusRestricted = true;
		}
		break;
		
		// Build a new collection list
		// x, y, w, h, array, unique
		// (def build added)
		case "collection":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			int h = Integer.parseInt(line.get(8));
			ArrayList<ListElement> ar = StringUtilities.buildElementList(line.get(9));
			boolean unique = Boolean.parseBoolean(line.get(10));
			wid = new DynCollectionList(getX(), getY(), x, y, w, h, ar, unique);
		}
		break;
		
		// Build a new database list
		// x, y, w, linecount, entry
		// (def build added)
		case "dblist":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			int h = Integer.parseInt(line.get(8));
			String entry = line.get(9);
			wid = new DynDBList(getX(), getY(), x, y, w, h, entry);
		}
		break;
		
		// Build a new droplist
		// x, y, w, linecount, array
		// (def build added)
		case "droplist":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			int h = Integer.parseInt(line.get(8));
			ArrayList<ListElement> ar = StringUtilities.buildElementList(line.get(9));
			wid = new DynDropList(getX(), getY(), x, y, w, h, ar);
		}
		break;
		
		// Build a new dirlist
		// x, y, w, linecount, path
		// (def build added)
		case "dirlist":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			int h = Integer.parseInt(line.get(8));
			String path = Wyvern.INTERPRETER_DIR + "/" + line.get(9);
			wid = new DynDirList(getX(), getY(), x, y, w, h, path, path);
			addToEnterFocusRestricted = true;
		}
		break;
		
		// Build a new dirlistmaps
		// x, y, w, linecount, path
		// (def build added)
		case "dirlistmaps":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			int h = Integer.parseInt(line.get(8));
			String path = Wyvern.INTERPRETER_DIR + "/" + line.get(9);
			wid = new DynDirListMaps(getX(), getY(), x, y, w, h, path, path);
			addToEnterFocusRestricted = true;
		}
		break;
		
		// Build a new event list
		// x, y, w, linecount, comev
		// (def build added)
		case "eventlist":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			int h = Integer.parseInt(line.get(8));
			boolean comev = Boolean.parseBoolean(line.get(9));
			wid = new DynEventList(getX(), getY(), x, y, w, h, comev);
		}
		break;
		
		// Build a new sprite picker
		// x, y, s
		// (def build added)
		case "spritepicker":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int s = Integer.parseInt(line.get(7));
			wid = new DynSpritePicker(getX(), getY(), x, y, s);
		}
		break;
		
		// Build a new color picker
		// x, y
		// (def build added)
		case "colorpicker":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			wid = new DynColorPicker(getX(), getY(), x, y);
		}
		break;
		
		// Build a new animation picker
		// x, y, s
		// (def build added)
		case "animpicker":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int s = Integer.parseInt(line.get(7));
			wid = new DynAnimationPicker(getX(), getY(), x, y, s);
		}
		break;
		
		// Build a new image picker
		// subdir, x, y, s
		// (def build added)
		case "imagepicker":
		{
			String sub = line.get(5);
			int x = Integer.parseInt(line.get(6));
			int y = Integer.parseInt(line.get(7));
			int s = Integer.parseInt(line.get(8));
			wid = new DynImagePicker(getX(), getY(), sub, x, y, s);
		}
		break;
		
		// Build a new tileset picker
		// x, y
		// (def build added)
		case "tilesetpicker":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			wid = new DynTilesetPicker(getX(), getY(), x, y);
		}
		break;
		
		// Build a new audio picker
		// x, y, w, h
		// (def build added)
		case "audiopicker":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			int h = Integer.parseInt(line.get(8));
			wid = new DynAudioPicker(getX(), getY(), x, y, w, h);
		}
		break;
		
		// Build a new slider
		// x, y, w, h, min, max, format
		// (def build added)
		case "slider":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			int h = Integer.parseInt(line.get(8));
			int min = Integer.parseInt(line.get(9));
			int max = Integer.parseInt(line.get(10));
			String format = line.get(11);
			wid = new DynSlider(getX(), getY(), x, y, w, h, min, max, format);
		}
		break;
		
		// Build a new coordinate map panel
		// x, y, w, h, tilesize
		// (def build added)
		case "coordmap":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			int h = Integer.parseInt(line.get(8));
			int ts = Integer.parseInt(line.get(9));
			wid = new DynCoordinateMapPanel(getX(), getY(), x, y, w, h, ts);
		}
		break;
		
		// Build a new varnum panel
		// x, y
		// (def build added)
		case "varnum":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			wid = new DynVarNum(getX(), getY(), x, y);
			addToEnterFocusRestricted = true;
		}
		break;
		
		// Build a new variable database
		// x, y
		// (def build added)
		case "vardb":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			wid = new DynVariableDatabase(getX(), getY(), x, y);
		}
		break;
		
		// Build a new movement command panel
		// x, y, eventlist
		// (def build added)
		case "movec":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			String bool = line.get(7);
			boolean val = false;
			if (bool.equals("true")) {
				val = true;
			}
			wid = new DynMoveCommands(getX(), getY(), x, y, val);
		}
		break;
		
		// Build a new db entry picker
		// x, y, w, h, entryname
		// (def build added)
		case "dbentrypicker":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			int h = Integer.parseInt(line.get(8));
			String entryname = line.get(9);
			wid = new DynDBEntryPicker(getX(), getY(), x, y, w, h, entryname);
		}
		break;

		// Build a new db drop list
		//
		//
		case "dbdroplist":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			int h = Integer.parseInt(line.get(8));
			String entryname = line.get(9);
			wid = new DynDropDBList(getX(), getY(), x, y, w, h, entryname);
		}
		break;

		// Build a new event picker
		// x, y, w, h, comev
		// (def build added)
		case "eventpicker":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			int w = Integer.parseInt(line.get(7));
			int h = Integer.parseInt(line.get(8));
			boolean comev = Boolean.parseBoolean(line.get(9));
			wid = new DynEventPicker(getX(), getY(), x, y, w, h, comev);
		}
		break;
		
		// Build a new map jumper
		// x, y
		// (def build added)
		case "mapjumper":
		{
			int x = Integer.parseInt(line.get(5));
			int y = Integer.parseInt(line.get(6));
			wid = new DynMapJumper(getX(), getY(), x, y);
		}
		break;
		
		// End of switch
		}
		
		// Set the receiver string
		wid.dynSetReceiver(line.get(3));
		
		// Add to parameters, if this widget is also a parameter
		if (NumberUtils.isNumber(line.get(0))) {
			params.add(wid);
		}
		
		// Add to either normal or modal widgets
		if (line.get(1).equals("w")) {
			widgets.put(line.get(2), wid);
			addWidget((Widget) wid);
		} else {
			modalWidgets.put(line.get(2), wid);
			addModalWidget((Widget) wid);
		}
		
		// Print a warning if identifiers are duplicated
		if (allWidgets.containsKey(line.get(2))) {
			System.err.println("A dynamic panel contains duplicate identifier: " + line.get(2));
		}
		// Add to all widgets
		allWidgets.put(line.get(2), wid);
		
		// Reset for the first time
		wid.dynReset();
		
		// This is a restricted widget when it comes to default enter focus:
		/*
		 * button
		 * switchbutton
		 * textfield (if > 1)
		 * arrayfield (if > 1)
		 * list (not a buttonlist)
		 * dirlist (not a buttonlist)
		 * dirlistmaps (not a buttonlist)
		 * varnum (contains button)
		 */
		if (addToEnterFocusRestricted) {
			this.addEnterFocusRestricted((Widget) wid);
		}
	}
	
	/**
	 * Build a default widget string component
	 */
	public ArrayList<String> buildDefaultStringComponent(String name) {
		
		ArrayList<String> finalList = new ArrayList<String>();
		
		switch (name) {
		
		// x, y, w, h, text
		case "button":
		{
			finalList.add("@> x, y, width, height, displayed text");
			finalList.add("0");
			finalList.add("0");
			finalList.add("40");
			finalList.add("20");
			finalList.add("button");
		}
		break;
		
		// x, y, w, h, text1, text2
		case "switchbutton":
		{
			finalList.add("@> x, y, width, height, switch text 1, switch text 2");
			finalList.add("0");
			finalList.add("0");
			finalList.add("40");
			finalList.add("20");
			finalList.add("true");
			finalList.add("false");
		}
		break;
		
		// x, y, w, array
		case "bulletboard":
		{
			finalList.add("@> x, y, width, array of bullet elements");
			finalList.add("0");
			finalList.add("0");
			finalList.add("80");
			finalList.add(Separator.array + "e1" + Separator.array + "e2" + Separator.array + "e3");
		}
		break;
		
		// x, y, w, h, text
		case "text":
		{
			finalList.add("@> x, y, width, height, text");
			finalList.add("0");
			finalList.add("0");
			finalList.add("40");
			finalList.add("20");
			finalList.add("text");
		}
		break;
		
		// x, y, w, linecount
		case "textfield":
		{
			finalList.add("@> x, y, width, count of lines");
			finalList.add("0");
			finalList.add("0");
			finalList.add("100");
			finalList.add("4");
		}
		break;
		
		// x, y, w, default, mode, max
		case "intfield":
		{
			finalList.add("@> x, y, width, mode (N, Z, Z+), maximum");
			finalList.add("0");
			finalList.add("0");
			finalList.add("40");
			finalList.add("N");
			finalList.add("10");
		}
		break;
		
		// x, y, w, default, mode, max
		case "doublefield":
		{
			finalList.add("@> x, y, width, mode (N, Z, Z+), maximum");
			finalList.add("0");
			finalList.add("0");
			finalList.add("40");
			finalList.add("N");
			finalList.add("10");
		}
		break;
		
		// x, y, w, h
		case "holdervariable":
		{
			finalList.add("@> x, y, width, height");
			finalList.add("0");
			finalList.add("0");
			finalList.add("40");
			finalList.add("20");
		}
		break;
		
		// x, y, w, h
		case "holdereventname":
		{
			finalList.add("@> x, y, width, height");
			finalList.add("0");
			finalList.add("0");
			finalList.add("40");
			finalList.add("20");
		}
		break;
		
		// x, y, w, h
		case "holderdbentry":
		{
			finalList.add("@> x, y, width, height");
			finalList.add("0");
			finalList.add("0");
			finalList.add("40");
			finalList.add("20");
		}
		break;
		
		// x, y, w, linecount
		case "arrayfield":
		{
			finalList.add("@> x, y, width, count of lines");
			finalList.add("0");
			finalList.add("0");
			finalList.add("100");
			finalList.add("1");
		}
		break;
		
		// x, y, w, linecount, array
		case "list":
		{
			finalList.add("@> x, y, width, line count (*16 in pixel), array of items");
			finalList.add("0");
			finalList.add("0");
			finalList.add("100");
			finalList.add("5");
			finalList.add(Separator.array + "e1");
		}
		break;
		
		// x, y, w, h, array, unique
		case "collection":
		{
			finalList.add("@> x, y, width, height, array of default items, unique");
			finalList.add("0");
			finalList.add("0");
			finalList.add("100");
			finalList.add("100");
			finalList.add(Separator.array + "e1");
			finalList.add("false");
		}
		break;
		
		// x, y, w, linecount, entry
		case "dblist":
		{
			finalList.add("@> x, y, width, line count (*16 in pixel), entry name to list");
			finalList.add("0");
			finalList.add("0");
			finalList.add("100");
			finalList.add("5");
			finalList.add("none");
		}
		break;
		
		// x, y, w, linecount, array
		case "droplist":
		{
			finalList.add("@> x, y, width, line count (*16 in pixel), array of elements");
			finalList.add("0");
			finalList.add("0");
			finalList.add("100");
			finalList.add("5");
			finalList.add(Separator.array + "e1");
		}
		break;
		
		// x, y, w, linecount, path
		case "dirlist":
		{
			finalList.add("@> x, y, width, line count (*16 in pixel), path");
			finalList.add("0");
			finalList.add("0");
			finalList.add("100");
			finalList.add("5");
			finalList.add("/preferences");
		}
		break;
		
		// x, y, w, linecount, path
		case "dirlistmaps":
		{
			finalList.add("@> x, y, width, line count (*16 in pixel), path");
			finalList.add("0");
			finalList.add("0");
			finalList.add("100");
			finalList.add("5");
			finalList.add("/maps");
		}
		break;
		
		// x, y, w, linecount, comev
		case "eventlist":
		{
			finalList.add("@> x, y, width, line count (*16 in pixel), list common events");
			finalList.add("0");
			finalList.add("0");
			finalList.add("100");
			finalList.add("5");
			finalList.add("false");
		}
		break;
		
		// x, y, s
		case "spritepicker":
		{
			finalList.add("@> x, y, size (width and height)");
			finalList.add("0");
			finalList.add("0");
			finalList.add("234");
		}
		break;
		
		// x, y
		case "colorpicker":
		{
			finalList.add("@> x, y");
			finalList.add("0");
			finalList.add("0");
		}
		break;
		
		// x, y, s
		case "animpicker":
		{
			finalList.add("@> x, y, size (width and height)");
			finalList.add("0");
			finalList.add("0");
			finalList.add("234");
		}
		break;
		
		// subdir, x, y, s
		case "imagepicker":
		{
			finalList.add("@> subdirectory, x, y, size (width and height)");
			finalList.add("");
			finalList.add("0");
			finalList.add("0");
			finalList.add("234");
		}
		break;
		
		// x, y
		case "tilesetpicker":
		{
			finalList.add("@> x, y");
			finalList.add("0");
			finalList.add("0");
		}
		break;
		
		// x, y, w, h
		case "audiopicker":
		{
			finalList.add("@> x, y, width, height");
			finalList.add("0");
			finalList.add("0");
			finalList.add("350");
			finalList.add("160");
		}
		break;
		
		// x, y, w, h, min, max, format
		case "slider":
		{
			finalList.add("@> x, y, width, height, minimum value, maximum value, number format");
			finalList.add("0");
			finalList.add("0");
			finalList.add("100");
			finalList.add("16");
			finalList.add("0");
			finalList.add("20");
			finalList.add("0");
		}
		break;
		
		// x, y, w, h, tilesize
		case "coordmap":
		{
			finalList.add("@> x, y, width, height");
			finalList.add("0");
			finalList.add("0");
			finalList.add("300");
			finalList.add("300");
			finalList.add("16");
		}
		break;
		
		// x, y
		case "varnum":
		{
			finalList.add("@> x, y");
			finalList.add("0");
			finalList.add("0");
		}
		break;
		
		// x, y
		case "vardb":
		{
			finalList.add("@> x, y");
			finalList.add("0");
			finalList.add("0");
		}
		break;
		
		// x, y, eventlist
		case "movec":
		{
			finalList.add("@> x, y, eventlist (true, false)");
			finalList.add("0");
			finalList.add("0");
			finalList.add("true");
		}
		break;
		
		// x, y, w, h, entryname
		case "dbentrypicker":
		{
			finalList.add("@>, x, y, width, height, db entry name");
			finalList.add("0");
			finalList.add("0");
			finalList.add("200");
			finalList.add("200");
			finalList.add("none");
		}
		break;

		// x, y, w, h, entryname
		case "dbdroplist":
		{
			finalList.add("@>, x, y, width, line count (*16 in pixel), db entry name");
			finalList.add("0");
			finalList.add("0");
			finalList.add("200");
			finalList.add("5");
			finalList.add("none");
		}
		break;
		
		// x, y, w, h, comev
		case "eventpicker":
		{
			finalList.add("@>, x, y, width, height, list common events");
			finalList.add("0");
			finalList.add("0");
			finalList.add("200");
			finalList.add("200");
			finalList.add("false");
		}
		break;
		
		// x, y
		case "mapjumper":
		{
			finalList.add("@>, x, y");
			finalList.add("0");
			finalList.add("0");
		}
		break;
		
		}
		
		return finalList;
	}
	
	/**
	 * Reset all the dynamic widgets
	 */
	public void reset() {
		for (Dynamic w : allWidgets.values()) {
			w.dynReset();
		}
	}
	
	/**
	 * Set params from data array
	 */
	public void setParams(ArrayList<String> ar) {
		for (int i = 0; i < ar.size(); i++) {
			params.get(i).dynSetValue(ar.get(i));
		}
	}
	
	/**
	 * Set params from data string
	 */
	public void setParams(String data) {
		String[] splitted = StringUtilities.safeSplit(data, Separator.dataUnit);
		for (int i = 1; i < splitted.length; i++) {
			params.get(i-1).dynSetValue(splitted[i]);
		}
	}
	
	/**
	 * Set params from data string without name
	 */
	public void setParamsWithoutName(String data) {
		String[] splitted = StringUtilities.safeSplit(data, Separator.dataUnit);
		for (int i = 0; i < splitted.length; i++) {
			params.get(i).dynSetValue(splitted[i]);
		}
	}
	
	/**
	 * Build data string from params (with name)
	 */
	public String buildParams() {
		String fin = name;
		for (Dynamic w : params) {
			fin += Separator.dataUnit+w.dynGetValue();
		}
		return fin;
	}
	
	/**
	 * Builds a panel type data. Sets the type for each param
	 */
	public ArrayList<Dynamic> buildPanelTypeData() {
		ArrayList<Dynamic> arr = new ArrayList<Dynamic>();
		for (Dynamic w : params) {
			arr.add(w);
		}
		return arr;
	}
	
	/**
	 * Build data string from params, without any name.
	 */
	public String buildParamsWithoutName() {
		String fin = "";
		int index = 0;
		for (Dynamic w : params) {
			if (index < params.size()-1) {
				fin += w.dynGetValue() + Separator.dataUnit;
			} else {
				fin += w.dynGetValue();
			}
			index++;
		}
		return fin;
	}
	
	public String getParam(int id) {
		return params.get(id).dynGetValue();
	}
	
	public ArrayList<Dynamic> getParams() {
		return params;
	}
	
	public Object getName() {
		return name;
	}
	
	public void setModalsToCenter() {
		for (Widget w : getModalWidgets()) {
			
			if (w instanceof DynDropList) {
				// Ignore this!
			} else {
				w.setRX((getW()-w.getW())/2);
				w.setRY((getH()-w.getH())/2);
			}
			
			// Without this flag the variable and category lists fly away on resize.
			if (w instanceof DynVariableDatabase) {
				((DynVariableDatabase) w).correctResize();
			}

		}
	}
	
}
