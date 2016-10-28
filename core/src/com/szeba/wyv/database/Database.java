package com.szeba.wyv.database;

/**
 * The main database, which holds predefined entries, and variables
 * @author Szeba
 */
public class Database {
	
	public Entries ent;
	public Variables var;
	public CommonEvents ce;
	
	public void init() {
		var = new Variables();
		var.load();
		ent = new Entries();
		ent.load();
		ce = new CommonEvents();
		ce.load();
	}

}
