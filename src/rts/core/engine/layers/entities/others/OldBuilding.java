package rts.core.engine.layers.entities.others;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import rts.core.engine.Engine;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.EData;
import rts.core.engine.layers.entities.IBigEntity;
import rts.core.engine.layers.entities.effects.Explosion;
import rts.utils.ResourceManager;

public class OldBuilding extends Misc implements IBigEntity {

	private Image building;

	public OldBuilding(Engine engine, int networkId) {
		super(engine, EData.OLD_BUILDING, networkId);
		building = ResourceManager.getImage("old_building");
		width = building.getWidth();
		height = building.getHeight();
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		int lx = (int) (x / engine.getTileW());
		int ly = (int) (y / engine.getTileH());

		for (int i = 0; i < width / 20; i++) {
			for (int j = 0; j < height / 20; j++) {
				engine.getMap().addEntityLocation(this, true, lx + i, ly + j);
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
		engine.removeEntity(this);
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.BIG, x, y + 10));
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.BIG, x + 30, y + 10));
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.BIG, x, y + 40));
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.BIG, x + 30, y + 40));
	}

	@Override
	public void renderEntity(GameContainer container, Graphics g) throws SlickException {
		g.drawImage(building, x, y);
	}

	@Override
	public void renderOnMiniMap(Graphics g, float x, float y, float tw, float th) {
		g.setColor(Color.gray);
		g.fillRect(x, y, (width / 20) * tw, (height / 20) * th);
	}

	@Override
	public void updateEntity(GameContainer container, int delta) throws SlickException {

	}

	@Override
	public float getRealX() {
		return x + (width / 2);
	}

	@Override
	public float getRealY() {
		return y + (height / 2);
	}
}
