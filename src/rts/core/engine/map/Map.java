package rts.core.engine.map;

import java.awt.Point;
import java.io.InputStream;
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

import rts.core.engine.Engine;
import rts.core.engine.Player;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.EData;
import rts.core.engine.layers.entities.EntityGenerator;
import rts.core.engine.layers.entities.MoveableEntity;
import rts.core.engine.layers.entities.effects.Lava;
import rts.core.engine.layers.entities.effects.Swamp;
import rts.core.engine.layers.entities.others.Wall;
import rts.utils.Configuration;
import rts.utils.ResourceManager;

/**
 * This class represent a map object.
 * 
 * The map is defined in 5 layers
 * 
 * 0. First decorating layer 1. Second decorating layer (objects) 2. Earth and
 * water collision layer 3. Spawn layer 4. Start entities
 * 
 * We only render an image (layer 1 & 2) that represent the map (because it's
 * less resources consuming)
 * 
 * @author Vincent PIRAULT
 * 
 */
public class Map extends TiledMap implements TileBasedMap, Comparable<Map> {

	// Debug
	private static final boolean ENABLE_FOG = true;
	private static final Color FADE_RED = new Color(255, 0, 0, 100);

	private boolean[][] blocked;
	private boolean[][] water;
	private boolean needScroll;
	private boolean visibleFow;
	private EntityLocation[][] entitiesLocations;
	private Wall wallLocations[][];
	private ArrayList<Point> spawns;
	private ArrayList<Ent> entites;
	private Image background;
	private String name;
	private ArrayList<ActiveEntity> rendererEntities;

	// FOW

	private boolean enableFow;
	private Image fowImage;
	private boolean[][] visibleLocations;

