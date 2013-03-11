package rts.core.engine.layers.entities.others;

import java.util.Random;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import rts.core.engine.Engine;
import rts.core.engine.layers.entities.EData;
import rts.utils.ResourceManager;
import rts.utils.Timer;

public class Mineral extends Misc {

	private static final Random RANDOM = new Random();

	private Timer timer;
	private Color green;
	private Animation lot;
	private Animation normal;
	private Animation few;

	public Mineral(Engine engine, int networkId) {
		super(engine, EData.MINERAL, networkId);
		this.width = engine.getTileW();
		this.height = engine.getTileH();
		this.green = new Color(0, 200, 0);

		this.lot = new Animation();
		this.lot.setLooping(false);
		this.normal = new Animation();
		this.normal.setLooping(false);
		this.few = new Animation();
		this.few.setLooping(false);
		SpriteSheet ss = ResourceManager.getSpriteSheet("mineral");
		for (int i = 0; i < 6; i++) {
			lot.addFrame(ss.getSprite(i, 0), 200);
			normal.addFrame(ss.getSprite(i, 1), 200);
			few.addFrame(ss.getSprite(i, 2), 200);
		}
		lot.addFrame(ss.getSprite(1, 0), 200);
		normal.addFrame(ss.getSprite(1, 1), 200);
		few.addFrame(ss.getSprite(1, 2), 200);
		lot.setCurrentFrame(5);
		normal.setCurrentFrame(5);
		few.setCurrentFrame(5);
		timer = new Timer(5000);
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		engine.getMap().addEntityLocation(this, false, (int) x / engine.getTileW(), (int) y / engine.getTileH());
	}

	@Override
	public void remove() {
		engine.removeEntity(this);
		engine.getMap().removeEntityLocation(this, (int) x / engine.getTileW(), (int) y / engine.getTileH());
	}

	@Override
	public void renderEntity(GameContainer container, Graphics g) throws SlickException {
		if (life <= 60) {
			g.drawAnimation(few, x, y);
		} else {
			if (life <= 120) {
				g.drawAnimation(normal, x, y);
			} else {
				g.drawAnimation(lot, x, y);
			}
		}
	}

	@Override
	public void renderOnMiniMap(Graphics g, float x, float y, float tw, float th) {
		g.setColor(Color.green);
		g.fillRect(x, y, tw, th);
	}

	@Override
	public void updateEntity(GameContainer container, int delta) throws SlickException {
		green.a = ((life * 255) / maxLife) / 255f;
		timer.update(delta);
		if (RANDOM.nextInt(7000) == 3500 && timer.isTimeComplete()) {
			this.lot.restart();
			this.normal.restart();
			this.few.restart();
			timer.resetTime();
		}
	}

}
