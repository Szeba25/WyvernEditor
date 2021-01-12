package com.szeba.wyv.screens.implemented;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import com.szeba.wyv.Wyvern;
import org.apache.commons.lang3.SystemUtils;

import com.badlogic.gdx.Input.Keys;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.data.maps.Cell;
import com.szeba.wyv.data.maps.GameMap;
import com.szeba.wyv.screens.GeneralScreen;
import com.szeba.wyv.utilities.MathUtilities;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.dynamic.DynamicPreview;
import com.szeba.wyv.widgets.ext.Warning;
import com.szeba.wyv.widgets.ext.button.TabButton;
import com.szeba.wyv.widgets.ext.list.DirListMapsEditable;
import com.szeba.wyv.widgets.panels.PromptPanel;
import com.szeba.wyv.widgets.panels.TabbedBar;
import com.szeba.wyv.widgets.panels.Toolbar;
import com.szeba.wyv.widgets.panels.about.About;
import com.szeba.wyv.widgets.panels.event.CommonEventPanelCls;
import com.szeba.wyv.widgets.panels.fields.FileNamePanel;
import com.szeba.wyv.widgets.panels.map.EditMapMetadata;
import com.szeba.wyv.widgets.panels.map.EventingMapPanel;
import com.szeba.wyv.widgets.panels.map.JumpPanel;
import com.szeba.wyv.widgets.panels.map.ModMapCloser;
import com.szeba.wyv.widgets.panels.map.NewMapPanel;
import com.szeba.wyv.widgets.panels.tileset.CellEditor;
import com.szeba.wyv.widgets.panels.tileset.TilesetPanel_Map;

/** 
 * The main editor screen, and widgets. Here we will update, and render the widgets,
 * and we also control their signals.
 * @author Szeba
 */
public class MainScreen extends GeneralScreen {
	
	// Widget objects
	private DirListMapsEditable e_List;
	private EventingMapPanel e_MapPanel;
	private TilesetPanel_Map e_TilesetPanel;
	private TabbedBar e_TabbedBar;
	private Toolbar e_Toolbar;
	private CellEditor e_CellEditor;
	private CommonEventPanelCls e_CommonEventPanel;
	private NewMapPanel e_NewMapPanel;
	private ModMapCloser e_ModMapCloser;
	private PromptPanel e_FileDeletePanel;
	private FileNamePanel e_NewFolderPanel;
	private FileNamePanel e_RenameFolderPanel;
	private JumpPanel e_JumpPanel;
	private EditMapMetadata e_EditMapMetadata;
	private PromptPanel e_PromptPanel;
	private About e_AboutWin;
	private DynamicPreview e_DynamicPreview;
	