	public Map(String name, InputStream ref, String tilePath) throws SlickException {
		super(ref, tilePath);
		this.name = name;
		this.blocked = new boolean[width][height];
		this.water = new boolean[width][height];
		this.entitiesLocations = new EntityLocation[width][height];
		this.wallLocations = new Wall[width][height];
		this.spawns = new ArrayList<Point>();
		this.entites = new ArrayList<Ent>();
		this.visibleLocations = new boolean[width][height];
		this.rendererEntities = new ArrayList<ActiveEntity>();
		this.enableFow = ENABLE_FOG;
		this.visibleFow = true;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				entitiesLocations[x][y] = new EntityLocation();

				// Collisions
				int tileID = this.getTileId(x, y, 2);
				String value = this.getTileProperty(tileID, "collision", "false");
				blocked[x][y] = value.equals("true");

				// Water
				value = this.getTileProperty(tileID, "water", "false");
				water[x][y] = value.equals("true");

				// Spawns
				tileID = this.getTileId(x, y, 3);
				value = this.getTileProperty(tileID, "spawn", "false");
				if (value.equals("true")) {
					spawns.add(new Point(x, y));
				}

				// Entities
				tileID = this.getTileId(x, y, 4);
				int type = Integer.parseInt(this.getTileProperty(tileID, "type", "-1"));
				if (type >= 0) {
					Ent ent = new Ent();
					ent.type = type;
					ent.life = Integer.parseInt(this.getTileProperty(tileID, "life", "100"));
					ent.x = x * tileWidth;
					ent.y = y * tileHeight;
					entites.add(ent);
				}
			}
		}
		background = ResourceManager.getImage(name);
		fowImage = ResourceManager.getImage("fow");
	}

	public void init(Engine engine) {
		// Need scroll ?
		needScroll = (getWidthInPixel() > engine.getContainer().getWidth() || getHeightInPixel() > engine.getContainer().getHeight());

		// Reset ents location
		for (int i = 0; i < entitiesLocations.length; i++) {
			for (int j = 0; j < entitiesLocations[i].length; j++) {
				entitiesLocations[i][j].clear();
			}
		}
		// Reset FOW
		for (int i = 0; i < visibleLocations.length; i++) {
			for (int j = 0; j < visibleLocations[i].length; j++) {
				visibleLocations[i][j] = false;
			}
		}

		if (engine.isNetwork()) {
			// Create own builder
			Player player = engine.getPlayer();
			Point p = spawns.get(player.getSpawn());
			engine.getNetworkManager().sendCreateEntity(EData.MOVER_BUILDER, player.getId(), player.getTeamId(), p.x * tileWidth, p.y * tileHeight);
			// Center scroll in builder
			engine.centerScrollOn(p.x * tileWidth, p.y * tileHeight);

			// Add 3 entities to begin
			engine.getNetworkManager().sendCreateEntity(EData.MOVER_SOLDIER, player.getId(), player.getTeamId(), (p.x - 3) * tileWidth, p.y * tileHeight);
			engine.getNetworkManager().sendCreateEntity(EData.MOVER_SOLDIER, player.getId(), player.getTeamId(), (p.x + 3) * tileWidth, p.y * tileHeight);
			engine.getNetworkManager().sendCreateEntity(EData.MOVER_SCOUT, player.getId(), player.getTeamId(), p.x * tileWidth, (p.y + 3) * tileHeight);

			if (engine.getNetworkManager().isServer()) {
				// Send entity associated to the map
				for (int i = 0; i < entites.size(); i++) {
					if (!effectEntity(engine, entites.get(i)))
						engine.getNetworkManager().sendCreateEntity(entites.get(i).type, -1, -1, entites.get(i).life, entites.get(i).x, entites.get(i).y);
				}
			}

		} else {
			for (int i = 0; i < entites.size(); i++) {
				if (!effectEntity(engine, entites.get(i))) {
					ActiveEntity ae = EntityGenerator.createActiveEntityFromMap(engine, entites.get(i).type, entites.get(i).x, entites.get(i).y);
					ae.setLife(entites.get(i).life);
					engine.addEntity(ae);
				}
			}
		}
	}

	private boolean effectEntity(Engine engine, Ent ent) {
		if (ent.type == EData.LAVA_EFFECT) {
			engine.addEntity(new Lava(engine, ent.x, ent.y));
			return true;
		} else {
			if (ent.type == EData.SWAMP_EFFECT) {
				engine.addEntity(new Swamp(engine, ent.x, ent.y));
				return true;
			}
		}
		return false;
	}

	// Walls

	public Wall getWall(int x, int y) {
		if (x >= 0 && x < width && y >= 0 && y < height)
			return wallLocations[x][y];
		else
			return null;
	}

	public void setWall(Wall wall, int x, int y) {
		if (x >= 0 && x < width && y >= 0 && y < height)
			wallLocations[x][y] = wall;
	}

	public void removeWall(int x, int y) {
		if (x >= 0 && x < width && y >= 0 && y < height)
			wallLocations[x][y] = null;
	}

	// PathFinding

	public boolean blocked(MoveableEntity e, int tx, int ty) {
		if (tx >= 0 && tx < width && ty >= 0 && ty < height) {
			if (e.getMoveType() == MoveableEntity.EVERYWHERE)
				return entitiesLocations[tx][ty].isBlocked();
			else {
				if (e.getMoveType() == MoveableEntity.EARTH_ONLY) {
					return blocked[tx][ty] || entitiesLocations[tx][ty].isBlocked() || water[tx][ty];
				} else {
					if (e.getMoveType() == MoveableEntity.WATER_ONLY) {
						return blocked[tx][ty] || entitiesLocations[tx][ty].isBlocked() || !water[tx][ty];
					}
				}
			}
		}
		return true;
	}

	public boolean isBlocked(int x, int y) {
		if (x >= 0 && x < width && y >= 0 && y < height)
			return blocked[x][y];
		else
			return true;
	}

	@Override
	public boolean blocked(PathFindingContext context, int tx, int ty) {
		if (context.getMover() instanceof MoveableEntity) {
			return blocked((MoveableEntity) context.getMover(), tx, ty);
		}
		return true;
	}

	@Override
	public float getCost(PathFindingContext context, int tx, int ty) {
		// Le cout d'un chemin d�pend du brouillard de guerre
		//(visibleLocations[tx][ty]) ? 0 : 10;
		return 1;
	}

	@Override
	public void pathFinderVisited(int x, int y) {

	}

	public void blockWithWater(int x, int y) {
		water[x][y] = true;
	}

	public void freeWithWater(int x, int y) {
		water[x][y] = false;
	}

	public boolean isWater(int x, int y) {
		if (x >= 0 && x < width && y >= 0 && y < height)
			return water[x][y];
		else
			return false;
	}

	public void addEntityLocation(ActiveEntity entity, boolean block, int x, int y) {
		entitiesLocations[x][y].addEntity(entity, block);
	}

	public void removeEntityLocation(ActiveEntity entity, int x, int y) {
		entitiesLocations[x][y].removeEntity(entity);
	}

	public boolean isEntityBlocked(int x, int y) {
		if (x >= 0 && x < width && y >= 0 && y < height)
			return entitiesLocations[x][y].isBlocked();
		else
			return true;
	}

	public boolean isEntityOccupy(int x, int y) {
		if (x >= 0 && x < width && y >= 0 && y < height)
			return entitiesLocations[x][y].isOccupy();
		else
			return true;
	}

	public ActiveEntity getEntityAt(ActiveEntity entity, int x, int y) {
		if (x >= 0 && x < width && y >= 0 && y < height) {
			return (entitiesLocations[x][y].getLastEntity() == entity) ? null : entitiesLocations[x][y].getLastEntity();
		} else
			return null;
	}

	// Rendering

	public void render(Graphics g, GameContainer container, int decalX, int decalY) {
		if (needScroll) {
			g.drawImage(background.getSubImage(decalX, decalY, (decalX > (getWidthInPixel() - container.getWidth())) ? container.getWidth()
					- (decalX - (getWidthInPixel() - container.getWidth())) : decalX + container.getWidth(), decalY + container.getHeight()), 0, 0);
		} else
			g.drawImage(background, 0, 0);
		if (Configuration.isDebug()) {
			// TODO DEBUG REMOVE
			g.translate(-decalX, -decalY);
			g.setColor(FADE_RED);
			for (int i = 0; i < blocked.length; i++) {
				for (int j = 0; j < blocked[i].length; j++) {
					if (blocked[i][j] || entitiesLocations[i][j].isBlocked()) {
						g.fillRect(i * 20, j * 20, 20, 20);
					}
				}
			}
			g.translate(decalX, decalY);
		}

	}

	public void renderFow(Graphics g, GameContainer container, int decalX, int decalY) {
		if (enableFow && visibleFow) {
			int fx = (decalX / tileWidth - 1 < 0) ? 0 : decalX / tileWidth - 1;
			int fy = (decalY / tileHeight - 1 < 0) ? 0 : decalY / tileHeight - 1;
			int ex = (fx + (container.getWidth() / tileWidth) + 2 > width) ? width : (fx + (container.getWidth() / tileWidth)) + 2;
			int ey = (fy + (container.getHeight() / tileHeight) + 2 > height) ? height : (fy + (container.getHeight() / tileHeight)) + 2;

			for (int i = fx; i < ex; i++) {
				for (int j = fy; j < ey; j++) {
					if (!visibleLocations[i][j])
						g.drawImage(fowImage, (i * tileWidth) - 20, (j * tileHeight) - 20);
				}
			}
		}
	}

	public void renderMiniMap(Graphics g, int x, int y, int width, int height, boolean drawFromMenu) {
		g.drawImage(background.getScaledCopy(width, height), x, y);

		float tw = ((float) width * tileWidth) / getWidthInPixel();
		float th = ((float) height * tileHeight) / getHeightInPixel();

		if (drawFromMenu) {
			g.setColor(Color.red);
			for (int i = 0; i < spawns.size(); i++) {
				g.drawString("" + (i + 1), (x + spawns.get(i).x * tw) - 2 * tw, (y + spawns.get(i).y * th) - 2 * th);
			}
		} else {
			rendererEntities.clear();
			for (int i = 0; i < visibleLocations.length; i++) {
				for (int j = 0; j < visibleLocations[i].length; j++) {
					if (!visibleLocations[i][j] && enableFow && visibleFow) {
						g.setColor(Color.black);
						g.fillRect(x + i * tw, y + j * th, tw, th);
					} else {
						ActiveEntity ae = entitiesLocations[i][j].getLastEntity();
						if (ae != null && !rendererEntities.contains(ae)) {
							ae.renderOnMiniMap(g, x + i * tw, y + j * th, tw, th);
							rendererEntities.add(ae);
						}
					}
				}
			}
		}
	}

	public boolean isNeededScroll() {
		return needScroll;
	}

	public boolean fogOn(int x, int y) {
		if (!enableFow || (enableFow && !visibleFow)) {
			return false;
		} else {
			if (x >= 0 && x < width && y >= 0 && y < height) {
				return !visibleLocations[x][y];
			}
			return true;
		}
	}

	public void setVisibleFow(boolean visibleFow) {
		this.visibleFow = visibleFow;
	}

	public void showFow(int x, int y) {
		if (x >= 0 && x < width && y >= 0 && y < height)
			visibleLocations[x][y] = true;
	}

	public int getWidthInPixel() {
		return width * tileWidth;
	}

    public String getName() {
        return name;
    }

	public int getHeightInPixel() {
		return height * tileHeight;
	}

	@Override
	public int getHeightInTiles() {
		return height;
	}

	@Override
	public int getWidthInTiles() {
		return width;
	}

	public boolean isEnableFow() {
		return enableFow;
	}

	public int getNumberOfSpawns() {
		return spawns.size();
	}

	@Override
	public String toString() {
		return "[" + (spawns.size() - 1) + " Opponents] " + name;
	}

	private static class Ent {
		public int type;
		public int life;
		public int x;
		public int y;
	}

	private static class EntityLocation {

		private ArrayList<Entity> entities;

		public EntityLocation() {
			entities = new ArrayList<Entity>();
		}

		private boolean contain(ActiveEntity e) {
			for (int i = 0; i < entities.size(); i++) {
				if (entities.get(i).entity == e)
					return true;
			}
			return false;
		}

		public void addEntity(ActiveEntity e, boolean block) {
			if (!contain(e))
				entities.add(new Entity(e, block));
		}

		public void removeEntity(ActiveEntity e) {
			for (int i = 0; i < entities.size(); i++) {
				if (entities.get(i).entity == e) {
					entities.remove(i);
					break;
				}
			}
		}

		public ActiveEntity getLastEntity() {
			if (entities.isEmpty())
				return null;
			else
				return entities.get(entities.size() - 1).entity;
		}

		public boolean isBlocked() {
			for (int i = 0; i < entities.size(); i++) {
				if (entities.get(i).blocked)
					return true;
			}
			return false;
		}

		public boolean isOccupy() {
			return !entities.isEmpty();
		}

		public void clear() {
			entities.clear();
		}

		private static class Entity {
			private ActiveEntity entity;
			private boolean blocked;

			public Entity(ActiveEntity entity, boolean blocked) {
				super();
				this.entity = entity;
				this.blocked = blocked;
			}
		}
	}

	@Override
	public int compareTo(Map map) {
		return this.spawns.size() - map.spawns.size();
	}

}
