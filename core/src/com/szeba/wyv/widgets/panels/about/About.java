package com.szeba.wyv.widgets.panels.about;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Text;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.ext.button.SwitchButton;
import com.szeba.wyv.widgets.ext.list.DirList;

public class About extends Widget {

	private Button close;
	private SwitchButton awesome;
	private DirList themeList;
	
	private Text title1;
	private Text title2;
	
	private Button[] banner;
	
	// The awesome song!
	private Music awesomeMusic;
	
	public About(int ox, int oy, int rx, int ry) {
		super(ox, oy, rx, ry, 600, 400);
		
		close = new Button(getX(), getY(), 535, 375, 60, 20, "close");
		
		awesome = new SwitchButton(getX(), getY(), 405, 5, 190, 30, "Awesome music!", "Stop...");
		
		themeList = new DirList(getX(), getY(), 405, 40, 190, 20, 
				Wyvern.DIRECTORY + "/themes/list", Wyvern.DIRECTORY + "/themes/list");
		themeList.setReturnRelative(false);
		
		title1 = new Text(getX(), getY(), 20, 40, 360, 20, "Created by");
		
		title2 = new Text(getX(), getY(), 20, 150, 360, 20, "Powered by");
		
		banner = new Button[3];
		
		banner[0] = new Button(getX(), getY(), 40, 180, 320, 30, "http://neverbeen.hu/");
		banner[0].setTooltip("http://neverbeen.hu/");
		banner[0].setRegion(Wyvern.cache.getBanner1());
		banner[1] = new Button(getX(), getY(), 40, 215, 320, 30, "http://libgdx.badlogicgames.com/");
		banner[1].setTooltip("http://libgdx.badlogicgames.com/");
		banner[1].setRegion(Wyvern.cache.getBanner2());
		banner[2] = new Button(getX(), getY(), 40, 250, 320, 30, "http://commons.apache.org/");
		banner[2].setTooltip("http://commons.apache.org/");
		banner[2].setRegion(Wyvern.cache.getBanner3());
		
		awesomeMusic = Gdx.audio.newMusic(new FileHandle(Wyvern.DIRECTORY+"/core files/awesome.mp3"));
		awesomeMusic.setLooping(true);
		
		addWidget(banner[0]);
		addWidget(banner[1]);
		addWidget(banner[2]);
		addWidget(title1);
		addWidget(title2);
		addWidget(close);
		addWidget(awesome);
		addWidget(themeList);
		
		this.setEnterFocusDefault(close);
		this.setEnterFocusRestricted(awesome, banner[0], banner[1], banner[2], themeList);
		this.setTabFocus(true);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		for (Widget w : this.getWidgets()) {
			w.setFocused(false);
		}
		awesome.setFocused(true);
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		// Close the about window, and save the theme changes
		if (close.getSignal() != null) {
			saveTheme();
			setVisible(false);
		}
		// Set a new theme
		Signal theme = themeList.getSignal();
		if (theme != null && theme.getType() != Signal.T_INVALID_DIRLIST) {
			System.out.println("Theme loaded: " + theme.getParam(0));
			Palette.load_theme(theme.getParam(0));
		}
		// Play music!
		if (awesome.getState() == 1) {
			if (!awesomeMusic.isPlaying()) {
				awesomeMusic.play();
			}
		} else {
			if (awesomeMusic.isPlaying()) {
				awesomeMusic.stop();
			}
		}
		// Uri!
		Signal sg;
		for (int x = 0; x < 3; x++) {
			sg = banner[x].getSignal();
			if (sg != null) {
				try {
					openWebpage(new URI(sg.getParam(0)));
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void mainDraw(SpriteBatch batch) {
		drawBackground(batch);
		drawOutline(batch);
		//FontUtilities.print(batch, "Choose theme: ", getX()+5, getY()+2);
		FontUtilities.print(batch, "Version: " + Wyvern.VERSION + " 2016", getX()+5, getY()+5);
		FontUtilities.print(batch, "Programming: Szeba", getX() + 40, getY() + 75);
		FontUtilities.print(batch, "Graphics: Zsuzsy", getX() + 40, getY() + 95);
		FontUtilities.print(batch, "Contributor: Böbi", getX() + 40, getY() + 115);
	}
	
	private void saveTheme() {
		TextFile t = new TextFile(Wyvern.DIRECTORY + "/themes/config/_config.txt");
		t.setValue(0, 0, Palette.NAME);
		t.save();
	}
	
	private void openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
