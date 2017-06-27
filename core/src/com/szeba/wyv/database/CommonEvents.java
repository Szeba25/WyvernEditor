package com.szeba.wyv.database;

import java.util.ArrayList;

import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.event.Event;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.data.maps.Cell;
import com.szeba.wyv.data.maps.GameMap;
import com.szeba.wyv.widgets.TextField;
import com.szeba.wyv.widgets.ext.list.CommonEventList;

public class CommonEvents {

	public ArrayList<Event> events;
	
	public CommonEventList eventList;
	public TextField filterField;
	
	public String filter;
	
	private boolean changed;
	
	public void load() {
		TextFile evf = new TextFile(Wyvern.INTERPRETER_DIR + "/database/common_events.wdat");
		events = new ArrayList<Event>();
		int index = 0;
		while(true) {
			Event event = new Event(0, 0, false, false);
			index = event.load(evf, index);
			if (index == -2) {
				break;
			} else if (index == -1) {
				events.add(event);
				break;
			} else {
				events.add(event);
			}
		}
		eventList = new CommonEventList(0, 0, 5, 40, 255, 27, null);
		filterField = new TextField(0, 0, 50, 490, 210, 1);
		rebuildLists(null);
		
		filter = "";
		changed = false;
	}
	
	public void save() {
		if (changed) {
			TextFile evf = new TextFile(Wyvern.INTERPRETER_DIR + "/database/common_events.wdat", null);
			for (Event e : events) {
				e.write(evf);
			}
			evf.save();
			changed = false;
			updateMapEvents();
			System.out.println("Data: Common events saved!");
		}
	}

	public void rebuildLists(String filter) {
		ArrayList<ListElement> ar = new ArrayList<ListElement>();
		for (int x = 0; x < events.size(); x++) {
			Event e = events.get(x);
			String finalString = Integer.toString(x) + ": " + e.getName();
			if (filter == null || finalString.contains(filter)) {
				ar.add(new ListElement(finalString, Integer.toString(x)));
			}
		}
		eventList.setElements(ar);
	}
	
	public void markChanged() {
		changed = true;
	}
	
	/*
	 * Refresh map common events.
	 */
	private void updateMapEvents() {
		for (GameMap map : Wyvern.cache.getMaps().values()) {
			for (Cell cell : map.getCells().values()) {
				for (Event ev : cell.getEvents().values()) {
					if (ev.getReference() != null) {
						// Update map references here.
						ev.setReference(ev.getReference().getSigID());
						cell.setChanged(true);
					}
				}
			}
		}
	}

	public boolean isChanged() {
		return changed;
	}

}
