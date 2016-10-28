package com.szeba.wyv.widgets.panels.event;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Widget;

public class CommonEventPanelCls extends CommonEventPanel {

	private Button close;
	private Button select;
	
	public CommonEventPanelCls(int ox, int oy, int rx, int ry) {
		super(ox, oy, rx, ry);
		
		select = new Button(getX(), getY(), getW()-150, getH()-25, 70, 20, "select");
		close = new Button(getX(), getY(), getW()-75, getH()-25, 70, 20, "close");
		
		addWidget(close);
		addWidget(select);
		
		this.setEnterFocusDefault(select);
		// We add close, and the set size button
		this.setEnterFocusRestricted(close, this.getWidget(5));
		this.setTabFocus(true);
	}

	@Override
	public void mainDraw(SpriteBatch batch) {
		drawBackground(batch);
		drawOutline(batch);
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		super.mainUpdate(scrolled);
		
		Signal sg;
		
		sg = select.getSignal();
		if (sg != null) {
			if (Wyvern.database.ce.eventList.isValidSelectedID() &&
					Wyvern.database.ce.eventList.getSelected() != null) {
				setSignal(new Signal(Signal.T_COMMON_EVENT, 
						Wyvern.database.ce.eventList.getSelected().getData()));
				select.setFocused(false);
				setVisible(false);
			}
		}
		
		sg = close.getSignal();
		if (sg != null) {
			close.setFocused(false);
			setVisible(false);
		}
		
	}
	
	@Override
	public void setVisible(boolean visible) {
		// If the panel just disappears.
		if (isVisible() && !visible) {
			Wyvern.database.ce.save();
		}
		super.setVisible(visible);
		// If the panel just appears
		if (visible) {
			for (Widget w : this.getWidgets()) {
				w.setFocused(false);
			}
			// This is the list.
			this.getWidget(2).setFocused(true);
		}
	}
	
	@Override
	protected int getReturnTo() {
		return 1;
	}

	public void hideSelect() {
		this.select.setVisible(false);
		this.setEnterFocusDefault(close);
		this.setEnterFocusRestricted(this.getWidget(5));
	}
	
	public void showSelect() {
		this.select.setVisible(true);
		this.setEnterFocusDefault(select);
		this.setEnterFocusRestricted(close, this.getWidget(5));
	}
	
	public void setSelected(int id) {
		this.turnOnForceEventName();
		Wyvern.database.ce.eventList.scrollToThis(id);
		Wyvern.database.ce.eventList.selectIndex(id);
		this.commitEventChangeName(null);
	}
	
}
