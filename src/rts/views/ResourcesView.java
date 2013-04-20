package rts.views;

import org.luawars.Log;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import de.matthiasmann.twl.ProgressBar;

import rts.core.Game;
import rts.core.engine.GameMusic;
import rts.utils.ResourceManager;
import rts.utils.Timer;

/**
 * The second state of the game, a simple state to load resources. Like
 * presentation state this state load his own resources.
 * 
 * After loading all resources, the state move on the first view, the main menu
 * view.
 * 
 * @author Vincent PIRAULT
 * 
 */
public class ResourcesView extends View {

	private static final int WAIT_TIME_BEFORE_NEXTR = 100;

	private boolean ready;
	private Image background;
	private ProgressBar bar;
	private Timer timer;

	public ResourcesView(GameContainer container) {
		timer = new Timer(WAIT_TIME_BEFORE_NEXTR);
		this.container = container;
        Log.debug(Log.me() + " initTwl...");
        initTwl();
        Log.debug(Log.me() + " initResources...");
		initResources();
        Log.debug(Log.me() + " done with ResourcesView()!");
	}

	public void initResources() {
		try {
			GameMusic.initMainTheme();
			GameMusic.loopMainTheme();
			background = new Image("resources/others/resources_view_background.jpg");
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initTwlComponent() {
		bar = new ProgressBar();
		bar.setTheme("progressbar-glow-anim");
		bar.setSize(300, 10);
		bar.setPosition(container.getWidth() / 2 - 160, container.getHeight() / 2 - 20);
		root.add(bar);

		container.setMouseGrabbed(false);
	}

	@Override
	public void render(GameContainer container, StateBasedGame sbGame, Graphics g) throws SlickException {
		g.drawImage(background, 0, 0);
		super.render(container, sbGame, g);
		g.setColor(Color.red);
		g.drawString("Loading ... " + ResourceManager.getAdvancement() + "%", container.getWidth() / 2 - 80, container.getHeight() / 2 - 60);

		if (ready) {
			g.drawString("Press a key or click", container.getWidth() / 2 - 90, container.getHeight() / 2 + 10);
		}
	}

    static boolean stated = false;
    @Override
	public void update(GameContainer container, StateBasedGame sbGame, int delta) throws SlickException {
        super.update(container, sbGame, delta);
		timer.update(delta);
		if (timer.isTimeComplete()) {
            Log.trace(Log.me() + " ... ResourceManager.loadNextResource() ...");
			ResourceManager.loadNextResource();
			if (ResourceManager.isLoadComplete() && !ready) {
				for (int i = 1; i < sbGame.getStateCount(); i++) {
					View view = ((Game) sbGame).getStateByIndex(i);
                    Log.debug(view.getClass().getSimpleName() + " initResources()...");
					view.initResources();
				}

				game.initAllTWLComponents();
				GameMusic.initMusics();
				ready = true;
			}
			timer.resetTime();
		}
		if (bar != null) {
            bar.setValue(((float) ResourceManager.getAdvancement()) / 100);
            if (ResourceManager.getAdvancement() >= 100 && !stated) {
                stated = true;
                Log.trace("done loading resources!!");
            }
        }
	}

	@Override
	public void keyPressed(int key, char c) {
		super.keyPressed(key, c);
		goToMenu();
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		super.mousePressed(button, x, y);
		goToMenu();
	}

	private void goToMenu() {
		if (ready) {
			container.setMouseGrabbed(false);
			game.enterState(Game.MAIN_MENU_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
		}
	}

	@Override
	public int getID() {
		return Game.RESOURCES_STATE_ID;
	}

}