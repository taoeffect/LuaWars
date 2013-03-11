package rts.core.network;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import rts.core.engine.Player;
import rts.core.network.ig_tcp_container.CreateEntityState;
import rts.core.network.ig_tcp_container.DeleteAllEntityState;
import rts.core.network.ig_tcp_container.DeleteEntityState;
import rts.core.network.ig_udp_containers.EntitiesStatePacket;
import rts.core.network.ig_udp_containers.EntityState;
import rts.core.network.menu_tcp_containers.AllClientState;
import rts.core.network.menu_tcp_containers.CRMessageState;
import rts.core.network.menu_tcp_containers.LoadGameMessage;
import rts.core.network.menu_tcp_containers.MessageState;
import rts.core.network.menu_tcp_containers.ServerState;
import rts.core.network.menu_tcp_containers.SwitchToGameMessage;
import rts.utils.Configuration;

public class ClientManager {

	private NetworkManager manager;
	private Client client;

	public ClientManager(NetworkManager manager) {
		this.manager = manager;
		this.client = new Client();
		this.client.addListener(new ClientListener());
	}

	public void refreshClientState() {
		client.sendTCP(manager.getClientState());
	}

	public void connect(String serverIp) throws IOException {
		ClassRegister.register(client.getKryo());
		client.start();
		client.connect(5000, serverIp, Configuration.getTcpPort(), Configuration.getUdpPort());
	}

	public void stop() {
		client.stop();
	}

	public int sendTCP(Object object) {
		return client.sendTCP(object);
	}

	public int sendUDP(Object object) {
		return client.sendUDP(object);
	}

	private class ClientListener extends Listener {

		@Override
		public void connected(Connection connection) {
			super.connected(connection);

			manager.getClientState().connectionId = connection.getID();
			manager.getClientState().name = Configuration.getPseudo();

			if (manager.getMenuListener() != null) {
				manager.getMenuListener().connectionSuccess();
			}

			refreshClientState();
		}

		@Override
		public void disconnected(Connection connection) {
			super.disconnected(connection);
			if (manager.isInGame()) {
				manager.serverClose();
			} else {
				if (manager.getMenuListener() != null) {
					manager.getMenuListener().disconnected();
				}
			}
		}

		@Override
		public void received(Connection connection, Object object) {
			super.received(connection, object);

			if (manager.isInGame()) {
				receiveInGameMessage(connection, object);
			} else {
				if (manager.getMenuListener() != null) {
					if (object instanceof AllClientState) {
						manager.getMenuListener().clientsInfosChange(((AllClientState) object).clientStates);
					} else {
						if (object instanceof ServerState) {
							manager.getMenuListener().serverInfosChange((ServerState) object);
						} else {
							if (object instanceof CRMessageState) {
								manager.getClientState().isReady = false;
							} else {
								if (object instanceof MessageState) {
									manager.getMenuListener().receiveMessage((MessageState) object);
								} else {
									if (object instanceof LoadGameMessage) {
										manager.getMenuListener().loadGame(((LoadGameMessage) object).state);
									} else {
										if (object instanceof SwitchToGameMessage) {
											manager.setInGame(true);
											manager.getMenuListener().switchToGame();
										}
									}
								}
							}
						}
					}
				}
			}
		}

		private void receiveInGameMessage(Connection connection, Object object) {
			if (object instanceof Player) {
				manager.getPool().receivePlayerState((Player) object);
			} else {
				if (object instanceof EntitiesStatePacket) {
					manager.getPool().receiveEntitiesStatePacket((EntitiesStatePacket) object);
				} else {
					if (object instanceof EntityState) {
						manager.getPool().receiveEntityState((EntityState) object);
					} else {
						if (object instanceof CreateEntityState) {
							manager.getPool().receiveCreateEntityState((CreateEntityState) object);
						} else {
							if (object instanceof DeleteEntityState) {
								manager.getPool().receiveDeleteEntityState((DeleteEntityState) object);
							} else {
								if (object instanceof DeleteAllEntityState) {
									manager.getPool().receiveDeleteAllEntityState((DeleteAllEntityState) object);
								} else {
									if (object instanceof MessageState) {
										manager.sendMessageToGui((MessageState) object);
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