	@Override
	public void init() {
		super.init();
		
		// Create elements
		e_List = new DirListMapsEditable(0, 0, 0, 0, 272, 18,
				Wyvern.INTERPRETER_DIR + "/maps", Wyvern.INTERPRETER_DIR + "/maps");
		e_List.setMultiSelection(true);
		
		e_MapPanel = new EventingMapPanel(0, 0, 0, 56, 0, 0);
		e_MapPanel.setScrollFocus(true);
		
		e_TilesetPanel = new TilesetPanel_Map(0, 0, 0, 32, 272, 0);
		e_TilesetPanel.recalculate(3200, 16);
		e_TilesetPanel.setScrollFocus(true);
		
		e_TabbedBar = new TabbedBar(0, 0, 0, 32, 0, 24);
		e_TabbedBar.setTabFocus(false);
		e_TabbedBar.setFocused(true);
		e_TabbedBar.setFocusLocked(true);
		
		e_Toolbar = new Toolbar(0, 0, 0, 0, 0, 32);
		
		e_Toolbar.addButton("N", "Create new map", 0);
		e_Toolbar.addButton("NF", "Create new folder", 0);
		e_Toolbar.addButton("S", "Save the current map", 0);
		e_Toolbar.addButton("SA", "Save all opened maps", 0);
		e_Toolbar.addButton("U", "Undo last tile change on map", 0);
		e_Toolbar.addButton("", "", 0);
		e_Toolbar.addButton("Pe", "Pencil tool", 0);
		e_Toolbar.addButton("Re", "Rectangle tool", 0);
		e_Toolbar.addButton("F", "Flood fill tool", 0);
		e_Toolbar.addButton("C", "Copy tool", 0);
		e_Toolbar.addButton("P", "Paste tool", 0);
		e_Toolbar.addButton("", "", 0);
		e_Toolbar.addButton("1", "First layer", 0);
		e_Toolbar.addButton("2", "Second layer", 0);
		e_Toolbar.addButton("3", "Third layer", 0);
		e_Toolbar.addButton("4", "Fourth layer", 0);
		e_Toolbar.addButton("5", "Fifth layer", 0);
		e_Toolbar.addButton("e", "Event layer", 0);
		e_Toolbar.addButton("ref", "Toggle creating common event references on map", 0);
		e_Toolbar.addButton("ply", "Place the player's starting position", 0);
		e_Toolbar.addButton("Gr", "Toggle grid", 0);
		e_Toolbar.addButton("J", "Jump to cell!", 0);
		e_Toolbar.addButton("dim", "Make non-active layers semi transparent", 0);
		e_Toolbar.addButton("shd", "Darken cells with different tilesets", 0);
		e_Toolbar.addButton("only", "Show only the current layer", 0);
		e_Toolbar.addButton("", "", 0);
		e_Toolbar.addButton("Ed", "Edit map settings", 0);
		e_Toolbar.addButton("D", "Edit the database", 0);
		e_Toolbar.addButton("Dce", "Edit common events", 0);
		e_Toolbar.addButton("", "", 0);
		e_Toolbar.addButton("Pl", "Play!", 0);
		e_Toolbar.addButton("Dyn", "Test dynamic panels", 0);
		e_Toolbar.addButton("", "", 0);
		e_Toolbar.addButton("A", "About Wyvern", 0);
		
		e_Toolbar.setDisabledIcon(4, 0);
		e_Toolbar.setDisabledIcon(26, 1);
		
		e_Toolbar.setDisabled(4, true);
		e_Toolbar.setDisabled(26, true);
		
		e_Toolbar.setTooltip("Toolbar.");
		
		setActiveToolIcon(6);
		setActiveLayerIcon(12);
		setActiveIcon(22);
		setActiveIcon(23);

		e_Toolbar.setFocused(true);
		e_Toolbar.setFocusLocked(true);
		
		e_CellEditor = new CellEditor(0, 0, 510, 100);
		
		e_CommonEventPanel = new CommonEventPanelCls(0, 0, 0, 0);
		
		e_NewMapPanel = new NewMapPanel(0, 0, 362, 200);
		e_NewMapPanel.setTabFocus(true);
		
		e_ModMapCloser = new ModMapCloser(0, 0, 462, 260, 200, 170);
		
		e_FileDeletePanel = new PromptPanel(0, 0, 0, 0, "Delete files?");
		
		e_NewFolderPanel = new FileNamePanel(0, 0, 0, 0, "New folder");
		e_RenameFolderPanel = new FileNamePanel(0, 0, 0, 0, "Rename file");
		
		e_JumpPanel = new JumpPanel(0, 0, 462, 265);
		
		e_EditMapMetadata = new EditMapMetadata(0, 0, 0, 0);
		
		e_PromptPanel = new PromptPanel(0, 0, 0, 0, "Save all unsaved data?");
		
		e_AboutWin = new About(0, 0, 362, 190);
		
		e_DynamicPreview = new DynamicPreview(0, 0, 0, 0);
		
		// Add them to container
		addWidget(e_Toolbar);
		addWidget(e_TabbedBar);
		addWidget(e_List);
		addWidget(e_TilesetPanel);
		addWidget(e_MapPanel);
		
		addModalWidget(e_CellEditor);
		addModalWidget(e_CommonEventPanel);
		addModalWidget(e_NewMapPanel);
		addModalWidget(e_ModMapCloser);
		addModalWidget(e_FileDeletePanel);
		addModalWidget(e_NewFolderPanel);
		addModalWidget(e_RenameFolderPanel);
		addModalWidget(e_JumpPanel);
		addModalWidget(e_EditMapMetadata);
		addModalWidget(e_PromptPanel);
		addModalWidget(e_AboutWin);
		addModalWidget(e_DynamicPreview);
	}

