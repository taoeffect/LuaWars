package rts.core.engine.layers.entities;

import java.awt.Point;
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import rts.core.engine.Engine;
import rts.core.engine.Utils;
import rts.core.network.ig_udp_containers.EntityState;
import rts.utils.Timer;

public abstract class ActiveEntity extends BasicEntity implements INetworkEntity {

	public static final int TEC_LEVEL_1 = 0;
	public static final int TEC_LEVEL_2 = 1;
	public static final int TEC_LEVEL_3 = 2;
	public static final int TEC_LEVEL_4 = 3;

	private static final int TIME_BEFORE_MOUSEOVER = 1000;

	private static final Color TRANS_GREEN = new Color(0, 255, 0, 100);
	private static final Color TRANS_RED = new Color(255, 0, 0, 100);
	private static final Color TRANS_YELLOW = new Color(255, 255, 0, 100);
	private static final Color TRANS_BLACK = new Color(255, 255, 255, 100);
	private static final Color TRANS_CYAN = new Color(51, 51, 102, 200);

	protected int type;
	protected int maxLife;
	protected int life;
	protected int playerId;
	protected int teamId;
	protected int networkId;
	protected int direction;
	protected int view;
	protected int minTecLevel;
	protected boolean selected;
	protected boolean mouseOver;
	protected boolean weak;
	protected boolean dying;
	protected boolean visible;
	protected EntityState state;
	protected Color color;
	protected ArrayList<Point> viewLimit;

	private String name;
	private Timer mouseOverTimer;
	private boolean remove;
	private int diX;
	private int diY;

	public ActiveEntity(Engine engine, int minTecLevel, int layer, int type, int maxLife, int view, int playerId, int teamId, int networkId) {
		super(engine, layer);
		this.minTecLevel = minTecLevel;
		this.type = type;
		this.maxLife = maxLife;
		this.life = maxLife;
		this.playerId = playerId;
		this.teamId = teamId;
		this.networkId = networkId;
		this.state = new EntityState();
		this.state.life = life;
		this.state.networkId = networkId;
		this.view = view;
		this.visible = true;
		this.viewLimit = new ArrayList<Point>();
		this.mouseOverTimer = new Timer(TIME_BEFORE_MOUSEOVER);
		this.diX = -1;
		this.diY = -1;
		this.name = EData.NAMES[type];
		this.color = Color.white;
	}

	public ActiveEntity(Engine engine, int minTecLevel, int layer, int type, int maxLife, int view, int networkId) {
		this(engine, minTecLevel, layer, type, maxLife, view, -1, -1, networkId);
	}

	public abstract void renderEntity(GameContainer container, Graphics g) throws SlickException;

	public abstract void updateEntity(GameContainer container, int delta) throws SlickException;

	public abstract void remove();

	public abstract void target(ActiveEntity target, int mx, int my);

	public abstract int getTargetCursor(ActiveEntity target, int mx, int my);

	public abstract boolean fogOnUnit();

	public abstract void renderOnMiniMap(Graphics g, float x, float y, float tw, float th);

	public boolean mouseOver() {
		return engine.getMouseX() > x && engine.getMouseX() < x + width && engine.getMouseY() > y && engine.getMouseY() < y + height;
	}

	private void findViewDistance(Point p, int view) {
		boolean contain = false;
		for (int i = 0; i < viewLimit.size(); i++) {
			if (viewLimit.get(i).equals(p)) {
				contain = true;
				break;
			}
		}
		if (!contain) {
			viewLimit.add(p);
		}
		for (int i = 0; i < Utils.AXES.length; i++) {
			Point np = new Point(p.x + Utils.AXES[i][0], p.y + Utils.AXES[i][1]);
			int v = view - 1;
			if (v > 0)
				findViewDistance(np, v);
		}
	}

	protected void calcViewLimit(int sx, int sy) {
		if (engine.getMap().isEnableFow()) {
			if (view > 0)
				findViewDistance(new Point(sx, sy), view);
		}
	}

	public void checkFowFromView() {
		if (engine.getMap().isEnableFow()) {
			if (teamId == engine.getPlayer().getTeamId() && view != 0) {
				for (int i = 0; i < viewLimit.size(); i++) {
					engine.getMap().showFow(viewLimit.get(i).x + (int) (x / engine.getTileW()), viewLimit.get(i).y + (int) (y / engine.getTileH()));
				}
			}
		}
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		if (visible && !fogOnUnit()) {
			renderEntity(container, g);
		}
	}

