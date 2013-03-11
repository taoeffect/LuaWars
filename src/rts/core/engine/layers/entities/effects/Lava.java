package rts.core.engine.layers.entities.effects;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import rts.core.engine.Engine;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.BasicEntity;
import rts.utils.ResourceManager;

public class Lava extends BasicEntity{

	private Animation animation;
	
	public Lava(Engine engine,float x,float y) {
		super(engine, Layer.FIRST_EFFECT);
		this.x = x;
		this.y = y;
		animation = new Animation();
		SpriteSheet ss = ResourceManager.getSpriteSheet("lava");
		for(int i=0;i<16;i++){
			animation.addFrame(ss.getSprite(i, 0), 150);
		}
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.drawAnimation(animation, x, y - 20);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		
	}

}
