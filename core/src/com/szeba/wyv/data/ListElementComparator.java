package com.szeba.wyv.data;

import java.util.Comparator;

public class ListElementComparator implements Comparator<ListElement> {

	@Override
	public int compare(ListElement o1, ListElement o2) {
		return o1.getOriginalName().compareTo(o2.getOriginalName());
	}

}