	public void renderInfos(Graphics g) {
		if (visible && !fogOnUnit()) {
			mouseOver = mouseOver();
			if (isAlive()) {
				if (selected) {
					g.setColor(Color.white);
					g.drawRect(x, y - 1, width, height + 1);
					if (dying) {
						g.setColor(Color.red);
					} else {
						if (weak)
							g.setColor(Color.yellow);
						else
							g.setColor(Color.green);
					}

					if (y < 6) {
						g.fillRect(x, y + height + 1, (life * width) / maxLife, 4);
						g.setColor(Color.black);
						g.drawRect(x, y + height + 1, width, 4);
					} else {
						g.fillRect(x, y - 6, (life * width) / maxLife, 4);
						g.setColor(Color.black);
						g.drawRect(x, y - 6, width, 4);
					}
				} else {
					if (mouseOver) {
						if (dying) {
							g.setColor(TRANS_RED);
						} else {
							if (weak)
								g.setColor(TRANS_YELLOW);
							else
								g.setColor(TRANS_GREEN);
						}

						if (y < 6) {
							g.fillRect(x, y + height + 1, (life * width) / maxLife, 4);
							g.setColor(TRANS_BLACK);
							g.drawRect(x, y + height + 1, width, 4);
						} else {
							g.fillRect(x, y - 6, (life * width) / maxLife, 4);
							g.setColor(TRANS_BLACK);
							g.drawRect(x, y - 6, width, 4);
						}
					}
				}
			}

			if (mouseOver && mouseOverTimer.isTimeComplete()) {
				if (diX == -1 && diY == -1) {
					diX = engine.getMouseX();
					diY = engine.getMouseY();
				}
				g.setColor(TRANS_CYAN);
				g.fillRect(diX, diY, name.length() * 10, 20);
				g.setColor(Color.black);
				g.drawRect(diX, diY, name.length() * 10, 20);
				g.setColor(color);
				g.drawString(name, diX + 2, diY);
			}
		}
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		if (visible) {
			updateEntity(container, delta);
			if (mouseOver) {
				mouseOverTimer.update(delta);
			} else {
				diX = -1;
				diY = -1;
				mouseOverTimer.resetTime();
			}
		}
	}

	@Override
	public int getNetworkID() {
		return networkId;
	}

	@Override
	public EntityState getState() {
		state.layer = layer;
		state.life = life;
		state.x = x;
		state.y = y;
		state.visible = visible;
		state.weak = weak;
		state.dying = dying;
		state.direction = direction;
		return state;
	}

	@Override
	public void setState(EntityState state) {
		this.layer = state.layer;
		this.life = state.life;
		this.x = state.x;
		this.y = state.y;
		this.visible = state.visible;
		this.weak = state.weak;
		this.dying = state.dying;
		this.direction = state.direction;
	}

	public void addLife(int bonus) {
		life = (life + bonus <= maxLife) ? life + bonus : maxLife;
		if ((float) life / (float) maxLife > 0.5f) {
			weak = false;
			dying = false;
		} else {
			if ((float) life / (float) maxLife > 0.2f) {
				dying = false;
			}
		}
		remove = false;
	}

	public void removeLife(int damage) {
		life -= damage;
		if (life <= 0) {
			if (!remove) {
				remove();
				remove = true;
			}
			life = 0;
		} else {
			if ((float) life / (float) maxLife <= 0.2f) {
				dying = true;
			} else {
				if ((float) life / (float) maxLife < 0.5f) {
					weak = true;
				}
			}
		}
	}

	public boolean isAlive() {
		return life > 0;
	}

	public void selected() {
		if (visible)
			this.selected = true;
		else
			selected = false;
	}

	public void deselected() {
		selected = false;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getTeamId() {
		return teamId;
	}

	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}

	public boolean isSelected() {
		return selected;
	}

	public int getType() {
		return type;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getLife() {
		return life;
	}

	public int getMaxLife() {
		return maxLife;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public int getTecLevel() {
		return minTecLevel;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
