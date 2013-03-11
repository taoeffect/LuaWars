package rts.utils;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import rts.core.engine.Engine;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.IEntity;

public class MoveUpEffect implements IEntity {

	private Engine engine;
	private float x;
	private float y;
	private String text;
	private Color color;
	private Timer timer;

	public MoveUpEffect(Engine engine, float x, float y, String text, Color color, int time) {
		this.engine = engine;
		this.x = x;
		this.y = y;
		this.text = text;
		this.color = color;
		this.timer = new Timer(time);
	}

	@Override
	public void update(GameContainer container, int delta) {
		timer.update(delta);
		if (timer.isTimeComplete()) {
			this.y -= 3;
			this.color.a -= 0.05f;
			this.timer.resetTime();
		}
		if (color.a < 0.1f) {
			engine.removeEntity(this);
		}
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.setColor(color);
		g.drawString(text, x, y);
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int getLayer() {
		return Layer.FIRST_EFFECT;
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

}
