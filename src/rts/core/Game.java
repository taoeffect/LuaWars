package rts.core;

import java.io.IOException;
import java.util.ArrayList;

import org.luawars.Log;
import org.luawars.gui.MainPanel;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.*;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.InputAdapter;

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;

import rts.core.engine.Engine;
import rts.core.network.NetworkManager;
import rts.utils.Configuration;
import rts.utils.ResourceManager;
import rts.views.CreateView;
import rts.views.CreditsView;
import rts.views.MainMenuView;
import rts.views.NetworkView;
import rts.views.OptionsView;
import rts.views.ResourcesView;
import rts.views.View;

import javax.swing.*;

/**
 * The main game class, contain the view lists and the launch process.
 * 
 * @author Vincent PIRAULT
 * 
 */
public class Game extends StateBasedGame {

	// State IDS
	public static final int PRESENTATION_STATE_ID = 0;
	public static final int RESOURCES_STATE_ID = 1;
	public static final int MAIN_MENU_VIEW_ID = 2;
	public static final int NETWORK_VIEW_ID = 3;
	public static final int OPTIONS_VIEW_ID = 4;
	public static final int CREDITS_VIEW_ID = 5;
	public static final int ENGINE_VIEW_ID = 6;
	public static final int CREATE_VIEW_ID = 7;

	/**
	 * The current name of the project.
	 */
	public static final String NAME = "STK RTS";
	/**
	 * The current version of the project.
	 */
	public static final String VERSION = "Version 1.0 Beta";

	private ArrayList<View> states;

	/**
	 * The network manager
	 */
	private NetworkManager networkManager;

	// TWL
	private static final String THEME_PATH = "resources/themes/guiTheme.xml";

	private LWJGLRenderer lwjglRenderer;
	private ThemeManager theme;
	private GUI gui;
	private TWLInputAdapter twlWrapper;

	/**
	 * The game class constructor, initialize the game from the resources and
	 * configuration files.
	 * 
	 * @param resourceJarLocation
	 * @param configFileLocation
	 * @throws IOException
	 * @throws SlickException
	 */
	public Game(String resourceJarLocation, String configFileLocation) throws IOException, SlickException {
		super(NAME);
		// Initialize resources
		ResourceManager.init(resourceJarLocation);
		// Initialize configuration
		Configuration.init(configFileLocation);

		states = new ArrayList<View>();

		networkManager = new NetworkManager();
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
        Log.logEnterMethod(Log.DEBUG);
        addState(new ResourcesView(container));
		addState(new MainMenuView());
		addState(new NetworkView());
		addState(new OptionsView());
		addState(new CreditsView());
		addState(new Engine());
		addState(new CreateView());
        Log.debug(Log.me() + " createTWLRenderer()...");
		createTWLRenderer();
        Log.logExitMethod(Log.DEBUG);
	}

	@Override
	public void addState(GameState state) {
		super.addState(state);
		states.add((View) state);
	}

	public View getStateByIndex(int index) {
		return (View) states.get(index);
	}

