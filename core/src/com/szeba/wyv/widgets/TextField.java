package com.szeba.wyv.widgets;

import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import com.szeba.wyv.Wyvern;
import org.apache.commons.lang3.StringUtils;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.data.DisplayedText;
import com.szeba.wyv.data.geometry.TextPosition;
import com.szeba.wyv.input.Input;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.Separator;
import com.szeba.wyv.utilities.ShapePainter;
import com.szeba.wyv.widgets.ext.Warning;

public class TextField extends Widget {

	private static String customNewLine = Separator.customNewLine;
	
	// The main string of the textfield
	private String text;
	private DisplayedText displayedText;
	private int lineCount;
	
	// The speed of the cursor blinking
	private final double cursorBlinkTime;
	private double cursorBlinkState;
	
	// Cursor parameters
	private TextPosition cursorPos;
	
	// Selection parameters
	private boolean selectAllOnFocus;
	private boolean justGotFocused;
	private TextPosition selectionPos;
	private boolean selectionGrab;
	private double selectionDelay;
	private double wordSelectionTime;
	
	// Backspace repeat delay
	private double deleteDelay;
	private double repeat1Delay;
	
	// Arrow keys repeat delay
	private double arrowDelay;
	private double repeat2Delay;
	
	public TextField(int ox, int oy, int rx, int ry, int w, int count) {
		super(ox, oy, rx, ry, w, count*17);
		
		displayedText = new DisplayedText();
		directTextChange("");
		
		lineCount = count;
		
		cursorBlinkTime = 0.9;
		cursorBlinkState = 0.0;
		
		cursorPos = new TextPosition(0, 0, 0);
		
		selectAllOnFocus = false;
		justGotFocused = false;
		
		selectionPos = new TextPosition(0, 0, 0);
		selectionGrab = false;
		selectionDelay = 0;
		wordSelectionTime = 0;
		
		resetSelection();
		
		deleteDelay = Input.delay1;
		repeat1Delay = Input.repeat1;
		
		arrowDelay = Input.delay1;
		repeat2Delay = Input.repeat1;
	}

	@Override
	public Color getBkgColor() {
		return Palette.WIDGET_BKG3;
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		drawBackground(batch);
		FontUtilities.displayedTextPrint(batch, displayedText, getStartX(), getY()-3);
		drawCursor(batch);
		drawSelection(batch);
		drawOutline(batch);
	}

	@Override
	public void mainUpdate(int scrolled) {
		// Clear justgotfocused if left mouse button is not holded down
		if (!Wyvern.input.isButtonHold(0)) {
			justGotFocused = false;
		}
		// Update
		updateCursorBlink();
		setCursorByClick();
		controlSelection();
		setSelectionByButtons();
		copyAndPaste();
		// Typing
		typeUpdate();
		// Substract times
		substractTimes();
	}

	@Override
	public void passiveUpdate(int scrolled) {
		// Reset the state of the textbox
		resetBlinking();
		resetSelection();
		// We still substract times
		substractTimes();
	}
	
	@Override
	public void setFocused(boolean focus) {
		if (focus == true && this.isFocused() == false && selectAllOnFocus) {
			justGotFocused = true;
			selectAll();
		}
		super.setFocused(focus);
	}
	
	private void substractTimes() {
		if (this.selectionDelay > 0) {
			this.selectionDelay -= Wyvern.getDelta();
		}
		if (this.wordSelectionTime > 0) {
			this.wordSelectionTime -= Wyvern.getDelta();
		}
	}
	
	public void selectAll() {
		selectionPos.setPos(text, displayedText, 0);
		cursorPos.setPos(text, displayedText, this.text.length());
	}
	
	public void deselectAll() {
		cursorPos.setPos(text, displayedText, this.text.length());
		selectionPos.setPos(text, displayedText, this.text.length());
	}
	
	protected void forceText(String text) {
		directTextChange(text);
	}
	
