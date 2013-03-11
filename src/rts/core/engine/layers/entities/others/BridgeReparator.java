package rts.core.engine.layers.entities.others;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import rts.core.engine.Engine;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.BasicEntity;
import rts.utils.ResourceManager;
import rts.utils.Timer;

public class BridgeReparator extends BasicEntity {

	private static final int REPAIR_INTERVAL = 50;

	private Image image;
	private Bridge bridge;
	private Timer timer;
	private boolean renderRepair;
	private int advancement;

	public BridgeReparator(Engine engine, Bridge bridge, int x, int y) {
		super(engine, Layer.FIRST_EFFECT);
		this.x = x;
		this.y = y;
		this.bridge = bridge;
		this.image = ResourceManager.getSpriteSheet("constructor").getSprite(1, 4);
		this.timer = new Timer(REPAIR_INTERVAL);
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.drawImage(image, x, y);
		if (renderRepair) {
			g.setColor(Color.blue);
			g.fillRect(x, y - 10, (advancement * 20) / bridge.getMaxLife(), 4);
			g.setColor(Color.black);
			g.drawRect(x, y - 10, 20, 4);
		}
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		if (bridge.isDestroy()) {
			ActiveEntity ae = engine.getEntityAt(null, x, y);
			if (ae != null) {
				renderRepair = true;
				timer.update(delta);
				if (timer.isTimeComplete()) {
					advancement++;
					if (advancement >= bridge.getMaxLife())
						bridge.addLife(bridge.getMaxLife());
					timer.resetTime();
				}
			} else {
				advancement = 0;
				renderRepair = false;
			}
		} else
			renderRepair = false;
	}

	public void reset() {
		advancement = 0;
	}

}
