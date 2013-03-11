package rts.core.engine.layers.entities.projectiles;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import rts.core.engine.Engine;
import rts.core.engine.GameSound;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.EData;
import rts.core.engine.layers.entities.buildings.Artillery;
import rts.core.engine.layers.entities.buildings.Turret;
import rts.core.engine.layers.entities.effects.Explosion;
import rts.utils.ResourceManager;

public class Bullet extends Projectile {

	private Image bullet;

	public Bullet(Engine engine, ActiveEntity owner, ActiveEntity target, int type, int decX, int decY) {
		super(engine, owner, target, Layer.EARTH_MARINE_ENT, decX, decY);
		this.bullet = ResourceManager.getSpriteSheet("bullets").getSprite(type, 0);
		if (owner instanceof Artillery) {
			this.speed = ((Artillery) owner).getBulletSpeed();
			this.power = ((Artillery) owner).getBulletPower();
		} else {
			if (owner instanceof Turret) {
				this.speed = ((Turret) owner).getBulletSpeed();
				this.power = ((Turret) owner).getBulletPower();
			} else {
				this.speed = EData.BULLET_SPEED[owner.getType()];
				this.power = EData.BULLET_POWER[owner.getType()][target.getType()];
			}
		}
		GameSound.shoot();
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.rotate(x + 10, y + 10, ang);
		g.drawImage(bullet, x, y);
		g.rotate(x + 10, y + 10, -ang);
	}

	@Override
	protected void destroy() {
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.SMALL_2, x - 10, y - 10));
	}

}
