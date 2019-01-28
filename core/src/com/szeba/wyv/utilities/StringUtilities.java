package com.szeba.wyv.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import com.szeba.wyv.data.ListElement;

public final class StringUtilities {

	private StringUtilities() { }
	
	/** 
	 * Get the special extension (marked with @) from a string 
	 */
	public static String getSpecialExtension(String name) {
		String[] values = name.split("@");
		if (values.length > 1) {
			return values[values.length-1];
		} else {
			return "";
		}
	}
	
	/**
	 * Get the name if the file has a special extension.
	 */
	public static String getSpecialFileName(String name) {
		for (int i = name.length()-1; i > 0; i--) {
			if (name.charAt(i) == '@') {
				return name.substring(0, i);
			}
		}
		return "";
	}
	
	/**
	 * Get the file extension of this string
	 */
	public static String getExtension(String name) {
		String[] values = name.split("\\.");
		if (values.length > 1) {
			return values[values.length-1];
		} else {
			return "";
		}
	}

	/**
	 * Crop a string to the given width in pixels (using the default font) 
	 */
	public static String cropString(String string, int maxWidth) {
		// Get original text width
		int textSize = FontUtilities.getBounds(string).width;
		// Reconstruct text, to contain only maxWidth pixel wide string
		if (textSize > maxWidth) {
			String newString = "";
			for (int i = 0; i < string.length(); i++) {
				if (FontUtilities.getBounds(newString+string.charAt(i)).width < maxWidth) {
					newString += string.charAt(i);
				} else {
					newString += "..";
					break;
				}
			}
			return newString;
		} else {
			return string;
		}
	}
	
	/**
	 * Get relative path to this directory
	 */
	public static String getRelativePath(String minDir, String path) {
		return path.subSequence(minDir.length() + 1, path.length()).toString();
	}
	
	/**
	 * Replace the backslashes to slashes in a path
	 */
	public static String replaceSlashesInPath(String path) {
		if (path != null && path.length() > 0) {
			return path.replace("\\", "/");
		} else {
			return "";
		}
		
	}
	
	/**
	 * Build a List from the given data string.
	 * Format:
	 * $*element1$*element2 ...
	 */
	public static ArrayList<String> buildStringList(String data) {
		ArrayList<String> ar = new ArrayList<String>();
		if (data.length() > 2) {
			data = data.substring(2, data.length());
			String[] splitted = safeSplit(data, Separator.array);
			for (int i = 0 ; i < splitted.length; i++) {
				ar.add(splitted[i]);
			}
		}
		return ar;
	}
	
	public static String buildListString(ArrayList<ListElement> ar) {
		String finalString = Separator.array;
		for (int x = 0; x < ar.size(); x++) {
			finalString += ar.get(x).getOriginalName();
			if (x != ar.size()-1) {
				finalString += Separator.array;
			}
		}
		return finalString;
	}
	
	public static ArrayList<ListElement> buildElementList(String data) {
		ArrayList<String> ar = buildStringList(data);
		ArrayList<ListElement> elm = new ArrayList<ListElement>();
		for (String s : ar) {
			elm.add(new ListElement(s));
		}
		return elm;
	}
	
	/** 
	 * Best method to split a string! 
	 */
	public static String[] safeSplit(String line, String splitBy) {
		// We first add a * symbol to indicate end of line.
		line += splitBy+"*";
		//System.out.println(line);

		String[] splitData;
		splitData = line.split(Pattern.quote(splitBy));

		String[] finalData = new String[splitData.length-1];
		for (int i = 0; i < splitData.length-1; i++) {
			finalData[i] = splitData[i];
		}
		//System.out.println(Arrays.toString(finalData));
		return finalData;
	}
	
}
