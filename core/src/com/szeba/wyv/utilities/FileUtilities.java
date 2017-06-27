package com.szeba.wyv.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

public final class FileUtilities {
	
	//private static StringSizeComparator stringSizeComparator = new StringSizeComparator();
	
	private FileUtilities() {}
	
	/** 
	 * Get if this path is accessible 
	 */
	public static boolean exists(String path) {
		File f = new File(path);
		return f.exists();
	}
	
	/** 
	 * Copy a directory recursively 
	 */
	public static void copyDirectory(File src, File dest) {
		try {
			FileUtils.copyDirectory(src, dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * Create an ArrayList of filenames of the given directory
	 */
	public static ArrayList<String> listFolderContents(String path) {
		File f = new File(path);
		if (f.list() == null) {
			return null;
		}
		ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));
		Collections.sort(names);
		return names;
	}
	
	/** 
	 * Create an ArrayList of filenames of the given directory 
	 */
	public static ArrayList<String> listFolderContentsWithDots(String path) {
		File f = new File(path);
		if (f.list() == null) {
			return null;
		}
		ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));
		Collections.sort(names);
		names.add(0, "...");
		return names;
	}
	
	/** 
	 * List Files in a directory 
	 */
	public static File[] listFolderFiles(String path) {
		File f = new File(path);
		File[] files = f.listFiles();
		return files;
	}
	
	/**
	 * Create a folder structure, to match the given path
	 */
	public static void createFolders(String path) {
		File f = new File(path);
		f.mkdirs();
	}
	
	/**
	 * Delete a folder structure 
	 */
	public static void deleteDirectory(File file) {
		try {
			FileUtils.deleteDirectory(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Recursively list all sub-directories in this directory.
	 * Uses a special filter to filter out directories with special extensions!
	 */
	public static Collection<File> listSubDirectories(File dir) {
		Collection<File> fc = FileUtils.listFilesAndDirs(dir, FileFilterUtils.falseFileFilter(), 
				new SpecDirFilter());
		return fc;
	}
	
	/**
	 * Get the file's attributes.
	 */
	public static BasicFileAttributes getFileAttributes(String filePath) {
		Path path = Paths.get(filePath);
		try {
			return Files.readAttributes(path, BasicFileAttributes.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Get if this path is a folder or not.
	 */
	public static boolean isFolder(String path) {
		File f = new File(path);
		return f.isDirectory();
	}
	
	/*
	 * Now here comes the file types
	 */
	
	/**
	 * Get if this strings special extension matches the editor standards
	 */
	public static boolean isValidWyvernSpecial(String name) {
		String special = StringUtilities.getSpecialExtension(name);
		if (special.equals("map") ||
				special.equals("spriteset") ||
				special.equals("animation") ||
				special.equals("tileset")) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Main method for special extension checks
	 */
	private static boolean isValidWyvernItem(String path, String atname) {
		String specialExtension = StringUtilities.getSpecialExtension(path);
		if (exists(path) && isFolder(path) && specialExtension.equals(atname)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Get if the given path is a map.
	 */
	public static boolean isValidMap(String path) {
		return isValidWyvernItem(path, "map");
	}
	
	/**
	 * Get if the given path is a spriteset.
	 */
	public static boolean isValidSpriteset(String path) {
		return isValidWyvernItem(path, "spriteset");
	}
	
	/**
	 * Get if the given path is a valid animation set.
	 */
	public static boolean isValidAnimation(String path) {
		return isValidWyvernItem(path, "animation");
	}
	
	/**
	 * Get if the given path is a valid tileset.
	 */
	public static boolean isValidTileset(String path) {
		return isValidWyvernItem(path, "tileset");
	}
	
	/**
	 * Get if the given path is a valid image file.
	 */
	public static boolean isValidImage(String path) {
		String extension = StringUtilities.getExtension(path);
		String ext_lowercase = extension.toLowerCase();
		
		// Check if this file exists, not a folder, and the lowercased extension equals to the following:
		if (exists(path) && !isFolder(path) &&
				(ext_lowercase.equals("png") ||
				ext_lowercase.equals("jpg") ||
				ext_lowercase.equals("jpeg") ||
				ext_lowercase.equals("gif") ||
				ext_lowercase.equals("bmp") ||
				ext_lowercase.equals("wdat"))
				) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Get if the given path is a valid texture file.
	 */
	public static boolean isValidTexture(String path) {
		String extension = StringUtilities.getExtension(path);
		String ext_lowercase = extension.toLowerCase();
		
		// Check if this file exists, not a folder, and the lowercased extension equals to the following:
		if (exists(path) && !isFolder(path) &&
				(ext_lowercase.equals("png") ||
				ext_lowercase.equals("jpg") ||
				ext_lowercase.equals("jpeg") ||
				ext_lowercase.equals("gif") ||
				ext_lowercase.equals("bmp"))
				) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Get if the given path is a valid audio file.
	 */
	public static boolean isValidAudio(String path) {
		String extension = StringUtilities.getExtension(path);
		String ext_lowercase = extension.toLowerCase();
		
		// Check if this file exists, not a folder, and the lowercased extension equals to the following:
		if (exists(path) && !isFolder(path) &&
				(ext_lowercase.equals("wav") ||
				ext_lowercase.equals("mp3") ||
				ext_lowercase.equals("ogg"))
				) {
			return true;
		} else {
			return false;
		}
	}
	
}
