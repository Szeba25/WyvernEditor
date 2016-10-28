package com.szeba.wyv.widgets.panels;

import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.Button;

public class VariableDatabaseCls extends VariableDatabase {

	private Button empty;
	private Button cancel;
	private Button select;
	
	public VariableDatabaseCls(int ox, int oy, int rx, int ry) {
		super(ox, oy, rx, ry);
		
		setH(450);
		
		select = new Button(getX(), getY(), getW()-225, getH()-25, 70, 20, "select");
		empty = new Button(getX(), getY(), getW()-150, getH()-25, 70, 20, "set empty");
		cancel = new Button(getX(), getY(), getW()-75, getH()-25, 70, 20, "cancel");
		
		addWidget(select);
		addWidget(empty);
		addWidget(cancel);
		
		this.setEnterFocusDefault(select);
		this.setEnterFocusRestricted(cancel, empty, setSize, addCategory);
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		super.mainUpdate(scrolled);
		
		Signal sg;
		
		sg = select.getSignal();
		if (sg != null && Wyvern.database.var.varList.isValidSelectedID()) {
			setSignal(new Signal(Signal.T_DEFAULT, 
					Wyvern.database.var.catList.getSelected().getOriginalName() + ": " +
					Wyvern.database.var.varList.getSelected().getData()));
			select.setFocused(false);
			setVisible(false);
		}
		
		sg = cancel.getSignal();
		if (sg != null) {
			cancel.setFocused(false);
			setVisible(false);
		}
		
		sg = empty.getSignal();
		if (sg != null) {
			setSignal(new Signal(Signal.T_DEFAULT, ""));
			empty.setFocused(false);
			setVisible(false);
		}
	}
	
}
