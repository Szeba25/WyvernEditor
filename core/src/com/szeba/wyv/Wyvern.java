package com.szeba.wyv;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.szeba.wyv.cache.Cache;
import com.szeba.wyv.data.files.TextFile;
import com.szeba.wyv.database.Database;
import com.szeba.wyv.input.Input;
import com.szeba.wyv.screens.Screen;
import com.szeba.wyv.screens.implemented.DatabaseScreen;
import com.szeba.wyv.screens.implemented.EventingScreen;
import com.szeba.wyv.screens.implemented.MainScreen;
import com.szeba.wyv.screens.implemented.SplashScreen;
import com.szeba.wyv.utilities.FileUtilities;
import com.szeba.wyv.utilities.FontUtilities;
import com.szeba.wyv.utilities.Palette;
import com.szeba.wyv.utilities.ShapePainter;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.viewport.CustomViewport;
import com.szeba.wyv.widgets.ext.Warning;

/** 
 * Main class of Wyvern. This listener will control the whole editor.
 * @author Szeba
 */
public class Wyvern implements ApplicationListener {
	
	// The current version number
	public static final String VERSION = "v0.816";
	
	// The current working directory
	public static String DIRECTORY;
	
	// The desired framerate of the editor
	public static int FPS;
	
	// The current interpreter name, and directory
	public static String INTERPRETER_NAME;
	public static String INTERPRETER_DIR;
	
	// The cache used to manage resources.
	public static Cache cache;
	
	// The input processor
	public static Input input;
	
	// The main database
	public static Database database;
	
	/* The current and the desired screen ID. currentScreen 
	will always change to screenChanger at the end of each frame */
	public static int currentScreen = 0;
	public static int screenChanger = 0;
	public static int returnToFromEventing = 0;
	
	// LibGdx stuff for rendering
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Viewport viewPort;
	
	// The screens the editor will operate on
	private ArrayList<Screen> screens;
	
	// Stores how much processing power the editor uses per frame
	private int editorLoad;
	private Runtime runtime;
	private boolean showDebug;
	private int memoryUnit;
	
	/** 
	 * Initialize the editor 
	 */
	public Wyvern(String workingDirectory, boolean externalInterpreter, String interpreter, int framerate) {
		DIRECTORY = workingDirectory;
		FPS = framerate;
		INTERPRETER_NAME = interpreter;

		if (externalInterpreter) {
			INTERPRETER_DIR = interpreter;
		} else {
			INTERPRETER_DIR = workingDirectory + "/interpreters/" + interpreter;
		}

		cache = new Cache();
		input = new Input();
		database = new Database();
		editorLoad = 0;
		runtime = Runtime.getRuntime();
		showDebug = false;
		memoryUnit = 1024;
	}
	
	@Override
	public void create() {
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(true);
		viewPort = new CustomViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
		
		// Load the editor theme
		Palette.load_theme(null);
		// Create empty screen objects
		SplashScreen splash = new SplashScreen();
		screens = new ArrayList<Screen>();
		screens.add(splash);
		
		screens.add(new MainScreen());
		screens.add(new EventingScreen());
		screens.add(new DatabaseScreen());
		
		// Initialize the splashscreen
		splash.init();
		splash.setScreens(screens);
		
		// Set the current inputprocessor (we set this processor, so we can poll for the mouse wheel)
		Gdx.input.setInputProcessor(Wyvern.input);
		
		// Map the game map IDs to print errors
		mapGameMapIDs();

		// Disable alpha values when drawing pixmaps
		Pixmap.setBlending(Blending.None);
	}

	@Override
	public void dispose() {
	}
	
	@Override
	public void pause() {
	}
	
	@Override
	public void resume() {
	}

