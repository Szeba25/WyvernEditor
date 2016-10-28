package com.szeba.wyv.widgets.panels.event;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.data.event.CommandData;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.List;
import com.szeba.wyv.widgets.Widget;

/**
 * From the CommandChooser you can select event commands, defined in the event_commands.ikd file.
 * @author Szeba
 */
public class CommandChooser extends Widget {

	// All the command definitions
	public static HashMap<String, CommandData> cDatabase = new HashMap<String, CommandData>();
	
	// Command categories widget
	private List categories;
	// The container of buttons
	private HashMap<String, ArrayList<Button>> commands;
	private ArrayList<Button> allButtons;
	
	// The last active category
	private String lastCategory;
	// The currently selected category index
	private int currentID;
	
	// The cancel button
	private Button cancel;
	
	public CommandChooser(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
		
		categories = new List(getX(), getY(), 5, 17, 200, (getH()/16)-2, new ArrayList<ListElement>(), false);
		commands = new HashMap<String, ArrayList<Button>>();
		allButtons = new ArrayList<Button>();
		addWidget(categories);
		
		lastCategory = null;
		currentID = -1;
		
		cancel = new Button(getX(), getY(), getW()-75, getH()-25, 70, 20, "cancel");
		addWidget(cancel);
		
		load();
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		// Update buttons visibility
		if (currentID != categories.getSelectedID()) {
			if (lastCategory != null) {
				for (Widget w : commands.get(lastCategory)) {
					w.setVisible(false);
				}
			}
			currentID = categories.getSelectedID();
			lastCategory = categories.getSelected().getOriginalName();
			for (Widget w : commands.get(lastCategory)) {
				w.setVisible(true);
			}
		}
		// Return signal if any button is pressed
		Signal sg;
		for (Button b : allButtons) {
			sg = b.getSignal();
			if (sg != null) {
				this.setSignal(new Signal(Signal.T_DEFAULT, sg.getParam(0)));
				this.setVisible(false);
			}
		}
		sg = cancel.getSignal();
		if (sg != null) {
			// Was a space bug... :D
			cancel.setFocused(false);
			this.setVisible(false);
		}
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		drawBackground(batch);
		drawOutline(batch);
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		// Defocus all the buttons
		if (visible == false) {
			for (Widget w : allButtons) {
				w.setFocused(false);
			}
		}
	}
	
	public void load() {
		
		TextFile file = new TextFile(Wyvern.INTERPRETER_DIR + "/preferences/events/event_commands.ikd");
		String currentCategory = "";
		String currentData = "";
		
		for (int i = 0; i < file.getLength(); i++) {
			
			ArrayList<String> line = file.getLine(i);
			if (line.size() > 0 && !line.get(0).isEmpty()) {
				if (line.get(0).equals("#")) {
					ListElement elm = new ListElement(line.get(1));
					categories.addElement(elm);
					currentCategory = elm.getOriginalName();
				} else if (line.get(0).equals("-")) {
					// This is a new command.
					cDatabase.put(line.get(1), new CommandData(line.get(1), line.get(2)));
					currentData = line.get(1);
					// Add category to the commands entry
					if (!commands.containsKey(currentCategory)) {
						commands.put(currentCategory, new ArrayList<Button>());
					}
					ArrayList<Button> ar = commands.get(currentCategory);
					// Determine button X value.
					int xplus = (ar.size() / 15)*160;
					int yminus = (ar.size() / 15)*450;
					Button but = new Button(getX(), getY(), 220+xplus, 10+(ar.size()*30)-yminus, 150, 25, line.get(1));
					but.setVisible(false);
					ar.add(but);
					allButtons.add(but);
					addWidget(but);
				} else if (line.get(0).equals("*d")) {
					// This is a command parameter list. *d means default parameters.
					CommandData cd = cDatabase.get(currentData);
					cd.setParameters(line);
				} else if (line.get(0).equals("*")) {
					// This is an additional line which will be added below the command.
					CommandData cd = cDatabase.get(currentData);
					cd.setAdditionalLine(line);
				} else if (line.get(0).equals("*e")) {
					// This is the end parameter for this command. This ends the multi line event command
					CommandData cd = cDatabase.get(currentData);
					cd.setEndParam(line.get(1));
				} else if (line.get(0).equals("s")) {
					CommandData cd = cDatabase.get(currentData);
					cd.getPanel().setW(Integer.parseInt(line.get(1)));
					cd.getPanel().setH(Integer.parseInt(line.get(2)));
				} else {
					// This is a definition of a command parameter.
					CommandData cd = cDatabase.get(currentData);
					cd.getPanel().buildWidget(line);
					cd.opens = true;
				}
			}
		}
		
	}
	
}
