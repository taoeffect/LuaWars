package rts.core.engine.layers;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import rts.core.engine.Engine;
import rts.core.engine.Utils;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.IEntity;
import rts.core.engine.layers.entities.INetworkEntity;
import rts.core.engine.layers.entities.MoveableEntity;
import rts.core.engine.layers.entities.buildings.Building;
import rts.core.engine.layers.entities.others.Mineral;
import rts.core.network.ig_udp_containers.EntityState;

public class Layer {

	public static final int FIRST_EFFECT = 0;
	public static final int EARTH_MARINE_ENT = 1;
	public static final int SECOND_EFFECT = 2;
	public static final int FLIGHT_ENT = 3;
	public static final int THIRD_EFFECT = 4;

	private int id;
	private boolean visible;
    private ArrayList<IEntity> array;
	private Engine engine;

	public Layer(Engine engine, int id) {
		this.engine = engine;
		this.id = id;
		this.array = new ArrayList<IEntity>();
		this.visible = true;
	}

    // TRUNG NGUYEN need to get all the entities to use in the engine to select closest units to a point
    public ArrayList<IEntity> getArray() {
        return array;
    }

	public void render(GameContainer container, Graphics g) throws SlickException {
		if (visible) {
			for (int i = 0; i < array.size(); i++) {
				array.get(i).render(container, g);
			}
		}
	}

	public void renderInfos(Graphics g) {
		if (visible) {
			for (int i = 0; i < array.size(); i++) {
				if (array.get(i) instanceof ActiveEntity) {
					((ActiveEntity) array.get(i)).renderInfos(g);
				}
			}
		}
	}

	public void updateAll(GameContainer container, int delta) throws SlickException {
		for (int i = 0; i < array.size(); i++) {
			array.get(i).update(container, delta);

			// Must check the size because an update can remove an entity
			if (i < array.size() && array.get(i) instanceof ActiveEntity) {
				engine.addEntToCount(((ActiveEntity) array.get(i)).getPlayerId());
			}
		}
	}

	public void addEntity(IEntity e) {
		array.add(e);
	}

	public void removeEntity(IEntity e) {
		array.remove(e);
	}

	public void removeAllEntity(int playerId) {
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i) instanceof ActiveEntity && ((ActiveEntity) array.get(i)).getPlayerId() == playerId) {
				((ActiveEntity) array.get(i)).remove();
				i--;
			}
		}

	}

	// Network

	public void removeEntity(int networkId) {
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i) instanceof ActiveEntity && ((ActiveEntity) array.get(i)).getNetworkID() == networkId) {
				((ActiveEntity) array.get(i)).setLife(0);
				((ActiveEntity) array.get(i)).remove();
				break;
			}
		}
	}

	public void updateEntityState(EntityState entityState) {
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i) instanceof ActiveEntity && ((ActiveEntity) array.get(i)).getNetworkID() == entityState.networkId) {
				((ActiveEntity) array.get(i)).setState(entityState);
			}
		}
	}

	// End Network

	public void deselectAllEntities() {
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i) instanceof ActiveEntity) {
				((ActiveEntity) array.get(i)).deselected();
			}
		}
	}

	public ArrayList<ActiveEntity> getPlayerSelectedEntities(int playerId) {
		ArrayList<ActiveEntity> a = new ArrayList<ActiveEntity>();
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i) instanceof ActiveEntity && ((ActiveEntity) array.get(i)).isSelected() && ((ActiveEntity) array.get(i)).getPlayerId() == playerId) {
				a.add((ActiveEntity) array.get(i));
			}
		}
		return a;
	}

	public ArrayList<EntityState> getPlayerEntitiesState(int playerId) {
		ArrayList<EntityState> a = new ArrayList<EntityState>();
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i) instanceof ActiveEntity && ((ActiveEntity) array.get(i)).getPlayerId() == playerId) {
				a.add(((ActiveEntity) array.get(i)).getState());
			}
		}
		return a;
	}

	public ArrayList<MoveableEntity> getSelectedMoveableEntities(int x, int y) {
		ArrayList<MoveableEntity> a = new ArrayList<MoveableEntity>();
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i) instanceof MoveableEntity && ((ActiveEntity) array.get(i)).isSelected()) {
				a.add((MoveableEntity) array.get(i));
			}
		}
		return a;
	}

	public void selectEntitiesBetween(int sx, int sy, int mx, int my) {
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i) instanceof ActiveEntity && !(array.get(i) instanceof Building)) {
				ActiveEntity e = (ActiveEntity) array.get(i);
				if (e.getPlayerId() == engine.getPlayer().getId() && (e.getX() + e.getWidth() > sx && e.getX() + e.getWidth() < mx)
						&& (e.getY() + e.getHeight() > sy && e.getY() + e.getHeight() < my)) {
					e.selected();
				} else
					e.deselected();
			}
		}
	}

	public ArrayList<Building> getPlayerBuilding() {
		ArrayList<Building> a = new ArrayList<Building>();
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i) instanceof Building) {
				Building b = (Building) array.get(i);
				if (engine.isPlayerEntity(b.getPlayerId())) {
					a.add(b);
				}
			}
		}
		return a;
	}

	public void removeNetworkEntity(int networkId) {
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i) instanceof INetworkEntity && ((INetworkEntity) array.get(i)).getNetworkID() == networkId) {
				array.remove(i);
				break;
			}
		}
	}

	public Mineral getCloserMineral(ActiveEntity entity) {
		Mineral mineral = null;
		float lastDist = 0;
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i) instanceof Mineral) {
				float newDistance = Utils.getDistanceBetween(entity.getX(), entity.getY(), array.get(i).getX(), array.get(i).getY());
				if (mineral == null) {
					mineral = (Mineral) array.get(i);
					lastDist = newDistance;
				} else {
					if (newDistance < lastDist
							&& !engine.getMap().isEntityBlocked((int) array.get(i).getX() / engine.getTileW(), (int) array.get(i).getY() / engine.getTileH())) {
						mineral = (Mineral) array.get(i);
						lastDist = newDistance;
					}
				}
			}
		}
		return mineral;
	}

	public void clear() {
		array.clear();
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public int getId() {
		return id;
	}

}
