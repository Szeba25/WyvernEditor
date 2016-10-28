package com.szeba.wyv.widgets.ext.list;

import java.nio.file.Path;
import java.util.ArrayList;

import com.badlogic.gdx.Input.Keys;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.FileUtilities;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.ext.Warning;

public class DirListMapsEditable extends DirListMaps {

	private ArrayList<ListElement> emptydrop;
	private ArrayList<ListElement> mapdrop;
	private ArrayList<ListElement> folderdrop;
	
	private Signal copyPath;
	
	public DirListMapsEditable(int ox, int oy, int rx, int ry, int w, int hval, String directory, String minDir) {
		super(ox, oy, rx, ry, w, hval, directory, minDir);
		
		emptydrop = new ArrayList<ListElement>();
		mapdrop = new ArrayList<ListElement>();
		folderdrop = new ArrayList<ListElement>();
		
		emptydrop.add(new ListElement("paste"));
		
		mapdrop.add(new ListElement("delete"));
		mapdrop.add(new ListElement("copy"));
		mapdrop.add(new ListElement("cut"));
		mapdrop.add(new ListElement("paste"));
		mapdrop.add(new ListElement("rename"));
		
		folderdrop.add(new ListElement("delete"));
		folderdrop.add(new ListElement("copy"));
		folderdrop.add(new ListElement("cut"));
		folderdrop.add(new ListElement("paste"));
		folderdrop.add(new ListElement("rename"));
		
		droplist.setW(90);
		
		copyPath = null;
	}
	
	@Override
	public void openDroplist() {
		super.openDroplist();
		if (getSelectedID() == -1) {
			droplist.setH(1);
			droplist.setElements(emptydrop);
		} else if (this.getSelected().getData().equals("...")) {
			droplist.setVisible(false);
		} else if (!FileUtilities.isValidWyvernSpecial(this.getSelected().getData())) {
			droplist.setH(5);
			droplist.setElements(folderdrop);
		} else {
			droplist.setH(5);
			droplist.setElements(mapdrop);
		}
		
	}
	
	@Override
	public boolean droplistShouldOpen() {
		return true;
	}
	
	@Override
	public void processNewElement(Path filename) {
		this.selectElementByName(StringUtilities.getSpecialFileName(
				filename.getFileName().toString()));
		this.scrollToThis(getSelectedID());
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		super.mainUpdate(scrolled);
		
		// Hotkeys
		if (Wyvern.input.isKeyPressed(Keys.FORWARD_DEL)) {
			this.setSignal(new Signal(Signal.T_DELETE));
		} else if (Wyvern.input.isKeyHold(Keys.CONTROL_LEFT) ||
				Wyvern.input.isKeyHold(Keys.CONTROL_RIGHT)) {
			if (Wyvern.input.isKeyPressed(Keys.C)) {
				setCopyPath(false);
			} else if (Wyvern.input.isKeyPressed(Keys.X)) {
				setCopyPath(true);
			} else if (Wyvern.input.isKeyPressed(Keys.V)) {
				paste();
			}
		}
		
		Signal sg = droplist.getSignal();
		if (sg != null) {
			if (sg.getParam(0).equals("delete")) {
				this.setSignal(new Signal(Signal.T_DELETE));
			} else if (sg.getParam(0).equals("copy")) {
				setCopyPath(false);
			} else if (sg.getParam(0).equals("rename")) {
				if (getSelected().getType() == 2) {
					this.setSignal(new Signal(Signal.T_RENAME_MAP, getSelected().getOriginalName()));
				} else {
					this.setSignal(new Signal(Signal.T_RENAME, getSelected().getOriginalName()));
				}
			} else if (sg.getParam(0).equals("copy")) {
				setCopyPath(false);
			} else if (sg.getParam(0).equals("cut")) {
				setCopyPath(true);
			} else if (sg.getParam(0).equals("paste")) {
				paste();
			}
		}
	}
	
	@Override
	public boolean drawTransparently(ListElement current) {
		if (copyPath != null && copyPath.getType() == Signal.T_CUT) {
			if (getDirectory().equals(copyPath.getParam(0))) {
				for (int i = 1; i < copyPath.getLength(); i++) {
					if (current.getData().equals(copyPath.getParam(i))) {
						return true;
					}
				}
				return false;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public void paste() {
		if (copyPath != null) {
			if (copyPath.getType() == Signal.T_CUT) {
				// Dont allow cut recursion
				if (cutRecursionOccours()) {
					Warning.showWarning("The snake just ate itself... (You can't cut elements inside"
							+ " the elements own subfolder)");
				} else {
					pasteElements(copyPath);
				}
			} else {
				pasteElements(copyPath);
			}
		}
	}
	
	/**
	 * This method will check if a cut recursion will occour with the current copy path.
	 */
	private boolean cutRecursionOccours() {
		for (int i = 1; i < copyPath.getLength(); i++) {
			if (containsPath(copyPath.getParam(0) + "/" + copyPath.getParam(i))) {
				return true;
			}
		}
		return false;
	}

	private boolean containsPath(String string) {
		// Return if the current directory contains the given path.
		String[] thisPath = StringUtilities.safeSplit(getDirectory(), "/");
		String[] checkedPath = StringUtilities.safeSplit(string, "/");
		
		// If the given elements path is longer than this directory, recursion cant occour
		if (checkedPath.length > thisPath.length) {
			return false;
		}
		
		boolean recursion = true;
		for (int i = 0; i < checkedPath.length; i++) {
			if (!thisPath[i].equals(checkedPath[i])) {
				recursion = false;
			}
		}
		
		return recursion;
	}

	private void setCopyPath(boolean cut) {
		if (this.isValidSelectedID()) {
			String[] params = new String[(this.getSelectedEndID() - this.getSelectedID()) + 2];
			params[0] = this.getDirectory();
			for (int i = getSelectedID(), a = 1; i <= getSelectedEndID(); i++, a++) {
				params[a] = this.getElement(i).getOriginalName();
				if (this.getElement(i).getType() == 2) {
					params[a] += "@map";
				}
			}
			if (cut) {
				copyPath = new Signal(Signal.T_CUT, params);
			} else {
				copyPath = new Signal(Signal.T_COPY, params);
			}
		}
	}
	
	/*
	 * We set these to select indexes only to avoid funny selection with shift...
	 */
	
	@Override
	protected void cursorUp() {
		capSelection();
		if (getSelectedID() == -1 && getListSize() > 0) {
			setSelectedID(0);
		} else if (getSelectedID() > 0) {
			selectIndex(getSelectedID() - 1);
			scrollToThis(getSelectedID());
		}
	}
	
	@Override
	protected void cursorDown() {
		capSelection();
		if (getSelectedID() == -1 && getListSize() > 0) {
			setSelectedID(0);
		} else if (getSelectedID() < getListSize() - 1) {
			selectIndex(getSelectedID() + 1);
			scrollToThis(getSelectedID());
		}
	}
	
}
