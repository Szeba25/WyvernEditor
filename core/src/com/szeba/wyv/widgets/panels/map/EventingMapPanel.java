package com.szeba.wyv.widgets.panels.map;

import java.awt.Point;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.data.event.Event;
import com.szeba.wyv.data.maps.Cell;
import com.szeba.wyv.input.Input;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.MathUtilities;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.ShapePainter;

/**
 * Eventing map panel extends the editable map panel, to add an eventing interface
 * @author Szebaszti�n
 */
public class EventingMapPanel extends EditableMapPanel {

	private boolean grabbed;
	private Point grabbedPoint;
	
	private Event copyBuffer;
	private String sameID;
	
	private boolean referenceMode;
	private boolean placePlayer;
	
	private double doubleClickTimer;
	
	public EventingMapPanel(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
		
		grabbed = false;
		grabbedPoint = new Point(0, 0);
		
		copyBuffer = new Event(0, 0, true, false);
		sameID = null;
		
		doubleClickTimer = 0;
	}
	
	@Override
	public void extendedDraw(SpriteBatch batch) {
		super.extendedDraw(batch);
		if (getLayerIndex() == 5) {
			drawMouseOver(batch);
			drawActiveTile(batch);
			drawEventInfo(batch);
		}
	}

	@Override
	public void extendedUpdate(int scrolled) {
		super.extendedUpdate(scrolled);
		if (getLayerIndex() == 5) {
			updateClicks();
			updateHotkeys();
		}
		if (doubleClickTimer > 0.0) {
			doubleClickTimer -= Wyvern.getDelta();
		}
	}
	
	public void setReferenceMode(boolean val) {
		referenceMode = val;
	}
	
	public boolean getReferenceMode() {
		return referenceMode;
	}
	
	public void setPlacePlayer(boolean val) {
		placePlayer = val;
	}
	
	public boolean getPlacePlayer() {
		return placePlayer;
	}
	
	private void drawActiveTile(SpriteBatch batch) {
		ShapePainter.drawRectangle(batch, Palette.EVENT_SELECTION, 
				getActiveTile().x*getCurrentMap().getTileSize() + getX() - getCurrentMap().getOffX(), 
				getActiveTile().y*getCurrentMap().getTileSize() + getY() - getCurrentMap().getOffY(), 
				getCurrentMap().getTileSize()+1, 
				getCurrentMap().getTileSize()+1);
		ShapePainter.drawRectangle(batch, Palette.EVENT_SELECTION,
				getActiveTile().x*getCurrentMap().getTileSize() + getX() - getCurrentMap().getOffX() + 2, 
				getActiveTile().y*getCurrentMap().getTileSize() + getY() - getCurrentMap().getOffY() + 2, 
				getCurrentMap().getTileSize()-3, 
				getCurrentMap().getTileSize()-3);
	}
	
	private void drawEventInfo(SpriteBatch batch) {
		Event ev = getEventAt(getActiveTile(), false);
		if (ev != null) {
			int drawX = getX()+getW()-120;
			ShapePainter.drawFilledRectangle(batch, Palette.WIDGET_BKG, drawX, getY()+17, 120, 67);
			if (ev.getReference() != null) {
				FontUtilities.print(batch, "Name: " + ev.getReference().getName(), drawX+2, getY()+19);
				FontUtilities.print(batch, "Common event", drawX+2, getY()+67);
			} else {
				FontUtilities.print(batch, "Name: " + ev.getName(), drawX+2, getY()+19);
				FontUtilities.print(batch, "Map event", drawX+2, getY()+67);
			}
			FontUtilities.print(batch, "ID: " + ev.getSigID(), drawX+2, getY()+35);
			FontUtilities.print(batch, "Coords: " + ev.getX() + "/" + ev.getY(), drawX+2, getY()+51);
			ShapePainter.drawRectangle(batch, this.getActiveBrdColor(), drawX, getY()+17, 120, 67);
		}
	}
	
	private void drawMouseOver(SpriteBatch batch) {
		ShapePainter.drawFilledRectangle(batch, Palette.EVENT_MOUSEOVER, 
				getMapCoord().x*getCurrentMap().getTileSize() + getX() - getCurrentMap().getOffX(), 
				getMapCoord().y*getCurrentMap().getTileSize() + getY() - getCurrentMap().getOffY(), 
				getCurrentMap().getTileSize(), 
				getCurrentMap().getTileSize());
	}
	
