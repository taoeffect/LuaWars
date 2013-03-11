package rts.core.engine.layers.entities.buildings;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
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
import rts.core.engine.layers.entities.projectiles.AtomicBomb;
import rts.core.network.ig_udp_containers.EntityState;
import rts.utils.ResourceManager;

public class MissileSilo extends Building {

	private static final int START_TIME_BEFORE_READY = 600;

	private Animation animation;
	private Image destroy;

	private CountDown countDown;

	public MissileSilo(Engine engine, int playerId, int teamId, int networkId) {
		super(engine, EData.BUILDING_MISSILE_SILO, false, playerId, teamId, networkId);
		SpriteSheet ss = ResourceManager.getSpriteSheet("televat2_" + engine.getPlayer(playerId).getColor());
		width = ss.getSprite(0, 0).getWidth();
		height = ss.getSprite(0, 0).getHeight();

		animation = new Animation(true);
		animation.setLooping(false);

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				animation.addFrame(ss.getSprite(j, i), 150);
			}
		}
		animation.setCurrentFrame(15);

		destroy = ss.getSprite(0, 4);

		calcViewLimit(width / 40, height / 40);

		countDown = new CountDown("A Missile", color, START_TIME_BEFORE_READY);

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
		animation.restart();
		state.tx = tx;
		state.ty = ty;
		direction = Utils.findDirection(x, y, tx, ty);
		engine.addEntity(new AtomicBomb(engine, playerId, x, y, tx, ty));
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
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.NORMAL_2, x, y));
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.NORMAL_2, x + 10, y));
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.NORMAL_2, x + 20, y));
		engine.removeCountDown(countDown);
	}

	@Override
	public void updateEntity(GameContainer container, int delta) throws SlickException {
		super.updateEntity(container, delta);
		if (engine.isPlayerEntity(playerId)) {
			countDown.update(delta);
			this.state.moneyState = countDown.getTime();
		}
		countDown.updateBlink(delta);
	}

	@Override
	public void renderBuilding(GameContainer container, Graphics g) throws SlickException {
		if (weak || dying) {
			g.drawImage(destroy, x, y);
		} else {
			g.drawAnimation(animation, x, y);
		}
	}

	@Override
	public void setState(EntityState state) {
		super.setState(state);
		if (state.moneyState > this.countDown.getTime()) {
			shoot(state.tx, state.ty);
		}
		this.countDown.setTime(state.moneyState);
	}

}
