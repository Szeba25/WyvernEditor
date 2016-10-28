package com.szeba.wyv.widgets.panels;

import java.util.ArrayList;

import com.szeba.wyv.Wyvern;
import org.apache.commons.lang3.StringUtils;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.TextField;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.ext.TextBorderless;
import com.szeba.wyv.widgets.ext.Warning;
import com.szeba.wyv.widgets.ext.list.ButtonList;
import com.szeba.wyv.widgets.ext.textfield.IntField;

public class VariableDatabase extends Widget {

	protected ButtonList categoryList;
	protected ButtonList variableList;
	protected TextField nameField1;
	
	protected TextBorderless editText;
	protected Button setSize;
	protected TextField nameField2;
	protected Button addCategory;
	protected TextField filterField;
	
	protected IntField sizeField;
	
	protected PromptPanel setSizePrompt;
	
	public VariableDatabase(int ox, int oy, int rx, int ry) {
		super(ox, oy, rx, ry, 475, 420);
		
		setTabFocus(true);
		
		categoryList = Wyvern.database.var.catList;
		variableList = Wyvern.database.var.varList;
		nameField1 = Wyvern.database.var.nameField1;
		sizeField = Wyvern.database.var.sizeField;
				
		editText = new TextBorderless(getX(), getY(), 185, 45, 35, 17, "edit:");
		setSize = new Button(getX(), getY(), 285, 25, 70, 17, "set size");
		nameField2 = new TextField(getX(), getY(), 10, 25, 130, 1);
		addCategory = new Button(getX(), getY(), 142, 25, 38, 17, "add");
		
		filterField = Wyvern.database.var.filterField;
		
		setSizePrompt = new PromptPanel(getX(), getY(), 95, 165, "");
		
		addWidget(nameField2);
		addWidget(addCategory);
		addWidget(categoryList);
		
		addWidget(sizeField);
		addWidget(setSize);
		
		addWidget(editText);
		addWidget(nameField1);
		addWidget(variableList);
		addWidget(filterField);
		
		addModalWidget(setSizePrompt);

		repositionSharedElements();
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		// Changed flag on active rename
		setChangedIfRenameActive();
		
		// Apply the filter to the variable list
		applyFilter();
		
		// Process input and signals
		processSignals();
		processInput();
		
		// Process renaming
		commitVariableName();
	}
	
	@Override
	public void setVisible(boolean visible) {
		// If the database just appears
		if (!isVisible() && visible) {
			repositionSharedElements();
		}
		super.setVisible(visible);
	}
	
	@Override
	public void overDraw(SpriteBatch batch) {
		FontUtilities.print(batch, "Categories:", getX()+10, getY()+7);
		FontUtilities.print(batch, "Variables:", getX()+185, getY()+7);
		FontUtilities.print(batch, "Variable filter:", getX()+10, getY()+397);
		if (Wyvern.database.var.getChanged()) {
			FontUtilities.print(batch, "*", getX()+getW()-10, getY()+3);
		}
	}

	private void setChangedIfRenameActive() {
		// Set the variable database as changed if the nameField1 is focused.
		if (nameField1.isFocused()) {
			Wyvern.database.var.markChanged();
		}
	}
	
	private void commitVariableName() {
		// We disable commiting and renaming if a filter is applied, or no valid selection is present.
		if (Wyvern.database.var.varFilter.length() != 0 || !variableList.isValidSelectedID()) {
			nameField1.setVisible(false);
			nameField1.setText("");
			return;
		}
		Signal sg = variableList.getSignal();
		if (sg != null) {
			// Set the namefield 1 to be equal with the variables name
			nameField1.setVisible(true);
			nameField1.setText(Wyvern.database.var.entries.get(
					categoryList.getSelected().getOriginalName()).get(Integer.parseInt(sg.getParam(0))));
		} else {
			// Set the variable name to be equal with namefield 1
			int id = variableList.getSelectedID();
			Wyvern.database.var.entries.get(
					categoryList.getSelected().getOriginalName()).set(id, nameField1.getText());
			// Update the corresponding list element too
			variableList.getSelected().setOriginalName(Integer.toString(id) + ": " + nameField1.getText());
			variableList.cropElement(variableList.getSelected());
		}
	}
	
