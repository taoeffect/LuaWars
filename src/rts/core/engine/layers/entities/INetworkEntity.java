package rts.core.engine.layers.entities;

import rts.core.network.ig_udp_containers.EntityState;

/**
 * 
 * Represent a network entity.
 * 
 * @author Vincent PIRAULT
 *
 */
public interface INetworkEntity {

	public int getNetworkID();

	public EntityState getState();

	public void setState(EntityState state);
}
