package com.szeba.wyv.utilities;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.szeba.wyv.widgets.dynamic.Dynamic;

/** 
 * Generate the displayed event command string from the list element name and data. 
 */
public final class CommandStringGen {

	private CommandStringGen() {}
	
	public static String generate(String name, String data, ArrayList<Dynamic> panelData) {
		// Get the elements of the data
		String[] chopped = StringUtilities.safeSplit(data, Separator.dataUnit);
		String fin = "";
		// Loop by the first data
		if (name.equals("add command")) {
			return ">";
		} else {
			fin += ">";
			for (int x = 0; x < chopped.length; x++) {
				
				if (isArrayText(chopped[x])) {
					chopped[x] = generateArrayText(chopped[x]);
				}
				
				if (isFileReference(chopped[x])) {
					chopped[x] = StringUtilities.safeSplit(chopped[x], Separator.dynParameter)[1];
				}
				
				if (panelData != null && x > 0) {
					chopped[x] = panelData.get(x-1).dynGetCommandStringFormatter(chopped[x]);
				}
				
				if (x == chopped.length-1) {
					fin += chopped[x];
				} else {
					fin += chopped[x] + ", ";
				}
			}
			return fin;
		}
	}
	
	public static boolean isArrayText(String data) {
		if (data.length() <= 1 || !(data.charAt(0) == Separator.escapeCharacter.charAt(0) &&
									data.charAt(1) == Separator.array.charAt(1)) ) {
			return false;
		}
		
		ArrayList<String> chopped = StringUtilities.buildStringList(data);
		
		for (String str : chopped) {
			try {
				Integer.parseInt(str);
			} catch (NumberFormatException nfe) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isFileReference(String data) {
		String[] chopped = StringUtilities.safeSplit(data, Separator.dynParameter);
		if (chopped.length < 2 || chopped.length > 2) {
			return false;
		}
		if (chopped[0].length() > 0 && Character.toString(chopped[0].charAt(0)).equals("/")) {
			if (chopped[1].length() > 0 && StringUtils.contains(chopped[1], ".")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public static String generateArrayText(String data) {
		ArrayList<String> chopped = StringUtilities.buildStringList(data);
		String finalString = "";
		for (String str : chopped) {
			int charint = Integer.parseInt(str);
			if (charint == 25) {
				finalString += "; ";
			} else {
				finalString += Character.toString((char)charint);
			}
		}
		return finalString;
	}
	
}