	@Override
	public void screenUpdate(int scrolled) {
		super.screenUpdate(scrolled);
		
		// Process the signals
		processListSignal();
		processFolders();
		processCloseMapButtons();
		processMapSignals();
		processCommonEventCreation();
		processHotkeys();
		processToolbar(null);
		processPromptPanel();
		
		// Communicate between panels...
				
		// Jump to cell
		processJumping();
		
		// Set a star mark to modified maps
		processTabbedBarStars();
		
		// Set the current map inside the panel, based on the tabbed bar buttons
		GameMap oldMap = Wyvern.cache.getCurrentMap();
		Wyvern.cache.setCurrentMap(
				e_MapPanel.setCurrentMap(Wyvern.cache.getMaps(), e_TabbedBar.getActiveButton()));
		
		// If oldMap is different than the current map, we kill the grab process.
		if (oldMap != Wyvern.cache.getCurrentMap()) {
			e_MapPanel.killGrabbed();
		}
		
		// Set the currently active cell. If we are on the event layer, the active cell is where the selection is.
		if (Wyvern.cache.getCurrentMap() != null) {
			if (e_MapPanel.getLayerIndex() == 5) {
				Wyvern.cache.setCurrentCellCoord(
						MathUtilities.divCorrect(e_MapPanel.getActiveTile().x, Wyvern.cache.getCurrentMap().getCellW()),
						MathUtilities.divCorrect(e_MapPanel.getActiveTile().y, Wyvern.cache.getCurrentMap().getCellH()));
			} else {
				Wyvern.cache.setCurrentCellCoord(e_MapPanel.getCellCoord());
			}
		}
		
		// Set disabled flags
		if (Wyvern.cache.getCurrentMap() == null) {
			this.e_Toolbar.setDisabled(4, true);
			this.e_Toolbar.setDisabled(26, true);
		} else {
			if (Wyvern.cache.getCurrentMap().undoIsEmpty()) {
				this.e_Toolbar.setDisabled(4, true);
			} else {
				this.e_Toolbar.setDisabled(4, false);
			}
			this.e_Toolbar.setDisabled(26, false);
		}
		
		// Set the current tileset inside the tileset panel based on the current maps active tileset
		setCurrentTileset();
		
		// Set the selected tiles
		e_MapPanel.setSelectedTiles(e_TilesetPanel.getSelectedTiles());
		
		// Load the newly created map
		loadNewMaps();
		
	}

	@Override
	public void resize(int width, int height) {
		
		e_Toolbar.setW(width);
		
		e_TabbedBar.setW(width-272);
		
		e_MapPanel.setH(height-56);
		e_MapPanel.setW(width-272);
		
		e_TilesetPanel.setRX(width-(272));
		e_TilesetPanel.setH(height-336);
		e_TilesetPanel.fullReset(3200);
		e_List.setRX(width-272);
		e_List.setRY(height-288);
		
		e_CellEditor.setToCenter();
		e_CommonEventPanel.setToCenter();
		e_NewMapPanel.setToCenter();
		e_ModMapCloser.setToCenter();
		e_FileDeletePanel.setToCenter();
		e_NewFolderPanel.setToCenter();
		e_RenameFolderPanel.setToCenter();
		e_JumpPanel.setToCenter();
		e_EditMapMetadata.setToCenter();
		e_PromptPanel.setToCenter();
		e_AboutWin.setToCenter();
		e_DynamicPreview.resize(width, height);
		e_DynamicPreview.setToCenter();
	}
	
