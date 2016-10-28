package com.szeba.wyv.widgets.ext.list;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.List;

public class DropList extends List {

	public DropList(int ox, int oy, int rx, int ry, int w, int hval,
			ArrayList<ListElement> elements) {
		super(ox, oy, rx, ry, w, hval, elements, false);
		setMouseoverHighlight(true);
	}

	@Override
	public void mainUpdate(int scrolled) {
		super.mainUpdate(scrolled);
		if ((Wyvern.input.isButtonPressed(0) || Wyvern.input.isButtonPressed(1)) && !mouseInside()) {
			this.setVisible(false);
		}
	}
	
	@Override
	public void drawBlackRects(SpriteBatch batch) {
		
	}
	
	@Override
	public void processClickedElement(int id) {
		super.processClickedElement(id);
		this.setSignal(new Signal(Signal.T_DROPLIST, this.getElement(id).getOriginalName()));
		this.setVisible(false);
	}
	
	@Override
	public int getModalUpdateDelay() {
		return 0;
	}
	
}
