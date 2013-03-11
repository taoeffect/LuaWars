package rts.core.engine.layers.entities.buildings;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import rts.core.engine.Engine;
import rts.core.engine.PlayerInput;
import rts.core.engine.Utils;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.EData;
import rts.core.engine.layers.entities.effects.Explosion;
import rts.core.engine.layers.entities.others.CountDown;
import rts.core.engine.layers.entities.projectiles.LightningMissile;
import rts.core.network.ig_udp_containers.EntityState;
import rts.utils.ResourceManager;
import rts.utils.Timer;

public class LightningWeapon extends Building {

	private static final int START_TIME_BEFORE_READY = 300;

	private Animation normal;
	private Animation fire;

	private Timer changeFrameTimer;
	private CountDown countDown;

	public LightningWeapon(Engine engine, int playerId, int teamId, int networkId) {
		super(engine, EData.BUILDING_LIGHTNING_WEAPON, true, playerId, teamId, networkId);
		SpriteSheet ss = ResourceManager.getSpriteSheet("finalweapon_" + engine.getPlayer(playerId).getColor());
		width = ss.getSprite(0, 0).getWidth();
		height = ss.getSprite(0, 0).getHeight();

		normal = new Animation(false);
		fire = new Animation(false);

		for (int i = 0; i < 8; i++) {
			normal.addFrame(ss.getSprite(i, 0), 150);
			fire.addFrame(ss.getSprite(i, 1), 150);
		}
		changeFrameTimer = new Timer(100);

		calcViewLimit(width / 40, height / 40);

		countDown = new CountDown("Lightning", color, START_TIME_BEFORE_READY);
	}

	@Override
	public void checkFowFromView() {
		if (engine.getMap().isEnableFow()) {
			if (view != 0) {
				for (int i = 0; i < viewLimit.size(); i++) {
					engine.getMap().showFow(viewLimit.get(i).x + (int) (x / engine.getTileW()), viewLimit.get(i).y + (int) (y / engine.getTileH()));
				}
			}
		}
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		engine.addCountDown(countDown);
	}

	private void shoot(float tx, float ty) {
		changeFrameTimer.resetTime();
		state.timer1Complete = true;
		state.tx = tx;
		state.ty = ty;
		direction = Utils.findDirection(x, y, tx, ty);
		engine.addEntity(new LightningMissile(engine, playerId, x, y, tx, ty));
		countDown.reset();
	}

	@Override
	public void target(ActiveEntity target, int mx, int my) {
		if (countDown.isFinish()) {
			if (target.getTeamId() != teamId) {
				shoot(mx * engine.getTileW(), my * engine.getTileH());
			}
		}
	}

	public int getTargetCursor(ActiveEntity target, int mx, int my) {
		if (countDown.isFinish()) {
			if (target != null && target.getTeamId() != teamId) {
				return PlayerInput.CURSOR_ATTACK;
			}
		}
		return PlayerInput.CURSOR_NO_ACTION;
	}

	@Override
	public void removeBuilding() {
		engine.removeEntity(this);
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.BIG, x + 20, y + 30));
		engine.removeCountDown(countDown);
	}

	@Override
	public void renderBuilding(GameContainer container, Graphics g) throws SlickException {
		if (!changeFrameTimer.isTimeComplete()) {
			g.drawImage(fire.getImage(direction), x, y);
		} else {
			state.timer1Complete = false;
			g.drawImage(normal.getImage(direction), x, y);
		}
	}

	@Override
	public void updateEntity(GameContainer container, int delta) throws SlickException {
		super.updateEntity(container, delta);
		changeFrameTimer.update(delta);
		if (engine.isPlayerEntity(playerId)) {
			countDown.update(delta);
			this.state.moneyState = countDown.getTime();
		}
		countDown.updateBlink(delta);
	}

	@Override
	public void setState(EntityState state) {
		super.setState(state);
		this.countDown.setTime(state.moneyState);
		if (!state.timer1Complete) {
			this.state.timer1Complete = false;
		} else {
			if (!this.state.timer1Complete && state.timer1Complete) {
				shoot(state.tx, state.ty);
			}
		}
	}
}
