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

public class Storage extends Building {

	private Animation animation;
	private Image destroy;

	public Storage(Engine engine, int playerId, int teamId, int networkId) {
		super(engine, EData.BUILDING_STORAGE, true, playerId, teamId, networkId);
		SpriteSheet ss = ResourceManager.getSpriteSheet("storage_"+engine.getPlayer(playerId).getColor());
		width = ss.getSprite(0, 0).getWidth();
		height = ss.getSprite(0, 0).getHeight();

		animation = new Animation();
		animation.setLooping(false);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 5; j++) {
				if (i == 2 && j == 4) {
					destroy = ss.getSprite(j, i);
				} else
					animation.addFrame(ss.getSprite(j, i), 150);
			}
		}

		calcViewLimit(width / 40, height / 40);
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		if (engine.isPlayerEntity(playerId)) {
			engine.getPlayer().increaseMaxMoney();
		}
	}

	@Override
	public void removeBuilding() {
		engine.removeEntity(this);
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.BIG, x, y));
		if (engine.isPlayerEntity(playerId)) {
			engine.getPlayer().decreaseMaxMoney();
		}
	}

	@Override
	public void renderBuilding(GameContainer container, Graphics g) throws SlickException {
		if (weak || dying) {
			g.drawImage(destroy, x, y);
		} else {
			g.drawAnimation(animation, x, y);
		}
	}

	@Override
	public void setPlayerId(int playerId) {
		if (engine.isPlayerEntity(playerId)) {
			engine.getPlayer().increaseMaxMoney();
		} else {
			if (engine.isPlayerEntity(this.playerId) && this.playerId != playerId) {
				engine.getPlayer().decreaseMaxMoney();
			}
		}
		super.setPlayerId(playerId);
	}

}