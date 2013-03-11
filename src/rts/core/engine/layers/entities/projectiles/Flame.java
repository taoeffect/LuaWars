package rts.core.engine.layers.entities.projectiles;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import rts.core.engine.Engine;
import rts.core.engine.GameSound;
import rts.core.engine.Utils;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.BasicEntity;
import rts.core.engine.layers.entities.EData;
import rts.utils.ResourceManager;

public class Flame extends BasicEntity {

	private Animation flame;
	private float ang;

	public Flame(Engine engine, ActiveEntity owner, ActiveEntity target) {
		super(engine, Layer.EARTH_MARINE_ENT);

		this.ang = Utils.ANGLES[owner.getDirection()];

		this.x = owner.getX() + ((float) Math.sin(Math.toRadians(ang)) * 25);
		this.y = owner.getY() + ((float) -Math.cos(Math.toRadians(ang)) * 25);

		this.flame = new Animation();
		this.flame.setLooping(false);

		SpriteSheet ss = ResourceManager.getSpriteSheet("flamelauncher");
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 5; j++) {
				flame.addFrame(ss.getSprite(i, j), 50);
			}
		}

		if (target.getPlayerId() == -1) {
			target.removeLife(EData.BULLET_POWER[owner.getType()][target.getType()]);

			engine.getNetworkManager().sendUpdateNPEntity(target.getState());
		} else {
			if (!engine.isPlayerEntity(owner.getPlayerId())) {
				target.removeLife(EData.BULLET_POWER[owner.getType()][target.getType()]);
			}
		}
		GameSound.fl();
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.rotate(x + 10, y + 15, ang);
		g.drawAnimation(flame, x, y);
		g.rotate(x + 10, y + 15, -ang);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		if (flame.isStopped()) {
			engine.removeEntity(this);
		}
	}

}
