package rts.core.engine.layers.entities.buildings;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.gui.GUIContext;

import rts.core.engine.Engine;
import rts.core.engine.GameSound;
import rts.core.engine.Utils;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.EData;
import rts.core.engine.layers.entities.IBigEntity;
import rts.core.network.ig_udp_containers.EntityState;
import rts.utils.Colors;
import rts.utils.MoveUpEffect;
import rts.utils.ResourceManager;
import rts.utils.Timer;

public abstract class Building extends ActiveEntity implements IBigEntity {

	private static final int DEFAULT_DISTANCE_MAX_BETWEEN_BUILDINGS = 100;
	protected static final Color FADE_RED = new Color(255, 0, 0, 100);
	protected static final Color FADE_BLUE = new Color(0, 0, 255, 100);

	private Animation flameAnimation;
	private Point flameOne;
	private Point flameTwo;
	private Point flameThree;
	private Timer repairTimer;
	private Timer showRepairTimer;
	private boolean block;
	private boolean repair;

	// For building with a different behaviour
	protected boolean validLocation;
	protected int distanceMaxBetweenBuilding;

	public Building(Engine engine, int type, boolean block, int playerId, int teamId, int networkId) {
		super(engine, EData.TEC_LEVEL[type], Layer.FIRST_EFFECT, type, EData.MAX_LIFE[type], EData.VIEW[type], playerId, teamId, networkId);
		this.distanceMaxBetweenBuilding = DEFAULT_DISTANCE_MAX_BETWEEN_BUILDINGS;
		this.color = Colors.getColor(engine.getPlayer(playerId).getColor());
		this.block = block;
		flameAnimation = new Animation();
		SpriteSheet ss = ResourceManager.getSpriteSheet("flame");
		for (int i = 0; i < 5; i++) {
			flameAnimation.addFrame(ss.getSprite(i, 0), 100);
		}
		flameOne = new Point();
		flameTwo = new Point();
		flameThree = new Point();
		repairTimer = new Timer(500);
		showRepairTimer = new Timer(500);
	}

	protected abstract void renderBuilding(GameContainer container, Graphics g) throws SlickException;

	protected abstract void removeBuilding();

	@Override
	public void renderEntity(GameContainer container, Graphics g) throws SlickException {
		renderBuilding(container, g);
		if (weak || dying) {
			g.drawAnimation(flameAnimation, flameOne.x - 10, flameOne.y - 35);
			g.drawAnimation(flameAnimation, flameTwo.x - 10, flameTwo.y - 35);
			g.drawAnimation(flameAnimation, flameThree.x - 10, flameThree.y - 35);
		}
		if (repair && showRepairTimer.isTimeComplete()) {
			engine.addEntity(new MoveUpEffect(engine, x + (width / 2) - 4, y - 15, "-$", new Color(255, 215, 0, 255), 50));
			showRepairTimer.resetTime();
		}
	}

	@Override
	public void renderOnMiniMap(Graphics g, float x, float y, float tw, float th) {
		g.setColor(color);
		g.fillRect(x, y, (width / 20) * tw, (height / 20) * th);
	}

	public void renderLocationOnMap(GUIContext container, Graphics g) {
		x = engine.getMouseX();
		y = engine.getMouseY();

		int lx = (int) (x / engine.getTileW());
		int ly = (int) (y / engine.getTileH());

		// Get closer bulding
		ArrayList<Building> buildings = engine.getPlayerBuilding();
		Building closer = null;
		float dist = 0;
		if (!buildings.isEmpty()) {
			dist = Utils.getDistanceBetween(getRealX(), getRealY(), buildings.get(0).getRealX(), buildings.get(0).getRealY());
			for (int i = 0; i < buildings.size(); i++) {
				float nd = Utils.getDistanceBetween(getRealX(), getRealY(), buildings.get(i).getRealX(), buildings.get(i).getRealY());
				if (nd <= dist) {
					dist = nd;
					closer = buildings.get(i);
				}
			}
		}
		g.translate(engine.getXScrollDecal(), engine.getYScrollDecal());
		if (closer != null) {
			validLocation = true;
			for (int i = 0; i < width / 20; i++) {
				for (int j = 0; j < height / 20; j++) {
					int x = (lx + i);
					int y = (ly + j);
					checkValidLocation(g, closer, x, y);
					g.fillRect(x * 20, y * 20, 20, 20);
				}
			}
		} else {
			validLocation = false;
			g.setColor(FADE_RED);
			for (int i = 0; i < width / 20; i++) {
				for (int j = 0; j < height / 20; j++) {
					g.fillRect((lx + i) * 20, (ly + j) * 20, 20, 20);
				}
			}
		}

		g.translate(-engine.getXScrollDecal(), -engine.getYScrollDecal());
	}

