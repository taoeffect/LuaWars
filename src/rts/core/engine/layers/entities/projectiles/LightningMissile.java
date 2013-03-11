package rts.core.engine.layers.entities.projectiles;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import rts.core.engine.Engine;
import rts.core.engine.Utils;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.BasicEntity;
import rts.core.engine.layers.entities.EData;
import rts.core.engine.layers.entities.effects.Explosion;
import rts.utils.ResourceManager;

public class LightningMissile extends BasicEntity {

	private Animation animation;
	private int playerId;
	private float ang;
	private float dirX;
	private float dirY;
	private float speed;
	private float tx;
	private float ty;

	public LightningMissile(Engine engine, int playerId, float x, float y, float tx, float ty) {
		super(engine, Layer.SECOND_EFFECT);
		this.playerId = playerId;
		this.tx = tx;
		this.ty = ty;
		speed = EData.LIGHTNING_MISSILE_SPEED;
		animation = new Animation();
		animation.setLooping(false);

		SpriteSheet ss = ResourceManager.getSpriteSheet("bullets");
		for (int i = 0; i < 3; i++) {
			animation.addFrame(ss.getSprite(13 + i, 0), 200);
		}

		this.ang = Utils.getTargetAngle(x, y, tx, ty);
		this.dirX = (float) Math.sin(Math.toRadians(ang));
		this.dirY = (float) -Math.cos(Math.toRadians(ang));
		this.x = x + (dirX * 35);
		this.y = y + (dirY * 35);
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.rotate(x + 10, y + 10, ang);
		g.drawAnimation(animation, x, y);
		g.rotate(x + 10, y + 10, -ang);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		float nextX = (dirX * speed * delta) + x;
		float nextY = (dirY * speed * delta) + y;
		if (Utils.getDistanceBetween(nextX, nextY, tx, ty) < 5) {
			dropEffect();
		} else {
			x = nextX;
			y = nextY;
		}
	}

	private void dropEffect() {
		ActiveEntity ae = engine.getEntityAt(null, tx, ty);
		if (ae != null) {
			if (engine.isPlayerEntity(playerId))
				ae.removeLife(500);
		}
		for (float i = ty; i > 0; i -= 80) {
			engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.LIGHTNING, tx, i));
		}
		engine.removeEntity(this);
	}

}
