package rts.core.engine.layers.entities.vehicles;

import java.awt.Point;
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import rts.core.engine.Engine;
import rts.core.engine.PlayerInput;
import rts.core.engine.Utils;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.EData;

public class Transport extends Mover implements ITransport {

	private static final int MAX = 5;

	private ArrayList<Mover> candidates;
	private ArrayList<Mover> ents;

	public Transport(Engine engine, boolean marine, int playerId, int teamId, int networkId) {
		super(engine, (marine) ? EData.MOVER_MARINE_TRANSPORT : EData.MOVER_TRANSPORT, playerId, teamId, networkId);
		candidates = new ArrayList<Mover>(5);
		ents = new ArrayList<Mover>();
	}

	@Override
	protected boolean specialTarget(ActiveEntity target) {
		if (super.specialTarget(target))
			return true;
		if (target == this && !ents.isEmpty()) {
			int tx = (int) this.x / engine.getTileW();
			int ty = (int) this.y / engine.getTileH();

			for (int i = 0; i < ents.size(); i++) {
				Mover m = ents.get(i);

				for (int j = 0; j < Utils.DIRECTIONS.length; j++) {
					if (!engine.getMap().blocked(m, tx + Utils.DIRECTIONS[j][0], ty + Utils.DIRECTIONS[j][1])) {
						m.setDirection(j);
						m.show(tx + Utils.DIRECTIONS[j][0], ty + Utils.DIRECTIONS[j][1]);
						candidates.add(m);
						break;
					}
				}
			}

			for (int i = 0; i < candidates.size(); i++)
				if (ents.contains(candidates.get(i)))
					ents.remove(candidates.get(i));

			return true;
		} else
			return false;
	}

	@Override
	protected void destroy() {
		super.destroy();
		for (int i = 0; i < candidates.size(); i++) {
			candidates.get(i).cancelTransport();
		}
		for (int i = 0; i < ents.size(); i++) {
			ents.get(i).remove();
		}
	}

	@Override
	public int getTargetCursor(ActiveEntity target, int mx, int my) {
		if (target != null) {
			if (engine.getMap().fogOn(mx / engine.getTileW(), my / engine.getTileH())) {
				return PlayerInput.CURSOR_MOVE;
			} else {
				if (target == this && !ents.isEmpty())
					return PlayerInput.CURSOR_SPECIAL_ACTION;
			}
		} else {
			if (!engine.getMap().isBlocked(mx / engine.getTileW(), my / engine.getTileH()))
				return PlayerInput.CURSOR_MOVE;
		}
		return PlayerInput.CURSOR_NO_ACTION;
	}

	@Override
	public void renderEntity(GameContainer container, Graphics g) throws SlickException {
		super.renderEntity(container, g);
		if (selected && engine.isPlayerEntity(playerId)) {
			for (int i = 0; i < MAX; i++) {
				if (i < ents.size()) {
					g.setColor(Color.blue);
					g.fillRect(x + (i * 4), y - 9, 4, 4);
				}
				g.setColor(Color.black);
				g.drawRect(x + (i * 4), y - 9, 4, 4);
			}
		}
	}

	@Override
	public void updateEntity(GameContainer container, int delta) throws SlickException {
		super.updateEntity(container, delta);
		for (int i = 0; i < candidates.size(); i++) {
			Mover m = candidates.get(i);
			if (m.isAlive() && m.isGoingToTransport() && !ents.contains(m)) {
				if (m.isStopped()) {
					int tx = (int) this.x / engine.getTileW();
					int ty = (int) this.y / engine.getTileH();
					if (Math.abs(tx - (m.getX() / engine.getTileW())) <= 1 && Math.abs(ty - (m.getY() / engine.getTileH())) <= 1) {
						m.hide();
						ents.add(m);
						candidates.remove(m);
					}
				}
			} else
				candidates.remove(m);
		}
	}

	@Override
	public Point transport(Mover entity) {
		if (ents.size() < MAX && candidates.size() < MAX) {
			int tx = (int) this.x / engine.getTileW();
			int ty = (int) this.y / engine.getTileH();

			for (int i = 0; i < Utils.DIRECTIONS.length; i++) {
				if (!engine.getMap().blocked(entity, tx + Utils.DIRECTIONS[i][0], ty + Utils.DIRECTIONS[i][1])) {
					candidates.add(entity);
					return new Point(tx + Utils.DIRECTIONS[i][0], ty + Utils.DIRECTIONS[i][1]);
				}
			}
		}
		return null;
	}

}
