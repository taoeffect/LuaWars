package rts.core.engine.layers.entities.buildings;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import rts.core.engine.Engine;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.EData;
import rts.core.engine.layers.entities.effects.Explosion;
import rts.utils.ResourceManager;

public class SpyRadar extends Building {

	private Animation animation;
	private Image destroy;

	public SpyRadar(Engine engine, int playerId, int teamId, int networkId) {
		super(engine, EData.BUILDING_SPYRADAR, true, playerId, teamId, networkId);
		SpriteSheet ss = ResourceManager.getSpriteSheet("shield_"+engine.getPlayer(playerId).getColor());
		width = ss.getSprite(0, 0).getWidth();
		height = ss.getSprite(0, 0).getHeight();

		animation = new Animation();
		animation.setLooping(false);

		for (int i = 0; i < 5; i++) {
			animation.addFrame(ss.getSprite(i, 0), 150);
		}

		destroy = ss.getSprite(5, 0);

		calcViewLimit(width / 40, height / 40);
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		if (engine.isPlayerEntity(playerId))
			engine.getMap().setVisibleFow(false);
	}

	@Override
	public void removeBuilding() {
		engine.removeEntity(this);
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.BIG, x, y));
		if (engine.isPlayerEntity(playerId))
			engine.getMap().setVisibleFow(true);
	}

	@Override
	public void renderBuilding(GameContainer container, Graphics g) throws SlickException {
		if (weak || dying) {
			g.drawImage(destroy, x, y);
		} else {
			g.drawAnimation(animation, x, y);
		}
	}

}