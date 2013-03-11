package rts.core.network;

import java.awt.Point;
import java.util.ArrayList;

import rts.core.engine.Engine;
import rts.core.engine.GameSound;
import rts.core.engine.Player;
import rts.core.engine.Utils;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.EData;
import rts.core.engine.layers.entities.EntityGenerator;
import rts.core.engine.layers.entities.MoveableEntity;
import rts.core.engine.layers.entities.buildings.BuildingECreator;
import rts.core.engine.layers.entities.buildings.LightningWeapon;
import rts.core.engine.layers.entities.buildings.MissileSilo;
import rts.core.network.ig_tcp_container.CreateEntityState;
import rts.core.network.ig_tcp_container.DeleteAllEntityState;
import rts.core.network.ig_tcp_container.DeleteEntityState;
import rts.core.network.ig_udp_containers.EntitiesStatePacket;
import rts.core.network.ig_udp_containers.EntityState;

public class NetworkPool {

	private ArrayList<Player> receivePlayerStateList;
	private ArrayList<EntitiesStatePacket> receiveEntitiesPacketStateList;
	private ArrayList<EntityState> receiveEntityStateList;
	private ArrayList<CreateEntityState> receiveCreateStateList;
	private ArrayList<DeleteEntityState> receiveDeleteStateList;
	private ArrayList<DeleteAllEntityState> receiveDeleteAllStateList;

	private ArrayList<CreateEntityState> sendCreateStateList;
	private ArrayList<DeleteEntityState> sendDeleteStateList;

	private int udpPlayerPoolId;
	private int lastPlayerId;

	private int udpPacketPoolId;
	private int lastPacketId;

	public NetworkPool() {
		receivePlayerStateList = new ArrayList<Player>();
		receiveEntitiesPacketStateList = new ArrayList<EntitiesStatePacket>();
		receiveEntityStateList = new ArrayList<EntityState>();
		receiveCreateStateList = new ArrayList<CreateEntityState>();
		receiveDeleteStateList = new ArrayList<DeleteEntityState>();
		receiveDeleteAllStateList = new ArrayList<DeleteAllEntityState>();

		sendCreateStateList = new ArrayList<CreateEntityState>();
		sendDeleteStateList = new ArrayList<DeleteEntityState>();
	}

	public void updateReceive(Engine engine) {

		synchronized (receivePlayerStateList) {
			for (int i = 0; i < receivePlayerStateList.size(); i++) {
				Player p = receivePlayerStateList.get(i);
				if (p.packetId >= lastPlayerId) {
					engine.updatePlayer(p);
					lastPlayerId = p.packetId;
				}
			}
			receivePlayerStateList.clear();
		}

		synchronized (receiveEntitiesPacketStateList) {
			for (int i = 0; i < receiveEntitiesPacketStateList.size(); i++) {
				EntitiesStatePacket p = receiveEntitiesPacketStateList.get(i);
				if (p.id >= lastPacketId) {
					engine.updateEntitiesState(p.states);
					lastPacketId = p.id;
				}
			}
			receiveEntitiesPacketStateList.clear();
		}

		synchronized (receiveEntityStateList) {
			for (int i = 0; i < receiveEntityStateList.size(); i++) {
				engine.updateEntityState(receiveEntityStateList.get(i));
			}
			receiveEntityStateList.clear();
		}

		synchronized (receiveCreateStateList) {
			for (int i = 0; i < receiveCreateStateList.size(); i++) {
				CreateEntityState ces = receiveCreateStateList.get(i);

				// Special case type = refinery = + 1 collector
				if (ces.type == EData.BUILDING_REFINERY && engine.isPlayerEntity(ces.playerId)) {
					Point p = Utils.getCloserPoint(engine.getMap(), (int) ces.x / engine.getTileW(), (int) ces.y / engine.getTileH());
					if (p != null) {
						engine.getNetworkManager().sendCreateEntity(EData.MOVER_COLLECTOR, ces.playerId, ces.teamId, ces.life, p.x * engine.getTileW(),
								p.y * engine.getTileH());
					}
				}
				ActiveEntity ae = EntityGenerator.createActiveEntityFromNetwork(engine, ces);
				engine.addEntity(ae);
				if (engine.isPlayerEntity(ces.playerId)) {
					if (ae instanceof BuildingECreator) {
						((BuildingECreator) ae).checkPrimary();
					}
					if (ae instanceof MoveableEntity && ces.rx != -1 && ces.ry != -1) {
						Point p = Utils.getCloserPoint(engine.getMap(), ces.rx / engine.getTileW(), ces.ry / engine.getTileH());
						((MoveableEntity) ae).move(p.x * engine.getTileW(), p.y * engine.getTileH());
					}
				} else {
					if (ae instanceof LightningWeapon) {
						GameSound.flashWeapon();
					} else {
						if (ae instanceof MissileSilo) {
							GameSound.nuclearMissile();
						}
					}
				}
			}
			receiveCreateStateList.clear();
		}

		synchronized (receiveDeleteStateList) {
			for (int i = 0; i < receiveDeleteStateList.size(); i++) {
				engine.removeEntity(receiveDeleteStateList.get(i).networkId, receiveDeleteStateList.get(i).layer);
			}
			receiveDeleteStateList.clear();
		}

		synchronized (receiveDeleteAllStateList) {
			if (!receiveDeleteAllStateList.isEmpty()) {
				engine.removeAllEntity(receiveDeleteAllStateList.get(0).playerId);
				receiveDeleteAllStateList.remove(0);
			}
		}

	}

