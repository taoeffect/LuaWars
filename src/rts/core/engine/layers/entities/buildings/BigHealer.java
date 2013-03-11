package rts.core.engine.layers.entities.buildings;

import java.awt.Point;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import rts.core.engine.Engine;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.EData;
import rts.core.engine.layers.entities.effects.Explosion;
import rts.core.engine.layers.entities.vehicles.Mover;
import rts.utils.ResourceManager;
import rts.utils.Timer;

public class BigHealer extends Building {

	private static final int HEAL_TIME = 500;
	private static final int LIFE_BONUS = 10;

	private Animation animation;
	private Image destroy;
	private Timer timer;

	private BHLocation[] locations;

	public BigHealer(Engine engine, int playerId, int teamId, int networkId) {
		super(engine, EData.BUILDING_BIG_HEALER, false, playerId, teamId, networkId);
		SpriteSheet ss = ResourceManager.getSpriteSheet("bighealer_"+engine.getPlayer(playerId).getColor());
		width = ss.getSprite(0, 0).getWidth();
		height = ss.getSprite(0, 0).getHeight();

		animation = new Animation();

		for (int i = 0; i < 7; i++) {
			animation.addFrame(ss.getSprite(i, 0), 300);
		}

		destroy = ss.getSprite(7, 0);

		timer = new Timer(HEAL_TIME);

		calcViewLimit(width / 40, height / 40);

		locations = new BHLocation[4];
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		int tx = (int) (x / engine.getTileW());
		int ty = (int) (y / engine.getTileH());

		locations[0] = new BHLocation(tx, ty);
		locations[1] = new BHLocation(tx + 1, ty);
		locations[2] = new BHLocation(tx, ty + 1);
		locations[3] = new BHLocation(tx + 1, ty + 1);
	}

	@Override
	public void removeBuilding() {
		for (int i = 0; i < locations.length; i++) {
			locations[i].free();
		}
		engine.removeEntity(this);
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.BIG, x, y));
	}

	@Override
	public void renderBuilding(GameContainer container, Graphics g) throws SlickException {
		if (weak || dying) {
			g.drawImage(destroy, x, y);
		} else
			g.drawAnimation(animation, x, y);
	}

	@Override
	public void updateEntity(GameContainer container, int delta) throws SlickException {
		super.updateEntity(container, delta);
		timer.update(delta);
		if (timer.isTimeComplete()) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					ActiveEntity ae = engine.getEntityAt(this, (int) x + (i * engine.getTileW()), (int) y + (j * engine.getTileH()));
					if (ae != null && this.teamId == ae.getTeamId()) {
						ae.addLife(LIFE_BONUS);
					}
				}
			}
			timer.resetTime();
		}

		for (int i = 0; i < locations.length; i++) {
			Mover mover = locations[i].getMover();
			if (mover != null) {
				if (!mover.isAlive() || !mover.isGoingToBigHealer()) {
					locations[i].free();
				}
			}
		}

	}

	public Point getFreeLocation(Mover mover) {
		for (int i = 0; i < locations.length; i++) {
			if (locations[i].isFree()) {
				locations[i].setMover(mover);
				return locations[i].getLocation();
			}
		}
		return null;
	}

	private static class BHLocation {

		private Point p;
		private Mover mover;

		public BHLocation(int x, int y) {
			p = new Point(x, y);
		}

		public void setMover(Mover mover) {
			this.mover = mover;
		}

		public Mover getMover() {
			return mover;
		}

		public boolean isFree() {
			return mover == null;
		}

		public void free() {
			if (mover != null)
				mover.cancelBigHealer();
			mover = null;
		}

		public Point getLocation() {
			return p;
		}
	}

}