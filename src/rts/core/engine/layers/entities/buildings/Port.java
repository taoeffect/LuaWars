package rts.core.engine.layers.entities.buildings;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import rts.core.engine.Engine;
import rts.core.engine.PlayerInput;
import rts.core.engine.Utils;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.EData;
import rts.core.engine.layers.entities.effects.Explosion;
import rts.utils.ResourceManager;

public class Port extends BuildingECreator {

	private Animation animation;
	private Image destroy;

	public Port(Engine engine, int playerId, int teamId, int networkId) {
		super(engine, EData.BUILDING_PORT, true, playerId, teamId, networkId);
		// Allow 3 distance for port
		distanceMaxBetweenBuilding = 300;
		SpriteSheet ss = ResourceManager.getSpriteSheet("port_" + engine.getPlayer(playerId).getColor());
		width = ss.getSprite(0, 0).getWidth();
		height = ss.getSprite(0, 0).getHeight();

		animation = new Animation();

		for (int i = 0; i < 7; i++) {
			animation.addFrame(ss.getSprite(i, 0), 300);
		}

		destroy = ss.getSprite(7, 0);

		calcViewLimit(width / 40, height / 40);
	}

	// Location only ok on water
	
	@Override
	protected void checkValidLocation(Graphics g, Building closer, int x, int y) {
		if (engine.getMap().isEntityOccupy(x, y) || engine.getMap().isBlocked(x, y) || !engine.getMap().isWater(x, y)
				|| (Utils.getDistanceBetween(x * engine.getTileW(), y * engine.getTileH(), closer.getRealX(), closer.getRealY()) > distanceMaxBetweenBuilding)) {
			g.setColor(FADE_RED);
			validLocation = false;
		} else {
			g.setColor(FADE_BLUE);
		}
	}
	
	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		if (engine.isPlayerEntity(playerId))
			engine.getGui().increaseBuildLimit(4, 1);
	}

	@Override
	public int getTargetCursor(ActiveEntity target, int mx, int my) {
		if (target == null && !engine.getMap().isBlocked(engine.getMouseX() / engine.getTileW(), engine.getMouseY() / engine.getTileH())
				&& engine.getMap().isWater(engine.getMouseX() / engine.getTileW(), engine.getMouseY() / engine.getTileH()))
			return PlayerInput.CURSOR_SPECIAL_ACTION;
		else
			return PlayerInput.CURSOR_NO_ACTION;
	}

	@Override
	public void changeRallyingPoint(int mx, int my) {
		if (onEntity(mx, my) || !engine.getMap().isWater(mx / engine.getTileW(), my / engine.getTileH())) {
			rp.x = -1;
			rp.y = -1;
		} else {
			if (!engine.getMap().isBlocked(mx / engine.getTileW(), my / engine.getTileH())) {
				rp.x = (mx / engine.getTileW()) * engine.getTileW();
				rp.y = (my / engine.getTileH()) * engine.getTileH();
			}
		}
	}

	@Override
	public void removeBuilding() {
		if (engine.isPlayerEntity(playerId))
			engine.getGui().decreaseBuildLimit(4, 1);
		engine.removeEntity(this);
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.BIG, x, y));
	}

	@Override
	public void setPlayerId(int playerId) {
		if (engine.isPlayerEntity(playerId)) {
			engine.getGui().increaseBuildLimit(4, 1);
		} else {
			if (engine.isPlayerEntity(this.playerId) && this.playerId != playerId)
				engine.getGui().decreaseBuildLimit(4, 1);
		}
		super.setPlayerId(playerId);
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