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
import rts.core.network.ig_udp_containers.EntityState;
import rts.utils.ResourceManager;

public class Builder extends Building {

	private Animation construct;
	private Animation normal;
	private Image destroy;

	private int moneyState; // Max 6 [0 - 6]

	public Builder(Engine engine, int playerId, int teamId, int networkId) {
		super(engine, EData.BUILDING_BUILDER, true, playerId, teamId, networkId);
		SpriteSheet ss = ResourceManager.getSpriteSheet("builder_"+engine.getPlayer(playerId).getColor());
		width = ss.getSprite(0, 0).getWidth();
		height = ss.getSprite(0, 0).getHeight();

		construct = new Animation();
		construct.setLooping(false);

		normal = new Animation(false);
		normal.setLooping(false);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 8; j++) {
				construct.addFrame(ss.getSprite(j, i), 150);
			}
		}

		for (int i = 0; i < 8; i++) {
			if (i > 2) {
				normal.addFrame(ss.getSprite(i, 3), 150);
			} else {
				construct.addFrame(ss.getSprite(i, 3), 150);
			}
		}

		normal.addFrame(ss.getSprite(0, 4), 150);
		normal.addFrame(ss.getSprite(1, 4), 150);
		destroy = ss.getSprite(2, 4);

		calcViewLimit(width / 40, height / 40);

	}

	@Override
	public void updateEntity(GameContainer container, int delta) throws SlickException {
		super.updateEntity(container, delta);
		if (engine.isPlayerEntity(playerId)) {
			// Set the state money
			if (engine.getPlayer().getMoney() >= engine.getPlayer().getMaxMoney())
				moneyState = 6;
			else
				moneyState = (int) ((engine.getPlayer().getMoney() * 6) / engine.getPlayer().getMaxMoney());
		}
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		if (engine.isPlayerEntity(playerId)) {
			engine.getGui().addEntityToBuildingList(type);
			engine.getGui().increaseBuildLimit(0, 1);
			engine.getGui().increaseBuildLimit(1, 1);
		}
	}

	@Override
	public void removeBuilding() {
		if (engine.isPlayerEntity(playerId)) {
			engine.getGui().decreaseBuildLimit(0, 1);
			engine.getGui().decreaseBuildLimit(1, 1);
		}
		engine.removeEntity(this);
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.BIG, x, y));
	}

	@Override
	public void setPlayerId(int playerId) {
		if (engine.isPlayerEntity(playerId)) {
			engine.getGui().addEntityToBuildingList(type);
			engine.getGui().increaseBuildLimit(0, 1);
			engine.getGui().increaseBuildLimit(1, 1);
		} else {
			if (engine.isPlayerEntity(this.playerId) && this.playerId != playerId) {
				engine.getGui().decreaseBuildLimit(0, 1);
				engine.getGui().decreaseBuildLimit(1, 1);
				engine.getGui().removeEntityFromBuildingList(type);
			}
		}
		super.setPlayerId(playerId);
	}

	@Override
	public void renderBuilding(GameContainer container, Graphics g) throws SlickException {
		if (weak || dying) {
			g.drawImage(destroy, x, y);
		} else {
			if (construct.isStopped()) {
				g.drawImage(normal.getImage(moneyState), x, y);
			} else
				g.drawAnimation(construct, x, y);
		}
	}

	@Override
	public EntityState getState() {
		state.moneyState = moneyState;
		return super.getState();
	}

	@Override
	public void setState(EntityState state) {
		super.setState(state);
		this.moneyState = state.moneyState;
	}

}