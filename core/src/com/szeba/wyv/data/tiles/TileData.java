package com.szeba.wyv.data.tiles;

/**
 * A simple representation of one layer of tile data. 
 * @author Szeba
 */
public class TileData {

	public int cellx;
	public int celly;
	public int tx;
	public int ty;
	public int layer;
	public int type;
	public int index;
	public int x;
	public int y;
	
	public TileData(int cellx, int celly, int tx, int ty, int layer ,int type, int index, int x, int y) {
		this.cellx = cellx;
		this.celly = celly;
		this.tx = tx;
		this.ty = ty;
		this.layer = layer;
		this.type = type;
		this.index = index;
		this.x = x;
		this.y = y;
	}
	
	public TileData(int cellx, int celly, int tx, int ty, int layer, Tile tile) {
		this.cellx = cellx;
		this.celly = celly;
		this.tx = tx;
		this.ty = ty;
		this.layer = layer;
		this.type = tile.getType(layer);
		this.index = tile.getIndex(layer);
		this.x = tile.getX(layer);
		this.y = tile.getY(layer);
	}
	
	/**
	 * Two tiledata is the same, if their autotile type match, or their coordinate match.
	 */
	public boolean sameTypeOfTileAs(TileData other) {
		if (this.type == -1) {
			if (this.x == other.x && this.y == other.y) {
				return true;
			} else {
				return false;
			}
		} else if (this.type == other.type) {
			return true;
		}
		return false;
	}
	
}
