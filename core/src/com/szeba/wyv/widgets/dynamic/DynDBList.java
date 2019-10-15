package com.szeba.wyv.widgets.dynamic;

import java.util.ArrayList;

import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.CommandStringGen;
import com.szeba.wyv.utilities.Separator;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.ext.list.ButtonList;

public class DynDBList extends ButtonList implements Dynamic {

	private String receiver;
	private String entry;
	
	public DynDBList(int ox, int oy, int rx, int ry, int w, int hval, String entry) {
		super(ox, oy, rx, ry, w, hval, null, false);
		
		this.entry = entry;
		
		// We must call this to list the entries
		this.dynReset();
	}

	@Override
	public void dynSetReceiver(String receiver) {
		this.receiver = receiver;
	}

	@Override
	public String dynGetReceiver() {
		return receiver;
	}

	@Override
	public void dynProcessSignal(Signal signal) {
	}

	@Override
	public void dynSetValue(String value) {
	}

	@Override
	public String dynGetValue() {
		return null;
	}

	@Override
	public void dynReset() {
		// List one database entry
		ArrayList<ListElement> elearr = new ArrayList<>();
		
		if (Wyvern.database.ent.entryData.get(entry) == null) {
			this.resetElements();
		} else {
			ArrayList<String> arr = Wyvern.database.ent.entryData.get(entry).getItems();
			
			for (int x = 0; x < arr.size(); x++) {
				
				// We must handle non ascii texts.
				String dataArr = StringUtilities.safeSplit(arr.get(x), Separator.dataUnit)[0];
				if (CommandStringGen.isArrayText(dataArr)) {
					dataArr = CommandStringGen.generateArrayText(dataArr);
				}
				
				elearr.add(new ListElement(Integer.toString(x) + ": " 
						+ dataArr,
						Integer.toString(x)));
			}
			
			this.setElements(elearr);	
		}
	}
	
	@Override
	protected void processClickedElement(int id) {
		selectIndex(id);
		setSignal(new Signal(Signal.T_DEFAULT, this.entry + ": " + getSelected().getData()));
	}

	@Override
	public String dynGetCommandStringFormatter(String data) {
		return data;
	}
	
}
