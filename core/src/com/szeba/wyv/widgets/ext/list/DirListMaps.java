package com.szeba.wyv.widgets.ext.list;

import java.util.ArrayList;

import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.utilities.FileUtilities;
import com.szeba.wyv.utilities.StringUtilities;

public class DirListMaps extends DirList {

	public DirListMaps(int ox, int oy, int rx, int ry, int w, int hval,
			String directory, String minDir) {
		super(ox, oy, rx, ry, w, hval, directory, minDir);
		
		this.setSpecialExtensions("map");
	}

	@Override
	public ArrayList<ListElement> refactorFolderList(String path, ArrayList<String> folderList) {
		// Refactor the folderlist, to contain the folders at the top, and maps at the bottom of the list
		ArrayList<ListElement> folders = new ArrayList<ListElement>();
		ArrayList<ListElement> maps = new ArrayList<ListElement>();
		// Loop in the folderList
		for (String str : folderList) {
			// Only list the back button
			if (str.equals("...")) {
				folders.add(new ListElement(str));
			} else if (FileUtilities.isFolder(path + "/" + str)){
				// List this folder, and determine type
				if (FileUtilities.isValidMap(path + "/" + str)) {
					maps.add(new ListElement(StringUtilities.getSpecialFileName(str), str, 2));
				} else {
					folders.add(new ListElement(str, str, 1));
				}
			}
		}
		// Join the two arraylists, and return the one which is merged
		folders.addAll(maps);
		return folders;
	}
	
}
