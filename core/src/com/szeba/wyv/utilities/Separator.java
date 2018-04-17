package com.szeba.wyv.utilities;

public final class Separator {

	private Separator() { }

	public static String escapeCharacter = "$";
	public static String customNewLine = Character.toString((char)96); // The backtick character! Ascii code: 96

	public static String dynParameter = "$]";
	public static String array = "$%";
	public static String dataUnit = "$@";
	public static String listElement = "$>";

	public static String fileTextChar = " ";
	public static String fileWyvChar = "|";

}
