package rts.core.network.ig_udp_containers;

public class EntityState {

	//Standard fields
	public int networkId;
	public int life;
	public int direction;
	public int layer;
	public float targetX;
	public float targetY;
	public float x;
	public float y;
	public boolean weak;
	public boolean dying;
	public boolean visible;
	public boolean fire;
	
	//Timers
	public boolean timer1Complete;
	public boolean timer2Complete;
	
	//Builder
	public int moneyState;
	
	//Final weapon
	public float tx;
	public float ty;
	
	//Collector
	public int burden;
}
