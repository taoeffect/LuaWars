package rts.core.network.menu_tcp_containers;

public class ClientState {

	public String name;
	public String color;
	public String spawn;
	public int connectionId;
	public int type;
	public int team;
	public int position;
	public boolean isReady;
	public boolean isLoad;

	public ClientState() {
		name = "";
		color = "";
		spawn = "";
	}
	
	public void transfer(ClientState state) {
		this.name = state.name;
		this.type = state.type;
		this.team = state.team;
		this.spawn = state.spawn;
		this.color = state.color;
		this.isReady = state.isReady;
		this.isLoad = state.isLoad;
	}

}
