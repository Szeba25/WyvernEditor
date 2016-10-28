package com.szeba.wyv.widgets.dynamic;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;

import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.FileUtilities;
import com.szeba.wyv.utilities.Separator;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.Text;
import com.szeba.wyv.widgets.Widget;

/**
 * Test dynamic panels so we dont have to reload the editor every time... 
 * @author szeba
 */
public class DynamicPreview extends Widget {

	private Button close;
	private ArrayList<Button> buttonArray;
	private DynamicPanel previewPanel;
	private FileTime timeModified;
	
	public DynamicPreview(int ox, int oy, int rx, int ry) {
		super(ox, oy, rx, ry, 600, 600);
		
		close = new Button(getX(), getY(), 0, 0, 55, 25, "close");
		
		ArrayList<String> commandArray = new ArrayList<String>();
		commandArray.add("button");
		commandArray.add("switchbutton");
		commandArray.add("bulletboard");
		commandArray.add("text");
		commandArray.add("textfield");
		commandArray.add("intfield");
		commandArray.add("doublefield");
		commandArray.add("holdervariable");
		commandArray.add("holdereventname");
		commandArray.add("holderdbentry");
		commandArray.add("arrayfield");
		commandArray.add("list");
		commandArray.add("collection");
		commandArray.add("dblist");
		commandArray.add("droplist");
		commandArray.add("dirlist");
		commandArray.add("dirlistmaps");
		commandArray.add("eventlist");
		commandArray.add("spritepicker");
		commandArray.add("colorpicker");
		commandArray.add("animpicker");
		commandArray.add("imagepicker");
		commandArray.add("tilesetpicker");
		commandArray.add("audiopicker");
		commandArray.add("slider");
		commandArray.add("coordmap");
		commandArray.add("varnum");
		commandArray.add("vardb");
		commandArray.add("movec");
		commandArray.add("dbentrypicker");
		commandArray.add("eventpicker");
		commandArray.add("mapjumper");
		
		buttonArray = new ArrayList<Button>();
		for (String buttonName : commandArray) {
			buttonArray.add(new Button(getX(), getY(), 0, 0 , 115, 16, buttonName));
		}
		
		this.createDynamic(false);
		previewPanel.loadWidgets(Wyvern.DIRECTORY + "/core files/preview_panel.ikd");
		
		this.setFileAttributes(FileUtilities.getFileAttributes(Wyvern.DIRECTORY + "/core files/preview_panel.ikd"));
		
		this.addWidget(close);
		for (Button b : buttonArray) {
			this.addWidget(b);
		}
		this.addWidget(previewPanel);
	}
	
	public void resize(int width, int height) {
		this.setW(width-40);
		this.setH(height-40);
		close.setRX(getW()-60);
		close.setRY(getH()-30);
		int ycounter = 5;
		for (Button b : buttonArray) {
			b.setRX(getW()-120);
			b.setRY(ycounter);
			ycounter += 17;
		}
		this.setPreviewPanelSize();
	}
	
	private void setPreviewPanelSize() {
		this.previewPanel.setW(getW()-130);
		this.previewPanel.setH(getH()-10);
	}
	
	@Override
	public void mainUpdate(int scrolled) {
		Signal sg;
		sg = close.getSignal();
		if (sg != null) {
			this.setVisible(false);
		}
		for (Button b : buttonArray) {
			sg = b.getSignal();
			if (sg != null) {
				// Copy this configuration
				this.copyConfiguration(sg.getParam(0));
			}
		}
		
		// Reload widget
		
		BasicFileAttributes attrs = FileUtilities.getFileAttributes(Wyvern.DIRECTORY + "/core files/preview_panel.ikd");
		if (!this.isEqualAttrs(attrs)) {
			this.setFileAttributes(attrs);
			try {
				this.createDynamic(true);
				this.previewPanel.loadWidgets(Wyvern.DIRECTORY + "/core files/preview_panel.ikd");
				System.out.println("Preview panel successfully loaded!");
			} catch (Exception e) {
				System.out.println("Preview panel syntax error...");
				this.createDynamic(true);
				this.previewPanel.addWidget(new Text(previewPanel.getX(), previewPanel.getY(), 5, 5, 200, 17, "Syntax error..."));
			}
		}
		
	}
	
	private void copyConfiguration(String param) {
		ArrayList<String> arr = previewPanel.buildDefaultStringComponent(param);
		String stringToClipboard = "";
		for (int x = 0; x < arr.size(); x++) {
			
			if (x == 1) {
				String prefix = "." + Separator.fileIkdChar + "w" + 
										Separator.fileIkdChar + "null" +
										Separator.fileIkdChar + "null" +
										Separator.fileIkdChar + param +
										Separator.fileIkdChar;
				stringToClipboard += prefix;
			}
			
			stringToClipboard += arr.get(x);
			
			if (x == 0) {
				stringToClipboard += "\n";
			}
			if (x != arr.size()-1 && x != 0) {
				stringToClipboard += Separator.fileIkdChar;
			}
			if (x == arr.size()-1) {
				stringToClipboard += "\n";
			}
		}
		// Send the constructed string to the clipboard
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(stringToClipboard), null);
	}

	private void createDynamic(boolean add) {
		previewPanel = new DynamicPanel("Preview", getX(), getY(), 5, 5, 510, 540);
		previewPanel.removeWidget(0);
		previewPanel.removeWidget(0);
		previewPanel.setEnterFocusDefault(null);
		if (add) {
			this.removeWidget(33);
			this.addWidget(previewPanel);
		}
		this.setPreviewPanelSize();
	}
	
	private void setFileAttributes(BasicFileAttributes attrs) {
		timeModified = attrs.lastModifiedTime();
	}
	
	private boolean isEqualAttrs(BasicFileAttributes attrs) {
		return timeModified.compareTo(attrs.lastModifiedTime()) == 0;
	}

	/**
	 * Reset the dynamic panel
	 */
	public void resetPanel() {
		this.previewPanel.reset();
	}

}
