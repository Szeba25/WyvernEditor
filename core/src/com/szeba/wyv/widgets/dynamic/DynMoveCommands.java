package com.szeba.wyv.widgets.dynamic;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.Separator;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.panels.MoveCommands;

public class DynMoveCommands extends MoveCommands implements Dynamic {

	private String receiver;
	
	public DynMoveCommands(int ox, int oy, int rx, int ry, boolean eventlist) {
		super(ox, oy, rx, ry, eventlist);
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
		this.setVisible(true);
	}

	@Override
	public void dynSetValue(String value) {
		String[] params = StringUtilities.safeSplit(value, Separator.dynParameter);
		this.setEventName(params[0]);
		this.selectThisEventInTheList(params[0]);
		this.getCollectionList().setList(StringUtilities.buildElementList(params[1]));
	}

	@Override
	public String dynGetValue() {
		String eventListSelection = this.getEventName();
		String collectionString = Separator.array;
		collectionString = StringUtilities.buildListString(this.getCollectionList().getList().getElements());
		return eventListSelection + Separator.dynParameter + collectionString;
	}

	@Override
	public void dynReset() {
		this.reset();
	}

	@Override
	public String dynGetCommandStringFormatter(String data) {
		return data;
	}
}
