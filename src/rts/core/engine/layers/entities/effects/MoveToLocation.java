package rts.core.engine.layers.entities.effects;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import rts.core.engine.Engine;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.BasicEntity;
import rts.utils.Timer;

public class MoveToLocation extends BasicEntity {

	private Timer timer;

	public MoveToLocation(Engine engine, int x, int y) {
		super(engine, Layer.FIRST_EFFECT);
		this.x = ((int)x / 20) * 20 + 10;
		this.y = ((int)y / 20) * 20 + 10;
		this.width = 0;
		this.height = 0;
		this.timer = new Timer(50);
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.setColor(Color.blue);
		g.drawOval(x, y, width, height);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		timer.update(delta);
		if (timer.isTimeComplete()) {
			x -= 2;
			y -= 2;
			width += 4;
			height += 4;
			timer.resetTime();
			if (width == 20)
				engine.removeEntity(this);
		}
	}

}
