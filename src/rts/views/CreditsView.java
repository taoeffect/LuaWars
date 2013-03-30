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
		
		Label l = new Label("--- CONCEPTION/DEVELOPMENT ---");
		l.setPosition(44, 35);
		w.add(l);
		
		l = new Label("Vin789");
		l.setPosition(125, 65);
		w.add(l);
		
		l = new Label("--- SPRITES/DESIGNS ---");
		l.setPosition(74, 95);
		w.add(l);
		
		l = new Label("Hard Vacuum From Lost Garden");
		l.setPosition(50, 120);
		w.add(l);
		
		l = new Label("Mr Qqn");
		l.setPosition(125, 150);
		w.add(l);
		
		l = new Label("Vin789");
		l.setPosition(125, 180);
		w.add(l);
		
		l = new Label("--- SOUNDS/MUSICS ---");
		l.setPosition(78, 210);
		w.add(l);
		
		l = new Label("Karam");
		l.setPosition(128, 240);
		w.add(l);
		
		l = new Label("--- API ---");
		l.setPosition(119, 270); // 330
		w.add(l);
		
		l = new Label("Slick 2D");
		l.setPosition(126, 300);
		w.add(l);
		
		l = new Label("TWL + Eforen");
		l.setPosition(108, 330);
		w.add(l);
		
		l = new Label("Kryo");
		l.setPosition(134, 360);
		w.add(l);
		
		l = new Label("--- TEST/DEBUGS ---");
		l.setPosition(90, 390);
		w.add(l);
		l = new Label("Sisko");
		l.setPosition(134, 420);
		w.add(l);
		
		l = new Label("Yoro");
		l.setPosition(136, 450);
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
