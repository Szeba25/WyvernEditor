package com.szeba.wyv.utilities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.files.TextFile;

public final class Palette {

	private Palette() { }
	
	// Name
	public static String NAME = "";
	
	// Basic colors
	public static Color BLACK = Color.BLACK;
	public static Color WHITE = Color.WHITE;
	public static Color GRAY = Color.GRAY;
	public static Color DARK_GRAY = Color.DARK_GRAY;
	public static Color BLACK05 = new Color(0, 0, 0, 0.5f);
	public static Color WHITE05 = new Color(1, 1, 1, 0.5f);
	public static Color WHITE075 = new Color(1, 1, 1, 0.75f);
	public static Color MAGENTA05 = new Color(1, 0, 1, 0.5f);
	public static Color LIGHT_RED = new Color(0.8f, 0.1f, 0.1f, 1);
	public static Color LIGHT_RED05 = new Color(0.8f, 0.1f, 0.1f, 0.5f);
	
	// Interface colors
	public static Color BATCH = WHITE;
	public static Color FONT;
	public static Color WIDGET_ACTIVE_BRD;
	public static Color WIDGET_PASSIVE_BRD;
	public static Color WIDGET_BKG;
	public static Color WIDGET_BKG2;
	public static Color WIDGET_BKG3;
	public static Color WIDGET_BKG4;
	public static Color WIDGET_HIGHLIGHT;
	public static Color EVENT_BKG_ACTIVE;
	public static Color EVENT_BKG_PASSIVE;
	public static Color COMMONEVENT_BKG_ACTIVE;
	public static Color COMMONEVENT_BKG_PASSIVE;
	public static Color EVENT_SELECTION;
	public static Color EVENT_MOUSEOVER;
	public static Color TILES_MAINTILE;
	public static Color MAP_PAINT_RECT;
	public static Color MAP_SELECTION_RECT;
	public static Color MAP_CELLGRID;
	public static Color MAP_TILEGRID;
	public static Color LIST_SCROLLBAR;
	public static Color LIST_MARK;
	public static Color TOOLBAR_MARK;
	public static Color TEXT_SELECTION;
	public static Color TEXT_CURSOR;
	public static Color OUT_OF_SCREEN;
	
	public static Color[] COMMAND_COLOR;
	
	public static void load_theme(String name) {
		
		TextFile f = null;
		
		if (name == null) {
			name = new TextFile(Wyvern.DIRECTORY + "/themes/config/_config.txt").getValue(0, 0);
			f = new TextFile(Wyvern.DIRECTORY + "/themes/list/"+name);
		} else {
			f = new TextFile(Wyvern.DIRECTORY + "/themes/list/"+name);
		}
		
		NAME = name;
		
		// Assign colors
		FONT = parseColor(f.getLine(3));
		
		WIDGET_ACTIVE_BRD = parseColor(f.getLine(5));
		WIDGET_PASSIVE_BRD = parseColor(f.getLine(6));
		WIDGET_BKG = parseColor(f.getLine(7));
		WIDGET_BKG2 = parseColor(f.getLine(8));
		WIDGET_BKG3 = parseColor(f.getLine(9));
		WIDGET_BKG4 = parseColor(f.getLine(10));
		WIDGET_HIGHLIGHT = parseColor(f.getLine(11));
		
		EVENT_BKG_ACTIVE = parseColor(f.getLine(13));
		EVENT_BKG_PASSIVE = parseColor(f.getLine(14));
		COMMONEVENT_BKG_ACTIVE = parseColor(f.getLine(15));
		COMMONEVENT_BKG_PASSIVE = parseColor(f.getLine(16));
		
		EVENT_SELECTION = parseColor(f.getLine(18));
		EVENT_MOUSEOVER = parseColor(f.getLine(19));
		TILES_MAINTILE = parseColor(f.getLine(20));
		MAP_PAINT_RECT = parseColor(f.getLine(21));
		MAP_SELECTION_RECT = parseColor(f.getLine(22));
		MAP_CELLGRID = parseColor(f.getLine(23));
		MAP_TILEGRID = parseColor(f.getLine(24));
		LIST_SCROLLBAR = parseColor(f.getLine(25));
		LIST_MARK = parseColor(f.getLine(26));
		TOOLBAR_MARK = parseColor(f.getLine(27));
		TEXT_SELECTION = parseColor(f.getLine(28));
		TEXT_CURSOR = parseColor(f.getLine(29));
		OUT_OF_SCREEN = parseColor(f.getLine(30));
		
		COMMAND_COLOR = new Color[12];
		for (int i = 0; i < 12; i++) {
			COMMAND_COLOR[i] = parseColor(f.getLine(32+i));
		}
		
	}
	
	public static Color parseColor(ArrayList<String> data) {
		Color c = new Color(Float.parseFloat(data.get(2))/255.0f,
				Float.parseFloat(data.get(3))/255.0f,
				Float.parseFloat(data.get(4))/255.0f,
				Float.parseFloat(data.get(5))/255.0f);
		return c;
	}
	
}
