package rts.core.engine.layers.entities.buildings;

import java.awt.Point;
import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import rts.core.engine.Engine;
import rts.utils.ResourceManager;

public abstract class BuildingECreator extends Building {

	private static ArrayList<BuildingECreator> creators = new ArrayList<BuildingECreator>();

	private Animation flag;
	protected Point rp;
	protected boolean isPrimary;

	public BuildingECreator(Engine engine, int type, boolean block, int playerId, int teamId, int networkId) {
		super(engine, type, block, playerId, teamId, networkId);

		// Flag and rallying point
		rp = new Point(-1, -1);
		flag = new Animation();
		SpriteSheet ss = ResourceManager.getSpriteSheet("flag");
		for (int i = 0; i < 5; i++) {
			flag.addFrame(ss.getSprite(i, 0), 150);
		}
	}

	public void checkPrimary() {
		if (engine.isPlayerEntity(playerId)) {

			// Check if primary means no entity with the same type in the array
			boolean findE = false;
			for (int i = 0; i < creators.size(); i++) {
				if (creators.get(i).getType() == type) {
					findE = true;
				}
			}
			isPrimary = !findE;

			creators.add(this);
		}
	}

	@Override
	public void remove() {
		super.remove();
		creators.remove(this);
	}

	@Override
	public void selected() {
		if (engine.isPlayerEntity(playerId) && selected && !isPrimary) {
			// Set this building primary
			for (int i = 0; i < creators.size(); i++) {
				if (creators.get(i).type == this.type)
					creators.get(i).setPrimary(false);
			}
			isPrimary = true;
		}
		super.selected();
	}

	@Override
	public void renderEntity(GameContainer container, Graphics g) throws SlickException {
		if (selected && rp.x != -1 && rp.y != -1) {
			g.setColor(Color.red);
			g.drawLine(getRealX(), getRealY(), rp.x + 8, rp.y + 10);
			g.drawAnimation(flag, rp.x - 6, rp.y - 6);
		}
		super.renderEntity(container, g);

		if (isPrimary && selected) {
			g.setColor(Color.white);
			g.drawRect(x, y - 2, 13, 13);
			g.drawString("P", x + 2, y - 4);
		}
	}

	public Point getRallyingPoint() {
		if (rp.x == -1 && rp.y == -1)
			return null;
		else
			return rp;
	}

	public void changeRallyingPoint(int mx, int my) {
		if (onEntity(mx, my)) {
			rp.x = -1;
			rp.y = -1;
		} else {
			if (!engine.getMap().isBlocked(mx / engine.getTileW(), my / engine.getTileH())) {
				rp.x = (mx / engine.getTileW()) * engine.getTileW();
				rp.y = (my / engine.getTileH()) * engine.getTileH();
			}
		}
	}

	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	public boolean isPrimary() {
		return isPrimary;
	}
}
