package rts.core.engine.layers.entities.buildings;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import rts.core.engine.Engine;
import rts.core.engine.PlayerInput;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.EData;
import rts.core.engine.layers.entities.effects.Explosion;
import rts.utils.ResourceManager;

public class BigConstructor extends BuildingECreator {

	private Animation[] animations;

	public BigConstructor(Engine engine, int playerId, int teamId, int networkId) {
		super(engine, EData.BUILDING_BIG_CONSTRUCTOR, true, playerId, teamId, networkId);
		SpriteSheet ss = ResourceManager.getSpriteSheet("constructor_"+engine.getPlayer(playerId).getColor());
		width = ss.getSprite(0, 0).getWidth() * 2;
		height = ss.getSprite(0, 0).getHeight() * 2;

		animations = new Animation[4];

		for (int k = 0; k < 4; k++) {
			animations[k] = new Animation();
			animations[k].setLooping(false);

			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 7; j++) {
					animations[k].addFrame(ss.getSprite(j, i), 50);
				}
			}
			animations[k].addFrame(ss.getSprite(0, 4), 50);
			animations[k].addFrame(ss.getSprite(1, 4), 50);
		}

		calcViewLimit(width / 40, height / 40);
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		if (engine.isPlayerEntity(playerId))
			engine.getGui().increaseBuildLimit(2, 4);
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
		if (engine.isPlayerEntity(playerId))
			engine.getGui().decreaseBuildLimit(2, 4);
		engine.removeEntity(this);
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.BIG, x, y));
	}

	@Override
	public void renderBuilding(GameContainer container, Graphics g) throws SlickException {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				g.drawAnimation(animations[i + j], x + i * engine.getTileW(), y + j * engine.getTileH());
			}
		}
	}

	@Override
	public void setPlayerId(int playerId) {
		if (engine.isPlayerEntity(playerId)) {
			engine.getGui().increaseBuildLimit(2, 4);
		} else {
			if (engine.isPlayerEntity(this.playerId) && this.playerId != playerId)
				engine.getGui().decreaseBuildLimit(2, 4);
		}
		super.setPlayerId(playerId);
	}
}
