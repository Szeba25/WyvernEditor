package com.szeba.wyv.widgets.panels.pickers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Slider;
import com.szeba.wyv.widgets.Text;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.ext.list.DirListAudio;

public class AudioPicker extends Widget {

	private String fileDir;
	private String fileName;
	
	private Text fileText;
	
	private DirListAudio audioList;
	
	private Button emptyButton;
	private Button playButton;
	
	private Slider volumeSlider;
	private Slider panSlider;
	private Slider pitchSlider;
	
	public AudioPicker(int ox, int oy, int rx, int ry, int w, int h) {
		super(ox, oy, rx, ry, w, h);
		
		fileDir = "";
		fileName = "";
		
		if (w < 350) {
			w = 350;
		}
		
		if (h < 160) {
			h = 160;
		}
		
		this.setW(w);
		this.setH(h);
		
		fileText = new Text(getX(), getY(), 160, 5, getW()-165, 16, "");
		
		audioList = new DirListAudio(getX(), getY(), 5, 5, 150, ((getH()-16)/16), 
				Wyvern.INTERPRETER_DIR + "/resources/audio", Wyvern.INTERPRETER_DIR + "/resources/audio");
		
		emptyButton = new Button(getX(), getY(), getW()-155, getH()-28, 70, 20, "set empty");
		playButton = new Button(getX(), getY(), getW()-75, getH()-28, 70, 20, "play");
		
		volumeSlider = new Slider(getX(), getY(), 215, 45, getW()-230, 15, 0, 100, "0");
		panSlider = new Slider(getX(), getY(), 215, 70, getW()-230, 15, 0, 100, "0");
		pitchSlider = new Slider(getX(), getY(), 215, 95, getW()-230, 15, 0, 100, "0");
		volumeSlider.setSliderValue(100);
		panSlider.setSliderValue(50);
		pitchSlider.setSliderValue(50);
		
		addWidget(fileText);
		
		addWidget(emptyButton);
		addWidget(playButton);
		
		addWidget(volumeSlider);
		addWidget(panSlider);
		addWidget(pitchSlider);
		
		addWidget(audioList);
	}
	
	@Override
	public void setFocused(boolean value) {
		super.setFocused(value);
		if (!value) {
			emptyButton.setFocused(value);
			playButton.setFocused(value);
			volumeSlider.setFocused(value);
			panSlider.setFocused(value);
			pitchSlider.setFocused(value);
			audioList.setFocused(value);
		} else {
			audioList.setFocused(value);
		}
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		Signal s = audioList.getSignal();
		if (s != null) {
			if (s.getType() != Signal.T_INVALID_DIRLIST) {
				fileDir = s.getParam(1);
				fileName = s.getParam(0);
				setFileText(fileName);
				Wyvern.cache.disposeMusic();
				Wyvern.cache.playStreamedMusic(fileDir, fileName);
			} else {
				processEmptyButton();
			}
		}
		s = emptyButton.getSignal();
		if (s != null) {
			processEmptyButton();
		}
		s = playButton.getSignal();
		if (s != null) {
			Wyvern.cache.playStreamedMusic(fileDir, fileName);
		}
		// Keep the music streamed
		Wyvern.cache.refreshStreamedMusic(
				(float) volumeSlider.getSliderValue()/100, 
				(float) (panSlider.getSliderValue()-50)/50 );
	}
	
	@Override
	public void overDraw(SpriteBatch batch) {
		FontUtilities.print(batch, "Volume:", getX()+160, getY()+45);
		FontUtilities.print(batch, "Pan:", getX()+160, getY()+70);
		FontUtilities.print(batch, "Pitch:", getX()+160, getY()+95);
		this.drawOutline(batch);
	}
	
	public String getFileDir() {
		return fileDir;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFile(String fileDir, String fileName, int volume, int pan, int pitch) {
		this.fileDir = fileDir;
		this.fileName = fileName;
		
		this.setVolume(volume);
		this.setPan(pan);
		this.setPitch(pitch);
		
		setFileText(fileName);
		
		this.scrollListToFile();
	}
	
	public void scrollListToFile() {
		
		audioList.returnToRoot();
		
		if (fileDir.length() > 0 && fileName.length() > 0) {
			audioList.setDirectory(fileDir);
			int id = audioList.getIDbyName(fileName);
			audioList.selectIndex(id);
			audioList.scrollToThis(id);
		}
		
	}
	
	private void processEmptyButton() {
		volumeSlider.setSliderValue(100);
		panSlider.setSliderValue(50);
		pitchSlider.setSliderValue(50);
		
		Wyvern.cache.disposeMusic();
		
		fileDir = "";
		fileName = "";
		setFileText(fileName);
		audioList.returnToRoot();
	}
	
	public void setVolume(int volume) {
		volumeSlider.setSliderValue(volume);
	}
	
	public int getVolume() {
		return (int) volumeSlider.getSliderValue();
	}
	
	public void setPan(int pan) {
		panSlider.setSliderValue(pan);
	}
	
	public int getPan() {
		return (int) panSlider.getSliderValue();
	}
	
	public void setPitch(int pitch) {
		pitchSlider.setSliderValue(pitch);
	}
	
	public int getPitch() {
		return (int) pitchSlider.getSliderValue();
	}
	
	public void reset() {
		processEmptyButton();
	}
	
	public void setFileText(String text) {
		this.fileText.setText(StringUtilities.cropString(text, fileText.getW()-10));
	}
	
}
