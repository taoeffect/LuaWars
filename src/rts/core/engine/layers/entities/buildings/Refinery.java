package rts.core.engine.layers.entities.buildings;

import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import rts.core.engine.Engine;
import rts.core.engine.Utils;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.EData;
import rts.core.engine.layers.entities.effects.Explosion;
import rts.utils.ResourceManager;

public class Refinery extends Building {

	private static final int[][] DROP_LOCATIONS = new int[][] { { 40, 20 }, { 0, 40 }, { 20, 40 }, { -20, 20 }, };
	private static final int[] DIRECTION = new int[] { Utils.LEFT, Utils.UP, Utils.UP, Utils.RIGHT };

	private static ArrayList<Refinery> allPlayerRefinery = new ArrayList<Refinery>();

	private Animation animation;
	private Image destroy;
	private boolean[] freeLocations = new boolean[] { true, true, true, true };

	public Refinery(Engine engine, int playerId, int teamId, int networkId) {
		super(engine, EData.BUILDING_REFINERY, true, playerId, teamId, networkId);
		SpriteSheet ss = ResourceManager.getSpriteSheet("refinery_" + engine.getPlayer(playerId).getColor());
		width = ss.getSprite(0, 0).getWidth();
		height = ss.getSprite(0, 0).getHeight();

		animation = new Animation();

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 6; j++) {
				if (i == 2 && j == 5) {
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
			allPlayerRefinery.add(this);
		}
	}

	@Override
	public void removeBuilding() {
		engine.removeEntity(this);
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.BIG, x, y));
		if (engine.isPlayerEntity(playerId)) {
			engine.getPlayer().decreaseMaxMoney();
			allPlayerRefinery.remove(this);
		}
	}

	@Override
	public void setPlayerId(int playerId) {
		if (engine.isPlayerEntity(playerId)) {
			engine.getPlayer().increaseMaxMoney();
			allPlayerRefinery.add(this);
		} else {
			if (engine.isPlayerEntity(this.playerId) && this.playerId != playerId) {
				engine.getPlayer().decreaseMaxMoney();
				allPlayerRefinery.remove(this);
			}
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

	public DropLocation getFreeLocation() {
		for (int i = 0; i < freeLocations.length; i++) {
			if (freeLocations[i]) {
				freeLocations[i] = false;
				DropLocation dl = new DropLocation();
				dl.x = (int) x + DROP_LOCATIONS[i][0];
				dl.y = (int) y + DROP_LOCATIONS[i][1];
				dl.direction = DIRECTION[i];
				return dl;
			}
		}
		return null;
	}

	public void freeLocation(int x, int y) {
		for (int i = 0; i < freeLocations.length; i++) {
			if ((int) this.x + DROP_LOCATIONS[i][0] == x && (int) this.y + DROP_LOCATIONS[i][1] == y) {
				freeLocations[i] = true;
				break;
			}
		}
	}

	public boolean hasOneFreeLocation() {
		for (int i = 0; i < freeLocations.length; i++) {
			if (freeLocations[i])
				return true;
		}
		return false;
	}

	// Static

	public static Refinery getCloserFreePlayerRefinery(float x, float y) {
		Refinery refinery = null;
		float dist = -1;
		for (int i = 0; i < allPlayerRefinery.size(); i++) {
			Refinery r = allPlayerRefinery.get(i);
			if (r.hasOneFreeLocation()) {
				if (dist == -1) {
					refinery = r;
					dist = Utils.getDistanceBetween(x, y, r.getRealX(), r.getRealY());
				} else {
					if (Utils.getDistanceBetween(x, y, r.getRealX(), r.getRealY()) < dist) {
						refinery = r;
						dist = Utils.getDistanceBetween(x, y, r.getRealX(), r.getRealY());
					}
				}
			}
		}

		return refinery;
	}

	public static class DropLocation {

		public int x;
		public int y;
		public int direction;

	}
}