package rts.core.engine.layers.entities;

import rts.core.engine.Engine;
import rts.core.engine.layers.entities.buildings.Artillery;
import rts.core.engine.layers.entities.buildings.Barrack;
import rts.core.engine.layers.entities.buildings.BigConstructor;
import rts.core.engine.layers.entities.buildings.BigHealer;
import rts.core.engine.layers.entities.buildings.Builder;
import rts.core.engine.layers.entities.buildings.Constructor;
import rts.core.engine.layers.entities.buildings.DevCenter;
import rts.core.engine.layers.entities.buildings.LightningWeapon;
import rts.core.engine.layers.entities.buildings.Healer;
import rts.core.engine.layers.entities.buildings.Port;
import rts.core.engine.layers.entities.buildings.Radar;
import rts.core.engine.layers.entities.buildings.Refinery;
import rts.core.engine.layers.entities.buildings.SpyRadar;
import rts.core.engine.layers.entities.buildings.Starport;
import rts.core.engine.layers.entities.buildings.Starport2;
import rts.core.engine.layers.entities.buildings.Storage;
import rts.core.engine.layers.entities.buildings.Televat;
import rts.core.engine.layers.entities.buildings.MissileSilo;
import rts.core.engine.layers.entities.buildings.Turret;
import rts.core.engine.layers.entities.others.Barrel;
import rts.core.engine.layers.entities.others.Bridge;
import rts.core.engine.layers.entities.others.Car;
import rts.core.engine.layers.entities.others.Lamp;
import rts.core.engine.layers.entities.others.Mineral;
import rts.core.engine.layers.entities.others.OldBuilding;
import rts.core.engine.layers.entities.others.OldRadar;
import rts.core.engine.layers.entities.others.Wall;
import rts.core.engine.layers.entities.vehicles.BuilderMover;
import rts.core.engine.layers.entities.vehicles.Collector;
import rts.core.engine.layers.entities.vehicles.Mover;
import rts.core.engine.layers.entities.vehicles.Transport;
import rts.core.network.ig_tcp_container.CreateEntityState;

public class EntityGenerator {

	// From map
	public static ActiveEntity createActiveEntityFromMap(Engine engine, int type, float x, float y) {
		ActiveEntity ae = createEntity(engine, type, -1, -1, -1);
		ae.setLocation(x, y);
		return ae;
	}

	// From network
	public static ActiveEntity createActiveEntityFromNetwork(Engine engine, CreateEntityState ces) {
		return createActiveEntity(engine, ces.type, ces.playerId, ces.teamId, ces.networkId, ces.life, ces.x, ces.y);
	}

	// Game without network
	public static ActiveEntity createActiveEntityNoNetwork(Engine engine, int type, int playerId, int teamId) {
		return createEntity(engine, type, playerId, teamId, -1);
	}

	private static ActiveEntity createActiveEntity(Engine engine, int type, int playerId, int teamId, int networkId, int life, float x, float y) {
		ActiveEntity ae = createEntity(engine, type, playerId, teamId, networkId);
		ae.setLocation(x, y);
		if (life != 0)
			ae.setLife(life);
		return ae;
	}

	private static ActiveEntity createEntity(Engine engine, int type, int playerId, int teamId, int networkId) {

		if (EData.isMisc(type)) {
			switch (type) {
			case EData.MINERAL:
				return new Mineral(engine, networkId);
			case EData.OLD_CAR1:
				return new Car(engine, type, networkId);
			case EData.OLD_CAR2:
				return new Car(engine, type, networkId);
			case EData.OLD_BUILDING:
				return new OldBuilding(engine, networkId);
			case EData.OLD_BARREL:
				return new Barrel(engine, networkId);
			case EData.OLD_RADAR:
				return new OldRadar(engine, networkId);
			case EData.OLD_LAMP:
				return new Lamp(engine, networkId);
			default:
				return null;
			}
		} else {
			if (EData.isMover(type)) {
				// Special case
				switch (type) {
				case EData.MOVER_COLLECTOR:
					return new Collector(engine, playerId, teamId, networkId);
				case EData.MOVER_BUILDER:
					return new BuilderMover(engine, playerId, teamId, networkId);
				case EData.MOVER_TRANSPORT:
					return new Transport(engine, false, playerId, teamId, networkId);
				case EData.MOVER_MARINE_TRANSPORT:
					return new Transport(engine, true, playerId, teamId, networkId);
				default:
					// Standard mover
					return new Mover(engine, type, playerId, teamId, networkId);
				}
			} else {
				switch (type) {
				case EData.BUILDING_ARTILLERY:
					return new Artillery(engine, playerId, teamId, networkId);
				case EData.BUILDING_BARRACK:
					return new Barrack(engine, playerId, teamId, networkId);
				case EData.BUILDING_BIG_CONSTRUCTOR:
					return new BigConstructor(engine, playerId, teamId, networkId);
				case EData.BUILDING_BIG_HEALER:
					return new BigHealer(engine, playerId, teamId, networkId);
				case EData.BUILDING_BUILDER:
					return new Builder(engine, playerId, teamId, networkId);
				case EData.BUILDING_CONSTRUCTOR:
					return new Constructor(engine, playerId, teamId, networkId);
				case EData.BUILDING_DEV_CENTER:
					return new DevCenter(engine, playerId, teamId, networkId);
				case EData.BUILDING_LIGHTNING_WEAPON:
					return new LightningWeapon(engine, playerId, teamId, networkId);
				case EData.BUILDING_HEALER:
					return new Healer(engine, playerId, teamId, networkId);
				case EData.BUILDING_PORT:
					return new Port(engine, playerId, teamId, networkId);
				case EData.BUILDING_RADAR:
					return new Radar(engine, playerId, teamId, networkId);
				case EData.BUILDING_REFINERY:
					return new Refinery(engine, playerId, teamId, networkId);
				case EData.BUILDING_SPYRADAR:
					return new SpyRadar(engine, playerId, teamId, networkId);
				case EData.BUILDING_STARPORT:
					return new Starport(engine, playerId, teamId, networkId);
				case EData.BUILDING_STARPORT_2:
					return new Starport2(engine, playerId, teamId, networkId);
				case EData.BUILDING_STORAGE:
					return new Storage(engine, playerId, teamId, networkId);
				case EData.BUILDING_TELEVAT:
					return new Televat(engine, playerId, teamId, networkId);
				case EData.BUILDING_MISSILE_SILO:
					return new MissileSilo(engine, playerId, teamId, networkId);
				case EData.BUILDING_TURRET:
					return new Turret(engine, playerId, teamId, networkId);
				case EData.VERTICAL_BRIDGE:
					return new Bridge(engine, EData.VERTICAL_BRIDGE, networkId);
				case EData.HORIZONTAL_BRIDGE:
					return new Bridge(engine, EData.HORIZONTAL_BRIDGE, networkId);
				case EData.WALL:
					return new Wall(engine, playerId, teamId, networkId);
				default:
					return null;
				}
			}
		}

	}
}
