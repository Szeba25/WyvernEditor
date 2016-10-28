package com.szeba.wyv.widgets;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.ShapePainter;

public class Slider extends Widget {

	private boolean grabbed;
	private double sliderPosition;
	private double sliderValue;
	
	private String sliderString;
	private DecimalFormatSymbols symbols;
	private DecimalFormat df;
	
	private double minValue;
	private double maxValue;
	
	public Slider(int ox, int oy, int rx, int ry, int w, int h, int min, int max, String format) {
		super(ox, oy, rx, ry, w, h);
		grabbed = false;
		sliderPosition = 3;
		sliderValue = min;
		
		sliderString = Double.toString(min);
		symbols = new DecimalFormatSymbols(Locale.ENGLISH);
		symbols.setDecimalSeparator('.');
		df = new DecimalFormat(format, symbols);
		
		minValue = min;
		maxValue = max;
	}

	@Override
	public void mainDraw(SpriteBatch batch) {
		this.drawBackground(batch);
		ShapePainter.drawFilledRectangle(batch, Palette.LIST_SCROLLBAR, 
				(int) (getX()+sliderPosition-3), getY(), 6, getH());
		ShapePainter.drawRectangle(batch, Palette.WIDGET_PASSIVE_BRD, getX(), getY(), getW()-60, getH());
		FontUtilities.print(batch, sliderString, getX()+getW()-58, getY()+2);
		this.drawOutline(batch);
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		if (scrolled != 0 && !Wyvern.input.isButtonHold(0)) {
			
			// Scroll the slider!
			setGrabbed(false);
			
			if (this.getSliderValue()-scrolled >= this.minValue
					&& this.getSliderValue()-scrolled <= this.maxValue) {
				this.setSliderValue(this.getSliderValue()-scrolled);
			}
			
		} else {
			if (!Wyvern.input.isButtonHold(0)) {
				setGrabbed(false);
			} else if (Wyvern.input.isButtonHold(0) && !grabbed && mouseInside()) {
				setGrabbed(true);
			} else if (grabbed) {
				sliderPosition = (double) (Wyvern.input.getX()-getX());
				capSlider();
				setSliderValue((((sliderPosition-3) / (getW()-66)) * (maxValue-minValue)) + minValue);
			}
		}
		
	}
	
	protected void setGrabbed(boolean value) {
		grabbed = value;
		if (!value) {
			// Implement slider jumping...
		}
	}
	
	public void setSliderValue(double value) {
		value = Double.valueOf(df.format(value));
		sliderValue = value;
		sliderString = Double.toString(value);
		// Set the sliderPosition according to this value.
		double percentValue = (sliderValue-minValue)/(maxValue-minValue);
		sliderPosition = (percentValue * (getW()-66)) + 3;
	}
	
	public double getSliderValue() {
		return sliderValue;
	}
	
	/**
	 * Cap's the slider inside the widget
	 */
	public void capSlider() {
		if (sliderPosition < 3) {
			sliderPosition = 3;
		} else if (sliderPosition > getW()-63) {
			sliderPosition = getW()-63;
		}
	}
	
	public double getMinValue() {
		return this.minValue;
	}
	
}
