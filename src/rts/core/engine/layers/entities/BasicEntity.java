package rts.core.engine.layers.entities;

import rts.core.engine.Engine;

public abstract class BasicEntity implements IEntity {

	protected Engine engine;
	protected int layer;
	protected int width;
	protected int height;
	protected float x;
	protected float y;

	public BasicEntity(Engine engine, int layer) {
		this.engine = engine;
		this.layer = layer;
	}

	public void setLocation(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public boolean onEntity(int mx, int my) {
		return mx >= x && mx <= x + width && my >= y && my <= y + height;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getLayer() {
		return layer;
	}

	@Override
	public int getWidth() {
		return width;
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
