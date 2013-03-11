package rts.core.network;

import java.util.ArrayList;

import rts.core.network.menu_tcp_containers.ClientState;
import rts.core.network.menu_tcp_containers.MessageState;
import rts.core.network.menu_tcp_containers.ServerState;

public interface INetworkMenuListener {

	public void connectionSuccess();
	
	public void serverInfosChange(ServerState serverState);
	
	public void clientsInfosChange(ArrayList<ClientState> clientStates);
	
	public void receiveMessage(MessageState message);
	
	public void loadGame(ServerState state);
	
	public void switchToGame();

	public void disconnected();

}
