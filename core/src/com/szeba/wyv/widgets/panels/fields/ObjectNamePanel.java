package com.szeba.wyv.widgets.panels.fields;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.TextField;
import com.szeba.wyv.widgets.Widget;

public class ObjectNamePanel extends Widget {

	protected boolean closeOnOk;
	protected TextField nameField;
	protected String text;
	protected Button done;
	protected Button cancel;
	
	public ObjectNamePanel(int ox, int oy, int rx, int ry, String text) {
		super(ox, oy, rx, ry, 260, 75);
		
		closeOnOk = false;
		nameField = new TextField(getX(), getY(), 5, 25, 250, 1);
		nameField.setFocused(true);
		nameField.setFocusLocked(true);
		this.text = text;
		done = new Button(getX(), getY(), 110, 50, 70, 20, "done");
		cancel = new Button(getX(), getY(), 185, 50, 70, 20, "cancel");
		
		addWidget(nameField);
		addWidget(done);
		addWidget(cancel);
		
		setTabFocus(true);
	}
	
	public void prepare() {
		nameField.setFocused(true);
		done.setFocused(false);
		cancel.setFocused(false);
		nameField.setText("");
	}
	
	public void setCloseOnOk(boolean value) {
		closeOnOk = value;
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		if (done.getSignal() != null) {
			setSignal(new Signal(Signal.T_DEFAULT, nameField.getText()));
			if (closeOnOk) {
				setVisible(false);
			}
		}
		if (cancel.getSignal() != null) {
			setVisible(false);
		}
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		drawBackground(batch);
		drawOutline(batch);
		FontUtilities.print(batch, text, getX()+5, getY()+5);
	}

	public void setFieldContents(String value) {
		nameField.setText(value);
		nameField.selectAll();
		nameField.setFocused(true);
		done.setFocused(false);
		cancel.setFocused(false);
	}
	
}
