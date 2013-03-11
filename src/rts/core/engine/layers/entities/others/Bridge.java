package rts.core.engine.layers.entities.others;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import rts.core.engine.Engine;
import rts.core.engine.PlayerInput;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.EData;
import rts.core.engine.layers.entities.IBigEntity;
import rts.core.engine.layers.entities.effects.Explosion;
import rts.core.network.ig_udp_containers.EntityState;
import rts.utils.ResourceManager;

public class Bridge extends ActiveEntity implements IBigEntity {

	private static final int LIFE = 200;

	private Image image;
	private BridgeReparator reperator1;
	private BridgeReparator reperator2;
	private boolean destroy;

	public Bridge(Engine engine, int type, int networkId) {
		super(engine,EData.TEC_LEVEL[type], Layer.FIRST_EFFECT, type, LIFE, 0, networkId);
		this.playerId = -1;
		this.image = (type == EData.HORIZONTAL_BRIDGE) ? ResourceManager.getImage("horizontal_bridge") : ResourceManager.getImage("vertical_bridge");
		this.width = (type == EData.HORIZONTAL_BRIDGE) ? image.getWidth() - 40 : image.getWidth();
		this.height = (type == EData.VERTICAL_BRIDGE) ? image.getHeight() - 40 : image.getHeight();
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		int tx = (int) x;
		int ty = (int) y;
		reperator1 = new BridgeReparator(engine, this, tx - 20, ty - 20);
		engine.addEntity(reperator1);
		if (type == EData.HORIZONTAL_BRIDGE) {
			reperator2 = new BridgeReparator(engine, this, tx + 80, ty + 40);
		} else {
			reperator2 = new BridgeReparator(engine, this, tx + 40, ty + 80);
		}

		engine.addEntity(reperator2);

		int mx = this.width / 20;
		int my = this.height / 20;

		for (int i = 0; i < mx; i++) {
			for (int j = 0; j < my; j++) {
				engine.getMap().addEntityLocation(this, false, (int) x / engine.getTileW() + i, (int) y / engine.getTileH() + j);
			}
		}
	}

	@Override
	public boolean fogOnUnit() {
		return false;
	}

	@Override
	public void remove() {
		int tx = (int) this.x / engine.getTileW();
		int ty = (int) this.y / engine.getTileH();
		int mx = this.width / 20;
		int my = this.height / 20;

		for (int i = 0; i < mx; i++) {
			for (int j = 0; j < my; j++) {
				ActiveEntity ae = engine.getEntityAt(this, x + i * engine.getTileW(), y + j * engine.getTileH());
				if (ae != null)
					ae.removeLife(500);
				engine.getMap().blockWithWater(tx + i, ty + j);
				engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.NORMAL_2, x + i * engine.getTileW(), y + j * engine.getTileH()));
			}
		}

		this.destroy = true;
		this.state.timer1Complete = true;
	}

	@Override
	public void renderEntity(GameContainer container, Graphics g) throws SlickException {
		if (!destroy) {
			if (type == EData.VERTICAL_BRIDGE) {
				g.drawImage(image, x, y - 20);
			} else {
				g.drawImage(image, x - 20, y);
			}
		}
	}

	@Override
	public void addLife(int bonus) {
		super.addLife(bonus);
		if (life == maxLife) {

			int tx = (int) this.x / engine.getTileW();
			int ty = (int) this.y / engine.getTileH();
			int mx = this.width / 20;
			int my = this.height / 20;

			for (int i = 0; i < mx; i++) {
				for (int j = 0; j < my; j++) {
					ActiveEntity ae = engine.getEntityAt(this, x + i * engine.getTileW(), y + j * engine.getTileH());
					if (ae != null)
						ae.removeLife(500);
					engine.getMap().freeWithWater(tx + i, ty + j);
				}
			}
			destroy = false;
			reperator1.reset();
			reperator2.reset();
		}
	}

	@Override
	public int getTargetCursor(ActiveEntity target, int mx, int my) {
		return PlayerInput.CURSOR_NO_ACTION;
	}

	@Override
	public void target(ActiveEntity target, int mx, int my) {

	}

	public boolean isDestroy() {
		return destroy;
	}

	@Override
	public void renderOnMiniMap(Graphics g, float x, float y, float tw, float th) {
		if (!destroy) {
			g.setColor(Color.gray);
			if (type == EData.VERTICAL_BRIDGE) {
				g.fillRect(x, y, tw * 2, th * 4);
			} else {
				g.fillRect(x, y, tw * 4, th * 2);
			}
		}
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
	public void setState(EntityState state) {
		super.setState(state);

		if (destroy && this.life == maxLife) {
			addLife(0);
		} else {
			if (!destroy && state.timer1Complete) {
				remove();
			}
		}
	}

	@Override
	public void updateEntity(GameContainer container, int delta) throws SlickException {
		
	}

}
