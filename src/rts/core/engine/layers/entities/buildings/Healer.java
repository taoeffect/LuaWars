package rts.core.engine.layers.entities.buildings;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
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

public class Healer extends Building {

	private static final int HEAL_TIME = 1000;
	private static final int LIFE_BONUS = 5;

	private Animation animation;
	private Timer timer;
	private Mover mover;

	public Healer(Engine engine, int playerId, int teamId, int networkId) {
		super(engine, EData.BUILDING_HEALER, false, playerId, teamId, networkId);
		SpriteSheet ss = ResourceManager.getSpriteSheet("healer_"+engine.getPlayer(playerId).getColor());
		width = ss.getSprite(0, 0).getWidth();
		height = ss.getSprite(0, 0).getHeight();

		animation = new Animation();

		for (int i = 0; i < 8; i++) {
			animation.addFrame(ss.getSprite(i, 0), 150);
		}

		timer = new Timer(HEAL_TIME);

		calcViewLimit(width / 40, height / 40);
	}

	@Override
	public void removeBuilding() {
		if (mover != null) {
			mover.cancelHealer();
		}
		engine.removeEntity(this);
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.NORMAL_2, x, y));
	}

	@Override
	public void renderBuilding(GameContainer container, Graphics g) throws SlickException {
		g.drawAnimation(animation, x, y);
	}

	@Override
	public void updateEntity(GameContainer container, int delta) throws SlickException {
		super.updateEntity(container, delta);
		if (engine.isPlayerEntity(playerId)) {
			timer.update(delta);
			if (timer.isTimeComplete()) {
				ActiveEntity ae = engine.getEntityAt(this, (int) x, (int) y);
				if (ae != null && this.teamId == ae.getTeamId()) {
					ae.addLife(LIFE_BONUS);
				}
				timer.resetTime();
			}

			if (mover != null) {
				if (!mover.isAlive() || !mover.isGoingToHealer()) {
					mover = null;
				}
			}
		}
	}

	public void reserved(Mover m) {
		this.mover = m;
	}

	public boolean isReserved() {
		return mover != null;
	}

}
