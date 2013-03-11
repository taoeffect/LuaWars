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

public class Radar extends Building {

	private Animation animation;
	private Image destroy;

	public Radar(Engine engine, int playerId, int teamId, int networkId) {
		super(engine, EData.BUILDING_RADAR, true, playerId, teamId, networkId);
		SpriteSheet ss = ResourceManager.getSpriteSheet("radar_"+engine.getPlayer(playerId).getColor());
		width = ss.getSprite(0, 0).getWidth();
		height = ss.getSprite(0, 0).getHeight();

		animation = new Animation();

		for (int i = 0; i < 8; i++) {
			animation.addFrame(ss.getSprite(i, 0), 150);
		}
		for (int i = 0; i < 7; i++) {
			animation.addFrame(ss.getSprite(i, 1), 150);
		}

		destroy = ss.getSprite(7, 1);

		calcViewLimit(width / 40, height / 40);
	}


	@Override
	public void removeBuilding() {
		engine.removeEntity(this);
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.BIG, x, y));
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
