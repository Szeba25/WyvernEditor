package com.szeba.wyv.data.event;

import java.util.ArrayList;

import org.apache.commons.lang3.math.NumberUtils;

import com.szeba.wyv.utilities.Separator;
import com.szeba.wyv.widgets.dynamic.DynamicPanel;

public class CommandData {

	private String name;

	private String color;
	private ArrayList<String> params;
	private ArrayList<ArrayList<String>> additionalLines;
	private ArrayList<ArrayList<String>> paramLines;
	private String endParam;
	private DynamicPanel panel;

	public boolean opens;

	public CommandData(String name, String color) {
		this.name = name;
		this.color = color;
		opens = false;
		params = new ArrayList<String>();
		additionalLines = new ArrayList<ArrayList<String>>();
		paramLines = new ArrayList<ArrayList<String>>();
		endParam = null;
		panel = new DynamicPanel(name, 0, 0, 0, 0, 500, 500);
	}

	/**
	 * Sets this commands default parameters
	 */
	public void setParameters(ArrayList<String> line) {
		for (int i = 1; i < line.size(); i++) {
			params.add(line.get(i));
		}
	}

	/**
	 * Get the parameter list as a string
	 */
	public String getParamString() {
		return getListAsString(params, 0);
	}

	public String getAdditionalLineString(int index) {
		return getListAsString(additionalLines.get(index), 3);
	}

	private String getListAsString(ArrayList<String> list, int start) {
		String p = "";
		for (int i = start; i < list.size(); i++) {
			if (i == list.size() - 1) {
				p += list.get(i);
			} else {
				p += list.get(i) + Separator.dataUnit;
			}
		}
		return p;
	}

	public void setAdditionalLine(ArrayList<String> line) {
		ArrayList<String> ar = new ArrayList<String>();
		additionalLines.add(ar);
		for (int i = 0; i < line.size(); i++) {
			ar.add(line.get(i));
		}
		// This additional line will hold a parameter too.
		if (NumberUtils.isNumber(ar.get(2))) {
			/*
			 * Add a paramline too (Paramline holds one parameter of the original command)
			 * a paramline does not hold the data itself, just the name of this param, and the index of the desired
			 * parameter.
			 */
			ArrayList<String> ar2 = new ArrayList<String>();
			paramLines.add(ar2);
			for (int i = 0; i < line.size(); i++) {
				ar2.add(line.get(i));
			}
		}
	}

	public void setEndParam(String string) {
		endParam = string;
	}

	public DynamicPanel getPanel() {
		return panel;
	}

	public String getEndParam() {
		return endParam;
	}

	public ArrayList<ArrayList<String>> getAdditionalLines() {
		return additionalLines;
	}

	public ArrayList<ArrayList<String>> getParamLines() {
		return paramLines;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

}
