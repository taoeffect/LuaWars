package rts.core.engine.layers.entities.buildings;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import rts.core.engine.Engine;
import rts.core.engine.PlayerInput;
import rts.core.engine.Utils;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.EData;
import rts.core.engine.layers.entities.MoveableEntity;
import rts.core.engine.layers.entities.effects.Explosion;
import rts.core.engine.layers.entities.projectiles.Bullet;
import rts.core.engine.layers.entities.vehicles.Mover;
import rts.core.network.ig_udp_containers.EntityState;
import rts.utils.ResourceManager;
import rts.utils.Timer;

public class Artillery extends Building {

	private Animation normal;
	private Animation fire;

	private Timer shootTimer;
	private Timer changeFrameTimer;

	public Artillery(Engine engine, int playerId, int teamId, int networkId) {
		super(engine, EData.BUILDING_ARTILLERY, true, playerId, teamId, networkId);
		
		SpriteSheet ss = ResourceManager.getSpriteSheet("artillery_"+engine.getPlayer(playerId).getColor());
		width = ss.getSprite(0, 0).getWidth();
		height = ss.getSprite(0, 0).getHeight();

		normal = new Animation(false);
		fire = new Animation(false);

		for (int i = 0; i < 8; i++) {
			normal.addFrame(ss.getSprite(i, 0), 150);
			fire.addFrame(ss.getSprite(i, 1), 150);
		}
		shootTimer = new Timer(EData.ARTILLERY_SHOOT_INTERVAL);
		changeFrameTimer = new Timer(100);

		calcViewLimit(width / 40, height / 40);
	}

	@Override
	public int getTargetCursor(ActiveEntity target, int mx, int my) {
		if (target != null && (target.getTeamId() != teamId) && target instanceof Mover && ((Mover) target).getMoveType() == MoveableEntity.EVERYWHERE)
			return PlayerInput.CURSOR_ATTACK;
		else
			return PlayerInput.CURSOR_NO_ACTION;
	}

	@Override
	public void removeBuilding() {
		engine.removeEntity(this);
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.BIG, x, y));
	}

	@Override
	public void renderBuilding(GameContainer container, Graphics g) throws SlickException {
		if (!changeFrameTimer.isTimeComplete()) {
			g.drawImage(fire.getImage(direction), x, y);
		} else {
			g.drawImage(normal.getImage(direction), x, y);
		}
	}

	@Override
	public void updateEntity(GameContainer container, int delta) throws SlickException {
		super.updateEntity(container, delta);
		if (engine.isPlayerEntity(playerId)) {
			shootTimer.update(delta);
			changeFrameTimer.update(delta);
			if (shootTimer.isTimeComplete()) {
				state.timer1Complete = true;
				ActiveEntity ae = engine.getFirstEnemyEntity(this, view);
				if (ae != null && ae instanceof Mover && ((Mover) ae).getMoveType() == MoveableEntity.EVERYWHERE) {
					changeFrameTimer.resetTime();
					direction = Utils.findDirection(x, y, ae.getX(), ae.getY());
					engine.addEntity(new Bullet(engine, this, ae, EData.ARTILLERY_BULLET_TYPE, 15, 15));
				}
				shootTimer.resetTime();
			} else
				state.timer1Complete = false;
		}
	}

	public float getBulletSpeed() {
		return EData.ARTILLERY_BULLET_SPEED;
	}

	public int getBulletPower() {
		return EData.ARTILLERY_BULLET_POWER;
	}

	@Override
	public void setState(EntityState state) {
		super.setState(state);

		if (state.timer1Complete) {
			ActiveEntity ae = engine.getFirstEnemyEntity(this, view);
			if (ae != null) {
				changeFrameTimer.resetTime();
				direction = Utils.findDirection(x, y, ae.getX(), ae.getY());
				engine.addEntity(new Bullet(engine, this, ae, EData.ARTILLERY_BULLET_TYPE, 15, 15));
			}
		}

	}

}