	/**
	 * Entry point to launch the game.
	 * 
	 * @throws SlickException
	 * @throws IOException
	 */
	public void launch() throws SlickException, IOException {
//		AppGameContainer container = new AppGameContainer(this);
        final CanvasGameContainer can = new CanvasGameContainer(this);
        AppGameContainer container = (AppGameContainer)can.getContainer();

        // Mandatory
		container.setMinimumLogicUpdateInterval(10);
		container.setMaximumLogicUpdateInterval(10);
		container.setUpdateOnlyWhenVisible(false);
		container.setAlwaysRender(true);

		// Apply Configuration
		applyCurrentConfiguration(container);

        // set size (greg added this)

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Lua Wars");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                MainPanel panel = new MainPanel();
                // TODO: make it so that container.getWidth() is used instead and works
                Log.debug("Setting container size: "+Configuration.getWidth()+"w "+Configuration.getHeight()+"h");
                can.setSize(Configuration.getWidth(), Configuration.getHeight());
                panel.splitPane.setRightComponent(can);
                frame.setContentPane(panel.panel);
                frame.pack();
                frame.setVisible(true);
            }
        });
        // Start the game
		can.start();

        // TODO: fix this exception when clicking 'Exit'
        /*
        AL lib: FreeDevice: (0x10fc30000) Deleting 31 Buffer(s)
Exception in thread "AWT-EventQueue-0" java.lang.IllegalStateException: Mouse must be created before you can read events
	at org.lwjgl.input.Mouse.next(Mouse.java:444)
	at org.newdawn.slick.Input.poll(Input.java:1205)
	at org.newdawn.slick.GameContainer.updateAndRender(GameContainer.java:656)
	at org.newdawn.slick.AppGameContainer.gameLoop(AppGameContainer.java:456)
	at org.newdawn.slick.CanvasGameContainer$2.run(CanvasGameContainer.java:100)
	at java.awt.event.InvocationEvent.dispatch(InvocationEvent.java:209)
	at java.awt.EventQueue.dispatchEventImpl(EventQueue.java:702)
	at java.awt.EventQueue.access$400(EventQueue.java:82)
	at java.awt.EventQueue$2.run(EventQueue.java:663)
	at java.awt.EventQueue$2.run(EventQueue.java:661)
	at java.security.AccessController.doPrivileged(Native Method)
	at java.security.AccessControlContext$1.doIntersectionPrivilege(AccessControlContext.java:87)
	at java.awt.EventQueue.dispatchEvent(EventQueue.java:672)
	at java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:296)
	at java.awt.EventDispatchThread.pumpEventsForFilter(EventDispatchThread.java:211)
	at java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:201)
	at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:196)
	at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:188)
	at java.awt.EventDispatchThread.run(EventDispatchThread.java:122)
         */
	}

	private void applyCurrentConfiguration(AppGameContainer container) throws IOException, SlickException {
		Configuration.updateConfigFile();
        container.setDisplayMode(Configuration.getWidth(), Configuration.getHeight(), Configuration.isFullScreen());
		container.setTargetFrameRate(Configuration.getTargetFPS());
		container.setSmoothDeltas(Configuration.isSmoothDeltas());
		container.setVSync(Configuration.isVSynch());
		container.setMusicVolume(Configuration.getMusicVolume());
		container.setSoundVolume(Configuration.getSoundVolume());
		container.setShowFPS((Configuration.isDebug()) ? true : false);
		container.setVerbose((Configuration.isDebug()) ? true : false);
	}

	/**
	 * Apply the current configuration to the game container of the game
	 * context.
	 * 
	 * @throws IOException
	 *             If the configuration loading failed.
	 * @throws SlickException
	 *             If the configuration loading failed.
	 */
	public void applyCurrentConfiguration() throws IOException, SlickException {
		applyCurrentConfiguration((AppGameContainer)this.getContainer());
	}

	/**
	 * A cut to get the engine instance directly.
	 * 
	 * @return The engine instance or null if the engine was not instantiated.
	 */
	public Engine getEngine() {
		return (Engine) this.getState(ENGINE_VIEW_ID);
	}

	public NetworkManager getNetworkManager() {
		return networkManager;
	}

	// TWL

	private void createTWLRenderer() {

		// save Slick's GL state while loading the theme
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		try {
			lwjglRenderer = new LWJGLRenderer();
			lwjglRenderer.setUseSWMouseCursors(true);
			theme = ThemeManager.createThemeManager(Thread.currentThread().getContextClassLoader().getResource(THEME_PATH), lwjglRenderer);
			gui = new GUI(lwjglRenderer);
			gui.applyTheme(theme);
		} catch (LWJGLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// restore Slick's GL state
			GL11.glPopAttrib();
		}

		// connect input
		twlWrapper = new TWLInputAdapter(gui, getContainer().getInput());
		getContainer().getInput().addPrimaryListener(twlWrapper);
	}

	public void updateTWL() {
		twlWrapper.update();
	}

	public void renderTWL() {
		// gui.setRootPane(((View) getCurrentState()).getTwlRootWidget());
		twlWrapper.render();
	}

	public void reloadTWL() {
		gui.destroy();
		lwjglRenderer.getActiveCacheContext().destroy();
		gui = null;
		lwjglRenderer = null;
		createTWLRenderer();
		getContainer().setMouseGrabbed(false);
	}

	public void initAllTWLComponents() {
        Log.logEnterMethod(Log.DEBUG);
		// We didn't init the resource view
		for (int i = 1; i < states.size(); i++) {
			states.get(i).initTwl();
		}
        Log.logExitMethod(Log.DEBUG);
	}

	public class TWLInputAdapter extends InputAdapter {

		private final Input input;
		private final GUI gui;

		private int mouseDown;
		private boolean ignoreMouse;
		private boolean lastPressConsumed;

		public TWLInputAdapter(GUI gui, Input input) {
			if (gui == null) {
				throw new NullPointerException("gui");
			}
			if (input == null) {
				throw new NullPointerException("input");
			}

			this.gui = gui;
			this.input = input;
		}

		@Override
		public void mouseWheelMoved(int change) {
			if (!ignoreMouse) {
				if (gui.handleMouseWheel(change)) {
					consume();
				}
			}
		}

		@Override
		public void mousePressed(int button, int x, int y) {
			if (mouseDown == 0) {
				// only the first button down counts
				lastPressConsumed = false;
			}

			mouseDown |= 1 << button;

			if (!ignoreMouse) {
				if (gui.handleMouse(x, y, button, true)) {
					consume();
					lastPressConsumed = true;
				}
			}
		}

		@Override
		public void mouseReleased(int button, int x, int y) {
			mouseDown &= ~(1 << button);

			if (!ignoreMouse) {
				if (gui.handleMouse(x, y, button, false)) {
					consume();
				}
			} else if (mouseDown == 0) {
				ignoreMouse = false;
			}
		}

		@Override
		public void mouseMoved(int oldX, int oldY, int newX, int newY) {
			if (mouseDown != 0 && !lastPressConsumed) {
				ignoreMouse = true;
				gui.clearMouseState();
			} else if (!ignoreMouse) {
				if (gui.handleMouse(newX, newY, -1, false)) {
					consume();
				}
			}
		}

		@Override
		public void mouseDragged(int oldx, int oldy, int newX, int newY) {
			mouseMoved(oldx, oldy, newX, newY);
		}

		@Override
		public void keyPressed(int key, char c) {
			if (gui.handleKey(key, c, true)) {
				consume();
			}
		}

		@Override
		public void keyReleased(int key, char c) {
			if (gui.handleKey(key, c, false)) {
				consume();
			}
		}

		@Override
		public void mouseClicked(int button, int x, int y, int clickCount) {
			if (!ignoreMouse && lastPressConsumed) {
				consume();
			}
		}

		private void consume() {
			input.consumeEvent();
		}

		@Override
		public void inputStarted() {
			gui.updateTime();
		}

		@Override
		public void inputEnded() {
			gui.handleKeyRepeat();
		}

		/**
		 * Call this method from {@code BasicGame.update}
		 * 
		 * @see StateBasedGame#update(org.newdawn.slick.GameContainer, int)
		 */
		public void update() {
			gui.setSize();
			gui.handleTooltips();
			gui.updateTimers();
			gui.invokeRunables();
			gui.validateLayout();
			gui.setCursor();
		}

		/**
		 * Call this method from {@code BasicGame.render}
		 * 
		 * @see StateBasedGame#render(org.newdawn.slick.GameContainer,
		 *      org.newdawn.slick.Graphics)
		 */
		public void render() {
			gui.draw();
		}
	}

	public void setRootPane(int id) {
		gui.setRootPane(((View) getState(id)).getTwlRootWidget());
	}

}
