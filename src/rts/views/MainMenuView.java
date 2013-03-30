package rts.views;

import java.io.IOException;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Widget;

import rts.core.Game;
import rts.utils.ResourceManager;

/**
 * The main menu of the game. Severals sub menus are linked here like:
 * 
 * Network menu Options menu Credits menu
 * 
 * @author Vincent PIRAULT
 * 
 */
public class MainMenuView extends View {

	private Image background;
	private Image gameTitle;

	@Override
	public void initResources() {
		background = ResourceManager.getImage("main_menu_view_background");
		gameTitle = ResourceManager.getImage("game_title");
	}

	@Override
	public void initTwlComponent() {

		int x = container.getWidth() / 2 - 150;
		int y = container.getHeight() / 2 - 100;

		Widget w = new Widget();
		w.setPosition(x, y);
		w.setSize(300, 300);

		Button networkButton = new Button("Campaign");
		networkButton.setPosition(50, 20);
		networkButton.setSize(150, 40);
		networkButton.addCallback(new Runnable() {
			@Override
			public void run() {
                try {
                    game.getNetworkManager().createServer();
                    game.getNetworkManager().joinServer("localhost");
                    game.enterState(Game.CREATE_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
                } catch (IOException e) {
                    e.printStackTrace();
                }
			}
		});
		w.add(networkButton);

		Button optionsButton = new Button("Options");
		optionsButton.setPosition(50, 90);
		optionsButton.setSize(150, 40);
		optionsButton.addCallback(new Runnable() {
			@Override
			public void run() {
				game.enterState(Game.OPTIONS_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
			}
		});
		w.add(optionsButton);

		Button creditsButton = new Button("Credits");
		creditsButton.setPosition(50, 160);
		creditsButton.setSize(150, 40);
		creditsButton.addCallback(new Runnable() {
			@Override
			public void run() {
				game.enterState(Game.CREDITS_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
			}
		});
		w.add(creditsButton);

		Button exitButton = new Button("Exit");
		exitButton.setPosition(50, 230);
		exitButton.setSize(150, 40);
		exitButton.addCallback(new Runnable() {
			@Override
			public void run() {
				game.getContainer().exit();
			}
		});
		w.add(exitButton);

		root.add(w);
	}

	@Override
	public void render(GameContainer container, StateBasedGame sbgame, Graphics g) throws SlickException {
		g.drawImage(background, 0, 0);
		g.drawImage(gameTitle, container.getWidth() / 2 - 160, container.getHeight() / 2 - 220);
		super.render(container, sbgame, g);
		g.setColor(Color.white);
		g.drawString(Game.VERSION, 5, container.getHeight() - 20);
	}

	@Override
	public int getID() {
		return Game.MAIN_MENU_VIEW_ID;
	}

}
