package rts.core.engine.layers.entities.others;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import rts.core.engine.Engine;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.EData;
import rts.core.engine.layers.entities.effects.Explosion;
import rts.utils.ResourceManager;

public class Car extends Misc {

	private Image car;

	public Car(Engine engine, int type, int networkId) {
		super(engine, type, networkId);
		car = (type == EData.OLD_CAR1) ? ResourceManager.getImage("old_car1") : ResourceManager.getImage("old_car2");
		width = car.getWidth();
		height = car.getHeight();
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		engine.getMap().addEntityLocation(this, true, (int) x / engine.getTileW(), (int) y / engine.getTileH());
	}

	@Override
	public void remove() {
		engine.removeEntity(this);
		engine.getMap().removeEntityLocation(this, (int) x / engine.getTileW(), (int) y / engine.getTileH());
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.NORMAL_2, x, y));
	}

	@Override
	public void renderEntity(GameContainer container, Graphics g) throws SlickException {
		g.drawImage(car, x, y);
	}

	@Override
	public void renderOnMiniMap(Graphics g, float x, float y, float tw, float th) {
		g.setColor(Color.gray);
		g.fillRect(x, y, tw, th);
	}

	@Override
	public void updateEntity(GameContainer container, int delta) throws SlickException {

	}

}
