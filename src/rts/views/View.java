package rts.views;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.luawars.Log;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.imageout.ImageOut;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import de.matthiasmann.twl.Widget;

import rts.core.Game;

/**
 * This class represent advance game state like "in game" phases.
 * 
 * @author Vincent PIRAULT
 *
 */
public abstract class View extends BasicGameState {

	protected Widget root;
	protected GameContainer container;
	protected Game game;
	protected boolean initTWL;

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		super.leave(container, game);
		initTWL = false;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		this.container = container;
		this.game = (Game) game;
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		this.game.updateTWL();
	}

	@Override
	public void keyPressed(int key, char c) {
		super.keyPressed(key, c);
		switch (key) {
		case Input.KEY_F1:
			takeScreenShot();
			break;
		default:
			break;
		}
	}

	private void takeScreenShot() {
		try {
			Image image = new Image(container.getWidth(), container.getHeight());
			container.getGraphics().copyArea(image, 0, 0);
			ImageOut.write(image, "screenshot\\screenshot_" + new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss").format(Calendar.getInstance().getTime()) + ".jpg");
		} catch (Exception e) {
			System.err.println("Could not save screenshot: " + e.getMessage());
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		if (!initTWL) {
			this.game.setRootPane(getID());
			this.game.updateTWL();
			this.initTWL = true;
		}
		this.game.renderTWL();
	}

	public void initTwl() {
		root = new Widget();
		root.setTheme("");
        Log.logEnterMethod(Log.DEBUG);
        initTwlComponent();
        Log.logExitMethod(Log.DEBUG);
	}

	/**
	 * Developer must initialize the state resources here.
	 * 
	 * @param container
	 *            The game container associated to the game context.
	 * @param game
	 *            The Game context.
	 */
	public abstract void initResources();

	public abstract void initTwlComponent();

	public Widget getTwlRootWidget() {
		return root;
	}

}
