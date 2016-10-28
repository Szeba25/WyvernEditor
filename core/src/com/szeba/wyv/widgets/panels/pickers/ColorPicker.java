package com.szeba.wyv.widgets.panels.pickers;

import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.Slider;
import com.szeba.wyv.widgets.Widget;

public class ColorPicker extends Widget {

	private ColorFrame mainFrame;
	private Slider redSlider;
	private Slider greenSlider;
	private Slider blueSlider;
	private Slider alphaSlider;
	
	public ColorPicker(int ox, int oy, int rx, int ry) {
		super(ox, oy, rx, ry, 250, 150);
		
		mainFrame = new ColorFrame(getX(), getY(), 10, 10, 230, 50);
		redSlider = new Slider(getX(), getY(), 10, 65, 230, 15, 0, 100, "0");
		greenSlider = new Slider(getX(), getY(), 10, 85, 230, 15, 0, 100, "0");
		blueSlider = new Slider(getX(), getY(), 10, 105, 230, 15, 0, 100, "0");
		alphaSlider = new Slider(getX(), getY(), 10, 125, 230, 15, 0, 100, "0");
		alphaSlider.setSliderValue(100);
		
		addWidget(mainFrame);
		addWidget(redSlider);
		addWidget(greenSlider);
		addWidget(blueSlider);
		addWidget(alphaSlider);
	}
	
	@Override
	public void setFocused(boolean value) {
		super.setFocused(value);
		if (!value) {
			mainFrame.setFocused(value);
			redSlider.setFocused(value);
			greenSlider.setFocused(value);
			blueSlider.setFocused(value);
			alphaSlider.setFocused(value);
		}
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		updateColors();
	}
	
	@Override
	public void passiveUpdate(int scrolled) {
		updateColors();
	}
	
	private void updateColors() {
		mainFrame.setRed((float) (redSlider.getSliderValue() / 100));
		mainFrame.setGreen((float) (greenSlider.getSliderValue() /100));
		mainFrame.setBlue((float) (blueSlider.getSliderValue() / 100));
		mainFrame.setAlpha((float) (alphaSlider.getSliderValue() / 100));
	}
	
	public String getColorString() {
		return redSlider.getSliderValue() + "x" +
				greenSlider.getSliderValue() + "x" +
				blueSlider.getSliderValue() + "x" +
				alphaSlider.getSliderValue();
	}
	
	public void setColorString(String color) {
		String[] splitted = StringUtilities.safeSplit(color, "x");
		redSlider.setSliderValue(Double.parseDouble(splitted[0]));
		greenSlider.setSliderValue(Double.parseDouble(splitted[1]));
		blueSlider.setSliderValue(Double.parseDouble(splitted[2]));
		alphaSlider.setSliderValue(Double.parseDouble(splitted[3]));
		updateColors();
	}
	
	public void reset() {
		redSlider.setSliderValue(0);
		greenSlider.setSliderValue(0);
		blueSlider.setSliderValue(0);
		alphaSlider.setSliderValue(100);
		updateColors();
	}
	
}
