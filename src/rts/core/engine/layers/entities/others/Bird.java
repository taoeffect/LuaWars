package rts.core.engine.layers.entities.others;

import java.util.Random;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import rts.core.engine.Engine;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.BasicEntity;
import rts.utils.ResourceManager;
import rts.utils.Timer;

public class Bird extends BasicEntity {

	private Timer moveTimer;
	private Animation animation;

	public Bird(Engine engine) {
		super(engine, Layer.THIRD_EFFECT);
		Random r = new Random();
		this.y = -30;
		this.x = r.nextInt(engine.getMap().getWidthInPixel() - 20) + 20;
		this.animation = new Animation();
		SpriteSheet ss = ResourceManager.getSpriteSheet("birds");
		int sy = r.nextInt(2);
		for (int i = 0; i < 5; i++) {
			animation.addFrame(ss.getSprite(i, sy), 150);
		}
		this.moveTimer = new Timer(50);
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.drawAnimation(animation, x, y);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		moveTimer.update(delta);
		if (moveTimer.isTimeComplete()) {
			this.y += 1;
			moveTimer.resetTime();
		}
	}

}
