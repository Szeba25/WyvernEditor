package com.szeba.wyv.widgets.ext.list;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import com.badlogic.gdx.Input.Keys;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.FileUtilities;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.List;
import com.szeba.wyv.widgets.ext.Warning;
import org.apache.commons.io.FileUtils;

/**
 * An extension of the basic List. This DirList represents a directory browser.
 * It can send various signals (open map, and various files)
 * @author Szeba
 */
public class DirList extends List {

	private ArrayList<String> specialExtensions;
	
	private boolean returnRelative;
	private String directory;
	private String minDir;
	
	public DirList(int ox, int oy, int rx, int ry, int w, int hval, String directory, String minDir) {
		super(ox, oy, rx, ry, w, hval, null, true);
		
		specialExtensions = new ArrayList<String>();
		
		// Set the current directory, and the minimum directory
		returnRelative = true;
		this.directory = directory;
		this.minDir = minDir;
		
		// Open the current directory
		openDirectory(directory);
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		super.mainUpdate(scrolled);
		if (Wyvern.input.isKeyPressed(Keys.BACKSPACE)) {
			if (this.getElement(0).getOriginalName().equals("...")) {
				this.jumpUp();
			}
		}
		if (Wyvern.input.isKeyPressed(Keys.F5)) {
			this.refreshList();
			// We send a signal too to avoid bugs.
			this.setSignal(new Signal(Signal.T_INVALID_DIRLIST));
		}
	}
	
	@Override
	protected void processDoubleClickedElement(int id) {
		// Get the current element
		String element = getElement(id).getData();
		
		// If the element is the back button
		if (element.equals("...")) {
			this.jumpUp();
		// If the element does not exists, remove it
		} else if (!FileUtilities.exists(directory + "/" + element)) {
			Warning.showWarning(StringUtilities.getRelativePath(Wyvern.INTERPRETER_DIR, directory)
					+ "/" + element + " is not accessible!");
			this.refreshList();
			this.setSignal(new Signal(Signal.T_INVALID_DIRLIST));
		// If the element has a valid special extension
		} else if (containsSpecialExtension(element)) {
			// Send signal
			if (returnRelative) {
				setSignal(new Signal(Signal.T_DIRLIST, element,
						StringUtilities.getRelativePath(Wyvern.INTERPRETER_DIR, directory)));
			} else {
				setSignal(new Signal(Signal.T_DIRLIST, element, directory));
			}
		// If the element is a folder
		} else if (FileUtilities.isFolder(directory + "/" + element)) {
			// Open this folder, and reload the list completely
			openDirectory(directory + "/" + element);
		} else {
			// Just set a signal, default action.
			if (returnRelative) {
				setSignal(new Signal(Signal.T_DIRLIST, element,
						StringUtilities.getRelativePath(Wyvern.INTERPRETER_DIR, directory)));
			} else {
				setSignal(new Signal(Signal.T_DIRLIST, element, directory));
			}
		}
	}
	
	protected void setSpecialExtensions(String... extensions) {
		this.specialExtensions.clear();
		for (String s : extensions) {
			this.specialExtensions.add(s);
		}
	}
	
	protected ArrayList<String> getSpecialExtensions() {
		return this.specialExtensions;
	}
	
