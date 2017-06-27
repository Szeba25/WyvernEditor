package com.szeba.wyv.widgets.panels;

import java.util.ArrayList;

import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.ext.HolderEventName;
import com.szeba.wyv.widgets.ext.list.CollectionList;
import com.szeba.wyv.widgets.ext.list.EventList;

public class MoveCommands extends Widget {

	private HolderEventName eventName;
	private EventList eventList;
	private CollectionList collectionList;
	private Button[] buttons;
	private Button done;
	
	public MoveCommands(int ox, int oy, int rx, int ry, boolean eventlist) {
		super(ox, oy, rx, ry, 0, 550);
		
		// Read the text file containing the movement commands, and create buttons from them
		TextFile file = new TextFile(Wyvern.INTERPRETER_DIR + "/preferences/move_commands.wdat");
		int coloumn = 0;
		int ypos = 0;
		buttons = new Button[file.getLength()];
		for (int x = 0; x < file.getLength(); x++) {
			buttons[x] = new Button(getX(), getY(), 160 + (coloumn*115), ypos + 18,
					105 , 18 ,file.getValue(x, 0));
			ypos += 19;
			if (ypos > 490) {
				ypos = 0;
				coloumn += 1;
			}
		}
		
		this.setW(270+(coloumn*115));
		
		eventName = new HolderEventName(getX(), getY(), 5, 5, 150, 20);
		eventList = new EventList(getX(), getY(), 5, 25, 150, 12, false);
		if (eventlist) {
			collectionList = new CollectionList(getX(),  getY(), 5, 240, 150, 300, 
					new ArrayList<ListElement>(), false);
		} else {
			collectionList = new CollectionList(getX(),  getY(), 5, 25, 150, 510, 
					new ArrayList<ListElement>(), false);
		}
		done = new Button(getX(), getY(), this.getW()-80, this.getH()-25, 75, 20, "done");
		
		addWidget(eventName);
		addWidget(collectionList);
		if (eventlist) {
			addWidget(eventList);
		}
		addWidget(done);
		
		for (int x = 0; x < buttons.length; x++) {
			addWidget(buttons[x]);
		}
		
		this.setEnterFocusDefault(done);
		
	}

	@Override
	public void mainUpdate(int scrolled) {
		Signal sg;
		for (int x = 0; x < buttons.length; x++) {
			sg = buttons[x].getSignal();
			if (sg != null) {
				collectionList.addString(sg.getParam(0));
			}
		}
		sg = done.getSignal();
		if (sg != null) {
			this.setVisible(false);
		}
		sg = eventList.getSignal();
		if (sg != null) {
			this.eventName.setText(sg.getParam(0));
		}
	}
	
	public void passiveUpdate(int scrolled) {
		super.passiveUpdate(scrolled);
		this.eventList.passiveUpdate(scrolled);
	}
	
	public CollectionList getCollectionList() {
		return this.collectionList;
	}
	
	public void setEventName(String value) {
		eventName.setText(value);
	}
	
	public String getEventName() {
		return eventName.getText();
	}
	
	public void selectThisEventInTheList(String data) {
		int id = eventList.getIDbyData(data);
		eventList.scrollToThis(id);
		eventList.selectIndex(id);
	}
	
	public void reset() {
		this.collectionList.resetList();
		this.eventList.refresh();
	}
	
}
