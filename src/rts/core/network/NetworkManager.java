package rts.core.network;

import java.io.IOException;

import org.luawars.Log;
import rts.core.engine.Engine;
import rts.core.network.ig_tcp_container.CreateEntityState;
import rts.core.network.ig_tcp_container.DeleteEntityState;
import rts.core.network.ig_udp_containers.EntityState;
import rts.core.network.menu_tcp_containers.ClientState;
import rts.core.network.menu_tcp_containers.MessageState;
import rts.core.network.menu_tcp_containers.ServerState;

public class NetworkManager {

	private Engine engine;
	private NetworkPool pool;
	private INetworkMenuListener menuListener;
	private NetworkServerDiscover discover;
	private boolean inGame;
	private boolean isServer;

	// Server
	private ServerManager server;
	private ServerState serverState;

	// Client
	private ClientState clientState;
	private ClientManager clientManager;

	public NetworkManager() {
		pool = new NetworkPool();
		discover = new NetworkServerDiscover(this);
	}

	public void update() {
		if (engine != null && clientManager != null) {
			// Receive
			pool.updateReceive(engine);

			// Send
			pool.updateSend(engine, clientManager);
		}
	}

	// Global methods

	public MessageState sendMessage(String message) {
		MessageState ms = new MessageState();
		ms.name = clientState.name;
		ms.color = clientState.color;
		ms.message = message;
		clientManager.sendTCP(ms);
		return ms;
	}

	public void sendCreateEntity(int type, int playerId, int teamId, int life, float x, float y) {
		CreateEntityState ces = new CreateEntityState();
		ces.life = life;
		ces.type = type;
		ces.playerId = playerId;
		ces.teamId = teamId;
		ces.x = x;
		ces.y = y;
		pool.sendCreateEntityState(ces);
	}

	public void sendCreateEntity(int type, int playerId, int teamId, float x, float y) {
		CreateEntityState ces = new CreateEntityState();
		ces.type = type;
		ces.playerId = playerId;
		ces.teamId = teamId;
		ces.x = x;
		ces.y = y;
		pool.sendCreateEntityState(ces);
	}

	public void sendCreateEntity(int type, int playerId, int teamId, int rx, int ry, float x, float y) {
		CreateEntityState ces = new CreateEntityState();
		ces.type = type;
		ces.playerId = playerId;
		ces.teamId = teamId;
		ces.rx = rx;
		ces.ry = ry;
		ces.x = x;
		ces.y = y;
		pool.sendCreateEntityState(ces);
	}

	public void sendUpdateNPEntity(EntityState state) {
		clientManager.sendTCP(state);
	}

	public void sendDeleteEntity(int networkId, int playerId, int layer) {
		DeleteEntityState des = new DeleteEntityState();
		des.networkId = networkId;
		des.layer = layer;
		pool.sendDeleteEntityState(des);
	}

	// Discover delegate methods

	public void discover() throws IOException {
		discover.discover();
	}

	public void launchClientListening(INetworkDiscoverListener l) {
		discover.launchClientListening(l);
	}

	public void stopClientListening() {
		discover.stopClientListening();
	}

	// Client methods

	public void joinServer(String serverIp) throws IOException {
		pool = new NetworkPool();
		clientManager = new ClientManager(this);
		clientState = new ClientState();
		clientManager.connect(serverIp);
	}

	public void updateClientState() {
		if (clientManager != null)
			clientManager.refreshClientState();
	}

	public void stopClient() {
		if (clientManager != null) {
			inGame = false;
			clientManager.stop();
		}
	}

	public void sendMessageToGui(MessageState message) {
		engine.getGui().addMessage(message);
	}

	public void serverClose() {
		engine.serverClose();
	}

	// Server methods

	public void createServer() throws IOException {
		this.discover.launchServerListening();
		this.server = new ServerManager(this);
		this.serverState = new ServerState();
		this.server.launch();
		this.isServer = true;
	}

	public void stopServer() {
		if (server != null) {
			this.server.stop();
			discover.stopServerListening();
		}

		this.isServer = false;
	}

	public void updateServerState() {
		if (server != null) {
			server.refreshServerState();
		}
	}

	public void launchGame() {
        Log.debug("lauchGame: " + (server != null ? server : "NULL"));
        if (server != null)
			server.launchGame();
	}

	// Getters and Setters

	public NetworkPool getPool() {
		return pool;
	}

	public ServerState getServerState() {
		return serverState;
	}

	public ClientState getClientState() {
		return clientState;
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	public void setMenuListener(INetworkMenuListener menuListener) {
		this.menuListener = menuListener;
	}

	public INetworkMenuListener getMenuListener() {
		return menuListener;
	}

	public boolean isInGame() {
		return inGame;
	}

	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}

	public boolean isServer() {
		return isServer;
	}

}