	private void processJumping() {
		Signal s = e_JumpPanel.getSignal();
		if (s != null && e_MapPanel.getCurrentMap() != null) {
			if (s.getType() == Signal.T_JUMP_COORD) {
				e_MapPanel.getCurrentMap().setCameraToCell(
						Integer.parseInt(s.getParam(0)),
						Integer.parseInt(s.getParam(1)) );
				e_JumpPanel.setVisible(false);
			} else {
				if (e_MapPanel.getCurrentMap().jumpToPlace(s.getParam(0))) {
					e_JumpPanel.setVisible(false);
				}
			}
		}
	}
	
	/**
	 * The list signal will control the opening of maps, and various file manipulation takes place here.
	 */
	private void processListSignal() {
		Signal signal = e_List.getSignal();
		if (signal != null) {
			if (signal.getType() == Signal.T_DIRLIST) {
				loadMapBySignal(signal);
			} else if (signal.getType() == Signal.T_DELETE) {
				e_FileDeletePanel.setVisible(true);
			} else if (signal.getType() == Signal.T_COPY) {
				// Not used
			} else if (signal.getType() == Signal.T_CUT) {
				// Not used
			} else if (signal.getType() == Signal.T_PASTE) {
				// Not used
			} else if (signal.getType() == Signal.T_RENAME) {
				e_RenameFolderPanel.setType(Signal.T_RENAME);
				e_RenameFolderPanel.setVisible(true);
				e_RenameFolderPanel.setFieldContents(signal.getParam(0));
			} else if (signal.getType() == Signal.T_RENAME_MAP) {
				e_RenameFolderPanel.setType(Signal.T_RENAME_MAP);
				e_RenameFolderPanel.setVisible(true);
				e_RenameFolderPanel.setFieldContents(signal.getParam(0));
			}
		}
		signal = e_FileDeletePanel.getSignal();
		if (signal != null) {
			if (signal.getParam(0).equals("yes")) {
				e_List.deleteSelectedElements();
			}
		}
	}
	
	private void processFolders() {
		Signal signal = e_NewFolderPanel.getSignal();
		if (signal != null) {
			if (signal.getParam(0).length() > 0) {
				e_List.newFolder(signal.getParam(0));
				e_NewFolderPanel.setVisible(false);
			} else {
				e_NewFolderPanel.showWarning();
			}
		}
		signal = e_RenameFolderPanel.getSignal();
		if (signal == null) {
			signal = this.e_EditMapMetadata.getSignal();
			if (signal != null) {
				// Set the warning message string, and select the map in the box
				e_RenameFolderPanel.setFieldContents(signal.getParam(1));
				// We set the directory to the current maps directory
				e_List.setDirectory(
						StringUtilities.replaceSlashesInPath(new File(
								e_MapPanel.getCurrentMap().getRelativePath()).getParent()));
				// Then we select this map (emulating right click rename)
				int id = e_List.getIDbyName(
						StringUtilities.getSpecialFileName(e_MapPanel.getCurrentMap().getName()));
				e_List.scrollToThis(id);
				e_List.selectIndex(id);
			}
		}
		if (signal != null) {
			if (signal.getParam(0).length() > 0) {
				if (signal.getType() == Signal.T_RENAME) {
					e_List.renameSelectedFolder(signal.getParam(0), 1);
				} else {
					
					String hashRemove = null;
					String hashAdd = null;
					GameMap addedMap = null;
					
					ListElement listElement = e_List.getSelected();
					
					// If this list element does not exist
					if (listElement == null) {
						
						Warning.showWarning("The map recently renamed does not exist on HDD.");
						
					} else {
						
						// Proceed with renaming!
						String oldListElementData = listElement.getData();

						if (!e_List.renameNecessary(signal.getParam(0) + "@map")) {
							// The name was the same as before!

						} else if (e_List.renameSelectedFolder(signal.getParam(0) + "@map", 2)) {
							
							// We are going to rename a map. We must update the interface too.
							for (GameMap gm : Wyvern.cache.getMaps().values()) {
								
								// If a maps old name equals to the renamed maps name
								if ((e_List.getDirectory()+"/"+oldListElementData).equals(gm.getPath())) {
	
									// Save the old tab button path, which belongs to this map.
									String oldTabButtonPath = gm.getRelativePath();
									
									// Remove the map from the cache hash
									hashRemove = gm.getRelativePath();
									
									// Rename the map object
									gm.renameMap(signal.getParam(0) + "@map");
									
									// Add the map to the cache again with the new hash value
									hashAdd = gm.getRelativePath();
									addedMap = gm;
									
									// Rename the corresponding tabbed bar button
									for (Widget b : e_TabbedBar.getWidgets()) {
										if (((TabButton) b).getFullPath().equals(oldTabButtonPath)) {
											// Rename this button.
											((TabButton) b).rename(
													signal.getParam(0), 
													gm.getRelativePath(), e_TabbedBar.getButtonWidth());
										}
									}
									// We renamed the map. Exit the for loop.
									break;
								}
							}
							
							if (hashRemove != null) {
								Wyvern.cache.getMaps().remove(hashRemove);
								Wyvern.cache.getMaps().put(hashAdd, addedMap);
							}
							
						} else {
							Warning.showWarning("A map with this name already exists!");
						}
					}
					
				}
				e_RenameFolderPanel.setVisible(false);
			} else {
				e_RenameFolderPanel.showWarning();
			}
		}
	}
	
