package rts.core.engine.layers.entities.effects;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import rts.core.engine.Engine;
import rts.core.engine.GameSound;
import rts.core.engine.layers.entities.BasicEntity;
import rts.utils.ResourceManager;

public class Explosion extends BasicEntity {

	public static final int BLOOD = 0;
	public static final int SMALL_1 = 1;
	public static final int SMALL_2 = 2;
	public static final int NORMAL_1 = 3;
	public static final int NORMAL_2 = 4;
	public static final int BIG = 5;
	public static final int ABOMB = 6;
	public static final int LIGHTNING = 7;

	private Animation explosion;
	private int type;

	public Explosion(Engine engine, int layer, int type, float x, float y) {
		super(engine, layer);
		this.type = type;
		this.x = x;
		this.y = y;

		explosion = new Animation();
		explosion.setLooping(false);

		if (type == LIGHTNING) {
			SpriteSheet ss = ResourceManager.getSpriteSheet("lightning");
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 4; j++) {
					explosion.addFrame(ss.getSprite(j, 0), 100);
				}
			}
		} else {
			SpriteSheet ss = ResourceManager.getSpriteSheet("explosion" + type);
			for (int j = 0; j < ss.getVerticalCount(); j++) {
				for (int i = 0; i < ss.getHorizontalCount(); i++) {
					explosion.addFrame(ss.getSprite(i, j), 100);
				}
			}
		}
		if (type >= NORMAL_1 && type <= ABOMB)
			GameSound.explosion();
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		if (type == LIGHTNING) {
			g.drawAnimation(explosion, x - 54, y - 100);
		} else
			g.drawAnimation(explosion, x, y);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		if (explosion.isStopped()) {
			engine.removeEntity(this);
		}
	}

}
