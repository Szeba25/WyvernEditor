package com.szeba.wyv.widgets.ext.list;

import java.util.ArrayList;

import com.szeba.wyv.data.ListElement;

public class EntryContentList extends ButtonList {

	public EntryContentList(int ox, int oy, int rx, int ry, int w, int hval, 
			ArrayList<ListElement> elements) {
		super(ox, oy, rx, ry, w, hval, elements, true);
	}
	
}
