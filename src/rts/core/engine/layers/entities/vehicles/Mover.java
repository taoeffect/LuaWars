package rts.core.engine.layers.entities.vehicles;

import java.awt.Point;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import rts.core.engine.Engine;
import rts.core.engine.GameSound;
import rts.core.engine.PlayerInput;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.EData;
import rts.core.engine.layers.entities.MoveableEntity;
import rts.core.engine.layers.entities.buildings.BigHealer;
import rts.core.engine.layers.entities.buildings.Healer;
import rts.core.engine.layers.entities.effects.Explosion;
import rts.core.engine.layers.entities.others.Mineral;
import rts.core.engine.layers.entities.projectiles.Bullet;
import rts.core.engine.layers.entities.projectiles.Flame;
import rts.utils.Colors;
import rts.utils.ResourceManager;
import rts.utils.Timer;

public class Mover extends MoveableEntity {

	private Animation animation;
	private Timer fireTimer;
	private boolean goToTransport;
	private boolean goToHealer;
	private boolean goToBigHealer;

	public Mover(Engine engine, int type, int playerId, int teamId, int networkId) {
		super(engine, EData.TEC_LEVEL[type], 0, type, EData.MAX_LIFE[type], EData.VIEW[type], playerId, teamId, networkId);
		this.speed = EData.SPEED[type];
		this.calcViewLimit(0, 0);

		int c = engine.getPlayer(playerId).getColor();
		this.color = Colors.getColor(c);

		SpriteSheet ss = null;
		int pos = 0;
		if (EData.isEarthMover(type)) {
			ss = ResourceManager.getSpriteSheet("earth_" + c);
			this.moveType = EARTH_ONLY;
			this.layer = Layer.EARTH_MARINE_ENT;
			pos = type;
		} else {
			if (EData.isMarineMover(type)) {
				ss = ResourceManager.getSpriteSheet("water_" + c);
				this.moveType = WATER_ONLY;
				this.layer = Layer.EARTH_MARINE_ENT;
				pos = type - EData.MAX_EARTH_ENTITES;
			} else {
				if (EData.isSkyMover(type)) {
					ss = ResourceManager.getSpriteSheet("fly_" + c);
					this.moveType = EVERYWHERE;
					this.layer = Layer.FLIGHT_ENT;
					pos = type - (EData.MAX_EARTH_ENTITES + EData.MAX_MARINE_ENTITES);
				}
			}
		}

		this.width = ss.getSprite(0, 0).getWidth();
		this.height = ss.getSprite(0, 0).getHeight();

		animation = new Animation(false);
		for (int i = 0; i < 8; i++) {
			animation.addFrame(ss.getSprite(i, pos), 10);
		}
		this.fireTimer = new Timer(EData.SHOOT_INTERVAL[type]);
	}

	public void selected() {
		super.selected();
		if (engine.isPlayerEntity(playerId))
			GameSound.selectMover();
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		checkFowFromView();
	}

	@Override
	public void renderEntity(GameContainer container, Graphics g) throws SlickException {
		g.drawImage(animation.getImage(direction), x, y);
	}

	@Override
	public void renderOnMiniMap(Graphics g, float x, float y, float tw, float th) {
		g.setColor(color);
		g.fillRect(x, y, tw, th);
	}

	@Override
	public void updateEntity(GameContainer container, int delta) throws SlickException {
		super.updateEntity(container, delta);
		fireTimer.update(delta);
	}

	@Override
	protected void destroy() {
		if (visible)
			engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.NORMAL_2, x, y));
	}

	@Override
	protected void fireOnTarget(ActiveEntity target) {
		if (fireTimer.isTimeComplete()) {
			if (this.type == EData.MOVER_FLAME_LAUNCHER || this.type == EData.MOVER_FLAME_LAUNCHER) {
				engine.addEntity(new Flame(engine, this, target));
			} else
				engine.addEntity(new Bullet(engine, this, target, EData.BULLET_TYPE[type], 15, 15));
			fireTimer.resetTime();
		}
	}

	@Override
	public int getTargetCursor(ActiveEntity target, int mx, int my) {
		if (target != null && target.isAlive()) {
			if (engine.getMap().fogOn(mx / engine.getTileW(), my / engine.getTileH())) {
				return PlayerInput.CURSOR_MOVE;
			} else {
				if ((target.getTeamId() != teamId) && !(target instanceof Mineral)) {
					return PlayerInput.CURSOR_ATTACK;
				} else {
					if (engine.isPlayerEntity(target.getPlayerId())
							&& (target instanceof ITransport || target instanceof Healer || target instanceof BigHealer)) {
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

	@Override
	public void moveFromPlayerAction(int mx, int my) {
		super.moveFromPlayerAction(mx, my);
		goToTransport = false;
		GameSound.moverMove();
	}

	@Override
	protected boolean specialTarget(ActiveEntity target) {
		if (target instanceof BigHealer) {
			BigHealer bh = (BigHealer) target;
			Point p = bh.getFreeLocation(this);
			if (p != null) {
				this.move(p.x * engine.getTileW(), p.y * engine.getTileH());
			}
			return true;
		} else {
			if (target instanceof Healer) {
				Healer h = (Healer) target;
				if (!h.isReserved()) {
					h.reserved(this);
					this.move((int) target.getX(), (int) target.getY());
				}
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean canGoToTransport(ActiveEntity target) {
		if (target.getTeamId() == teamId && !(this instanceof ITransport) && target instanceof ITransport && this.type < EData.MAX_EARTH_ENTITES) {
			Point p = ((ITransport) target).transport(this);
			if (p != null) {
				move(p.x * engine.getTileW(), p.y * engine.getTileH());
				if (path != null) {
					goToTransport = true;
				}
			}
			return true;
		} else
			return false;
	}

	public boolean isGoingToTransport() {
		return goToTransport;
	}

	public boolean isGoingToHealer() {
		return goToHealer;
	}

	public boolean isGoingToBigHealer() {
		return goToBigHealer;
	}

	public void hide() {
		path = null;
		goToTransport = false;
		goToHealer = false;
		goToBigHealer = false;
		selected = false;
		visible = false;
		engine.getMap().removeEntityLocation(this, (int) x / engine.getTileW(), (int) y / engine.getTileH());
	}

	public void show(int x, int y) {
		this.x = x * engine.getTileW();
		this.y = y * engine.getTileH();
		engine.getMap().addEntityLocation(this, true, x, y);
		visible = true;
	}

	public void cancelTransport() {
		goToTransport = false;
	}

	public void cancelHealer() {
		goToHealer = false;
	}

	public void cancelBigHealer() {
		goToBigHealer = false;
	}

	@Override
	public boolean fogOnUnit() {
		return engine.getMap().fogOn((int) x / engine.getTileW(), (int) y / engine.getTileH());
	}
}
