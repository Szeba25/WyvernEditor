package com.szeba.wyv.widgets.dynamic;

import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.Separator;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.panels.map.CoordinateMapPanel;

public class DynCoordinateMapPanel extends CoordinateMapPanel implements Dynamic {

	private String receiver;
	private int tileSize;
	
	public DynCoordinateMapPanel(int ox, int oy, int rx, int ry, int w, int h, int tileSize) {
		super(ox, oy, rx, ry, w, h);
		setFixTileSize(true);
		this.tileSize = tileSize;
	}

	@Override
	public void dynSetReceiver(String receiver) {
		this.receiver = receiver;
	}

	@Override
	public String dynGetReceiver() {
		return receiver;
	}

	@Override
	public void dynProcessSignal(Signal signal) {
		if (signal.getType() == Signal.T_JUMP_COORD) {
			getCurrentMap().setCameraToCell(
					Integer.parseInt(signal.getParam(0)), 
					Integer.parseInt(signal.getParam(1)));
			
		} else if (signal.getType() == Signal.T_SEARCH_PLACE) {
			getCurrentMap().jumpToPlace(signal.getParam(0));
			
		} else if (signal.getType() != Signal.T_INVALID_DIRLIST){
			String mapPath = signal.getParam(1) + "/" + signal.getParam(0);
			String mapName = signal.getParam(0);
			String sigid = "-1"; // Signals come from lists, and they cannot send broken file links.
			this.setMap(Wyvern.cache.getMaps(), sigid, mapName, mapPath);
			getCurrentMap().setTileSize(tileSize);
			getCurrentMap().setOffX(getCurrentMap().getStartingX() * getCurrentMap().getCellPixelX());
			getCurrentMap().setOffY(getCurrentMap().getStartingY() * getCurrentMap().getCellPixelY());
			
		}
	}

	@Override
	public void dynSetValue(String value) {
		
		String[] splitted = StringUtilities.safeSplit(value, Separator.dynParameter);
		
		String sigidValue = "-1";
		if (splitted[0].length() > 0) {
			sigidValue = splitted[0];
		}
		
		this.setMap(Wyvern.cache.getMaps(), sigidValue, splitted[1], splitted[2]);
		
		if (this.getCurrentMap() != null) {
			this.setCoordinates(Integer.parseInt(splitted[3]), 
					Integer.parseInt(splitted[4]), 
					Integer.parseInt(splitted[5]), 
					Integer.parseInt(splitted[6]));
			this.getCurrentMap().setTileSize(tileSize);
			this.getCurrentMap().setOffX(Integer.parseInt(splitted[7]));
			this.getCurrentMap().setOffY(Integer.parseInt(splitted[8]));
		}
	}

	@Override
	public String dynGetValue() {
		String finalString = "";
		if (this.getCurrentMap() != null) {
			finalString = this.getCurrentMap().getSignatureID() + 
					Separator.dynParameter + this.getCurrentMap().getName() + 
					Separator.dynParameter + this.getCurrentMap().getRelativePath() +
					Separator.dynParameter + this.getCoordCellX() +
					Separator.dynParameter + this.getCoordCellY() + 
					Separator.dynParameter + this.getCoordX() + 
					Separator.dynParameter + this.getCoordY() + 
					Separator.dynParameter + this.getCurrentMap().getOffX() +
					Separator.dynParameter + this.getCurrentMap().getOffY();
		} else {
			finalString = -1 +
					Separator.dynParameter + "" +
					Separator.dynParameter + "" +
					Separator.dynParameter + 0 +
					Separator.dynParameter + 0 +
					Separator.dynParameter + 0 +
					Separator.dynParameter + 0 +
					Separator.dynParameter + 0 +
					Separator.dynParameter + 0;
		}
		return finalString;
	}

	@Override
	public void dynReset() {
		this.setMap(null, "-1", null, null);
	}

	@Override
	public String dynGetCommandStringFormatter(String data) {
		return data;
	}
	
}
