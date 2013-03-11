package rts.core.engine.layers.entities.projectiles;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

import rts.core.engine.Engine;
import rts.core.engine.Utils;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.BasicEntity;
import rts.core.engine.layers.entities.IBigEntity;
import rts.core.engine.layers.entities.buildings.Turret;

public abstract class Projectile extends BasicEntity {

	private int playerId;
	protected int power;
	protected float tx;
	protected float ty;
	protected float ang;
	protected float dirX;
	protected float dirY;
	protected float speed;
	protected ActiveEntity target;

	public Projectile(Engine engine, ActiveEntity owner, ActiveEntity target, int layer, int decX, int decY) {
		super(engine, layer);
		this.playerId = owner.getPlayerId();
		this.target = target;
		this.tx = getTargetX();
		this.ty = getTargetY();
		if (owner instanceof Turret) {
			this.ang = Utils.getTargetAngle(owner.getX() + 20, owner.getY() + 20, tx, ty);
		} else
			this.ang = Utils.getTargetAngle(owner.getX(), owner.getY(), tx, ty);
		this.dirX = (float) Math.sin(Math.toRadians(ang));
		this.dirY = (float) -Math.cos(Math.toRadians(ang));
		if (owner instanceof Turret) {
			this.x = owner.getX() + 20 + (dirX * decX);
			this.y = owner.getY() + 20 + (dirY * decY);
		} else {
			this.x = owner.getX() + (dirX * decX);
			this.y = owner.getY() + (dirY * decY);
		}
	}

	private float getTargetX() {
		return (target instanceof IBigEntity) ? ((IBigEntity) target).getRealX() : target.getX() + (engine.getTileW() / 2);
	}

	private float getTargetY() {
		return (target instanceof IBigEntity) ? ((IBigEntity) target).getRealY() : target.getY() + (engine.getTileH() / 2);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		float nextX = (dirX * speed * delta) + x;
		float nextY = (dirY * speed * delta) + y;
		if (Utils.getDistanceBetween(nextX, nextY, tx, ty) < 5) {
			if (Utils.getDistanceBetween(nextX, nextY, getTargetX(), getTargetY()) < 5) {
				if (target.getPlayerId() == -1) {
					target.removeLife(power);
					engine.getNetworkManager().sendUpdateNPEntity(target.getState());
				} else {
					if (!engine.isPlayerEntity(playerId)) {
						target.removeLife(power);
					}
				}
			}
			destroy();
			engine.removeEntity(this);
		} else {
			x = nextX;
			y = nextY;
		}
	}

	protected abstract void destroy();

}
