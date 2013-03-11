package rts.core.engine.layers.entities.others;

import java.awt.Point;
import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.gui.GUIContext;

import rts.core.engine.Engine;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.EData;
import rts.core.engine.layers.entities.buildings.Building;
import rts.core.engine.layers.entities.effects.Explosion;
import rts.utils.ResourceManager;

public class Wall extends Building {

	private static int[][] DIRECTIONS = new int[][] { { 0, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 }, };

	private Image[][] currentImages;
	private SpriteSheet sheet;
	private ArrayList<Point> othersValidLocation;

	public Wall(Engine engine, int playerId, int teamId, int networkId) {
		super(engine, EData.WALL, true, playerId, teamId, networkId);
		this.width = 20;
		this.height = 20;
		this.sheet = ResourceManager.getSpriteSheet("walls");
		this.currentImages = new Image[2][3];
		this.othersValidLocation = new ArrayList<Point>();

		// Default case

		this.currentImages[0][0] = sheet.getSprite(0, 0);
		this.currentImages[1][0] = sheet.getSprite(2, 0);

		this.currentImages[0][1] = sheet.getSprite(0, 1);
		this.currentImages[1][1] = sheet.getSprite(2, 1);

		this.currentImages[0][2] = sheet.getSprite(0, 2);
		this.currentImages[1][2] = sheet.getSprite(2, 2);

		calcViewLimit(width / 20, height / 20);
	}

	// CONFIGURATION CASES

	public void checkWallCase(boolean checkAround) {

		int x = (int) this.x / engine.getTileW();
		int y = (int) this.y / engine.getTileH();

		checkLeftUpCorner(x, y, checkAround);
		checkRightUpCorner(x, y, checkAround);
		checkLeftDownCorner(x, y, checkAround);
		checkRightDownCorner(x, y, checkAround);
	}

	private void checkLeftUpCorner(int x, int y, boolean checkAround) {
		Wall up = engine.getMap().getWall(x, y - 1);
		Wall left = engine.getMap().getWall(x - 1, y);
		Wall upLeft = engine.getMap().getWall(x - 1, y - 1);

		if (up != null && left != null && upLeft != null) {
			this.currentImages[0][0] = sheet.getSprite(9, 0);
			if (checkAround) {
				up.checkWallCase(false);
				left.checkWallCase(false);
				upLeft.checkWallCase(false);
			}
		} else {
			if (up != null && left != null) {
				this.currentImages[0][0] = sheet.getSprite(4, 1);
				if (checkAround) {
					up.checkWallCase(false);
					left.checkWallCase(false);
				}
			} else {
				if (left != null) {
					this.currentImages[0][0] = sheet.getSprite(1, 0);
					if (checkAround) {
						left.checkWallCase(false);
					}
				} else {
					if (up != null) {
						this.currentImages[0][0] = sheet.getSprite(3, 0);
						if (checkAround) {
							up.checkWallCase(false);
						}
					} else {
						this.currentImages[0][0] = sheet.getSprite(0, 0);
					}
				}
			}
		}
	}

	private void checkRightUpCorner(int x, int y, boolean checkAround) {
		Wall up = engine.getMap().getWall(x, y - 1);
		Wall right = engine.getMap().getWall(x + 1, y);
		Wall upRight = engine.getMap().getWall(x + 1, y - 1);

		if (up != null && right != null && upRight != null) {
			this.currentImages[1][0] = sheet.getSprite(9, 0);
			if (checkAround) {
				up.checkWallCase(false);
				right.checkWallCase(false);
				upRight.checkWallCase(false);
			}
		} else {
			if (up != null && right != null) {
				this.currentImages[1][0] = sheet.getSprite(3, 1);
				if (checkAround) {
					up.checkWallCase(false);
					right.checkWallCase(false);
				}
			} else {
				if (right != null) {
					this.currentImages[1][0] = sheet.getSprite(1, 0);
					if (checkAround) {
						right.checkWallCase(false);
					}
				} else {
					if (up != null) {
						this.currentImages[1][0] = sheet.getSprite(4, 0);
						if (checkAround) {
							up.checkWallCase(false);
						}
					} else {
						this.currentImages[1][0] = sheet.getSprite(2, 0);
					}
				}
			}
		}

	}

	private void checkLeftDownCorner(int x, int y, boolean checkAround) {
		Wall down = engine.getMap().getWall(x, y + 1);
		Wall left = engine.getMap().getWall(x - 1, y);
		Wall downLeft = engine.getMap().getWall(x - 1, y + 1);

		if (down != null && left != null && downLeft != null) {
			this.currentImages[0][1] = sheet.getSprite(9, 0);
			this.currentImages[0][2] = sheet.getSprite(9, 0);
			if (checkAround) {
				down.checkWallCase(false);
				left.checkWallCase(false);
				downLeft.checkWallCase(false);
			}
		} else {
			if (down != null && left != null) {
				this.currentImages[0][1] = sheet.getSprite(4, 2);
				this.currentImages[0][2] = sheet.getSprite(3, 0);
				if (checkAround) {
					down.checkWallCase(false);
					left.checkWallCase(false);
				}
			} else {
				if (left != null) {
					this.currentImages[0][1] = sheet.getSprite(1, 1);
					this.currentImages[0][2] = sheet.getSprite(1, 2);
					if (checkAround) {
						left.checkWallCase(false);
					}
				} else {
					if (down != null) {
						this.currentImages[0][1] = sheet.getSprite(3, 0);
						this.currentImages[0][2] = sheet.getSprite(3, 0);
						if (checkAround) {
							down.checkWallCase(false);
						}
					} else {
						this.currentImages[0][1] = sheet.getSprite(0, 1);
						this.currentImages[0][2] = sheet.getSprite(0, 2);
					}
				}
			}
		}
	}

