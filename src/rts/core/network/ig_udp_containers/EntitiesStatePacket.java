package rts.core.network.ig_udp_containers;

import java.util.ArrayList;

public class EntitiesStatePacket {

	public int id;
	public ArrayList<EntityState> states;

	public EntitiesStatePacket() {
		states = new ArrayList<EntityState>();
	}
}