	protected boolean containsSpecialExtension(String name) {
		if (!FileUtilities.isValidWyvernSpecial(name)) {
			return false;
		} else {
			String ext = StringUtilities.getSpecialExtension(name);
			if (specialExtensions.contains(ext)) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * Jumps one directory up.
	 */
	private void jumpUp() {
		// Jump one directory up
		File f = new File(directory);
		if (f.getParent() != null) {
			openDirectory(StringUtilities.replaceSlashesInPath(f.getParent()));
			int ind = this.getIDbyName(f.getName());
			scrollToThis(ind);
			selectIndex(ind);
		}
	}

	/**
	 * Deletes everything in this selection.
	 */
	public void deleteSelectedElements() {
		capSelection();
		int start = getSelectedID();
		int end = getSelectedEndID();
		
		if (start > -1) {
			if (getElement(start).getData().equals("...")) {
				start++;
			}
		} else {
			return;
		}
		
		for (int delid = start; delid <= end; delid++) {
			FileUtilities.deleteDirectory(new File(directory + "/" + this.getElement(start).getData()));
			removeElement(start);
		}
		selectIndex(-1);
	}
	
	/**
	 * Paste objects based on a copy signal
	 */
	public void pasteElements(Signal signal) {
		
		int i = 1;
		
		while (i < signal.getLength()) {
			if (signal.getParam(i).equals("...")) {
				// We ignore ... files.
				i++;
			} else if (!FileUtilities.exists(this.getDirectory() + "/" + signal.getParam(i))) {
				
				String destPath = this.getDirectory() + "/" + signal.getParam(i);
				String sourcePath = signal.getParam(0) + "/" + signal.getParam(i);
				
				if (FileUtilities.exists(sourcePath)) {
					
					// The target file does not exists, the copied file exists, and is not the back button.
					
					File destFile = new File(destPath);
					
					FileUtilities.copyDirectory(
							new File(sourcePath), destFile);
					
					/* We must handle map IDs here too... Reqursively we must find all maps, and append their
					 * id numbers, if the operation was a copy!
					 */ 
					if (signal.getType() == Signal.T_COPY) {
						Wyvern.appendMapIDs(destFile);
					}
					
					if (signal.getType() == Signal.T_CUT) {
						FileUtilities.deleteDirectory(
								new File(sourcePath));
					}
						
					if (FileUtilities.isValidMap(destPath)) {
						String objName = StringUtilities.getSpecialFileName(signal.getParam(i));
						this.addElement(new ListElement(objName, signal.getParam(i), 2));
					} else {
						this.addElement(new ListElement(signal.getParam(i), signal.getParam(i), 1));
					}
				}
				i++;
			} else {
				Warning.showWarning(signal.getParam(i) + " exists in this directory.");
				i++;
			}
			
		}
	}
	
	/**
	 * Create a new folder in this directory.
	 */
	public void newFolder(String name) {
		if (FileUtilities.exists(directory + "/" + name)) {
			Warning.showWarning("A folder with this name already exists.");
		} else {
			FileUtilities.createFolders(directory + "/" + name);
			refreshList();
		}
		
	}

	public boolean renameNecessary(String name) {
		String f1 = directory + "/" + name;
		String f2 = directory + "/" + getSelected().getData();
		return !(f2.equals(f1));
	}

	public boolean renameSelectedFolder(String name, int type) {
		File f1 = new File(directory + "/" + name);
		File f2 = new File(directory + "/" + getSelected().getData());

		boolean succeed;
		try {
			FileUtils.moveDirectory(f2, f1);
			succeed = true;
		} catch (IOException e) {
			succeed = false;
		}

		if (succeed) {
			int id = this.getSelectedID();
			if (type == 1) {
				// If type is 1, its a folder
				this.replaceElement(id, new ListElement(name, name, type));
			} else if (type == 2) {
				// If type is 2, its a map
				this.replaceElement(id, new ListElement(StringUtilities.getSpecialFileName(name), name, type));
			}
		}

		return succeed;
	}
	
	/** 
	 * Refactors the folderlist, to contain listElements
	 */
	public ArrayList<ListElement> refactorFolderList(String path, ArrayList<String> folderList) {
		ArrayList<ListElement> folders = new ArrayList<ListElement>();
		// Loop in the folderList
		for (String str : folderList) {
			if (str.equals("...")) {
				folders.add(new ListElement(str));
			} else if (!FileUtilities.isFolder(path + "/" + str)) {
				folders.add(new ListElement(str, str, 0));
			} else {
				folders.add(new ListElement(str, str, 1));
			}
		}
		return folders;
	}
	
	public void setReturnRelative(boolean val) {
		returnRelative = val;
	}
	
	public boolean getReturnRelative() {
		return returnRelative;
	}
	
	public void refreshList() {
		openDirectory(directory);
	}
	
	public String getDirectory() {
		return directory;
	}
	
	public String getRelativeDirectory() {
		return StringUtilities.getRelativePath(Wyvern.INTERPRETER_DIR, directory);
	}
	
	public String getMinDir() {
		return minDir;
	}
	
	/** 
	 * Controls what to do with the newly added, or modified element 
	 */
	public void processNewElement(Path filename) {
		
	}
	
	public void setDirectory(String directory) {
		openDirectory(Wyvern.INTERPRETER_DIR + "/" + directory);
	}
	
	public void returnToRoot() {
		openDirectory(minDir);
	}
	
	protected void openDirectory(String targetDirectory) {
		// Get the elements inside this new path
		ArrayList<String> folderList = null;
		ArrayList<ListElement> finalFolderList = null;
		if (minDir.equals(targetDirectory)) {
			folderList = FileUtilities.listFolderContents(targetDirectory);
		} else {
			folderList = FileUtilities.listFolderContentsWithDots(targetDirectory);
		}
		// This folder is accessible
		if (folderList != null) {
			// Create the final folder list which will appear in the list widget
			finalFolderList = refactorFolderList(targetDirectory, folderList);
			// Set elements, and update the directory
			setElements(finalFolderList);
			directory = targetDirectory;
		} else {
			// This folder is probably missing, print a warning out, and return to the default directory
		    if (targetDirectory.equals(minDir)) {
		    	Warning.showWarning(minDir + " is not accessible!!! (default directory)");
		    	// We miss the min directory... do nothing.
		    } else {
		    	Warning.showWarning(targetDirectory + " is not accessible!");
		    	openDirectory(minDir);
		    }
			
		}
	}

}
