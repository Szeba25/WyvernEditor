package com.szeba.wyv.data.texture;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import com.badlogic.gdx.graphics.Texture;

/**
 * An advanced texture, storing it's files attribute dates, and a boolean flag, that
 * the loaded texture is the same as the target texture.
 * @author Szeba
 */
public class ManagedTexture extends Texture {

	private boolean found;
	
	private String fileDir;
	private String fileName;
	
	private FileTime timeModified;
	
	public ManagedTexture(String loadedPath, boolean found, String dir, String name) {
		super(loadedPath);
		
		this.found = found;
		
		fileDir = dir;
		fileName = name;
		
		timeModified = null;
	}
	
	public String getFileDir() {
		return fileDir;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public boolean getFound() {
		return found;
	}
	
	public void setFileAttributes(BasicFileAttributes attrs) {
		timeModified = attrs.lastModifiedTime();
	}
	
	public boolean isEqualAttrs(BasicFileAttributes attrs) {
		return timeModified.compareTo(attrs.lastModifiedTime()) == 0;
	}
	
}
