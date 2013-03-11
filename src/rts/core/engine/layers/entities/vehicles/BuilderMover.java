package rts.core.engine.layers.entities.vehicles;

import rts.core.engine.Engine;
import rts.core.engine.PlayerInput;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.EData;
import rts.core.engine.layers.entities.buildings.BigHealer;
import rts.core.engine.layers.entities.buildings.Builder;
import rts.core.engine.layers.entities.buildings.Healer;

public class BuilderMover extends Mover {

	public BuilderMover(Engine engine, int playerId, int teamId, int networkId) {
		super(engine, EData.MOVER_BUILDER, playerId, teamId, networkId);
	}

	@Override
	protected boolean specialTarget(ActiveEntity target) {
		if (super.specialTarget(target))
			return true;
		if (target.getPlayerId() == engine.getPlayer().getId() && locationCorrect) {
			int tx = (int) x / engine.getTileW();
			int ty = ((int) y / engine.getTileH()) - 2;
			boolean ok = true;
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					if (engine.getMap().blocked(this, tx + i, ty + j)) {
						ok = false;
						break;
					}
				}
			}

			if (engine.getMap().blocked(this, tx + 1, ty + 2)) {
				ok = false;
			}

			if (ok) {
				remove();
				if (engine.isNetwork()) {
					engine.getNetworkManager().sendCreateEntity(EData.BUILDING_BUILDER, playerId, teamId, x, y - 40);
				} else {
					Builder builder = new Builder(engine, playerId, teamId, networkId);
					builder.setLocation(x, y - 40);
					engine.addEntity(builder);
				}
			}
		}
		return true;
	}

	@Override
	protected void destroy() {
		// Must be override to stop the call to explosion
	}

	@Override
	public int getTargetCursor(ActiveEntity target, int mx, int my) {
		if (target != null) {
			if (engine.getMap().fogOn(mx / engine.getTileW(), my / engine.getTileH())) {
				return PlayerInput.CURSOR_MOVE;
			} else {
				if (engine.isPlayerEntity(target.getPlayerId()) && (target instanceof ITransport || target instanceof Healer || target instanceof BigHealer)) {
					return PlayerInput.CURSOR_SPECIAL_ACTION;
				} else {
					if (target.getPlayerId() == engine.getPlayer().getId() && target == this) {
						return PlayerInput.CURSOR_SPECIAL_ACTION;
					}
				}
			}
		} else {
			if (!engine.getMap().isBlocked(mx / engine.getTileW(), my / engine.getTileH()))
				return PlayerInput.CURSOR_MOVE;
		}
		return PlayerInput.CURSOR_NO_ACTION;
	}
}
