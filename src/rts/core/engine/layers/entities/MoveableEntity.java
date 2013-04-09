package rts.core.engine.layers.entities;

import java.awt.Point;
import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.Path;

import rts.core.engine.Engine;
import rts.core.engine.GameSound;
import rts.core.engine.Utils;
import rts.core.engine.layers.entities.effects.MoveToLocation;
import rts.core.network.ig_udp_containers.EntityState;
import rts.utils.Timer;

public abstract class MoveableEntity extends ActiveEntity implements Mover {

	public static final int EARTH_ONLY = 0;
	public static final int WATER_ONLY = 1;
	public static final int EVERYWHERE = 2;

	public static final int INACTIVE = 0;
	public static final int DEFENSIVE = 1;
	public static final int AGGRESSIVE = 2;

	private static final int MAX_SEEK_ATTEMPT = 5;
	private static final int TIME_BEFORE_SEEK = 1250;

	private int pathNumber;
	private int targetX;
	private int targetY;
	private int attempt;
	private boolean changePath;
	private boolean seekPath;
	private ActiveEntity target;
	private Timer seekTimer;
	private ArrayList<Point> locations;

	protected Path path;
	protected int moveType;
	protected int behavior;
	protected float speed;
	protected boolean locationCorrect;

	public MoveableEntity(Engine engine, int minTecLevel, int layer, int type, int maxLife, int view, int playerId, int teamId, int networkId) {
		super(engine, minTecLevel, layer, type, maxLife, view, playerId, teamId, networkId);
		this.behavior = DEFENSIVE;
		this.seekTimer = new Timer(TIME_BEFORE_SEEK);
		this.locations = new ArrayList<Point>();
		this.locationCorrect = true;
	}

	protected abstract void destroy();

	protected abstract void fireOnTarget(ActiveEntity target);

	protected abstract boolean specialTarget(ActiveEntity target);

	protected abstract boolean canGoToTransport(ActiveEntity target);

    // single unit calls this
	@Override
	public void target(ActiveEntity target, int mx, int my) {
		if (visible) {
			if (!canGoToTransport(target)) {
				if (!specialTarget(target)) {
					if (target.getTeamId() != engine.getPlayer().getTeamId()) {
						if (engine.getMap().isEntityBlocked(mx, my)) {
							Point p = Utils.getCloserPoint(engine.getMap(), this, mx, my);
							mx = p.x;
							my = p.y;
						}
						if (EData.BULLET_POWER[type][target.getType()] != 0) {
							checkTarget(target, mx, my);
						} else {
							this.move(mx * engine.getTileW(), my * engine.getTileH());
						}
						GameSound.moverAttack();
					}
				} else
					GameSound.moverMove();
			}
		}
	}

	private void checkTarget(ActiveEntity target, int mx, int my) {
		if (!(Utils.getDistanceBetween(x, y, target.getX(), target.getY()) < (view - 1) * engine.getTileW())) {
			this.move(mx * engine.getTileW(), my * engine.getTileH());
			if (path != null)
				this.target = target;
		} else {
			this.target = target;
		}
	}

	// Must check new positions to block/unblock map

	@Override
	public void setState(EntityState state) {
		int oldX = (int) (x / engine.getTileW());
		int oldY = (int) (y / engine.getTileH());

		// Get the new position
		super.setState(state);

		if (oldX != ((int) (x / engine.getTileW())) || oldY != ((int) (y / engine.getTileH()))) {
			locations.remove(new Point(oldX, oldY));
			engine.getMap().removeEntityLocation(this, oldX, oldY);
		}

		// Fire
		if (state.fire && (state.targetX != -1 || state.targetY != -1)) {
			ActiveEntity ae = engine.getEntityAt(this, state.targetX, state.targetY);
			if (ae != null) {
				fireOnTarget(ae);
			}
		}

	}

	@Override
	public void updateEntity(GameContainer container, int delta) throws SlickException {

		// By default not shooting and reset target state if not alive
		state.fire = false;
		state.targetX = -1;
		state.targetY = -1;

		if (target != null && (!target.isAlive() || !target.isVisible())) {
			target = null;
		}

		if (path != null) {
			updatePath(delta);
		} else {
			if (seekPath) {
				seekTimer.update(delta);
				if (seekTimer.isTimeComplete()) {
					if (attempt == MAX_SEEK_ATTEMPT) {
						seekPath = false;
						attempt = 0;
					} else {
						seekPath();
						attempt++;
					}
					seekTimer.resetTime();
				}
			}
			// A CAUSE DE PLUSIEURS ENTITES CERTAINES CASES
			// SONT LIBRES VOIR SYNCHRO ACCES MAP
			addLocation((int) x / engine.getTileW(), (int) y / engine.getTileH());

			if (target != null) {
				checkTargetState();
			} else {
				if (engine.isPlayerEntity(playerId)) {
					if (behavior == DEFENSIVE) {
						ActiveEntity ent = engine.getFirstEnemyEntity(this, view);
						if (ent != null) {
							direction = Utils.findDirection(x / engine.getTileW(), y / engine.getTileH(), (int) ent.getX() / engine.getTileW(), (int) ent
									.getY()
									/ engine.getTileH());
							if (!(ent instanceof IBigEntity)) {
								if (EData.BULLET_POWER[type][ent.getType()] != 0) {
									fireOnTarget(ent);
									state.fire = true;
									state.targetX = ent.getX();
									state.targetY = ent.getY();
								}
							}
						}
					} else {
						if (behavior == AGGRESSIVE && target == null) {
							ActiveEntity ent = engine.getFirstEnemyEntity(this, view);
							if (ent != null) {
								Point p = Utils.getCloserPoint(engine.getMap(), this, (int) ent.getX() / engine.getTileW(), (int) ent.getY()
										/ engine.getTileH());
								target(ent, p.x, p.y);
							}
						}
					}
				}
			}
		}
	}

