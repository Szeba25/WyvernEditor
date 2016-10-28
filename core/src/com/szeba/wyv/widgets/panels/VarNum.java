package com.szeba.wyv.widgets.panels;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.ext.HolderVariable;
import com.szeba.wyv.widgets.ext.button.SwitchButton;
import com.szeba.wyv.widgets.ext.textfield.IntField;

public class VarNum extends Widget {

	private SwitchButton varnumButton;
	private IntField numField;
	private HolderVariable varField;
	private Button varChg;
	
	public VarNum(int ox, int oy, int rx, int ry) {
		super(ox, oy, rx, ry, 200, 28);
		
		varnumButton = new SwitchButton(getX(), getY(), 5, 5, 40, 17, "Num", "Var");
		numField = new IntField(getX(), getY(), 50, 5, 145, "Z", 99999999);
		varField = new HolderVariable(getX(), getY(), 50, 5, 120, 17);
		varChg = new Button(getX(), getY(), 175, 5, 20, 17, "..");
		
		this.reset();
		
		addWidget(varnumButton);
		addWidget(numField);
		addWidget(varField);
		addWidget(varChg);
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		this.setState(varnumButton.getState());
		if (varChg.getSignal() != null) {
			this.setSignal(new Signal(Signal.T_DEFAULT));
		}
	}
	
	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		if (!focused) {
			numField.setFocused(false);
			numField.deselectAll();
			numField.resetBlinking();
			varChg.setFocused(false);
			varnumButton.setFocused(false);
		} else {
			if (varnumButton.getState() == 0) {
				numField.setFocused(true);
			} else {
				varChg.setFocused(true);
			}
		}
	}
	
	public void setNumber(int number) {
		numField.setText(Integer.toString(number));
	}
	
	public void setVariable(String value) {
		varField.setText(value);
	}
	
	public int getState() {
		return varnumButton.getState();
	}
	
	public void setState(int value) {
		varnumButton.setState(value);
		if (value == 0) {
			varField.setVisible(false);
			varField.setText("");
			varChg.setVisible(false);
			numField.setVisible(true);
		} else {
			varField.setVisible(true);
			varChg.setVisible(true);
			numField.setVisible(false);
			numField.setText("0");
		}
	}
	
	public String getValue() {
		if (varnumButton.getState() == 0) {
			return numField.getValue();
		} else {
			return varField.getText();
		}
	}
	
	public HolderVariable getHolder() {
		return varField;
	}
	
	public void reset() {
		varnumButton.setState(0);
		numField.setText("0");
		varField.setText("");
	}
	
}
