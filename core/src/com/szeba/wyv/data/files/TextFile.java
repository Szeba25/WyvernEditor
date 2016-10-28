package com.szeba.wyv.data.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.badlogic.gdx.files.FileHandle;
import com.szeba.wyv.utilities.Separator;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.ext.Warning;

/** 
 * This class holds a loaded text file, in a 2d String arraylist. The file is loaded from
 * the default running directory. (is outside the .jar file) 
 * @author Szeba
 */
public class TextFile {

	// The double arraylist for the 2d table.
	private String path;
	private String splitBy;
	private ArrayList<ArrayList<String>> lines;
	
	public TextFile(String path, ArrayList<ArrayList<String>> contents) {
		this.path = path;
		setSplitBy(path);
		if (contents == null) {
			lines = new ArrayList<ArrayList<String>>();
		}
	}
	
	public TextFile(String path) {
		// The double arraylist holding the data
		this.path = path;
		lines = new ArrayList<ArrayList<String>>();
		
		// Inputstream.
		File tempFile = new File(path);
		
		if (!tempFile.exists()) {
			Warning.showWarning(path + " text file does not exist.");
			return;
		}
		
		// Filehandle
		FileHandle handle = new FileHandle(tempFile);
		InputStream is = handle.read();
		
		// Create buffered reader from the stream
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		
		// The line variable holding the current readed line
		String line;
		
		// Check whether this is an ikd file or text file
		setSplitBy(path);
		
		// Add lines of data. Catch IOException
		try {
			while ((line=r.readLine()) != null) {
				// We have a line of data here. Split, and add to arraylist
				
				String[] finalData = StringUtilities.safeSplit(line, splitBy);
				
				/*
				for (int i = 0; i < finalData.length; i++) {
					finalData[i] = StringUtils.trim(finalData[i]);
					System.out.println(finalData[i]);
				}
				*/
				
				ArrayList<String> list = new ArrayList<String>(Arrays.asList(finalData));
				
				// Add list to lines
				lines.add(list);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Close the stream
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setSplitBy(String path) {
		splitBy = Separator.fileTextChar;
		if (StringUtilities.getExtension(path).equals("ikd")) {
			splitBy = Separator.fileIkdChar;
		}
	}
	
	public void clear() {
		lines.clear();
	}
	
	public void addLine() {
		lines.add(new ArrayList<String>());
	}
	
	public void addValue(int a, String value) {
		lines.get(a).add(value);
	}
	
	public void addValue(String value) {
		lines.get(getLength()-1).add(value);
	}
	
	/** 
	 * Save this text file.
	 */
	public void save() {
		try {
			FileWriter writer = new FileWriter(path, false);
			BufferedWriter out = new BufferedWriter(writer);
			for (ArrayList<String> arr : lines) {
				String writed = "";
				for (int i = 0; i < arr.size(); i++) {
					// Write to file, using separator to seperate elements
					
					// The writed data cannot contain backslashes, or the separator itself.
					if (StringUtils.contains(arr.get(i), splitBy)) {
						arr.set(i, StringUtils.replace(arr.get(i), splitBy, "#"));
						System.out.println("File: Separator replaced!");
					} else if (StringUtils.contains(arr.get(i), "\\")) {
						arr.set(i, StringUtils.replace(arr.get(i), "\\", "#"));
						System.out.println("File: Backslash replaced!");
					}
					
					if (i == arr.size()-1) {
						writed += arr.get(i);
					} else {
						writed += arr.get(i) + splitBy;
					}
				}
				out.write(writed);
				out.newLine();
			}
			out.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * Since this file is a 2d table, we can get the data here.
	 */
	public String getValue(int a, int b) {
		if (a < lines.size()) {
			if (b < lines.get(a).size()) {
				return lines.get(a).get(b);
			}
		}
		//Warning.showWarning("Non-existing data requested from a text file.");
		System.err.println("Non-existing data requested from a text file.");
		return "";
	}
	
	public void setValue(int a, int b, String value) {
		if (a < lines.size()) {
			if (b < lines.get(a).size()) {
				lines.get(a).set(b, value);
				return;
			}
		}
		//Warning.showWarning("Non-existing data replace request in a text file.");
		System.err.println("Non-existing data replace request in a text file.");
	}
	
	public ArrayList<String> getLine(int a) {
		if (a < lines.size()) {
			return lines.get(a);
		}
		//Warning.showWarning("Non-existing line requested from a text file.");
		System.err.println("Non-existing line requested from a text file.");
		return null;
	}
	
	/** 
	 * Get how many lines are there in this file. 
	 */
	public int getLength() {
		return lines.size();
	}
	
}

