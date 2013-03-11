package rts.core.network.ig_tcp_container;

public class CreateEntityState {

	public int type;
	public int playerId;
	public int teamId;
	public int networkId;
	public int life;
	public int rx;
	public int ry;
	public float x;
	public float y;

	public CreateEntityState() {
		rx = -1;
		ry = -1;
	}
}
