package com.szeba.wyv.widgets.panels.pickers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Text;
import com.szeba.wyv.widgets.ext.list.DirListAnimations;

public class AnimationPicker extends BasePicker {

	private DirListAnimations animList;
	private AnimationFrame animFrame;
	
	private Button emptyButton;
	
	public AnimationPicker(int ox, int oy, int rx, int ry, int s) {
		super(ox, oy, rx, ry, 0, 0);
		
		if (s < 234) {
			s = 234;
		}
		
		this.setW(s);
		this.setH(s-90);
		
		animList = new DirListAnimations(getX(), getY(), 5, 5, 150, ((getH()-16)/16),
				Wyvern.INTERPRETER_DIR + "/resources/animations", Wyvern.INTERPRETER_DIR + "/resources/animations");
		animFrame = new AnimationFrame(getX(), getY(), 160, 25, getW()-165, getW()-165);
		
		name = new Text(getX(), getY(), 160, 5, getW()-165, 18, "");
		
		emptyButton = new Button(getX(), getY(), getW()-75, getH()-25, 70, 20, "set empty");
		
		this.addWidget(name);
		this.addWidget(emptyButton);
		this.addWidget(animList);
		this.addWidget(animFrame);
	}
	
	@Override
	public void setFocused(boolean value) {
		super.setFocused(value);
		if (!value) {
			emptyButton.setFocused(value);
			animList.setFocused(value);
			animFrame.setFocused(value);
		} else {
			animList.setFocused(value);
		}
	}
	
	@Override
	public void overDraw(SpriteBatch batch) {
		this.drawOutline(batch);
	}
	
	public AnimationFrame getAnimFrame() {
		return animFrame;
	}
	
	public void scrollListToFile() {
		this.scrollListToFile(animList, animFrame.getFileDir(), 
				StringUtilities.getSpecialFileName(animFrame.getFileName()));
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		this.updateCycle(animList, animFrame, emptyButton);
	}

	@Override
	protected void processEmptyButton(Signal s) {
		name.setText("");
		animList.selectIndex(-1);
		animFrame.reset();
	}
	
	@Override
	public void setFile(String fileDir, String fileName) {
		animFrame.setFile(fileDir, fileName);
		setName(fileName);
		scrollListToFile();
	}
	
	@Override
	public void reset() {
		processEmptyButton(null);
	}

}