	@Override
	public void render() {	
		long time1 = System.nanoTime();
		// Clear the buffer to black
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// Set the camera to this batch
		batch.setProjectionMatrix(camera.combined);
		
		// Schedule audio unload
		Wyvern.cache.scheduleMusicUnload();
		
		// Begin the actual drawing
		batch.begin();
		screens.get(currentScreen).screenDraw(batch);
		drawFramerate();
		drawEditorLoad();
		drawRuntimeLoad();
		drawWarning();
		// End of drawing code
		batch.end();
		
		/* Now, update the current screen, and the Warning widget.
		 * The warning widget is not in a container, but we still need the updateDelay, so it's implemented
		 * inside the warning widget too. */
		if (Warning.widget != null && Warning.widget.isVisible()) {
			Warning.widget.update(0);
		} else if (!(Warning.getUpdateDelay() > 0)) {
			screens.get(currentScreen).screenUpdate(input.updateScrolling());
		} else if (!Wyvern.input.isButtonHold(0) && Warning.getUpdateDelay() > 0) {
			Warning.subUpdateDelay();
		}
		
		// Update input
		input.update();
		input.updateDelta();
		input.resetTypedChar();
		
		// Unload music if not refreshed
		Wyvern.cache.unloadMusicIfInactive();
		
		// Process debugging hotkeys
		debugHotkeys();
		
		// Change states at the end of the frame
		stateChanger();
		long time2 = System.nanoTime();
		editorLoad = (int) (((time2 - time1) / (1000000000.0 / FPS)) * 100);
	}

	@Override
	public void resize(int width, int height) {
		viewPort.update(width, height);
		// Resize the screens only if the splashscreen finished initializing them.
		if (currentScreen > 0) {
			screens.get(currentScreen).resize(width, height);
			Warning.widget.setToCenter();
		}
	}
	
	/**
	 * Draws the warning widget.
	 */
	public void drawWarning() {
		if (currentScreen > 0) {
			Warning.widget.draw(batch);
		}
	}
	
	/**
	 * Draw the framerate at the corner of the screen.
	 */
	public void drawFramerate() {
		if (currentScreen > 0 && showDebug) {
			ShapePainter.drawFilledRectangle(batch, Palette.WIDGET_BKG, 
					Gdx.graphics.getWidth() - 90, 
					Gdx.graphics.getHeight() - 16, 
					50, 16);
			ShapePainter.drawRectangle(batch, Palette.WIDGET_ACTIVE_BRD, 
					Gdx.graphics.getWidth() - 90, 
					Gdx.graphics.getHeight() - 16, 
					50, 16);
			FontUtilities.print(batch, Integer.toString(((int)Gdx.graphics.getFramesPerSecond())), 
					Gdx.graphics.getWidth() - 89, Gdx.graphics.getHeight() - 15);
		}
	}
	
	/**
	 * Draws the editor load on screen
	 */
	public void drawEditorLoad() {
		if (currentScreen > 0 && showDebug) {
			ShapePainter.drawFilledRectangle(batch, Palette.WIDGET_BKG, 
					Gdx.graphics.getWidth() - 41, 
					Gdx.graphics.getHeight() - 16, 
					41, 16);
			ShapePainter.drawRectangle(batch, Palette.WIDGET_ACTIVE_BRD, 
					Gdx.graphics.getWidth() - 41, 
					Gdx.graphics.getHeight() - 16, 
					41, 16);
			FontUtilities.print(batch, Integer.toString(editorLoad) + "%", 
					Gdx.graphics.getWidth() - 40, Gdx.graphics.getHeight() - 15);
		}
	}
	
	/**
	 * Draws runtime load on screen
	 */
	public void drawRuntimeLoad() {
		if (currentScreen > 0 && showDebug) {
			ShapePainter.drawFilledRectangle(batch, Palette.WIDGET_BKG, 
					Gdx.graphics.getWidth() - 90, 
					Gdx.graphics.getHeight() - 104, 
					90, 89);
			ShapePainter.drawRectangle(batch, Palette.WIDGET_ACTIVE_BRD, 
					Gdx.graphics.getWidth() - 90, 
					Gdx.graphics.getHeight() - 104, 
					90, 89);
			
			FontUtilities.print(batch, "used: " +
					Long.toString((runtime.totalMemory() - runtime.freeMemory())/memoryUnit), 
					Gdx.graphics.getWidth() - 89, Gdx.graphics.getHeight() - 103);
			
			FontUtilities.print(batch, "free: " +
					Long.toString(runtime.freeMemory()/memoryUnit), 
					Gdx.graphics.getWidth() - 89, Gdx.graphics.getHeight() - 87);
			
			FontUtilities.print(batch, "total: " + 
					Long.toString(runtime.totalMemory()/memoryUnit), 
					Gdx.graphics.getWidth() - 89, Gdx.graphics.getHeight() - 71);
			
			FontUtilities.print(batch, "max: " + 
					Long.toString(runtime.maxMemory()/memoryUnit),
					Gdx.graphics.getWidth() - 89, Gdx.graphics.getHeight() - 55);
		}
	}
	
