package rts.core.network;

import java.io.IOException;
import java.util.ArrayList;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import rts.core.engine.Player;
import rts.core.network.ig_tcp_container.CreateEntityState;
import rts.core.network.ig_tcp_container.DeleteAllEntityState;
import rts.core.network.ig_tcp_container.DeleteEntityState;
import rts.core.network.ig_udp_containers.EntitiesStatePacket;
import rts.core.network.ig_udp_containers.EntityState;
import rts.core.network.menu_tcp_containers.AllClientState;
import rts.core.network.menu_tcp_containers.CRMessageState;
import rts.core.network.menu_tcp_containers.ClientState;
import rts.core.network.menu_tcp_containers.LoadGameMessage;
import rts.core.network.menu_tcp_containers.MessageState;
import rts.core.network.menu_tcp_containers.ServerState;
import rts.core.network.menu_tcp_containers.SwitchToGameMessage;
import rts.utils.Configuration;

public class ServerManager {

	private int networkIdPool;
	private NetworkManager manager;
	private Server server;
	private ArrayList<ClientState> clients;
	private ClientPosition[] position;

	public ServerManager(NetworkManager manager) {
		this.manager = manager;
		this.server = new Server();
		this.server.addListener(new ServerListener());
		this.clients = new ArrayList<ClientState>();
		this.position = new ClientPosition[8];
		for (int i = 0; i < position.length; i++)
			position[i] = new ClientPosition(i);
	}

	public void launch() throws IOException {
		ClassRegister.register(server.getKryo());
		server.start();
		server.bind(Configuration.getTcpPort(), Configuration.getUdpPort());
	}

	public void launchGame() {
		boolean allReady = true;
		for (int i = 0; i < clients.size(); i++) {
			if (!clients.get(i).isReady) {
				MessageState ms = new MessageState();
				ms.name = "[Server]";
				ms.message = "Can't launch the game because " + clients.get(i).name + " is not ready...";
				server.sendToAllTCP(ms);
				allReady = false;
			}
		}

		// Check not all in the same team
		if (allReady) {
			// On n'accepte plus d'autres joueurs
			manager.getServerState().state = ServerState.IN_GAME;
			refreshServerState();

			// On demande aux joueurs de charger le jeu
			LoadGameMessage lms = new LoadGameMessage();
			lms.state = manager.getServerState();
			server.sendToAllTCP(lms);
		}
	}

	public void stop() {
		server.stop();
	}

	public void refreshClientStates() {
		AllClientState acs = new AllClientState();
		acs.clientStates = clients;
		server.sendToAllTCP(acs);
	}

	public void refreshServerState() {
		// Check the logic of server state
		manager.getServerState().update();
		server.sendToAllTCP(manager.getServerState());
	}

	public void cancelPlayerReady(String reason) {
		for (int i = 0; i < clients.size(); i++) {
			clients.get(i).isReady = false;
		}
		CRMessageState crm = new CRMessageState();
		crm.reason = reason;
		server.sendToAllTCP(crm);
	}

	public int getNextId() {
		if (networkIdPool + 1 == Integer.MAX_VALUE) {
			networkIdPool = 0;
		} else
			networkIdPool++;

		return networkIdPool;
	}

	private class ClientPosition {
		private int connectionId;
		private int position;

		public ClientPosition(int position) {
			connectionId = -1;
			this.position = position;
		}
	}

	private class ServerListener extends Listener {

		@Override
		public void connected(Connection connection) {
			super.connected(connection);
			manager.getServerState().nbPlayer++;

			refreshServerState();
		}

		@Override
		public void disconnected(Connection connection) {
			super.disconnected(connection);
			if (manager.isInGame()) {
				for (int i = 0; i < clients.size(); i++) {
					if (clients.get(i).connectionId == connection.getID()) {
						DeleteAllEntityState dellAll = new DeleteAllEntityState();
						dellAll.playerId = clients.get(i).position;
						server.sendToAllTCP(dellAll);
						MessageState ms = new MessageState();
						ms.color = "";
						ms.name = "[Server]";
						ms.message = clients.get(i).name + " is gone !";
						server.sendToAllTCP(ms);
						clients.remove(i);
						break;
					}
				}
			} else {
				manager.getServerState().nbPlayer--;

				refreshServerState();

				synchronized (clients) {
					for (int i = 0; i < clients.size(); i++) {
						if (clients.get(i).connectionId == connection.getID()) {

							for (int j = 0; j < position.length; j++) {
								if (position[j].connectionId == connection.getID()) {
									position[j].connectionId = -1;
									break;
								}
							}

							clients.remove(i);
							break;
						}
					}
				}

				refreshClientStates();
			}
		}

		@Override
		public void received(Connection connection, Object object) {
			super.received(connection, object);

			if (manager.isInGame()) {
				receiveInGameMessage(connection, object);
			} else {
				if (object instanceof ClientState) {
					ClientState cs = null;
					for (int i = 0; i < clients.size(); i++) {
						if (clients.get(i).connectionId == connection.getID()) {
							cs = clients.get(i);
							break;
						}
					}

					if (cs != null) {
						cs.transfer((ClientState) object);

						// Check all load and ready to play
						boolean allLoad = true;
						for (int i = 0; i < clients.size(); i++) {
							if (!clients.get(i).isLoad) {
								allLoad = false;
								break;
							}
						}

						if (allLoad) {
							// THE GAME IS LOAD AND LAUNCH
							server.sendToAllTCP(new SwitchToGameMessage());
							manager.setInGame(true);
							if (manager.getMenuListener() != null) {
								manager.getMenuListener().switchToGame();
							}
						} else {
							refreshClientStates();
						}

					} else {
						// First time client connected wait and send
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						ClientState clientState = (ClientState) object;
						int pos = 0;
						for (int i = 0; i < position.length; i++) {
							if (position[i].connectionId == -1) {
								// find one
								pos = position[i].position;
								position[i].connectionId = clientState.connectionId;
								break;
							}
						}

						clientState.position = pos;
						clients.add(clientState);

						// Send infos about all client to client
						refreshClientStates();

					}
				}
			}

			if (object instanceof MessageState) {
				server.sendToAllExceptTCP(connection.getID(), object);
			}
		}

		private void receiveInGameMessage(Connection connection, Object object) {
			if (object instanceof Player || object instanceof EntitiesStatePacket) {
				server.sendToAllExceptUDP(connection.getID(), object);
			} else {
				if (object instanceof CreateEntityState) {
					CreateEntityState ces = (CreateEntityState) object;
					ces.networkId = getNextId();
					server.sendToAllTCP(ces);
				} else {
					if (object instanceof DeleteEntityState) {
						server.sendToAllExceptTCP(connection.getID(), object);
					} else {
						if (object instanceof EntityState) {
							server.sendToAllExceptTCP(connection.getID(), object);
						}
					}
				}
			}
		}
	}

}
