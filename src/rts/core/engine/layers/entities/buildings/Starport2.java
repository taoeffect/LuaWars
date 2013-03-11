package rts.core.engine.layers.entities.buildings;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import rts.core.engine.Engine;
import rts.core.engine.PlayerInput;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.EData;
import rts.core.engine.layers.entities.effects.Explosion;
import rts.utils.ResourceManager;

public class Starport2 extends BuildingECreator {

	private Animation animation;
	private Image destroy;

	public Starport2(Engine engine, int playerId, int teamId, int networkId) {
		super(engine, EData.BUILDING_STARPORT_2, true, playerId, teamId, networkId);
		SpriteSheet ss = ResourceManager.getSpriteSheet("starport2_"+engine.getPlayer(playerId).getColor());
		width = ss.getSprite(0, 0).getWidth();
		height = ss.getSprite(0, 0).getHeight();

		animation = new Animation();

		for (int i = 0; i < 5; i++) {
			animation.addFrame(ss.getSprite(i, 0), 150);
		}

		destroy = ss.getSprite(5, 0);

		calcViewLimit(width / 40, height / 40);
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		engine.getGui().increaseBuildLimit(3, 1);
	}

	@Override
	public int getTargetCursor(ActiveEntity target, int mx, int my) {
		if (target == null && !engine.getMap().isBlocked(engine.getMouseX()/engine.getTileW(), engine.getMouseY()/engine.getTileH()))
			return PlayerInput.CURSOR_SPECIAL_ACTION;
		else
			return PlayerInput.CURSOR_NO_ACTION;
	}

	@Override
	public void removeBuilding() {
		engine.getGui().decreaseBuildLimit(3, 1);
		engine.removeEntity(this);
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.BIG, x, y + 20));
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
			engine.getGui().increaseBuildLimit(3, 1);
		} else {
			if (engine.isPlayerEntity(this.playerId) && this.playerId != playerId) {
				engine.getGui().decreaseBuildLimit(3, 1);
			}
		}
		super.setPlayerId(playerId);
	}

}
