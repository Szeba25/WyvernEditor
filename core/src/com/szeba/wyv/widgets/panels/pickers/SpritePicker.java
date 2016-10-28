package com.szeba.wyv.widgets.panels.pickers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Text;
import com.szeba.wyv.widgets.ext.list.DirListSprites;

public class SpritePicker extends BasePicker {

	protected DirListSprites spriteList;
	protected SpriteFrame spriteFrame;
	
	protected Button emptyButton;
	
	public SpritePicker(int ox, int oy, int rx, int ry, int s) {
		super(ox, oy, rx, ry, 0, 0);
		
		if (s < 234) {
			s = 234;
		}
		
		this.setW(s);
		this.setH(s-90);
		
		spriteList = new DirListSprites(getX(), getY(), 5, 5, 150, ((getH()-16)/16),
				Wyvern.INTERPRETER_DIR + "/resources/spritesets", Wyvern.INTERPRETER_DIR + "/resources/spritesets");
		spriteFrame = new SpriteFrame(getX(), getY(), 160, 25, getW()-165, getW()-165);
		
		name = new Text(getX(), getY(), 160, 5, getW()-165, 18, "");
		
		emptyButton = new Button(getX(), getY(), getW()-75, getH()-25, 70, 20, "set empty");
		
		this.addWidget(name);
		this.addWidget(emptyButton);
		this.addWidget(spriteList);
		this.addWidget(spriteFrame);
		
	}
	
	@Override
	public void setFocused(boolean value) {
		super.setFocused(value);
		if (!value) {
			emptyButton.setFocused(value);
			spriteList.setFocused(value);
			spriteFrame.setFocused(value);
		} else {
			spriteList.setFocused(value);
		}
	}
	
	@Override
	public void overDraw(SpriteBatch batch) {
		this.drawOutline(batch);
	}
	
	@Override
	public void setRX(int x) {
		super.setRX(x);
		this.spriteFrame.refreshSpriteHigh();
	}
	
	@Override
	public void setRY(int y) {
		super.setRY(y);
		this.spriteFrame.refreshSpriteHigh();
	}
	
	public DirListSprites getSpriteList() {
		return spriteList;
	}
	
	public SpriteFrame getSpriteFrame() {
		return spriteFrame;
	}
	
	public void scrollListToFile() {
		this.scrollListToFile(spriteList, spriteFrame.getFileDir(), spriteFrame.getFileName());
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		this.updateCycle(spriteList, spriteFrame, emptyButton);
	}

	@Override
	public void reset() {
		processEmptyButton(null);
	}
	
	// Not overridden!
	public void setFile(String fileDir, String fileName, String coord) {
		spriteFrame.setFile(fileDir, fileName);
		spriteFrame.setSpriteCoordStr(coord);
		setName(fileName);
		scrollListToFile();
	}
	
	@Override
	protected void processEmptyButton(Signal s) {
		name.setText("");
		spriteList.selectIndex(-1);
		spriteFrame.reset();
	}

}