	public void repair() {
		if (this.isAlive() && this.visible) {
			repair = !repair;
			if (repair)
				GameSound.repair();
		}
	}

	public void sell() {
		if (this.isAlive() && this.visible) {
			GameSound.buildingSold();
			this.remove();
			this.engine.addEntity(new MoveUpEffect(engine, x, y - 10, "+" + (EData.PRICE[type] / 2) + " $", new Color(255, 215, 0, 255), 50));
			engine.getPlayer().addMoney((EData.PRICE[type] / 2));
		}
	}

	public boolean isValidLocation(int mx, int my) {
		return validLocation;
	}

	protected void checkValidLocation(Graphics g, Building closer, int x, int y) {
		if (engine.getMap().isEntityOccupy(x, y) || engine.getMap().isBlocked(x, y) || engine.getMap().isWater(x, y)
				|| (Utils.getDistanceBetween(x * engine.getTileW(), y * engine.getTileH(), closer.getRealX(), closer.getRealY()) > distanceMaxBetweenBuilding)) {
			g.setColor(FADE_RED);
			validLocation = false;
		} else {
			g.setColor(FADE_BLUE);
		}
	}

	public void updateEntity(GameContainer container, int delta) throws SlickException {
		repairTimer.update(delta);
		showRepairTimer.update(delta);
		if (repair && repairTimer.isTimeComplete()) {
			if (this.life < this.maxLife) {
				if (engine.getPlayer().removeMoney(5)) {
					this.addLife(1);
				}
			} else {
				repair = false;
			}
			repairTimer.resetTime();
		}
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		checkFowFromView();
		Random r = new Random();
		flameOne.x = (int) (x + 5 + r.nextInt(width - 5));
		flameOne.y = (int) (y + 5 + r.nextInt(height - 5));

		flameTwo.x = (int) (x + 5 + r.nextInt(width - 5));
		flameTwo.y = (int) (y + 5 + r.nextInt(height - 5));

		flameThree.x = (int) (x + 5 + r.nextInt(width - 5));
		flameThree.y = (int) (y + 5 + r.nextInt(height - 5));

		int lx = (int) (x / engine.getTileW());
		int ly = (int) (y / engine.getTileH());

		for (int i = 0; i < width / 20; i++) {
			for (int j = 0; j < height / 20; j++) {
				engine.getMap().addEntityLocation(this, block, lx + i, ly + j);
			}
		}
	}

	@Override
	public void remove() {
		int lx = (int) (x / engine.getTileW());
		int ly = (int) (y / engine.getTileH());
		for (int i = 0; i < width / 20; i++) {
			for (int j = 0; j < height / 20; j++) {
				engine.getMap().removeEntityLocation(this, lx + i, ly + j);
			}
		}
		removeBuilding();
		if (engine.isPlayerEntity(playerId))
			engine.getGui().removeEntityFromBuildingList(type);
	}

	@Override
	public void setState(EntityState state) {
		if (state.life < this.life && engine.isPlayerEntity(playerId)) {
			GameSound.ourBaseIsUnderAttack();
		}
		super.setState(state);
	}

	@Override
	public int getTargetCursor(ActiveEntity target, int mx, int my) {
		return 0;
	}

	@Override
	public void target(ActiveEntity target, int mx, int my) {
		engine.deselectAllEntities();
		target.selected();
	}

	@Override
	public float getRealX() {
		return x + (width / 2);
	}

	@Override
	public float getRealY() {
		return y + (height / 2);
	}

	@Override
	public boolean fogOnUnit() {
		return false;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
