package rts.core.engine.layers.entities.others;

import rts.core.engine.Engine;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.EData;

public abstract class Misc extends ActiveEntity {

	public Misc(Engine engine, int type, int networkId) {
		super(engine, 0, Layer.FIRST_EFFECT, type, EData.MAX_LIFE[type], 0, networkId);
	}

	@Override
	public void removeLife(int damage) {
		super.removeLife(damage);
		if (engine.isNetwork()) {
			engine.getNetworkManager().sendUpdateNPEntity(getState());
		}
	}

	@Override
	public void target(ActiveEntity target, int mx, int my) {

	}

	@Override
	public boolean fogOnUnit() {
		return engine.getMap().fogOn((int) x / engine.getTileW(), (int) y / engine.getTileH());
	}

	@Override
	public int getTargetCursor(ActiveEntity target, int mx, int my) {
		return 0;
	}

}