	private void addLocation(int x, int y) {
		Point p = new Point(x, y);
		if (!locations.contains(p)) {
			engine.getMap().addEntityLocation(this, true, x, y);
			locations.add(new Point(x, y));
		}
	}

	private boolean closeFromTarget() {
		int tx;
		int ty;
		if (target instanceof IBigEntity) {
			tx = (int) ((IBigEntity) target).getRealX() / engine.getTileW();
			ty = (int) ((IBigEntity) target).getRealY() / engine.getTileH();

		} else {
			tx = (int) target.getX() / engine.getTileW();
			ty = (int) target.getY() / engine.getTileH();
		}

		if (Utils.getDistanceBetween(x, y, tx * engine.getTileW(), ty * engine.getTileH()) < (view - 1) * engine.getTileW()) {
			direction = Utils.findDirection(x / engine.getTileW(), y / engine.getTileH(), tx, ty);
			fireOnTarget(target);
			state.fire = true;
			state.targetX = target.getX();
			state.targetY = target.getY();
			return true;
		}

		return false;
	}

	private void checkTargetState() {
		if (target.isAlive()) {
			if (!closeFromTarget()) {
				Point p = Utils.getCloserPoint(engine.getMap(), this, (int) target.getX() / engine.getTileW(), (int) target.getY() / engine.getTileH());
				if (!engine.getMap().blocked(this, p.x, p.y)) {
					targetX = p.x * engine.getTileW();
					targetY = p.y * engine.getTileH();
					seekPath();
				}
			}
		} else {
			target = null;
		}
	}

	private void updatePath(int delta) {
		if (path.getLength() > pathNumber) {

			float cx = x / engine.getTileW();
			float cy = y / engine.getTileH();
			int nx = path.getStep(pathNumber).getX();
			int ny = path.getStep(pathNumber).getY();

			if ((cx < nx + 0.1f && cx > nx - 0.1f) && (cy < ny + 0.1f && cy > ny - 0.1f)) {
				this.x = nx * engine.getTileW();
				this.y = ny * engine.getTileH();

				locationCorrect = true;

				// On lib�re la case pr�c�dente
				engine.getMap().removeEntityLocation(this, path.getStep(pathNumber - 1).getX(), path.getStep(pathNumber - 1).getY());
				locations.remove(new Point(path.getStep(pathNumber - 1).getX(), path.getStep(pathNumber - 1).getY()));

				// On s'assure que la case suivante est toujours accessible
				if (path.getLength() > pathNumber + 1) {
					if (engine.getMap().blocked(this, path.getStep(pathNumber + 1).getX(), path.getStep(pathNumber + 1).getY())) {
						changePath = true;
					}
				}

				if (changePath) {
					seekPath();
					changePath = false;
				} else {
					pathNumber++;

					checkFowFromView();

					if (target != null && closeFromTarget()) {
						seekPath = false;
						path = null;
					} else {
						if (path.getLength() > pathNumber) {
							addLocation(path.getStep(pathNumber).getX(), path.getStep(pathNumber).getY());
						} else {
							seekPath = false;
							path = null;
						}
					}
				}
			} else {
				locationCorrect = false;
				// Find Direction
				direction = Utils.findDirection(cx, cy, nx, ny);
				this.x += speed * delta * Utils.DIRECTIONS[direction][0];
				this.y += speed * delta * Utils.DIRECTIONS[direction][1];
			}

		}
	}

	public void move(int mx, int my) {
		// Before moving check that the current move is finish
		targetX = mx;
		targetY = my;
		seekPath = true;
		if (path == null) {
			seekPath();
		} else
			changePath = true;
	}

	private void seekPath() {
		path = engine.getPathFinder().findPath(this, (int) (x / engine.getTileW()), (int) (y / engine.getTileH()), (targetX / engine.getTileW()),
				(targetY / engine.getTileH()));
		this.pathNumber = 1;
		if (path != null) {
			seekPath = false;
			attempt = 0;
			if (path.getLength() > pathNumber) {
				addLocation(path.getStep(pathNumber).getX(), path.getStep(pathNumber).getY());
			}
		}
	}

	public void moveFromPlayerAction(int mx, int my) {
		if (selected && playerId == engine.getPlayer().getId()) {
			target = null;
			engine.addEntity(new MoveToLocation(engine, mx, my));
			move(mx, my);
		}
	}

	@Override
	public void remove() {
		if (visible) {
			for (int i = 0; i < locations.size(); i++) {
				engine.getMap().removeEntityLocation(this, locations.get(i).x, locations.get(i).y);
			}
			destroy();
		}
		engine.getMap().removeEntityLocation(this, (int) x / engine.getTileW(), (int) y / engine.getTileH());
		engine.removeEntity(this);
	}

	public int getMoveType() {
		return moveType;
	}

	public void setBehavior(int behavior) {
		this.behavior = behavior;
	}

	public boolean isStopped() {
		return path == null;
	}
}
