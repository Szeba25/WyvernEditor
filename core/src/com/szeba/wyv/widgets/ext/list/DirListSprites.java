package com.szeba.wyv.widgets.ext.list;

import java.util.ArrayList;

import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.utilities.FileUtilities;
import com.szeba.wyv.utilities.StringUtilities;

public class DirListSprites extends DirList {

	public DirListSprites(int ox, int oy, int rx, int ry, int w, int hval, String directory, String minDir) {
		super(ox, oy, rx, ry, w, hval, directory, minDir);
		
		this.setSpecialExtensions("spriteset");
	}

	@Override
	public ArrayList<ListElement> refactorFolderList(String path, ArrayList<String> folderList) {
		ArrayList<ListElement> folders = new ArrayList<ListElement>();
		// Loop in the folderList
		for (String str : folderList) {
			// Only list the back button
			if (str.equals("...")) {
				folders.add(new ListElement(str));
			} else if (FileUtilities.isValidImage(path + "/" + str)) {
				
				if (StringUtilities.getExtension(str).equals("wimg")) {
					folders.add(new ListElement(str, str, 6));
				} else {
					folders.add(new ListElement(str, str, 5));
				}
				
			} else if (FileUtilities.isValidSpriteset(path + "/" + str)) {
				folders.add(new ListElement(StringUtilities.getSpecialFileName(str), str, 3));
			} else {
				// List this folder
				folders.add(new ListElement(str, str, 1));
			}
		}
		return folders;
	}
	
	@Override
	public void processClickedElement(int id) {
		// Simulate a double click!
		super.processClickedElement(id);
		if (
				!FileUtilities.isFolder(getDirectory() + "/" + getSelected().getData()) ||
				this.containsSpecialExtension(getSelected().getData())
				) {
			super.processDoubleClickedElement(id);
		}
	}
	
}