	private void processInput() {
		if (Wyvern.input.isKeyPressed(Keys.FORWARD_DEL)) {
			if (categoryList.isFocused()) {
				if (categoryList.isValidSelectedID()) {
					// Remove this category...
					String removed = categoryList.getSelected().getOriginalName();
					Wyvern.database.var.entries.remove(removed);
					categoryList.removeElement(categoryList.getSelectedID());
					// Reset the variable list
					if (categoryList.isValidSelectedID()) {
						resetVariableListContents(categoryList.getSelected().getOriginalName());
					} else {
						resetVariableListContents(null);
					}
					
					// Variables changed, mark for save
					Wyvern.database.var.markChanged();
					
				}
			}
		} else if (Wyvern.input.isKeyPressed(Keys.ENTER)) {
			if (nameField2.isFocused()) {
				nameField2.setFocused(false);
				addCategory.setFocused(true);
			}
		}
	}
	
	public void repositionSharedElements() {
		categoryList.setOX(getX());
		categoryList.setOY(getY());
		variableList.setOX(getX());
		variableList.setOY(getY());
		filterField.setOX(getX());
		filterField.setOY(getY());
		nameField1.setOX(getX());
		nameField1.setOY(getY());
		sizeField.setOX(getX());
		sizeField.setOY(getY());
	}
	
	public void correctResize() {
		repositionSharedElements();
	}
	
	private void processSignals() {
		Signal s;
		
		s = categoryList.getSignal();
		if (s != null) {
			resetVariableListContents(s.getParam(0));
			sizeField.setText(Integer.toString(Wyvern.database.var.entries.get(s.getParam(0)).size()));
		}
		
		s = addCategory.getSignal();
		if (s != null) {
			String name = nameField2.getText();
			if (!Wyvern.database.var.entries.containsKey(name)) {
				if (name.length() > 0) {
					
					Wyvern.database.var.entries.put(name, new ArrayList<String>());
					
					categoryList.addElement(new ListElement(name));
					categoryList.scrollToThis(categoryList.getListSize()-1);
					categoryList.selectIndex(categoryList.getListSize()-1);
					
					resetVariableListContents(name);
					
					addCategory.setFocused(false);
					nameField2.setFocused(true);
					nameField2.setText("");
					
					// Variables changed, mark for save
					Wyvern.database.var.markChanged();
					
				}
			} else {
				Warning.showWarning("This category already exists.");
				int id = categoryList.getIDbyName(name);
				categoryList.scrollToThis(id);
				categoryList.selectIndex(id);
				resetVariableListContents(name);
				addCategory.setFocused(false);
				nameField2.setFocused(true);
				nameField2.selectAll();
			}
			
		}
		
		s = setSize.getSignal();
		if (s != null && categoryList.getSelected() != null) {
			setSizePrompt.changeText("Set entry size to " + sizeField.getText() + "?");
			setSizePrompt.setVisible(true);
		}
		
		s = setSizePrompt.getSignal();
		if (s != null && s.getParam(0).equals("yes")) {
			// Resize the variable list!
			if (categoryList.getSelected() != null && sizeField.getText().length() > 0) {
				String category = categoryList.getSelected().getOriginalName();
				
				ArrayList<String> newList = new ArrayList<String>();
				ArrayList<String> oldList = Wyvern.database.var.entries.get(category);
				
				for (int z = 0; z < Integer.parseInt(sizeField.getText()); z++) {
					if (z < oldList.size()) {
						newList.add(oldList.get(z));
					} else {
						newList.add("");
					}
				}
				
				Wyvern.database.var.entries.put(category, newList);
				
				resetVariableListContents(category);
				nameField1.setText("");
				
				// Variables changed, mark for save
				Wyvern.database.var.markChanged();
			}
		}
	}

	private void resetVariableListContents(String category) {
		ArrayList<ListElement> ar = new ArrayList<ListElement>();
		ArrayList<String> varlist = null;
		if (category != null) {
			varlist = Wyvern.database.var.entries.get(category);
		}
		if (varlist != null) {
			for (int ind = 0; ind < varlist.size(); ind++) {
				
				String finalName = Integer.toString(ind) + ": " + varlist.get(ind);
				
				if (Wyvern.database.var.varFilter.length() == 0 ||
						StringUtils.contains(finalName, Wyvern.database.var.varFilter)) {
					ar.add(new ListElement(finalName, Integer.toString(ind)));
				}
			}
		}
		variableList.setElements(ar);

	}
	
	private void applyFilter() {
		if (!Wyvern.database.var.varFilter.equals(filterField.getText())) {
			Wyvern.database.var.varFilter = filterField.getText();
			if (categoryList.isValidSelectedID()) {
				resetVariableListContents(categoryList.getSelected().getOriginalName());
			}
		}
	}
	
}
