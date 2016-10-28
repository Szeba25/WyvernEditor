package com.szeba.wyv.widgets.ext.list;

import java.util.ArrayList;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.List;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.ext.Warning;

public class CollectionList extends Widget {

	private ArrayList<ListElement> originalList;
	private List collection;
	private Button up;
	private Button down;
	private boolean unique;
	
	public CollectionList(int ox, int oy, int rx, int ry, int w, int h, ArrayList<ListElement> ar, boolean unique) {
		super(ox, oy, rx, ry, w, h);
		
		originalList = ar;
		
		collection = new List(getX(), getY(), 5, 18, w-10, (h-32)/16, getOriginalList(), false);
		up = new Button(getX(), getY(), 5, 2, 30, 15, "up");
		down = new Button(getX(), getY(), 37, 2, 30, 15, "dn");
		this.unique = unique;
		
		addWidget(up);
		addWidget(down);
		addWidget(collection);
		
		// Focus elements by scrolling above them!
		collection.setScrollFocus(true);
		this.setScrollFocus(true);
	}
	
	private ArrayList<ListElement> getOriginalList() {
		ArrayList<ListElement> newList = new ArrayList<ListElement>();
		for (ListElement el : originalList) {
			ListElement newEl = new ListElement("");
			newEl.setEqualTo(el);
			newList.add(newEl);
		}
		newList.add(new ListElement("add..."));
		return newList;
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		if (!focused) {
			up.setFocused(false);
			down.setFocused(false);
			collection.setFocused(false);
		}
	}
	
	@Override
	public void overDraw(SpriteBatch batch) {
		this.drawOutline(batch);
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		Signal s = up.getSignal();
		if (s != null) {
			
			if (collection.isValidSelectedID()) {
				if (!collection.getSelected().getOriginalName().equals("add...")) {
					if (collection.getSelectedID()-1 >= 0) {
						ArrayList<ListElement> arr = collection.getElements();
						ListElement el1 = arr.get(collection.getSelectedID());
						ListElement el2 = arr.get(collection.getSelectedID()-1);
						arr.set(collection.getSelectedID()-1, el1);
						arr.set(collection.getSelectedID(), el2);
						collection.selectIndex(collection.getSelectedID()-1);
					}
				}
			}
			
		}
		s = down.getSignal();
		if (s != null) {
			
			if (collection.isValidSelectedID()) {
				if (!collection.getSelected().getOriginalName().equals("add...")) {
					if (!collection.getElement(collection.getSelectedID()+1).getOriginalName().equals("add...")) {
						ArrayList<ListElement> arr = collection.getElements();
						ListElement el1 = arr.get(collection.getSelectedID());
						ListElement el2 = arr.get(collection.getSelectedID()+1);
						arr.set(collection.getSelectedID()+1, el1);
						arr.set(collection.getSelectedID(), el2);
						collection.selectIndex(collection.getSelectedID()+1);
					}
				}
			}
			
		}
		if (collection.isFocused()) {
			if (Wyvern.input.isKeyPressed(Keys.FORWARD_DEL)) {
				if (collection.isValidSelectedID() && 
						!collection.getSelected().getOriginalName().equals("add...")) {
					collection.removeElement(collection.getSelectedID());
					
					if (collection.getSelectedID() < collection.getListSize()-1) {
						collection.selectIndex(collection.getSelectedID());
					} else if (collection.getSelectedID() > 0) {
						collection.selectIndex(collection.getSelectedID()-1);
					}
				}
			}
		}
	}
	
	public void addString(String value) {
		
		// If unique, and value already exists, return.
		if (unique) {
			for (ListElement el : collection.getElements()) {
				if (el.getOriginalName().equals(value)) {
					Warning.showWarning("Element already exists in list.");
					return;
				}
			}
		}
		
		if (collection.isValidSelectedID()) {
			collection.addElement(collection.getSelectedID(), new ListElement(value));
			collection.selectIndex(collection.getSelectedID()+1);
			collection.scrollList(1, 1, 16);
		} else {
			collection.addElement(collection.getListSize()-1, new ListElement(value));
		}
	}
	
	public void resetList() {
		collection.setElements(getOriginalList());
	}
	
	public void setList(ArrayList<ListElement> elements) {
		collection.setElements(elements);
	}
	
	public List getList() {
		return collection;
	}

}
