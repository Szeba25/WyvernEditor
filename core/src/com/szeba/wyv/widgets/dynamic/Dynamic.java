package com.szeba.wyv.widgets.dynamic;

import com.szeba.wyv.data.Signal;

public interface Dynamic {

	public void dynSetReceiver(String receiver);
	public String dynGetReceiver();
	public void dynProcessSignal(Signal signal);
	public void dynSetValue(String value);
	public String dynGetValue();
	public void dynReset();
	public String dynGetCommandStringFormatter(String data);
	
}
