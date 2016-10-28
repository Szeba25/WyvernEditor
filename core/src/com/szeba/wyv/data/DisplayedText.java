package com.szeba.wyv.data;

import java.util.ArrayList;
import java.util.Arrays;

import com.szeba.wyv.utilities.Separator;
import com.szeba.wyv.utilities.StringUtilities;

public class DisplayedText {

	public ArrayList<String> lines = new ArrayList<String>();
	public ArrayList<String> scrapLines = new ArrayList<String>();
	
	public DisplayedText() {
	}
	
	public DisplayedText(String text) {
		setLines(text);
	}
	
	public void setLines(String text) {
		// We dont need the added custom new line and star, because we use safesplit.
		//text+=Separator.customNewLine+"*";
		lines = new ArrayList<String>(Arrays.asList(StringUtilities.safeSplit(text, Separator.customNewLine)));
		//lines.remove(lines.size()-1);
		scrapLines.clear();
		
		for (int z = 0; z < lines.size(); z++) {
			scrapLines.add(lines.get(z));
			if (z != lines.size()-1) {
				lines.set(z, lines.get(z)+Separator.customNewLine);
			}
		}
		
	}
	
}
