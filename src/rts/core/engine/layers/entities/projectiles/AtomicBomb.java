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

public class AtomicBomb extends BasicEntity {

	private Animation animation;
	private int playerId;
	private float ang;
	private float dirX;
	private float dirY;
	private float speed;
	private float tx;
	private float ty;

	public AtomicBomb(Engine engine, int playerId, float x, float y, float tx, float ty) {
		super(engine, Layer.SECOND_EFFECT);
		this.playerId = playerId;
		this.x = x;
		this.y = y;
		this.tx = tx;
		this.ty = ty;
		this.speed = EData.ABOMB_SPEED;
		this.animation = new Animation();

		SpriteSheet ss = ResourceManager.getSpriteSheet("amissile");
		for (int i = 0; i < 4; i++) {
			animation.addFrame(ss.getSprite(i, 0), 150);
		}

		this.dirX = (float) Math.sin(Math.toRadians(ang));
		this.dirY = (float) -Math.cos(Math.toRadians(ang));
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
			dropBomb();
		} else {
			x = nextX;
			y = nextY;

			if (y < -120) {
				this.y = -120;
				this.x = tx;
				this.ang = 180;
				this.dirX = (float) Math.sin(Math.toRadians(ang));
				this.dirY = (float) -Math.cos(Math.toRadians(ang));
			}
		}
	}

	private void dropBomb() {
		float sx = tx - 60;
		float sy = ty - 60;
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				ActiveEntity ae = engine.getEntityAt(null, sx + i * 20, sy + j * 20);
				if (ae != null) {
					if (engine.isPlayerEntity(playerId))
						ae.removeLife(200);
				}
			}
		}

		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.ABOMB, sx, sy));
		engine.removeEntity(this);
	}

}