	private void updateClicks() {
		if (!getTilesetChange() && mouseInside()) {
			if (Wyvern.input.isButtonPressed(0)) {
				if (doubleClickTimer > 0.0) {
					if (getMapCoord().x == getActiveTile().x && getMapCoord().y == getActiveTile().y) {
						// Process events at this coordinate...
						processThisTile(getActiveTile(), false, "-1");
						doubleClickTimer = 0.0;
					} else {
						setActiveTileLocation(getMapCoord().x, getMapCoord().y, true);
					}
				} else {
					setActiveTileLocation(getMapCoord().x, getMapCoord().y, true);
				}
			} else if (!Wyvern.input.isButtonHold(0) && grabbed) {
				// Set that events location to a new location, if a cell is present at that coordinate.
				if (cellPresentAt(getMapCoord()) && getEventAt(getMapCoord(), false) == null) {
					Event ev = getEventAt(grabbedPoint, true);
					setEventTo(getMapCoord(), ev);
					getActiveTile().setLocation(getMapCoord());
					grabbed = false;
				} else {
					grabbed = false;
				}
			}
		}
	}
	
	private void setCursorByDifference(int xdiff, int ydiff) {
		this.setActiveTileLocation(this.getActiveTile().x+xdiff, this.getActiveTile().y+ydiff, false);
	}
	
	private void setCursorByButtons() {
		if (Wyvern.input.isKeyPressed(Keys.LEFT)) {
			this.setCursorByDifference(-1, 0);
		} 
		if (Wyvern.input.isKeyPressed(Keys.RIGHT)) {
			this.setCursorByDifference(1, 0);
		} 
		if (Wyvern.input.isKeyPressed(Keys.DOWN)) {
			this.setCursorByDifference(0, 1);
		} 
		if (Wyvern.input.isKeyPressed(Keys.UP)) {
			this.setCursorByDifference(0, -1);
		}
	}
	
	private void updateHotkeys() {
		
		if (Wyvern.input.isHotkeyRestricted()) {
			// We DONT allow hokeys when mouse is pressed.
			return;
		}
		
		// Simple cursor setting by buttons!
		this.setCursorByButtons();
		
		if (Wyvern.input.isKeyPressed(Keys.ENTER)) {
			// We enter or add an event here.
			processThisTile(getActiveTile(), false, "-1");
		} else if (Wyvern.input.isKeyPressed(Keys.FORWARD_DEL)) {
			getEventAt(getActiveTile(), true);
		} else if (Wyvern.input.isKeyHold(Keys.CONTROL_LEFT)) {
			if (Wyvern.input.isKeyPressed(Keys.C)) {
				Event ev = getEventAt(getActiveTile(), false);
				copyBuffer.setEqualTo(ev);
				sameID = null;
			} else if (Wyvern.input.isKeyPressed(Keys.X)) {
				Event ev = getEventAt(getActiveTile(), true);
				copyBuffer.setEqualTo(ev);
				sameID = ev.getSigID();
			} else if (Wyvern.input.isKeyPressed(Keys.V)) {
				if (getEventAt(getActiveTile(), false) == null) {
					Event newEvent = null;
					if (sameID != null) {
						newEvent = new Event(0, 0, true, false);
						newEvent.setSigID(sameID);
						sameID = null;
						System.out.println("Editor: Event ID did not change on first cut/paste!");
					} else {
						newEvent = new Event(0, 0, true, true);
					}
					newEvent.setEqualTo(copyBuffer);
					setEventTo(getActiveTile(), newEvent);
				}
			}
		}
	}
	
	public void killGrabbed() {
		grabbed = false;
	}
	
	private void setActiveTileLocation(int x, int y, boolean grab) {
		getActiveTile().setLocation(x, y);
		if (getEventAt(getActiveTile(), false) != null) {
			grabbed = grab;
			grabbedPoint.setLocation(getActiveTile());
		}
		doubleClickTimer = Input.doubleClick;
	}
	
	private boolean cellPresentAt(Point tile) {
		int cellX = MathUtilities.divCorrect(tile.x, getCurrentMap().getCellW());
		int cellY = MathUtilities.divCorrect(tile.y, getCurrentMap().getCellH());
		Cell currentCell = getCurrentMap().getCell(cellX, cellY);
		if (currentCell != null) {
			return true;
		} else {
			return false;
		}
	}
	
	private void setEventTo(Point tile, Event ev) {
		int cellX = MathUtilities.divCorrect(tile.x, getCurrentMap().getCellW());
		int cellY = MathUtilities.divCorrect(tile.y, getCurrentMap().getCellH());
		int finalX = tile.x - cellX*getCurrentMap().getCellW();
		int finalY = tile.y - cellY*getCurrentMap().getCellH();
		Cell currentCell = getCurrentMap().getCell(cellX, cellY);
		if (currentCell != null) {
			ev.setX(finalX);
			ev.setY(finalY);
			currentCell.getEvents().put(finalX + "x" + finalY, ev);
			currentCell.setChanged(true);
			if (!currentCell.isValid()) {
				currentCell.setTileset(getCurrentMap().getActiveTileset());
				currentCell.setValid(true);
			}
		}
	}
	