	private void processCloseMapButtons() {
		// Try to get the result from the modified map closer window
		Signal signal = e_ModMapCloser.getSignal();
		if (signal != null) {
			if (signal.getParam(0).equals("save")) {
				e_MapPanel.saveMap(Wyvern.cache.getMaps(), signal.getParam(1));
				closeThisTabButton(signal.getParam(1));
			} else if (signal.getParam(0).equals("dontsave")) {
				closeThisTabButton(signal.getParam(1));
			}
			e_ModMapCloser.setVisible(false);
		} else {
			// Try to poll for X button presses
			for (Widget w : e_TabbedBar.getWidgets()) {
				signal = w.getSignal();
				if (signal != null) {
					break;	
				}
			}
			if (signal != null) {
				if (e_TabbedBar.getButton(signal.getParam(0)).isStar()) {
					e_ModMapCloser.setVisible(true);
					e_ModMapCloser.setSavedSignal(signal);
				} else {
					closeThisTabButton(signal.getParam(0));
				}
			}
		}
	}
	
	private void closeThisTabButton(String buttonPath) {
		e_MapPanel.closeMap(Wyvern.cache.getMaps(), buttonPath);
		e_TabbedBar.closeButton(buttonPath);
	}
	
	private void processMapSignals() {
		Signal signal = e_MapPanel.getSignal();
		if (signal != null) { 
			if (signal.getType() == Signal.T_COMMON_EVENT) {
				e_CommonEventPanel.setToCenter();
				e_CommonEventPanel.setVisible(true);
				e_CommonEventPanel.showSelect();
			} else if (signal.getType() == Signal.T_COMMON_EVENT_EDIT) {
				e_CommonEventPanel.setToCenter();
				e_CommonEventPanel.setVisible(true);
				e_CommonEventPanel.showSelect();
				e_CommonEventPanel.setSelected(Integer.parseInt(signal.getParam(0)));
			} else if (signal.getType() == Signal.T_TPICK) {
				// Set the tileset picker to be visible
				GameMap editedMap = e_MapPanel.getCurrentMap();
				int cellX = Integer.parseInt(signal.getParam(0));
				int cellY = Integer.parseInt(signal.getParam(1));
				Cell editedCell = e_MapPanel.getCurrentMap().getCell(cellX, cellY);
				if (editedCell != null && editedCell.isValid()) {
					e_CellEditor.setVisible(true);
					e_CellEditor.setEditedCell(editedMap, editedCell);
				
					e_CellEditor.setTilesetsToTop();
				}
			}
		}
	}
	
