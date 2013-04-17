package rts.views;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.Widget;

import rts.core.Game;
import rts.utils.ResourceManager;

/**
 * Menu associated to the credits.
 * 
 * @author Vincent PIRAULT
 * 
 */
public class CreditsView extends View {

	private Image background;
	private Image title;
	
	@Override
	public void initResources() {
		background = ResourceManager.getImage("credit_view_background");
		title = ResourceManager.getSpriteSheet("menutitles").getSprite(0, 4);
	}
	

	@Override
	public void initTwlComponent() {
		int x = container.getWidth() / 2;
		int y = container.getHeight() / 2;
		
		Widget w = new Widget();
		w.setSize(300, 480);
		w.setPosition(x - 150, y - 240);
		
		Label l = new Label("--- LuaWars Development Team ---");
		l.setPosition(44, 35);
		w.add(l);
		
		l = new Label("Greg Slepak");
		l.setPosition(110, 85);
		w.add(l);
		
		l = new Label("Austin Baylis");
		l.setPosition(110, 125);
		w.add(l);
		
		l = new Label("Max Ussin");
		l.setPosition(110, 160);
		w.add(l);
		
		l = new Label("David Garcia");
		l.setPosition(110, 200);
		w.add(l);
		
		l = new Label("Trung Nguyen");
		l.setPosition(110, 240);
		w.add(l);
		
		l = new Label("");
		l.setPosition(78, 210);
		w.add(l);
		
		l = new Label("--- Also Thanks To ---");
		l.setPosition(90, 340);
		w.add(l);
		
		l = new Label("Vin789");
		l.setPosition(60, 360); // 330
		w.add(l);
		
		l = new Label("Mr Qqn");
		l.setPosition(130, 360);
		w.add(l);
		
		l = new Label("Karam");
		l.setPosition(210, 360);
		w.add(l);
		
		l = new Label("Kryo");
		l.setPosition(60, 380);
		w.add(l);
		
		l = new Label("Sisko");
		l.setPosition(130, 380);
		w.add(l);
		l = new Label("Yoro");
		l.setPosition(210, 380);
		w.add(l);
		
		l = new Label("Lostgarden.com");
		l.setPosition(60, 410);
		w.add(l);
		
		root.add(w);
		
		Button exitButton = new Button("Back");
		exitButton.setSize(70, 30);
		exitButton.setPosition(x - 350, y + 250);
		exitButton.addCallback(new Runnable() {
			@Override
			public void run() {
				game.enterState(Game.MAIN_MENU_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
			}
		});
		root.add(exitButton);
	}


	@Override
	public int getID() {
		return Game.CREDITS_VIEW_ID;
	}


	@Override
	public void render(GameContainer container, StateBasedGame sbGame, Graphics g) throws SlickException {
		g.drawImage(background, 0, 0);
		g.drawImage(title, container.getWidth() / 2 - 65, container.getHeight() / 2 - 280);
		super.render(container, game, g);
	}

}
