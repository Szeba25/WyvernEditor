package com.szeba.wyv.utilities;

import java.util.Comparator;

public class StringSizeComparator implements Comparator<String> {

	@Override
	public int compare(String arg0, String arg1) {
		if (arg0.length() > arg1.length()) {
			return 1;
		}
		if (arg1.length() > arg0.length()) {
			return -1;
		}
		return 0;
	}

}
