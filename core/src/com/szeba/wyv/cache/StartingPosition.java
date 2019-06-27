package com.szeba.wyv.cache;

import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.files.TextFile;

public class StartingPosition {

	public String mapPath;
	public int cellX;
	public int cellY;
	public int x;
	public int y;
	
	public StartingPosition() {
		TextFile file = new TextFile(Wyvern.INTERPRETER_DIR + "/preferences/start.wdat");
		mapPath = file.getValue(0, 0);
		cellX = Integer.parseInt(file.getValue(1, 0));
		cellY = Integer.parseInt(file.getValue(2, 0));
		x = Integer.parseInt(file.getValue(3, 0));
		y = Integer.parseInt(file.getValue(4, 0));
	}
	
	public void save() {
		TextFile file = new TextFile(Wyvern.INTERPRETER_DIR + "/preferences/start.wdat", null);
		file.addLine();
		file.addValue(mapPath);
		file.addLine();
		file.addValue(Integer.toString(cellX));
		file.addLine();
		file.addValue(Integer.toString(cellY));
		file.addLine();
		file.addValue(Integer.toString(x));
		file.addLine();
		file.addValue(Integer.toString(y));
		file.save();
	}
	
}
