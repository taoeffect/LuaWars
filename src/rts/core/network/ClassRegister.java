package rts.core.network;

import java.util.ArrayList;

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

import com.esotericsoftware.kryo.Kryo;

public final class ClassRegister {

	public static void register(Kryo kryo) {

		// Globals messages
		kryo.register(MessageState.class);

		// Menus messages
		kryo.register(ArrayList.class);
		kryo.register(AllClientState.class);
		kryo.register(CRMessageState.class);
		kryo.register(ClientState.class);
		kryo.register(ServerState.class);
		kryo.register(LoadGameMessage.class);
		kryo.register(SwitchToGameMessage.class);

		// Tcp containers
		kryo.register(CreateEntityState.class);
		kryo.register(DeleteEntityState.class);
		kryo.register(DeleteAllEntityState.class);

		// Udp container
		kryo.register(EntityState.class);
		kryo.register(Player.class);
		kryo.register(EntitiesStatePacket.class);
	}

}