	/**
	 * Hotkeys for debugging the editor
	 */
	private void debugHotkeys() {
		if (Wyvern.input.isKeyPressed(Keys.F10)) {
			if (showDebug) {
				showDebug = false;
			} else {
				showDebug = true;
			}
		}
		if (Wyvern.input.isKeyPressed(Keys.F11)) {
			System.out.println("### Memory usage###");
			System.out.println("used: " + (runtime.totalMemory() - runtime.freeMemory())/memoryUnit);
			System.out.println("free: " + runtime.freeMemory()/memoryUnit);
			System.out.println("total: " + runtime.totalMemory()/memoryUnit);
			System.out.println("max: " + runtime.maxMemory()/memoryUnit);
			System.out.println("### End ###");
		}
	}
	
	/**
	 * Called at the end of each frame, to change screens.
	 */
	private void stateChanger() {
		if (currentScreen != screenChanger) {
			screens.get(currentScreen).leave();
			currentScreen = screenChanger;
			screens.get(currentScreen).enter();
		}
	}
	
	/** 
	 * This method is used instead of the direct Gdx.graphics.getDeltaTime() method, to prevent the delta from
	 * spiking up.
	 */
	public static float getDelta() {
		float delta = Gdx.graphics.getDeltaTime();
		if (delta > 0.02f) {
			delta = 0.02f;
		}
		return delta;
	}
	
	public static String getNextEventID() {
		return idGetter("id_counter.txt", "event");
	}
	
	public static String getNextMapID() {
		return idGetter("map_id_counter.txt", "map");
	}
	
	private static String idGetter(String filename, String type) {
		TextFile file = new TextFile(INTERPRETER_DIR + "/preferences/" + cache.getSignature() 
											+ "@sig_id/" + filename);
		int id = Integer.parseInt(file.getValue(0, 0)) + 1;
		String sid = Integer.toString(id);
		String fsid = cache.getSignature() + "@" + sid;
		file.setValue(0, 0, sid);
		file.save();
		System.out.println("Editor: ID requested for " + type + ": " + fsid);
		return fsid;
	}
	
	public static HashMap<String, String> mapGameMapIDs() {
		
		System.out.println("Map ID table requested!");
		
		HashMap<String, String> mapList = new HashMap<String, String>();

		for (File f : FileUtils.listFilesAndDirs(new File(INTERPRETER_DIR + "/maps"), 
				TrueFileFilter.INSTANCE, DirectoryFileFilter.DIRECTORY)) {
			
			// Replace slashes
			String finalPath = StringUtilities.replaceSlashesInPath(f.getAbsolutePath());
			
			if (FileUtilities.isValidMap(finalPath)) {
				
				// Read the signature ID in this file
				TextFile file = new TextFile(finalPath + "/map_id.wdat");
				
				// Map the signature ID
				String sigid = file.getValue(0, 0);
				if (mapList.containsKey(sigid)) {
					// Print a warning
					System.err.println("Map ID conflict... (" + sigid + ") " + 
											mapList.get(sigid) + " " + finalPath);
				}
				mapList.put(sigid, finalPath);
			}
		}
		
		/*
		for (Entry<String, String> ent : mapList.entrySet()) {
			System.out.println(ent.getKey() + " ** " + ent.getValue());
		}
		*/
		
		return mapList;
	}
	
	public static void appendMapIDs(File target) {
		for (File f : FileUtils.listFilesAndDirs(target, 
				TrueFileFilter.INSTANCE, DirectoryFileFilter.DIRECTORY)) {
			
			// Replace slashes
			String finalPath = StringUtilities.replaceSlashesInPath(f.getAbsolutePath());
			
			if (FileUtilities.isValidMap(finalPath)) {
				
				// We change the ID in this map.
				System.out.println("Editor: ID change in: " + f.getName());
				
				// Replace the ID in this file!!!
				TextFile file = new TextFile(finalPath + "/map_id.wdat");
				file.setValue(0, 0, Wyvern.getNextMapID());
				file.save();
			}
		}
	}
	
}
