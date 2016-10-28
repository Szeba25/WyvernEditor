package com.szeba.wyv.widgets.panels.event;

import java.util.ArrayList;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.data.event.Event;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.TextField;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.ext.TextBorderless;
import com.szeba.wyv.widgets.ext.list.CommonEventList;
import com.szeba.wyv.widgets.ext.textfield.IntField;
import com.szeba.wyv.widgets.panels.PromptPanel;

public class CommonEventPanel extends Widget {

	private CommonEventList eventList;
	private TextField filterField;
	private Button setSizeButton;
	private IntField setSizeField;
	private PromptPanel setSizePrompt;
	private TextBorderless renameText;
	private TextField renameField;
	private boolean forceEventName;
	
	public CommonEventPanel(int ox, int oy, int rx, int ry) {
		super(ox, oy, rx, ry, 600, 520);
		
		eventList = Wyvern.database.ce.eventList;
		filterField = Wyvern.database.ce.filterField;
		
		setSizeButton = new Button(getX(), getY(), 400, 20, 80, 17, "set size");
		setSizeField = new IntField(getX(), getY(), 270, 20, 128, "Z+", 99999);
		setSizePrompt = new PromptPanel(getX(), getY(), 212, 215, "");
		
		renameText = new TextBorderless(getX(), getY(), 5, 20, 70, 17, "edit name:");
		renameField = new TextField(getX(), getY(), 80, 20, 180, 1);
		renameField.setVisible(false);
		forceEventName = false;
		
		addWidget(renameText);
		addWidget(renameField);
		addWidget(eventList);
		addWidget(filterField);
		addWidget(setSizeField);
		addWidget(setSizeButton);
		addModalWidget(setSizePrompt);

	}

	@Override
	public void setVisible(boolean visible) {
		// If the panel just appears
		if (!isVisible() && visible) {
			repositionSharedElements();
			// Set the size field contents
			this.setSizeField.setText(Integer.toString(Wyvern.database.ce.events.size()));
		}
		super.setVisible(visible);
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		
		// Mark as changed if textfield gets focus
		if (renameField.isFocused()) {
			Wyvern.database.ce.markChanged();
		}
		
		applyFilter();
		
		if (Wyvern.input.isKeyPressed(Keys.FORWARD_DEL) && eventList.isFocused()) {
			if (eventList.isValidSelectedID()) {
				// Reset one common event!
				int id = Integer.parseInt(eventList.getSelected().getData());
				Event ev = Wyvern.database.ce.events.get(id);
				ev.removePages();
				ev.addPage();
				ev.setName("");
				renameField.setText("");
				Wyvern.database.ce.markChanged();
			}
		}
		
		Signal sg;
		
		sg = setSizeButton.getSignal();
		if (sg != null) {
			setSizePrompt.changeText("Set entry size to " + setSizeField.getText() + "?");
			setSizePrompt.setVisible(true);
		}
		
		sg = setSizePrompt.getSignal();
		if (sg != null && sg.getParam(0).equals("yes") && setSizeField.getText().length() > 0) {
			
			// Resize the common event data!
			ArrayList<Event> oldList = Wyvern.database.ce.events;
			ArrayList<Event> newList = new ArrayList<Event>();
			
			int newSize = Integer.parseInt(setSizeField.getText());
			
			for (int x = 0; x < newSize; x++) {
				// Populate the new list with new events, or events from the old list
				if (x < oldList.size()) {
					newList.add(oldList.get(x));
				} else {
					Event ev = new Event(0, 0, false, false);
					ev.setSigID(Integer.toString(x));
					ev.setName("");
					newList.add(ev);
				}
			}
			
			Wyvern.database.ce.events = newList;
			resetListContents();
			
			Wyvern.database.ce.markChanged();

		}
		
		sg = eventList.getSignal();
		if (sg != null) {
			if (sg.getType() == Signal.T_EDIT) {
				Wyvern.cache.setEditedEvent(
						Wyvern.database.ce.events.get(Integer.parseInt(sg.getParam(0))));
				
				// When we edit common events, we must set the edited cell to null...
				Wyvern.cache.setEditedEventCell(null);
				
				Wyvern.returnToFromEventing = getReturnTo();
				Wyvern.screenChanger = 2;
			}
		}
		// We pass the eventlist signal!
		commitEventChangeName(sg);
		
	}
	
	public void turnOnForceEventName() {
		this.forceEventName = true;
	}
	
	protected void commitEventChangeName(Signal sg) {
		// We disable commiting if filtering is on, or no event is selected.
		if (Wyvern.database.ce.filter.length() != 0 || !eventList.isValidSelectedID()) {
			renameField.setText("");
			renameField.setVisible(false);
			return;
		}
		if (sg != null && sg.getType() == Signal.T_DEFAULT) {
			// Set the field name to be equal to the events name
			renameField.setText(Wyvern.database.ce.events.get(Integer.parseInt(sg.getParam(0))).getName());
			renameField.setVisible(true);
		} else if (forceEventName) {
			// Hackish way to prevent auto rename...
			// This happens if filtering is off, and list jumps to selected common event.
			forceEventName = false;
			renameField.setText(Wyvern.database.ce.events.get(eventList.getSelectedID()).getName());
			renameField.setVisible(true);
		} else {
			Wyvern.database.ce.events.get(eventList.getSelectedID()).setName(renameField.getText());
			eventList.getSelected().setName(Integer.toString(eventList.getSelectedID()) + ": " +
					renameField.getText());
		}
	}

	@Override
	public void overDraw(SpriteBatch batch) {
		FontUtilities.print(batch, "Filter:", getX()+5, getY()+492);
		if (Wyvern.database.ce.isChanged()) {
			FontUtilities.print(batch, "*", getX()+getW()-10, getY()+3);
		}
	}
	
	public void repositionSharedElements() {
		eventList.setOX(getX());
		eventList.setOY(getY());
		filterField.setOX(getX());
		filterField.setOY(getY());
	}
	
	private void applyFilter() {
		if (!Wyvern.database.ce.filter.equals(filterField.getText())) {
			Wyvern.database.ce.filter = filterField.getText();
			resetListContents();
		}
	}
	
	private void resetListContents() {
		Wyvern.database.ce.rebuildLists(filterField.getText());
	}

	protected int getReturnTo() {
		return 3;
	}
	
}
