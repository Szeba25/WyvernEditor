package com.szeba.wyv.widgets.ext.textfield;

import java.util.HashSet;

import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.widgets.TextField;
import com.szeba.wyv.widgets.ext.Warning;

public class FileNameField extends TextField {

	private String restricted;
	private String allowed;
	private HashSet<Character> restrictedChars;
	private HashSet<Character> allowedChars;
	
	public FileNameField(int ox, int oy, int rx, int ry, int w) {
		super(ox, oy, rx, ry, w, 1);
		
		restrictedChars = new HashSet<Character>();
		allowedChars = new HashSet<Character>();
		TextFile file = new TextFile(Wyvern.INTERPRETER_DIR + "/preferences/file_names.wdat");
		restricted = file.getValue(0, 0);
		allowed = file.getValue(1, 0);
				
		for (Character ch : restricted.toCharArray()) {
			restrictedChars.add(ch);
		}
		for (Character ch : allowed.toCharArray()) {
			allowedChars.add(ch);
		}
	}
	
	public String getRestricted() {
		return restricted;
	}
	
	public String getAllowed() {
		return allowed;
	}
	
	public String getFileName() {
		if (restricted.length() > 0) {
			for (Character ch : getText().toCharArray()) {
				if (restrictedChars.contains(ch)) {
					return "";
				}
			}
		}
		if (allowed.length() > 0) {
			for (Character ch : getText().toCharArray()) {
				if (!allowedChars.contains(ch)) {
					return "";
				}
			}
		}
		return getText();
	}

	public void showWarning() {
		Warning.showWarning(getText() + " is not a valid FileName!");
		Warning.showWarning("Restricted characters: " + getRestricted());
		Warning.showWarning("Allowed characters: " + getAllowed());
	}

}
