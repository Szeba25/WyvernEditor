package com.szeba.wyv.desktop;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.utilities.MultiOutputStream;
import com.szeba.wyv.utilities.StringUtilities;

public class DesktopLauncher {
	
	public static void main(String[] args) {
		// Get the working directory
		String workingDirectory = getWorkingDirectory();
		
		// Get the current interpreter name
		TextFile file = new TextFile(workingDirectory + "/core files/interpreter.ikd");
		String interpreter = file.getValue(0, 0);
		
		// Redirect the output stream
		redirectOutputStream(workingDirectory);
		
		// Print the current java version
		System.out.print("Runtime version: " + System.getProperty("java.version") + " ");
		System.out.println(System.getProperty("sun.arch.data.model") + "bit");
		
		// Print the working directory
		System.out.println("Working directory: " + workingDirectory);
		System.out.println("Interpreter: " + interpreter);
		
		// Set the username to ASCII...
		System.setProperty("user.name","EnglishWords");
		
		// Set the lwjgl configuration
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Wyvern - " + Wyvern.VERSION;
		cfg.resizable = true;
		cfg.addIcon(workingDirectory + "/core files/icons/current/icon.png", FileType.Absolute);
		
		// Get the fps counts
		TextFile file2 = new TextFile(workingDirectory + "/core files/main_config.ikd");
		
		cfg.foregroundFPS = Integer.parseInt(file2.getValue(0, 1));
		cfg.backgroundFPS = Integer.parseInt(file2.getValue(1, 1));
		cfg.vSyncEnabled = Boolean.parseBoolean(file2.getValue(2, 1));
		
		// Set window position
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		cfg.width = 1022;
		cfg.height = 640;
		cfg.x = (width - cfg.width) / 2;
		cfg.y = ((height - cfg.height) / 2) - 40;
		
		// Create the application
		new LwjglApplication(new Wyvern(workingDirectory, interpreter, cfg.foregroundFPS), cfg);
	}
	
	/**
	 * Redirect the System.out and System.err streams to a file.
	 */
	public static void redirectOutputStream(String workingDirectory) {
		try {
			FileOutputStream fout = new FileOutputStream(workingDirectory + "/logs/stdout.log");
			FileOutputStream ferr = new FileOutputStream(workingDirectory + "/logs/stderr.log");
			
			MultiOutputStream multiOut = new MultiOutputStream(System.out, fout);
			MultiOutputStream multiErr = new MultiOutputStream(System.err, ferr);
			
			PrintStream stdout = new PrintStream(multiOut);
			PrintStream stderr = new PrintStream(multiErr);
			
			System.setOut(stdout);
			System.setErr(stderr);
		} catch (FileNotFoundException ex) {
			
		}
	}
	
	/** 
	 * Get the current working directory (where the .jar file is)
	 */
	public static String getWorkingDirectory() {
		String finalPath = StringUtilities.replaceSlashesInPath(new File("").getAbsolutePath());
		// Return the final path
		return finalPath;
	}
}
