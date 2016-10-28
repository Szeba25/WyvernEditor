package com.szeba.wyv.widgets.panels.tileset;

import com.szeba.wyv.data.Signal;
import com.szeba.wyv.widgets.Button;
import com.szeba.wyv.widgets.TextField;
import com.szeba.wyv.widgets.Widget;
import com.szeba.wyv.widgets.ext.TextBorderless;
import com.szeba.wyv.widgets.ext.textfield.IntField;

public class ExportDialog extends Widget {

	private Button exportButton;
	private TextBorderless text1;
	private TextBorderless text2;
	private TextField magentaField;
	private IntField ignoreField;
	
	public ExportDialog(int ox, int oy, int rx, int ry) {
		super(ox, oy, rx, ry, 200, 200);
		
		exportButton = new Button(getX(), getY(), getW()-90, getH()-25, 85, 20, "export now!");
		text1 = new TextBorderless(getX(), getY(), 5, 5, 0, 0, "Set this tag to magenta:");
		text2 = new TextBorderless(getX(), getY(), 5, 50, 0, 0, "Ignore pixels from bottom:");
		magentaField = new TextField(getX(), getY(), 5, 25, 100, 1);
		ignoreField = new IntField(getX(), getY(), 5, 70, 100, "N", 999);
		
		magentaField.setText("100");
		ignoreField.setText("0");
		
		addWidget(text1);
		addWidget(text2);
		addWidget(magentaField);
		addWidget(ignoreField);
		addWidget(exportButton);
		
		this.setTabFocus(true);
		this.setEnterFocusDefault(exportButton);
	}

	@Override
	public void mainUpdate(int scrolled) {
		Signal sig = exportButton.getSignal();
		if (sig != null) {
			this.setSignal(new Signal(Signal.T_DEFAULT, magentaField.getText(), ignoreField.getValue()));
			this.setVisible(false);
		}
	}
	
}