	private void checkRightDownCorner(int x, int y, boolean checkAround) {
		Wall down = engine.getMap().getWall(x, y + 1);
		Wall right = engine.getMap().getWall(x + 1, y);
		Wall downRight = engine.getMap().getWall(x + 1, y + 1);

		if (down != null && right != null && downRight != null) {
			this.currentImages[1][1] = sheet.getSprite(9, 0);
			this.currentImages[1][2] = sheet.getSprite(9, 0);
			if (checkAround) {
				down.checkWallCase(false);
				right.checkWallCase(false);
				downRight.checkWallCase(false);
			}
		} else {
			if (down != null && right != null) {
				this.currentImages[1][1] = sheet.getSprite(3, 2);
				this.currentImages[1][2] = sheet.getSprite(4, 0);
				if (checkAround) {
					down.checkWallCase(false);
					right.checkWallCase(false);
				}
			} else {
				if (right != null) {
					this.currentImages[1][1] = sheet.getSprite(1, 1);
					this.currentImages[1][2] = sheet.getSprite(1, 2);
					if (checkAround) {
						right.checkWallCase(false);
					}
				} else {
					if (down != null) {
						this.currentImages[1][1] = sheet.getSprite(4, 0);
						this.currentImages[1][2] = sheet.getSprite(4, 0);
						if (checkAround) {
							down.checkWallCase(false);
						}
					} else {
						this.currentImages[1][1] = sheet.getSprite(2, 1);
						this.currentImages[1][2] = sheet.getSprite(2, 2);
					}
				}
			}
		}
	}

	// END CASES

	@Override
	public void renderLocationOnMap(GUIContext container, Graphics g) {
		super.renderLocationOnMap(container, g);
		if (validLocation) {
			othersValidLocation.clear();
			for (int i = 0; i < 4; i++) {
				ArrayList<Point> a = new ArrayList<Point>();
				int[] direction = DIRECTIONS[i];
				boolean findWall = false;
				for (int j = 5; j > 0; j--) {
					int x = ((int) (this.x / engine.getTileW()) + (direction[0] * j));
					int y = ((int) (this.y / engine.getTileH()) + (direction[1] * j));
					ActiveEntity ae = engine.getMap().getEntityAt(null, x, y);
					if (ae != null) {
						if (ae instanceof Wall && engine.isPlayerEntity(ae.getPlayerId())) {
							findWall = true;
							continue;
						}
					}
					if (findWall) {
						if (!engine.getMap().isEntityBlocked(x, y) && !engine.getMap().isBlocked(x, y)) {
							a.add(new Point(x, y));
						} else {
							findWall = false;
							a.clear();
						}
					}
				}
				othersValidLocation.addAll(a);
			}
			g.setColor(FADE_BLUE);
			g.translate(engine.getXScrollDecal(), engine.getYScrollDecal());
			for (int i = 0; i < othersValidLocation.size(); i++) {
				g.fillRect(othersValidLocation.get(i).x * engine.getTileW(), othersValidLocation.get(i).y * engine.getTileW(), 20, 20);
			}
			g.translate(-engine.getXScrollDecal(), -engine.getYScrollDecal());
		}
	}

	@Override
	protected void renderBuilding(GameContainer container, Graphics g) throws SlickException {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 3; j++) {
				g.drawImage(currentImages[i][j], x + i * 10, y + j * 7);
			}
		}
	}

	@Override
	public void setLocation(float x, float y) {
		this.x = x;
		this.y = y;

		checkFowFromView();
		engine.getMap().addEntityLocation(this, true, (int) x / engine.getTileW(), (int) y / engine.getTileH());
		engine.getMap().setWall(this, (int) x / engine.getTileW(), (int) y / engine.getTileH());
		checkWallCase(true);
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
	protected void removeBuilding() {
		engine.addEntity(new Explosion(engine, Layer.SECOND_EFFECT, Explosion.NORMAL_2, x * engine.getTileW(), y * engine.getTileH()));
		engine.getMap().removeEntityLocation(this, (int) x / engine.getTileW(), (int) y / engine.getTileH());
		engine.getMap().removeWall((int) x / engine.getTileW(), (int) y / engine.getTileH());
		engine.removeEntity(this);
		checkWallCase(true);
	}

	public ArrayList<Point> getOthersValidLocation() {
		return othersValidLocation;
	}
}
