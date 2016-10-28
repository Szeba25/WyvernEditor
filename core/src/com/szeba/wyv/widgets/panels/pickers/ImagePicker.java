package com.szeba.wyv.widgets.panels.pickers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Text;
import com.szeba.wyv.widgets.ext.list.DirListImages;

public class ImagePicker extends BasePicker {

	private DirListImages imageList;
	private ImageFrame imageFrame;
	
	private Button emptyButton;
	
	public ImagePicker(int ox, int oy, String sub, int rx, int ry, int s) {
		super(ox, oy, rx, ry, 0, 0);
		
		if (s < 234) {
			s = 234;
		}
		
		this.setW(s);
		this.setH(s-90);
		
		imageList = new DirListImages(getX(), getY(), 5, 5, 150, ((getH()-16)/16),
				Wyvern.INTERPRETER_DIR + "/resources/" + sub, Wyvern.INTERPRETER_DIR + "/resources/" + sub);
		imageFrame = new ImageFrame(getX(), getY(), 160, 25, getW()-165, getW()-165);
		
		name = new Text(getX(), getY(), 160, 5, getW()-165, 18, "");
		
		emptyButton = new Button(getX(), getY(), getW()-75, getH()-25, 70, 20, "set empty");
		
		this.addWidget(name);
		this.addWidget(emptyButton);
		this.addWidget(imageList);
		this.addWidget(imageFrame);
	}
	
	@Override
	public void setFocused(boolean value) {
		super.setFocused(value);
		if (!value) {
			emptyButton.setFocused(value);
			imageList.setFocused(value);
			imageFrame.setFocused(value);
		} else {
			imageList.setFocused(value);
		}
	}
	
	@Override
	public void overDraw(SpriteBatch batch) {
		this.drawOutline(batch);
	}
	
	public ImageFrame getImageFrame() {
		return imageFrame;
	}
	
	public void scrollListToFile() {
		this.scrollListToFile(imageList, imageFrame.getFileDir(), imageFrame.getFileName());
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		this.updateCycle(imageList, imageFrame, emptyButton);
	}

	@Override
	public void reset() {
		imageFrame.setFile("", "");
		imageList.selectIndex(-1);
		setName("");
	}
	
	@Override
	public void setFile(String fileDir, String fileName) {
		imageFrame.setFile(fileDir, fileName);
		setName(fileName);
		scrollListToFile();
	}
	
	@Override
	protected void processEmptyButton(Signal s) {
		name.setText("");
		imageList.selectIndex(-1);
		imageFrame.reset();
	}

}
