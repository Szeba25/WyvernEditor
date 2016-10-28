package com.szeba.wyv.data.tiles;

/**
 * Represents a region of copied tile data in a 2d array.
 * @author Szeba
 */
public class TileCopyRegion {

	private TileData[][][] copiedTiles;
	private int copyW;
	private int copyH;
	
	public TileCopyRegion() {
		copiedTiles = new TileData[100][100][5];
		copyW = 0;
		copyH = 0;
	}
	
	public TileData[][][] getCopiedTiles() {
		return copiedTiles;
	}
	
	public void setCopiedTile(int x, int y, int layer, TileData data) {
		copiedTiles[x][y][layer] = data;
	}
	
	public int getCopyW() {
		return copyW;
	}
	
	public int getCopyH() {
		return copyH;
	}
	
	public void setDimensions(int w, int h) {
		copyW = w;
		copyH = h;
	}

	public void clear() {
		for (int x = 0; x < 100; x++) {
			for (int y = 0; y < 100; y++) {
				for (int z = 0; z < 5; z++) {
					copiedTiles[x][y][z] = null;
				}
			}
		}
		copyW = 0;
		copyH = 0;
	}
	
}