	private void processHotkeys() {
		
		if (Wyvern.input.isHotkeyRestricted()) {
			return;
		}
		
		if (Wyvern.input.isKeyHold(Keys.CONTROL_LEFT)) {
			if (Wyvern.input.isKeyPressed(Keys.Z)) {
				processToolbar(new Signal(Signal.T_DEFAULT, "U"));
			} else if (Wyvern.input.isKeyPressed(Keys.S)) {
				if (Wyvern.input.isKeyHold(Keys.SHIFT_LEFT)) {
					processToolbar(new Signal(Signal.T_DEFAULT, "SA"));
				} else {
					processToolbar(new Signal(Signal.T_DEFAULT, "S"));
				}
			} else if (Wyvern.input.isKeyPressed(Keys.J)) {
				processToolbar(new Signal(Signal.T_DEFAULT, "J"));
			} else if (Wyvern.input.isKeyPressed(Keys.M)) {
				processToolbar(new Signal(Signal.T_DEFAULT, "N"));
			} else if (Wyvern.input.isKeyPressed(Keys.N)) {
				processToolbar(new Signal(Signal.T_DEFAULT, "NF"));
			} else if (Wyvern.input.isKeyPressed(Keys.O)) {
				try {
					File indir = new File (Wyvern.INTERPRETER_DIR);
					Desktop desktop = Desktop.getDesktop();
					desktop.open(indir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (Wyvern.input.isKeyPressed(Keys.NUM_1)) {
			processToolbar(new Signal(Signal.T_DEFAULT, "1"));
		} else if (Wyvern.input.isKeyPressed(Keys.NUM_2)) {
			processToolbar(new Signal(Signal.T_DEFAULT, "2"));
		} else if (Wyvern.input.isKeyPressed(Keys.NUM_3)) {
			processToolbar(new Signal(Signal.T_DEFAULT, "3"));
		} else if (Wyvern.input.isKeyPressed(Keys.NUM_4)) {
			processToolbar(new Signal(Signal.T_DEFAULT, "4"));
		} else if (Wyvern.input.isKeyPressed(Keys.NUM_5)) {
			processToolbar(new Signal(Signal.T_DEFAULT, "5"));
		}
	}
	
	private void processToolbar(Signal signal) {
		if (signal == null) {
			signal = e_Toolbar.getSignal();
		}
		if (signal != null && !isModalVisible()) {
			
			switch(signal.getParam(0)) {
			case "N":
				prepareNewMapPanel();
				break;
			case "NF":
				e_NewFolderPanel.prepare();
				e_NewFolderPanel.setVisible(true);
				break;
			case "S":
				if (anyMapActive()) {
					e_MapPanel.getCurrentMap().save();
				}
				break;
			case "SA":
				for (GameMap m : Wyvern.cache.getMaps().values()) {
					m.save();
				}
				break;
			case "O":
				try {
					Desktop.getDesktop().open(new File(e_List.getDirectory()));
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "Pe":
				e_MapPanel.setActiveTool(0);
				setActiveToolIcon(6);
				break;
			case "Re":
				e_MapPanel.setActiveTool(1);
				setActiveToolIcon(7);
				break;
			case "F":
				e_MapPanel.setActiveTool(2);
				setActiveToolIcon(8);
				break;
			case "C":
				e_MapPanel.setActiveTool(3);
				setActiveToolIcon(9);
				break;
			case "P":
				e_MapPanel.setActiveTool(4);
				setActiveToolIcon(10);
				break;
			case "U":
				if (anyMapActive()) {
					e_MapPanel.getCurrentMap().restoreLastState();
				}
				break;
			case "Gr":
				if (e_MapPanel.isGridEnabled()) {
					e_Toolbar.setMarked(20, false);
					e_MapPanel.setGridEnabled(false);
				} else {
					e_Toolbar.setMarked(20, true);
					e_MapPanel.setGridEnabled(true);
				}
				break;
			case "1":
			case "2":
			case "3":
			case "4":
			case "5":
				int buttonIndex = Integer.parseInt(signal.getParam(0)); 
				e_MapPanel.setLayerIndex(buttonIndex - 1);
				setActiveLayerIcon(12 + buttonIndex -1);
				
				e_Toolbar.setMarked(18, false);
				e_MapPanel.setReferenceMode(false);
				
				e_Toolbar.setMarked(19, false);
				e_MapPanel.setPlacePlayer(false);
				
				break;
			case "e":
				e_MapPanel.setLayerIndex(5);
				setActiveLayerIcon(17);
				break;
			case "J":
				e_JumpPanel.setVisible(true);
				e_JumpPanel.prepare();
				break;
			case "dim":
				// Dim the layers...
				if (e_Toolbar.getMarked(22)) {
					e_MapPanel.setShadeLayer(false);
					e_Toolbar.setMarked(22, false);
				} else {
					e_MapPanel.setShadeLayer(true);
					e_Toolbar.setMarked(22, true);
				}
				break;
			case "shd":
				// Shade the cells...
				if (e_Toolbar.getMarked(23)) {
					e_MapPanel.setShadeCells(false);
					e_Toolbar.setMarked(23, false);
				} else {
					e_MapPanel.setShadeCells(true);
					e_Toolbar.setMarked(23, true);
				}
				break;
			case "ref":
				if (e_MapPanel.getReferenceMode() == true) {
					e_Toolbar.setMarked(18, false);
					e_MapPanel.setReferenceMode(false);
				} else {
					e_Toolbar.setMarked(18, true);
					e_MapPanel.setPlacePlayer(false);
					e_Toolbar.setMarked(19, false);
					e_MapPanel.setReferenceMode(true);
					e_MapPanel.setLayerIndex(5);
					setActiveLayerIcon(17);
				}
				break;
			case "ply":
				if (e_MapPanel.getPlacePlayer() == true) {
					e_Toolbar.setMarked(19, false);
					e_MapPanel.setPlacePlayer(false);
				} else {
					e_Toolbar.setMarked(19, true);
					e_MapPanel.setReferenceMode(false);
					e_Toolbar.setMarked(18, false);
					e_MapPanel.setPlacePlayer(true);
					e_MapPanel.setLayerIndex(5);
					setActiveLayerIcon(17);
				}
				break;
			case "only":
				// Hide the non active layers...
				if (e_Toolbar.getMarked(24)) {
					e_MapPanel.setCompleteHide(false);
					e_Toolbar.setMarked(24, false);
				} else {
					e_MapPanel.setCompleteHide(true);
					e_Toolbar.setMarked(24, true);
				}
				break;
			case "Ed":
				if (anyMapActive()) {
					e_EditMapMetadata.setCurrentMap(e_MapPanel.getCurrentMap());
					e_EditMapMetadata.setVisible(true);
				}
				break;
			case "D":
				Wyvern.screenChanger = 3;
				break;
			case "Pl":
				for (GameMap m : Wyvern.cache.getMaps().values()) {
					if (m.isChanged()) {
						e_PromptPanel.setVisible(true);
						e_PromptPanel.setYesFocused();
						return;
					}
				}
				processTestPlay();
				break;
			case "Dce":
				e_CommonEventPanel.setToCenter();
				e_CommonEventPanel.setVisible(true);
				e_CommonEventPanel.hideSelect();
				// We must set the edited cell to null!
				Wyvern.cache.setEditedEventCell(null);
				break;
			case "Dyn":
				e_DynamicPreview.resetPanel();
				e_DynamicPreview.setVisible(true);
				break;
			case "A":
				e_AboutWin.setVisible(true);
				break;
			default:
				break;
			}
		}
	}
	
	private void processPromptPanel() {
		Signal sg = e_PromptPanel.getSignal();
		if (sg != null) {
			if (sg.getParam(0).equals("yes")) {
				this.processToolbar(new Signal(Signal.T_DEFAULT, "SA"));
			}
			processTestPlay();
		}
	}
	
	private void processTestPlay() {
		if (SystemUtils.IS_OS_LINUX) {
			// Run the TERMINAL launcher on linux
			/*
			String[] cmdarray = new String[3];
			cmdarray[0] = "sh";
			cmdarray[1] = "linux_launcher.sh";
			cmdarray[2] = Wyvern.INTERPRETER_DIR;
			File dir = new File(Wyvern.INTERPRETER_DIR);
			ProcessBuilder pb = new ProcessBuilder(cmdarray);
			pb.directory(dir);
			try {
				pb.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			*/
			Warning.showWarning("Linux playtest is NYI. Please use \"./gradlew desktop:run\" command from the main directory!");
		} else if (SystemUtils.IS_OS_WINDOWS) {
			// Run the CMD launcher on windows
			String[] cmdarray = new String[4];
			cmdarray[0] = "cmd.exe";
			cmdarray[1] = "/c";
			cmdarray[2] = "start";
			cmdarray[3] = "windows_launcher.bat";
			File dir = new File(Wyvern.INTERPRETER_DIR);
			ProcessBuilder pb = new ProcessBuilder(cmdarray);
			pb.directory(dir);
			try {
				pb.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void processCommonEventCreation() {
		Signal sg = e_CommonEventPanel.getSignal();
		if (sg != null) {
			e_MapPanel.createCommonEvent(sg.getParam(0));
		}
	}
	
	private void processTabbedBarStars() {
		for (Widget w : e_TabbedBar.getWidgets()) {
			if (Wyvern.cache.getMaps().get(((TabButton) w).getFullPath()).isChanged()) {
				((TabButton) w).setStar(true);
			} else {
				((TabButton) w).setStar(false);
			}
		}
	}
	
	private void setCurrentTileset() {
		if (e_MapPanel.getCurrentMap() != null) {
			e_TilesetPanel.setActiveTileset(e_MapPanel.getCurrentMap().getActiveTileset());
		} else {
			e_TilesetPanel.setActiveTileset(null);
		}
	}
	
	private void loadMapBySignal(Signal signal) {
		// Split the name to scrap the @map extension
		String pureName = StringUtilities.getSpecialFileName(signal.getParam(0));
		e_MapPanel.loadMap(Wyvern.cache.getMaps(), signal.getParam(0), signal.getParam(1)+"/"+signal.getParam(0));
		// Update the tabbed bar
		e_TabbedBar.setActiveButton(pureName, signal.getParam(1)+"/"+signal.getParam(0));
	}
	
	private void loadNewMaps() {
		Signal newSignal = e_NewMapPanel.getSignal();
		if (newSignal != null) {
			loadMapBySignal(newSignal);
			e_List.refreshList();
		}
	}
	
	private boolean anyMapActive() {
		if (this.e_MapPanel.getCurrentMap() == null) {
			return false;
		}
		return true;
	}
	
	private void prepareNewMapPanel() {
		e_NewMapPanel.set(e_List.getRelativeDirectory());
		e_NewMapPanel.setVisible(true);
	}

	private void setActiveToolIcon(int active) {
		for (int i = 6; i < 11; i++) {
			if (i == active) {
				e_Toolbar.setMarked(i, true);
			} else {
				e_Toolbar.setMarked(i, false);
			}
		}
	}
	
	private void setActiveLayerIcon(int active) {
		for (int i = 12; i < 18; i++) {
			if (i == active) {
				e_Toolbar.setMarked(i, true);
			} else {
				e_Toolbar.setMarked(i, false);
			}
		}
	}
	
	private void setActiveIcon(int active) {
		for (int i = 0; i < e_Toolbar.getSize(); i++) {
			if (i == active) {
				e_Toolbar.setMarked(i, true);
			}
		}
	}

}
