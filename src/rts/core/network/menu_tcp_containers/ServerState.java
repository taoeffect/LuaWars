package rts.core.network.menu_tcp_containers;

public class ServerState {

	public static final int OK = 0;
	public static final int IN_GAME = 1;
	public static final int FULL = 2;

	public int state;
	public int gameType;
	public int nbPlayer;
	public int nbMaxPlayer;
	public int startMoney;
	public int tecLevel;
	public String mapName;
	public String ip;

	public ServerState() {
		mapName = "";
		ip = "";
	}

	public void update() {
		if (state != IN_GAME) {
			if (nbPlayer == nbMaxPlayer) {
				state = FULL;
			} else {
				state = OK;
			}
		}
	}

}
