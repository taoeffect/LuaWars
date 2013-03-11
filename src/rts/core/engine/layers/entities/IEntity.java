package rts.core.engine.layers.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

/**
 * Represent a game entity.
 * 
 * @author Vincent PIRAULT
 * 
 */
public interface IEntity {

	public void render(GameContainer container, Graphics g) throws SlickException;

	public void update(GameContainer container, int delta) throws SlickException;

	public float getX();

	public float getY();

	public int getWidth();

	public int getHeight();

	public int getLayer();

}
