package com.szeba.wyv.data.tiles;

import java.awt.Point;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** 
 * A Tile holds a small chunk of terrain inside a cell. One tile can hold 5 layers
 * of data. 
 * @author Szeba
 */
public class Tile {

	private int[] aType;
	private int[] aIndex;
	
	private int[] xData;
	private int[] yData;
	
	public Tile() {
		aType = new int[5];
		aIndex = new int[5];
		xData = new int[5];
		yData = new int[5];
		
		for (int i = 0; i < 5; i++) {
			aType[i] = -1;
			aIndex[i] = -1;
			xData[i] = 0;
			yData[i] = 0;
		}
	}
	
	public void draw(SpriteBatch batch, Tileset tileset, int x, int y, int tileSize, boolean hide, int layerIndex, 
			float c_alpha, float c_alpha05) {
		// Draw the five layers of data.
		for (int z = 0; z < 5; z++) {
			if (xData[z] != 0 || yData[z] != 0) {
				if (layerIndex == 5 || layerIndex == z) {
					batch.setColor(1, 1, 1, c_alpha);
					batch.draw(tileset.getSmallTile(xData[z], yData[z]), x, y, tileSize, tileSize);
				} else {
					if (!hide) {
						batch.setColor(1, 1, 1, c_alpha05);
						batch.draw(tileset.getSmallTile(xData[z], yData[z]), x, y, tileSize, tileSize);
					}
					
				}
			}
		}
	}
	
	public String getAsString(int layer) {
		String s = "";
		s += Integer.toString(this.xData[layer]) + "\n" +
				Integer.toString(this.yData[layer]) + "\n" +
				Integer.toString(this.aType[layer]) + "\n" +
				Integer.toString(this.aIndex[layer]);
		return s;
	}

	public String getAsDataString(int layer) {
		return Integer.toHexString(getX(layer) + getY(layer)*48) + "x" +
				Integer.toHexString(getType(layer) + 1);
	}
	
	public void setData(int layer, int type, int index, int x, int y) {
		aType[layer] = type;
		aIndex[layer] = index;
		if (y > 47) {
			y-=48;
			x+=24;
		}
		xData[layer] = x;
		yData[layer] = y;
	}
	
	public void setData(int layer, int type) {
		aType[layer] = type;
		aIndex[layer] = -1;
		xData[layer] = 0;
		yData[layer] = 0;
	}
	
	public void setData(int layer, int x, int y) {
		aType[layer] = -1;
		aIndex[layer] = -1;
		if (y > 47) {
			y-=48;
			x+=24;
		}
		xData[layer] = x;
		yData[layer] = y;
	}
	
	public int getX(int layer) {
		return xData[layer];
	}
	
	public int getY(int layer) {
		return yData[layer];
	}
	
	public int getType(int layer) {
		return aType[layer];
	}
	
	public int getIndex(int layer) {
		return aIndex[layer];
	}
	
	public void setAbsoluteIndex(int layer, int index) {
		this.aIndex[layer] = index;
	}
	
	public void setIndex(int layer, int index) {
		this.aIndex[layer] = index;
		setAutoPos(layer, index);
	}

	public Point getData(int layer) {
		return new Point(xData[layer], yData[layer]);
	}
	
	public TileData constructTileData(int layer) {
		return new TileData(-1, -1, -1, -1, layer, this);
	}
	
	private void setAutoPos(int layer, int index) {
		// First get the autotile relative X and Y
		int autoX = 0;
		int autoY = 0;
		if (aType[layer] < 16) {
			autoX = ((aType[layer]/8) * 8) + 8;
			autoY = aType[layer] * 6 - ((aType[layer] / 8) * 48);
		} else {
			autoX = (((aType[layer]-16)/8) * 8) + 32;
			autoY = (aType[layer]-16) * 6 - (((aType[layer]-16) / 8) * 48);
		}
		// Then set these values
		this.xData[layer] = autoX + (index - ((index/8)*8));
		this.yData[layer] = autoY + (index/8);
	}
	
	/* Special case... this method remains at the bottom */
	