	public void updateSend(Engine engine, ClientManager clientManager) {

		synchronized (this) {
			Player p = engine.getPlayer();
			p.packetId = getNextPlayerId();
			clientManager.sendUDP(p);

			int id = getNextPacketId();
			ArrayList<EntityState> es = engine.getAllPlayerEntities();
			if (es.size() < 50) {
				EntitiesStatePacket packet = new EntitiesStatePacket();
				packet.id = id;
				packet.states = es;
				clientManager.sendUDP(packet);
			} else {
				int nbPacket = es.size() / 50;
				for (int i = 0; i < nbPacket; i++) {
					EntitiesStatePacket packet = new EntitiesStatePacket();
					packet.id = id;
					packet.states.addAll(es.subList(i * 50, (i * 50) + 50));
					clientManager.sendUDP(packet);
				}

				// The last packet if any
				if (nbPacket * 50 < es.size()) {
					EntitiesStatePacket packet = new EntitiesStatePacket();
					packet.id = id;
					packet.states.addAll(es.subList(nbPacket * 50, es.size()));
					clientManager.sendUDP(packet);
				}
			}
		}

		synchronized (sendCreateStateList) {
			for (int i = 0; i < sendCreateStateList.size(); i++) {
				clientManager.sendTCP(sendCreateStateList.get(i));
			}
			sendCreateStateList.clear();
		}

		synchronized (sendDeleteStateList) {
			for (int i = 0; i < sendDeleteStateList.size(); i++) {
				clientManager.sendTCP(sendDeleteStateList.get(i));
			}
			sendDeleteStateList.clear();
		}

	}

	public void receivePlayerState(Player player) {
		synchronized (receivePlayerStateList) {
			receivePlayerStateList.add(player);
		}
	}

	public void receiveEntitiesStatePacket(EntitiesStatePacket packet) {
		synchronized (receiveEntitiesPacketStateList) {
			receiveEntitiesPacketStateList.add(packet);
		}
	}

	public void receiveEntityState(EntityState state) {
		synchronized (receiveEntityStateList) {
			receiveEntityStateList.add(state);
		}
	}

	public void receiveCreateEntityState(CreateEntityState create) {
		synchronized (receivePlayerStateList) {
			receiveCreateStateList.add(create);
		}
	}

	public void receiveDeleteEntityState(DeleteEntityState delete) {
		synchronized (receivePlayerStateList) {
			receiveDeleteStateList.add(delete);
		}
	}

	public void receiveDeleteAllEntityState(DeleteAllEntityState deleteAll) {
		synchronized (receiveDeleteAllStateList) {
			receiveDeleteAllStateList.add(deleteAll);
		}
	}

	public void sendCreateEntityState(CreateEntityState create) {
		synchronized (sendCreateStateList) {
			sendCreateStateList.add(create);
		}
	}

	public void sendDeleteEntityState(DeleteEntityState delete) {
		synchronized (sendDeleteStateList) {
			sendDeleteStateList.add(delete);
		}
	}

	public int getNextPlayerId() {
		if (udpPlayerPoolId == Integer.MAX_VALUE)
			udpPlayerPoolId = 0;
		else
			udpPlayerPoolId++;

		return udpPlayerPoolId;
	}

	public int getNextPacketId() {
		if (udpPacketPoolId == Integer.MAX_VALUE)
			udpPacketPoolId = 0;
		else
			udpPacketPoolId++;

		return udpPacketPoolId;
	}

}
