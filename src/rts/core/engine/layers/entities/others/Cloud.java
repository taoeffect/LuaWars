package rts.core.engine.layers.entities.others;

import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import rts.core.engine.Engine;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.BasicEntity;
import rts.utils.ResourceManager;
import rts.utils.Timer;

public class Cloud extends BasicEntity {

	private static final Color TRANS = new Color(255, 255, 255, 100);

	private Timer moveTimer;
	private Image image;

	public Cloud(Engine engine) {
		super(engine, Layer.THIRD_EFFECT);
		Random r = new Random();
		this.image = ResourceManager.getImage("cloud" + (r.nextInt(3) + 1));
		this.y = r.nextInt(engine.getMap().getHeightInPixel() - (image.getHeight())) + image.getHeight();
		this.x = -300;
		this.moveTimer = new Timer(50);
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.drawImage(image, x, y, TRANS);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		moveTimer.update(delta);
		if (moveTimer.isTimeComplete()) {
			this.x += 1;
			moveTimer.resetTime();
		}
	}

}