	public int getBinIndex(int binary) {
		switch(binary) {
		case 511:
			return 0;
		case 255:
			return 1;
		case 507:
			return 2;
		case 251:
			return 3;
		case 510:
			return 4;
		case 254:
			return 5;
		case 506:
			return 6;
		case 250:
			return 7;
		case 447:
			return 8;
		case 191:
			return 9;
		case 443:
			return 10;
		case 187:
			return 11;
		case 446:
			return 12;
		case 190:
			return 13;
		case 442:
			return 14;
		case 186:
			return 15;
		case 63:
		case 319:
		case 127:
		case 383:
			return 16;
		case 59:
		case 315:
		case 123:
		case 379:
			return 17;
		case 62:
		case 318:
		case 126:
		case 382:
			return 18;
		case 58:
		case 314:
		case 122:
		case 378:
			return 19;
		case 219:
		case 223:
		case 475:
		case 479:
			return 20;
		case 218:
		case 222:
		case 474:
		case 478:
			return 21;
		case 155:
		case 411:
		case 415:
		case 159:
			return 22;
		case 154:
		case 414:
		case 410:
		case 158:
		    return 23;
		case 504:
		case 509:
		case 508:
		case 505:
		    return 24;
		case 440:
		case 445:
		case 444:
		case 441:
		    return 25;
		case 248:
		case 253:
		case 252:
		case 249:
		    return 26;
		case 184:
		case 189:
		case 188:
		case 185:
		    return 27;
		case 438:
		case 503:
		case 502:
		case 439:
		    return 28;
		case 182:
		case 247:
		case 246:
		case 183:
		    return 29;
		case 434:
		case 499:
		case 435:
		case 498:
		    return 30;
		case 178:
		case 243:
		case 242:
		case 179:
		    return 31;
		case 56:
		case 381:
		case 316:
		case 121:
		case 60:
		case 380:
		case 317:
		case 124:
		case 57:
		case 377:
		case 313:
		case 125:
		case 61:
		case 376:
		case 312:
		case 120:
		    return 32;
		case 146:
		case 471:
		case 215:
		case 402:
		case 147:
		case 467:
		case 211:
		case 403:
		case 150:
		case 470:
		case 214:
		case 406:
		case 151:
		case 466:
		case 210:
		case 407:
		    return 33;
		case 27:
		case 351:
		case 287:
		case 95:
		case 31:
		case 347:
		case 283:
		case 91:
		    return 34;
		case 26:
		case 350:
		case 282:
		case 90:
		case 30:
		case 346:
		case 286:
		case 94:
		    return 35;
		case 216:
		case 477:
		case 221:
		case 472:
		case 220:
		case 476:
		case 217:
		case 473:
		    return 36;
		case 152:
		case 408:
		case 157:
		case 413:
		case 156:
		case 412:
		case 153:
		case 409:
		    return 37;
		case 432:
		case 496:
		case 437:
		case 501:
		case 436:
		case 500:
		case 433:
		case 497:
		    return 38;
		case 176:
		case 240:
		case 181:
		case 245:
		case 180:
		case 244:
		case 177:
		case 241:
		    return 39;
		case 54:
		case 375:
		case 311:
		case 119:
		case 55:
		case 374:
		case 310:
		case 118:
		    return 40;
		case 50:
		case 371:
		case 307:
		case 115:
		case 51:
		case 370:
		case 306:
		case 114:
		    return 41;
		case 24:
		case 89:
		case 25:
		case 88:
		case 28:
		case 92:
		case 29:
		case 93:
		case 280:
		case 344:
		case 281:
		case 345:
		case 284:
		case 348:
		case 285:
		case 349:
		    return 42;
		case 18:
		case 343:
		case 87:
		case 279:
		case 23:
		case 338:
		case 82:
		case 274:
		case 22:
		case 342:
		case 86:
		case 278:
		case 19:
		case 339:
		case 83:
		case 275:
		    return 43;
		case 48:
		case 373:
		case 112:
		case 304:
		case 53:
		case 368:
		case 117:
		case 309:
		case 52:
		case 372:
		case 116:
		case 308:
		case 49:
		case 369:
		case 113:
		case 305:
		    return 44;
		case 144:
		case 469:
		case 208:
		case 400:
		case 149:
		case 464:
		case 213:
		case 405:
		case 148:
		case 468:
		case 212:
		case 404:
		case 145:
		case 465:
		case 209:
		case 401:
		    return 45;
		case 16:
		case 272:
		case 80:
		case 20:
		case 276:
		case 84:
		case 17:
		case 273:
		case 81:
		case 277:
		case 340:
		case 336:
		case 337:
		case 21:
		case 341:
		case 85:
		    return 46;
		}
		return -1;
	}
	
}
