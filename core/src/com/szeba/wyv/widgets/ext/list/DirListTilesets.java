package com.szeba.wyv.widgets.ext.list;

import java.util.ArrayList;

import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.FileUtilities;
import com.szeba.wyv.utilities.StringUtilities;

public class DirListTilesets extends DirList {

	public DirListTilesets(int ox, int oy, int rx, int ry, int w, int hval, String directory,String minDir) {
		super(ox, oy, rx, ry, w, hval, directory, minDir);
		
		this.setSpecialExtensions("tileset");
	}
	
	@Override
	public ArrayList<ListElement> refactorFolderList(String path, ArrayList<String> folderList) {
		ArrayList<ListElement> folders = new ArrayList<ListElement>();
		// Loop in the folderList
		for (String str : folderList) {
			// Only list the back button
			if (str.equals("...")) {
				folders.add(new ListElement(str));
			} else if (!str.equals("_default")) {
				// List this tileset
				if (FileUtilities.isValidTileset(path + "/" + str)) {
					folders.add(new ListElement(StringUtilities.getSpecialFileName(str), str, 3));
				}
			}
		}
		return folders;
	}
	
	@Override
	protected void processDoubleClickedElement(int id) {
		
	}
	
	@Override
	protected void processClickedElement(int id) {
		super.processClickedElement(id);
		setSignal(new Signal(Signal.T_DEFAULT, getSelected().getOriginalName()));
	}

}
