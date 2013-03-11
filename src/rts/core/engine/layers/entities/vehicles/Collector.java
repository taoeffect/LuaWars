package rts.core.engine.layers.entities.vehicles;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import rts.core.engine.Engine;
import rts.core.engine.GameSound;
import rts.core.engine.PlayerInput;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.EData;
import rts.core.engine.layers.entities.buildings.BigHealer;
import rts.core.engine.layers.entities.buildings.Healer;
import rts.core.engine.layers.entities.buildings.Refinery;
import rts.core.engine.layers.entities.buildings.Refinery.DropLocation;
import rts.core.engine.layers.entities.others.Mineral;
import rts.core.network.ig_udp_containers.EntityState;
import rts.utils.MoveUpEffect;
import rts.utils.Timer;

public class Collector extends Mover {

	private static final int MAX_MINERAL_BURDEN = 100;
	private static final int DIG_SPEED = 100;

	private static final int SEEK_MINERAL = 0;
	private static final int MOVE_TO_MINERAL = 1;
	private static final int MOVE_TO_REFINERY = 2;
	private static final int DO_NOTHING = 3;

	private int burden;
	private int collState;
	private Mineral currentMineral;
	private Timer digDropTimer;
	private Timer drawDollardTimer;
	private Refinery currentRefinery;
	private DropLocation dropLocation;

	public Collector(Engine engine, int playerId, int teamId, int networkId) {
		super(engine, EData.MOVER_COLLECTOR, playerId, teamId, networkId);
		collState = SEEK_MINERAL;
		digDropTimer = new Timer(DIG_SPEED);
		drawDollardTimer = new Timer(600);
	}

	@Override
	protected boolean specialTarget(ActiveEntity target) {
		if (super.specialTarget(target))
			return true;
		if (target instanceof Mineral && !engine.getMap().isEntityBlocked((int) target.getX() / engine.getTileW(), (int) target.getY() / engine.getTileH())) {
			currentMineral = (Mineral) target;
			move((int) currentMineral.getX(), (int) currentMineral.getY());
			collState = MOVE_TO_MINERAL;
		} else {
			if (target instanceof Refinery && ((Refinery) target).getPlayerId() == playerId) {
				DropLocation dl = null;
				if ((dl = ((Refinery) target).getFreeLocation()) != null) {
					freeRefinery();
					dropLocation = dl;
					currentRefinery = (Refinery) target;
					currentMineral = null;
					move((int) dropLocation.x, (int) dropLocation.y);
					collState = MOVE_TO_REFINERY;
				}
			}
		}
		return true;
	}

	@Override
	public void renderEntity(GameContainer container, Graphics g) throws SlickException {
		super.renderEntity(container, g);
		if (selected) {
			g.setColor(Color.blue);
			g.fillRect(x, y - 10, (burden * width) / MAX_MINERAL_BURDEN, 4);
			g.setColor(Color.black);
			g.drawRect(x, y - 10, width, 4);
		}
	}

	@Override
	public void moveFromPlayerAction(int mx, int my) {
		super.moveFromPlayerAction(mx, my);
		collState = DO_NOTHING;
		if (currentRefinery != null && dropLocation != null) {
			freeRefinery();
		}
	}

	@Override
	public void updateEntity(GameContainer container, int delta) throws SlickException {
		super.updateEntity(container, delta);
		if (engine.isPlayerEntity(playerId)) {
			switch (collState) {
			case SEEK_MINERAL:
				seekMineral();
				break;
			case MOVE_TO_MINERAL:
				moveToMineral(delta);
				break;
			case MOVE_TO_REFINERY:
				moveToRefinery(delta);
				break;
			default:
				break;
			}
		}
	}

	private void seekMineral() {
		if (currentMineral == null) {
			currentMineral = engine.getCloserMineral(this);
			if (currentMineral != null)
				move((int) currentMineral.getX(), (int) currentMineral.getY());
		} else
			collState = MOVE_TO_MINERAL;
	}

	private void moveToMineral(int delta) {
		if (currentMineral != null && currentMineral.isAlive()) {
			if (currentMineral.getX() == this.x && currentMineral.getY() == this.y) {
				// Dig
				digDropTimer.update(delta);
				if (digDropTimer.isTimeComplete()) {
					if (burden == MAX_MINERAL_BURDEN) {
						currentRefinery = Refinery.getCloserFreePlayerRefinery(x, y);
						if (currentRefinery != null) {
							if ((dropLocation = currentRefinery.getFreeLocation()) != null) {
								currentMineral = null;
								move((int) dropLocation.x, (int) dropLocation.y);
								collState = MOVE_TO_REFINERY;
							}
						}
					} else {
						currentMineral.removeLife(1);
						burden += 1;
					}
					digDropTimer.resetTime();
				}
			} else {
				if (path == null) {
					move((int) currentMineral.getX(), (int) currentMineral.getY());
				}
			}
		} else {
			currentMineral = null;
			if (burden == MAX_MINERAL_BURDEN) {
				currentRefinery = Refinery.getCloserFreePlayerRefinery(x, y);
				if (currentRefinery != null) {
					if ((dropLocation = currentRefinery.getFreeLocation()) != null) {
						move((int) dropLocation.x, (int) dropLocation.y);
						collState = MOVE_TO_REFINERY;
					}
				}
			} else {
				collState = SEEK_MINERAL;
			}
		}
	}

	private void moveToRefinery(int delta) {
		if (burden == 0) {
			collState = SEEK_MINERAL;
			freeRefinery();
		} else {
			if (currentRefinery != null && this.x == dropLocation.x && this.y == dropLocation.y) {
				this.direction = dropLocation.direction;
				digDropTimer.update(delta);
				drawDollardTimer.update(delta);
				if (digDropTimer.isTimeComplete()) {
					if (drawDollardTimer.isTimeComplete()) {
						engine.addEntity(new MoveUpEffect(engine,x + 5, y - 15, "$", new Color(255, 215, 0, 255), 50));
						drawDollardTimer.resetTime();
					}
					if (!engine.getPlayer().addMoney(7)) {
						GameSound.storageUnitNeeded();
					}else GameSound.addMoney();
					burden--;
					digDropTimer.resetTime();
				}
			}
		}
	}

	private void freeRefinery() {
		if (currentRefinery != null && dropLocation != null) {
			currentRefinery.freeLocation(dropLocation.x, dropLocation.y);
			dropLocation = null;
		}
	}

	@Override
	public int getTargetCursor(ActiveEntity target, int mx, int my) {
		if (target != null) {
			if (engine.getMap().fogOn(mx / engine.getTileW(), my / engine.getTileH())) {
				return PlayerInput.CURSOR_MOVE;
			} else {
				if (engine.isPlayerEntity(target.getPlayerId()) && (target instanceof ITransport || target instanceof Healer || target instanceof BigHealer)) {
					return PlayerInput.CURSOR_SPECIAL_ACTION;
				} else {
					if (target instanceof Mineral) {
						return PlayerInput.CURSOR_ATTACK;
					} else {
						if (target instanceof Refinery && ((Refinery) target).getPlayerId() == playerId) {
							return PlayerInput.CURSOR_SPECIAL_ACTION;
						}
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
	public void setState(EntityState state) {
		if (state.life < this.life && engine.isPlayerEntity(playerId)) {
			GameSound.collectorAttack();
		}
		super.setState(state);
	}

}
