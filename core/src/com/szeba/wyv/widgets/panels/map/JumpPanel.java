package com.szeba.wyv.widgets.panels.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.TextField;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.ext.textfield.IntField;

public class JumpPanel extends Widget {

	private Button jump;
	private Button search;
	private Button close;
	
	private IntField xfield;
	private IntField yfield;
	private TextField nameField;
	
	public JumpPanel(int ox, int oy, int rx, int ry) {
		super(ox, oy, rx, ry, 230, 150);
		
		this.setTabFocus(true);
		
		jump = new Button(getX(), getY(), 5, 125, 70, 20, "jump!");
		search = new Button(getX(), getY(), 80, 125, 70, 20, "search!");
		close = new Button(getX(), getY(), 155, 125, 70, 20, "cancel");
		
		xfield = new IntField(getX(), getY(), 5, 25, 80, "N", 100000);
		yfield = new IntField(getX(), getY(), 5, 45, 80, "N", 100000);
		nameField = new TextField(getX(), getY(), 5, 85, 220, 1);
		
		addWidget(jump);
		addWidget(search);
		addWidget(close);
		addWidget(xfield);
		addWidget(yfield);
		addWidget(nameField);
	}

	@Override
	public void mainUpdate(int scrolled) {
		if (search.getSignal() != null) {
			setSignal(new Signal(Signal.T_SEARCH_PLACE, nameField.getText()));
		} else if (jump.getSignal() != null) {
			if (xfield.getText().length() > 0 && yfield.getText().length() > 0) {
				setSignal(new Signal(Signal.T_JUMP_COORD, xfield.getText(), yfield.getText()));
			}
		} else if (close.getSignal() != null) {
			setVisible(false);
		}
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		drawBackground(batch);
		drawOutline(batch);
		FontUtilities.print(batch, "Jump to this cell coordinate... ", getX() + 5, getY() + 5);
		FontUtilities.print(batch, "Or search for this place... ", getX() + 5, getY() + 68);
	}
	
	public void prepare() {
		xfield.setFocused(true);
		yfield.setFocused(false);
		jump.setFocused(false);
		search.setFocused(false);
		close.setFocused(false);
	}
	
}