	protected void modifyText(String text) {
		String newText = "";
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == this.customNewLine.charAt(0)) {
				newText += text.charAt(i);
			} else if (text.charAt(i) > 128) {
				newText += (char)35; // Replace with #...
			} else if (text.charAt(i) < 32) {
				newText += (char)35; // Replace with #...
			} else if (text.charAt(i) == Separator.fileWyvChar.charAt(0) ||
					text.charAt(i) == Separator.dataUnit.charAt(0) ||
					text.charAt(i) == Separator.listElement.charAt(0) ||
					text.charAt(i) == Separator.array.charAt(0) ||
					text.charAt(i) == Separator.dynParameter.charAt(0)) {
				// This is the case with any separator!
				newText += (char)35; // Replace with #...
			} else {
				newText += text.charAt(i);
			}
		}
		directTextChange(newText);
	}

	public void setText(String text) {
		modifyText(text);
		cursorPos.setPos(this.text, displayedText, this.text.length());
		resetSelection();
	}
	
	private void directTextChange(String text) {
		this.text = text;
		this.displayedText.setLines(text);
	}
	
	public String getText() {
		return text;
	}
	
	public int getCursorPos() {
		return cursorPos.getReal();
	}
	
	public void setSelectAllOnFocus(boolean value) {
		selectAllOnFocus = value;
	}
	
	public void resetBlinking() {
		cursorBlinkState = 0.0;
	}
	
	/** 
	 * Add a string to the text, on the cursor position. Also expand cursor
	 * position too 
	 */
	protected void addThisString(String character) {
		// Add this string.
		String newText = text.substring(0, cursorPos.getReal()) + character +
				text.substring(cursorPos.getReal(), text.length());
		modifyText(newText);
		if (character.length() > 0) {
			cursorPos.setPos(text, displayedText, cursorPos.getReal() + character.length());
			resetSelection();
		}
	}
	
	private void copyAndPaste() {
		if (Wyvern.input.isKeyHold(Keys.CONTROL_LEFT)) {
			if (Wyvern.input.isKeyPressed(Keys.V)) {
				try {
					// First remove the selected text
					if (cursorPos.getReal() != selectionPos.getReal()) {
						removeBySequence();
					}
					// Add clipboard contents to text
					String textC = (String) Toolkit.getDefaultToolkit()
					        .getSystemClipboard().getData(DataFlavor.stringFlavor);
					textC = StringUtils.replace(textC, "\n", customNewLine);
					int newLines = StringUtils.countMatches(textC, customNewLine);
					int existingLines = StringUtils.countMatches(text, customNewLine);
					if (newLines+existingLines < this.lineCount) {
						addThisString(textC);
					} else {
						Warning.showWarning("Too much line count!");
					}
				} catch (HeadlessException e) {
					e.printStackTrace();
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} else if (Wyvern.input.isKeyPressed(Keys.C)) {
				Point seq = getSequence(cursorPos.getReal(), selectionPos.getReal());
				String finalString = text.substring(seq.x, seq.y);
				finalString = StringUtils.replace(finalString, customNewLine, "\n");
				StringSelection string = new StringSelection(finalString);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(string, null);
				
			} else if (Wyvern.input.isKeyPressed(Keys.X)) {
				Point seq = getSequence(cursorPos.getReal(), selectionPos.getReal());
				String finalString = text.substring(seq.x, seq.y);
				finalString = StringUtils.replace(finalString, customNewLine, "\n");
				StringSelection string = new StringSelection(finalString);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(string, null);
				// Also delete this sequence
				removeBySequence();
			}
		}
	}

	/** 
	 * Draw the blinking cursor 
	 */
	private void drawCursor(SpriteBatch batch) {
		if (cursorBlinkState > cursorBlinkTime/2) {
			ShapePainter.drawFilledRectangle(batch, Palette.TEXT_CURSOR, 
					getStartX()+cursorPos.getPix(), getY()+(cursorPos.getLine()*17), 
					1, 17);	
		}
	}
	
	/** 
	 * Draw the text selection
	 */
	private void drawSelection(SpriteBatch batch) {
		
		if (cursorPos.getLine() == selectionPos.getLine()) {
			ShapePainter.drawFilledRectangle(batch, Palette.TEXT_SELECTION, 
					getStartX()+cursorPos.getPix(), getY() + (cursorPos.getLine()*17), 
					selectionPos.getPix()-cursorPos.getPix(), 17);
		} else {
			
			// Selection and cursor in different lines
			for (int line = 0; line < displayedText.lines.size(); line++) {
				
				if (cursorPos.getLine() == line && cursorPos.getLine() < selectionPos.getLine()) {
					int startX = getStartX()+cursorPos.getPix();
					int startY = getY() + (cursorPos.getLine()*17);
					int strWidth = (int) FontUtilities.getBounds(displayedText.lines.get(line)).width;
					ShapePainter.drawFilledRectangle(batch, Palette.TEXT_SELECTION, 
							startX, startY, strWidth - cursorPos.getPix(), 17);
				}
				if (cursorPos.getLine() == line && cursorPos.getLine() > selectionPos.getLine()) {
					int startX = getStartX();
					int startY = getY() + (cursorPos.getLine()*17);
					int strWidth = (int) FontUtilities.getBounds(displayedText.lines.get(line), 0, 
							cursorPos.getRelativeReal(text, displayedText, cursorPos.getReal())).width;
					ShapePainter.drawFilledRectangle(batch, Palette.TEXT_SELECTION, 
							startX, startY, strWidth, 17);
				}
				
				if (selectionPos.getLine() == line && selectionPos.getLine() > cursorPos.getLine()) {
					int startX = getStartX();
					int startY = getY() + (selectionPos.getLine()*17);
					int strWidth = (int) FontUtilities.getBounds(displayedText.lines.get(line), 0, 
							selectionPos.getRelativeReal(text, displayedText, selectionPos.getReal())).width;
					ShapePainter.drawFilledRectangle(batch, Palette.TEXT_SELECTION, 
							startX, startY, strWidth, 17);
				}
				if (selectionPos.getLine() == line && selectionPos.getLine() < cursorPos.getLine()) {
					int startX = getStartX()+selectionPos.getPix();
					int startY = getY() + (selectionPos.getLine()*17);
					int strWidth = (int) FontUtilities.getBounds(displayedText.lines.get(line)).width;
					ShapePainter.drawFilledRectangle(batch, Palette.TEXT_SELECTION, 
							startX, startY, strWidth - selectionPos.getPix(), 17);
				}
				
				if (lineInsideSelection(line)) {
					int startX = getStartX();
					int startY = getY() + (line*17);
					int strWidth = (int) FontUtilities.getBounds(displayedText.lines.get(line)).width;
					ShapePainter.drawFilledRectangle(batch, Palette.TEXT_SELECTION, 
							startX, startY, strWidth, 17);
				}
				
			}
		}
		
	}
	
	private boolean lineInsideSelection(int line) {
		if (cursorPos.getLine() < selectionPos.getLine()) {
			if (line > cursorPos.getLine() && line < selectionPos.getLine()) {
				return true;
			}
		} else if (selectionPos.getLine() < cursorPos.getLine()) {
			if (line > selectionPos.getLine() && line < cursorPos.getLine()) {
				return true;
			}
		}
		return false;
	}
	
	/** 
	 * A regular delete by backspace, relative to the cursor position 
	 */
	private void removeByCursorPos() {
		// Process a regular delete
		StringBuilder sb = new StringBuilder(text);
		sb.deleteCharAt(cursorPos.getReal()-1);
		directTextChange(sb.toString());
		cursorPos.setPos(text, displayedText, cursorPos.getReal()-1);
		resetSelection();
	}
	
	/**
	 * A regular delete by delete, from the right side of the text
	 */
	private void removeByCursorPosFromRight() {
		// Process this type
		StringBuilder sb = new StringBuilder(text);
		sb.deleteCharAt(cursorPos.getReal());
		directTextChange(sb.toString());
		resetSelection();
	}
	
	/** 
	 * Remove the characters based on the cursor, and selection position 
	 */
	private void removeBySequence() {
		// Delete a range of characters...
		// First get the real range
		Point seq = getSequence(cursorPos.getReal(), selectionPos.getReal());
		// Now delete the sequence, and reset cursor positions
		StringBuilder sb = new StringBuilder(text);
		sb.delete(seq.x, seq.y);
		directTextChange(sb.toString());
		cursorPos.setPos(text, displayedText, seq.x);
		resetSelection();
	}
	
	/** 
	 * Get a sequence, and possibly refactor the start and end coordinations 
	 */
	private Point getSequence(int oldStart, int oldEnd) {
		int start;
		int end;
		if (oldStart < oldEnd) {
			start = oldStart;
			end = oldEnd;
		} else {
			start = oldEnd;
			end = oldStart;
		}
		return new Point(start, end);
	}
	
	/** 
	 * The main update for key input 
	 */
	private void typeUpdate() {
		// If typing with any key
		String toAdd = Wyvern.input.getTypedChar();
		if (toAdd != null) {
			// Remove the highlighted text
			if (cursorPos.getReal() != selectionPos.getReal()) {
				removeBySequence();
			}
			
			// Show the cursor
			cursorBlinkState = cursorBlinkTime;
			
			// Add typed character
			addThisString(toAdd);
		}
		
		// Add a new line
		if (Wyvern.input.isKeyPressed(Keys.ENTER)) {
			if (StringUtils.countMatches(text, customNewLine) < lineCount-1) {
				
				// Show the cursor
				cursorBlinkState = cursorBlinkTime;
				
				// Add new line
				addThisString(customNewLine);
			}
		}
		
		// Delete key (with repeat)
		if (Wyvern.input.isKeyHold(Keys.BACKSPACE) || Wyvern.input.isKeyHold(Keys.FORWARD_DEL)) {
			
			// Determine direction
			boolean forward = false;
			if (Wyvern.input.isKeyHold(Keys.FORWARD_DEL)) {
				forward = true;
			}
			
			// Show the cursor
			cursorBlinkState = cursorBlinkTime;
			
			if (deleteDelay == Input.delay1) {
				processDeleteKey(forward);
				deleteDelay -= Wyvern.getDelta();
			} else if (deleteDelay > 0) {
				deleteDelay -= Wyvern.getDelta();
			} else if (deleteDelay <= 0) {
				// Repeat action
				if (repeat1Delay > 0) {
					repeat1Delay -= Wyvern.getDelta();
				} else {
					repeat1Delay = Input.repeat1;
					processDeleteKey(forward);
				}
			}
		} else {
			// Reset repeating
			deleteDelay = Input.delay1;
			repeat1Delay = Input.repeat1;
		}
		
		// Arrow key (with repeat)
		if (Wyvern.input.isKeyHold(Keys.UP) || Wyvern.input.isKeyHold(Keys.DOWN) ||
				Wyvern.input.isKeyHold(Keys.LEFT) || Wyvern.input.isKeyHold(Keys.RIGHT)) {
			
			// Show the cursor
			cursorBlinkState = cursorBlinkTime;
			
			if (arrowDelay == Input.delay1) {
				setCursorByButtons();
				arrowDelay -= Wyvern.getDelta();
			} else if (arrowDelay > 0) {
				arrowDelay -= Wyvern.getDelta();
			} else if (arrowDelay <= 0) {
				// Repeat action
				if (repeat2Delay > 0) {
					repeat2Delay -= Wyvern.getDelta();
				} else {
					repeat2Delay = Input.repeat1;
					setCursorByButtons();
				}
			}
		} else {
			// Reset repeating
			arrowDelay = Input.delay1;
			repeat2Delay = Input.repeat1;
		}
		
	}

	/** 
	 * Process the backspace and delete key 
	 */
	private void processDeleteKey(boolean forward) {
		if (cursorPos.getReal() != selectionPos.getReal()) {
			removeBySequence();
		} else if (!forward && cursorPos.getReal() - 1 >= 0 && text.length() > 0) {
			removeByCursorPos();
		} else if (forward && cursorPos.getReal() < text.length()) {
			removeByCursorPosFromRight();
		}
	}

	/** 
	 * Control how selection works
	 */
	private void controlSelection() {
		
		if (justGotFocused) {
			return;
		}
		
		if (!selectionGrab && Wyvern.input.isButtonHold(0) && mouseInside()) {
			selectionGrab = true;
		}
		
		if (Wyvern.input.isButtonHold(0) && selectionGrab && selectionDelay <= 0.0f) {
			// Set the selection value
			int relativeX = Wyvern.input.getX() - getStartX();
			int relativeY = Wyvern.input.getY() - getY();
			int index = indexByPixelValue(relativeX, relativeY);
			selectionPos.setPos(text, displayedText, index);
		} else if (selectionGrab && selectionDelay <= 0.0f) {
			selectionGrab = false;
		}
		
	}

	/** 
	 * Set a new cursor position when clicking anywhere in the text 
	 */
	private void setCursorByClick() {
		if (Wyvern.input.isButtonPressed(0) && mouseInside() && !justGotFocused) {
			// Calculate cursor position from mouse position
			int relativeX = Wyvern.input.getX() - getStartX();
			int relativeY = Wyvern.input.getY() - getY();
			int index = indexByPixelValue(relativeX, relativeY);
			
			int old = cursorPos.getReal();
			
			cursorPos.setPos(text, displayedText, index);
			
			resetSelection();
			
			extendWordSelection(old);
			
		}
	}
	
	private void extendWordSelection(int old) {
		// If we clicked on the same position as before, and we still have time
		if (cursorPos.getReal() == old && wordSelectionTime > 0.0f) {
			
			// Add selection delay
			selectionDelay = 0.2f;
			
			// Expand selection to the left
			while (old > 0) {
				old -= 1;
				if (text.charAt(old) == 32 
						|| text.charAt(old) == Separator.customNewLine.charAt(0) 
						|| old == 0) {
					if (text.charAt(old) == 32 ||
							text.charAt(old) == Separator.customNewLine.charAt(0)) {
						old += 1;
					}
					cursorPos.setPos(text, displayedText, old);
					break;
				}
			}
			// We bring the selection back to the left cursor
			resetSelection();
			
			// And to the right
			old--; // Substract one to refine the selection
			while (old < text.length()-1) {
				old += 1;
				if (old == text.length()-1
						|| text.charAt(old) == 32
						|| text.charAt(old) == Separator.customNewLine.charAt(0)) {
					if (old == text.length()-1 && text.charAt(old) != 32) {
						old += 1;
					}
					selectionPos.setPos(text, displayedText, old);
					break;
				}
			}
			
		} else {
			// Add time (we clicked elsewhere, or ran out of time)
			wordSelectionTime = Input.doubleClick;
		}
		
	}
	
	private void setCursorByButtons() {
		if (Wyvern.input.isKeyHold(Keys.DOWN)) {
			int index = indexByPixelValue(cursorPos.getPix(), (cursorPos.getLine()+1)*17);
			cursorPos.setPos(text, displayedText, index);
			resetSelection();
		} else if (Wyvern.input.isKeyHold(Keys.UP)) {
			int index = indexByPixelValue(cursorPos.getPix(), (cursorPos.getLine()-1)*17);
			cursorPos.setPos(text, displayedText, index);
			resetSelection();
		} else if (Wyvern.input.isKeyHold(Keys.LEFT)) {
			if (cursorPos.getReal() > 0) {
				cursorPos.setPos(text, displayedText, cursorPos.getReal()-1);
				resetSelection();
			}
		} else if (Wyvern.input.isKeyHold(Keys.RIGHT)) {
			if (cursorPos.getReal() < text.length()) {
				cursorPos.setPos(text, displayedText, cursorPos.getReal()+1);
				resetSelection();
			}
		}
	}
	
	private void setSelectionByButtons() {
		if (Wyvern.input.isKeyHold(Keys.CONTROL_LEFT) && Wyvern.input.isKeyHold(Keys.A)) {
			cursorPos.setPos(text, displayedText, text.length());
			selectionPos.setPos(text, displayedText, 0);
		}
	}
	
	/** 
	 * Make the selection position equal with the cursor position 
	 */
	private void resetSelection() {
		selectionPos.setEqualTo(cursorPos);
	}
	
	/** 
	 * Get the text character index by a pixel value 
	 */
	private int indexByPixelValue(int relativeX, int relativeY) {
		
		// Get the clicked line
		int lineNum = relativeY / 17;
		if (lineNum > displayedText.lines.size()-1) {
			lineNum = displayedText.lines.size()-1;
		} else if (lineNum < 0) {
			lineNum = 0;
		}
		String lineString = displayedText.scrapLines.get(lineNum);
		
		// Get the starting index value
		int startIndexValue = 0;
		for (int f = 0; f < lineNum; f++) {
			startIndexValue += displayedText.lines.get(f).length();
		}
		
		// Check for the string until the matching width is found
		int boundsX;
		int charBoundX;
		for (int x = 0; x < lineString.length(); x++) {
			boundsX = (int) FontUtilities.getBounds(lineString, 0, x+1).width;
			charBoundX = (int) FontUtilities.getBounds(Character.toString(lineString.charAt(x))).width;
			if (relativeX <= boundsX-(charBoundX/2) && relativeX >= boundsX-charBoundX) {
				if (x < 0) {
					return startIndexValue;
				} else {
					return startIndexValue + x;
				}
			} else if ( relativeX >= boundsX-(charBoundX/2) && relativeX <= boundsX ) {
				return startIndexValue + x + 1;
			}
		}
		// No matching width found
		if (relativeX <= 0) { 
			return startIndexValue;
		} else {
			return startIndexValue + lineString.length();
		}
	}

	/** 
	 * Update cursor blinking 
	 */
	private void updateCursorBlink() {
		if (cursorBlinkState > 0.0) {
			cursorBlinkState -= Wyvern.getDelta();
		}
		if (cursorBlinkState <= 0.0) {
			cursorBlinkState = cursorBlinkTime;
		}
	}

	/** 
	 * Get the value where the text actually starts 
	 */
	private int getStartX() {
		return getX()+5;
	}
	
}
