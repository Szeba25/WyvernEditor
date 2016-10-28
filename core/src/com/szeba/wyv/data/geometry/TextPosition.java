package com.szeba.wyv.data.geometry;

import com.szeba.wyv.data.DisplayedText;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.TextBounds;

public class TextPosition {

	private int real;
	private int line;
	private int pix;
	
	public TextPosition(int real, int line, int pix) {
		this.real = real;
		this.line = line;
		this.pix = pix;
	}
	
	public int getReal() {
		return real;
	}
	
	public int getPix() {
		return pix;
	}
	
	public int getLine() {
		return line;
	}
	
	public void setPos(String text, DisplayedText dispText, int index) {
		real = index;
		line = getLinePos(text, dispText, index);
		setPixPos(text, dispText);
	}
	
	public void setEqualTo(int real, int pix, int line) {
		this.real = real;
		this.pix = pix;
		this.line = line;
	}
	
	public void setEqualTo(TextPosition tpos) {
		this.real = tpos.real;
		this.pix = tpos.pix;
		this.line = tpos.line;
	}
	
	public boolean isEqualTo(int real, int pix, int line) {
		if (this.real == real && this.pix == pix && this.line == line) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getLinePos(String text, DisplayedText dispText, int index) {
		int tempIndex = 0;
		int tempLineSize = 0;
		for (int z = 0; z < dispText.lines.size(); z++) {
			tempLineSize += dispText.lines.get(z).length();
			if (index < tempLineSize) {
				break;
			} else {
				tempIndex++;
			}
		}
		if (tempIndex > dispText.lines.size()-1) {
			tempIndex = dispText.lines.size()-1;
		}
		return tempIndex;
	}
	
	public int getRelativeReal(String text, DisplayedText dispText, int index) {
		int linePos = getLinePos(text, dispText, index);
		for (int i = 0; i < linePos; i++) {
			index -= dispText.lines.get(i).length();
		}
		return index;
	}
	
	private void setPixPos(String text, DisplayedText dispText) {
		// Calculate the pixel position based on the index, and the text
		int startingXPos = 0;
		for (int f = 0; f < line; f++) {
			startingXPos += dispText.lines.get(f).length();
		}
		TextBounds bounds = FontUtilities.getBounds(dispText.lines.get(line), 0, real-startingXPos);
		pix = (int) bounds.width;
	}

}