	private Event getEventAt(Point tile, boolean remove) {
		int cellX = MathUtilities.divCorrect(tile.x, getCurrentMap().getCellW());
		int cellY = MathUtilities.divCorrect(tile.y, getCurrentMap().getCellH());
		int finalX = tile.x - cellX*getCurrentMap().getCellW();
		int finalY = tile.y - cellY*getCurrentMap().getCellH();
		Cell currentCell = getCurrentMap().getCell(cellX, cellY);
		if (currentCell != null) {
			Event ev;
			if (remove) {
				ev = currentCell.getEvents().remove(finalX + "x" + finalY);
			} else {
				ev =  currentCell.getEvents().get(finalX + "x" + finalY);
			}
			if (ev != null && remove) {
				currentCell.setChanged(true);
			}
			return ev;
		} else {
			return null;
		}
	}

	private void processThisTile(Point tile, boolean commonEventCreation, String commonEventID) {
		int cellX = MathUtilities.divCorrect(tile.x, getCurrentMap().getCellW());
		int cellY = MathUtilities.divCorrect(tile.y, getCurrentMap().getCellH());
		int finalX = tile.x - cellX*getCurrentMap().getCellW();
		int finalY = tile.y - cellY*getCurrentMap().getCellH();
		Cell currentCell = getCurrentMap().getCell(cellX, cellY);
		if (currentCell != null) {
			currentCell.setTileset(getCurrentMap().getActiveTileset());
			currentCell.setValid(true);
			String eventKey = finalX + "x" + finalY;
			if (getEventAt(getActiveTile(), false) == null) {
				if (commonEventCreation) {
					/* Create a common event */
					Event comev = new Event(finalX, finalY, true, true);
					comev.setReference(commonEventID);
					currentCell.getEvents().put(eventKey, comev);
					// We set the cell to changed, if a common event was created.
					currentCell.setChanged(true);
					/* End */
				} else if (placePlayer) {
					Wyvern.cache.getStartingPosition().mapPath = getCurrentMap().getRelativePath();
					Wyvern.cache.getStartingPosition().cellX = cellX;
					Wyvern.cache.getStartingPosition().cellY = cellY;
					Wyvern.cache.getStartingPosition().x = finalX;
					Wyvern.cache.getStartingPosition().y = finalY;
					Wyvern.cache.getStartingPosition().save();
				} else if (referenceMode) {
					// Send a signal to open the common event panel
					setSignal(new Signal(Signal.T_COMMON_EVENT, ""));
					// We must not set the edited event cell when we deal with common events
					//Wyvern.cache.setEditedEventCell(currentCell);
				} else {
					currentCell.getEvents().put(eventKey, new Event(finalX, finalY, true, true));
					Wyvern.returnToFromEventing = 1;
					Wyvern.screenChanger = 2;
					Wyvern.cache.setEditedEvent(getEventAt(getActiveTile(), false));
					currentCell.setChanged(true);
				}
			} else {
				if (commonEventCreation) {
					/* Create a common event, only if its another common event reference
					 * We must check the current event if its a common event, and if it equals
					 * to the currently set common event by name. 
					 */
					
					Event existing = getEventAt(getActiveTile(), false);
					
					if ((existing.getReference() == null) || 
						!(existing.getReference() == Wyvern.database.ce.events.get(Integer.parseInt(commonEventID)))) {
						// True!
						Event comev = new Event(finalX, finalY, true, true);
						comev.setReference(commonEventID);
						currentCell.getEvents().put(eventKey, comev);
						// We set the cell to changed if a common event was created.
						currentCell.setChanged(true);
					}
					
					/* End */
				} else {
					if (getEventAt(getActiveTile(), false).getReference() == null) {
						// Enter this event.
						Wyvern.returnToFromEventing = 1;
						Wyvern.screenChanger = 2;
						Wyvern.cache.setEditedEvent(getEventAt(getActiveTile(), false));
						Wyvern.cache.setEditedEventCell(currentCell);
					} else {
						// Send a signal to open the common event panel
						setSignal(new Signal(Signal.T_COMMON_EVENT_EDIT, 
								getEventAt(getActiveTile(), false).getReference().getSigID()));
						// We must not pass the current cell when we edit common events
						//Wyvern.cache.setEditedEventCell(currentCell);
					}
				}
			}
		}
	}

	public void createCommonEvent(String sigid) {
		processThisTile(getActiveTile(), true, sigid);
	}
	
}
