package rts.core.engine.ingamegui;

import java.util.ArrayList;

import org.newdawn.slick.SpriteSheet;

import rts.core.engine.Engine;
import rts.core.engine.layers.entities.EData;
import rts.utils.ResourceManager;

public final class GuiPanelFactory {

    // NOTE: this creates a new array of panels each time you call it.
    // If you want to access panels, don't access panels using this function.
    // access them using the GuiMenu's panels
	public static ArrayList<GuiPanel> getAllPanels(Engine engine, GuiMenu menu) {
		ArrayList<GuiPanel> array = new ArrayList<GuiPanel>();
		array.add(getBuildingPanel(engine, menu, 0));
		array.add(getDefBuildingPanel(engine, menu, 1));
		array.add(getEarthPanel(engine, menu, 2));
		array.add(getAirPanel(engine, menu, 3));
		array.add(getMarinePanel(engine, menu, 4));
		return array;
	}

	private static GuiPanel getBuildingPanel(Engine engine, GuiMenu menu, int id) {
		GuiPanel panel = new GuiPanel(menu, id);
		panel.setLocation(engine.getContainer().getWidth() - 190, 280);

		SpriteSheet ss = ResourceManager.getSpriteSheet("buildingbuttons");

		// Barrack
		GuiButton barrackButton = new GuiButton(engine, ss.getSprite(1, 0), 0, 0);
		barrackButton.setEntType(EData.BUILDING_BARRACK);
		barrackButton.addEnableCombination(new int[] { EData.BUILDING_BUILDER });
		panel.addButton(barrackButton);

		// Refinery
		GuiButton refineryButton = new GuiButton(engine, ss.getSprite(0, 0), 0, 0);
		refineryButton.setEntType(EData.BUILDING_REFINERY);
		refineryButton.addEnableCombination(new int[] { EData.BUILDING_BUILDER });
		panel.addButton(refineryButton);

		// Storage
		GuiButton storageButton = new GuiButton(engine, ss.getSprite(2, 0), 0, 0);
		storageButton.setEntType(EData.BUILDING_STORAGE);
		storageButton.addEnableCombination(new int[] { EData.BUILDING_REFINERY });
		panel.addButton(storageButton);

		// Healer
		GuiButton healerButton = new GuiButton(engine, ss.getSprite(0, 1), 0, 0);
		healerButton.setEntType(EData.BUILDING_HEALER);
		healerButton.addEnableCombination(new int[] { EData.BUILDING_BARRACK });
		panel.addButton(healerButton);

		// Constructor
		GuiButton constructorButton = new GuiButton(engine, ss.getSprite(1, 1), 0, 0);
		constructorButton.setEntType(EData.BUILDING_CONSTRUCTOR);
		constructorButton.addEnableCombination(new int[] { EData.BUILDING_REFINERY });
		panel.addButton(constructorButton);

		// Radar
		GuiButton radarButton = new GuiButton(engine, ss.getSprite(2, 1), 0, 0);
		radarButton.setEntType(EData.BUILDING_RADAR);
		radarButton.addEnableCombination(new int[] { EData.BUILDING_REFINERY });
		panel.addButton(radarButton);

		// Port
		GuiButton portButton = new GuiButton(engine, ss.getSprite(0, 2), 0, 0);
		portButton.setEntType(EData.BUILDING_PORT);
		portButton.addEnableCombination(new int[] { EData.BUILDING_CONSTRUCTOR });
		portButton.addEnableCombination(new int[] { EData.BUILDING_BIG_CONSTRUCTOR });
		panel.addButton(portButton);

		// Starport
		GuiButton starportButton = new GuiButton(engine, ss.getSprite(1, 2), 0, 0);
		starportButton.setEntType(EData.BUILDING_STARPORT);
		starportButton.addEnableCombination(new int[] { EData.BUILDING_CONSTRUCTOR });
		starportButton.addEnableCombination(new int[] { EData.BUILDING_BIG_CONSTRUCTOR });
		panel.addButton(starportButton);

		// Starport 2
		GuiButton starport2Button = new GuiButton(engine, ss.getSprite(2, 2), 0, 0);
		starport2Button.setEntType(EData.BUILDING_STARPORT_2);
		starport2Button.addEnableCombination(new int[] { EData.BUILDING_CONSTRUCTOR, EData.BUILDING_STARPORT });
		starport2Button.addEnableCombination(new int[] { EData.BUILDING_BIG_CONSTRUCTOR, EData.BUILDING_STARPORT });
		panel.addButton(starport2Button);

		// DevCenter
		GuiButton devCenterButton = new GuiButton(engine, ss.getSprite(0, 3), 0, 0);
		devCenterButton.setEntType(EData.BUILDING_DEV_CENTER);
		devCenterButton.addEnableCombination(new int[] { EData.BUILDING_RADAR });
		panel.addButton(devCenterButton);

		// Big Healer
		GuiButton bighealerButton = new GuiButton(engine, ss.getSprite(1, 3), 0, 0);
		bighealerButton.setEntType(EData.BUILDING_BIG_HEALER);
		bighealerButton.addEnableCombination(new int[] { EData.BUILDING_HEALER, EData.BUILDING_DEV_CENTER });
		panel.addButton(bighealerButton);

		// Big Constructor
		GuiButton bigConstructorButton = new GuiButton(engine, ss.getSprite(2, 3), 0, 0);
		bigConstructorButton.setEntType(EData.BUILDING_BIG_CONSTRUCTOR);
		bigConstructorButton.addEnableCombination(new int[] { EData.BUILDING_CONSTRUCTOR, EData.BUILDING_DEV_CENTER });
		panel.addButton(bigConstructorButton);

		return panel;
	}

	private static GuiPanel getDefBuildingPanel(Engine engine, GuiMenu menu, int id) {
		GuiPanel panel = new GuiPanel(menu, id);
		panel.setLocation(engine.getContainer().getWidth() - 190, 280);

		SpriteSheet ss = ResourceManager.getSpriteSheet("defbuildingbuttons");

		// Wall
		GuiButton wallButton = new GuiButton(engine, ss.getSprite(0, 0), 0, 0);
		wallButton.setEntType(EData.WALL);
		wallButton.addEnableCombination(new int[] { EData.BUILDING_BUILDER });
		panel.addButton(wallButton);

		// Turret
		GuiButton turretButton = new GuiButton(engine, ss.getSprite(1, 0), 0, 0);
		turretButton.setEntType(EData.BUILDING_TURRET);
		turretButton.addEnableCombination(new int[] { EData.BUILDING_BARRACK });
		panel.addButton(turretButton);

		// Artillery
		GuiButton artilleryButton = new GuiButton(engine, ss.getSprite(2, 0), 0, 0);
		artilleryButton.setEntType(EData.BUILDING_ARTILLERY);
		artilleryButton.addEnableCombination(new int[] { EData.BUILDING_BARRACK });
		panel.addButton(artilleryButton);

		// Spy Radar
		GuiButton spyRadarButton = new GuiButton(engine, ss.getSprite(3, 0), 0, 0);
		spyRadarButton.setEntType(EData.BUILDING_SPYRADAR);
		spyRadarButton.addEnableCombination(new int[] { EData.BUILDING_DEV_CENTER });
		spyRadarButton.setLimitAtOne(true);
		panel.addButton(spyRadarButton);

		// Lightning weapon
		GuiButton lightningWeaponButton = new GuiButton(engine, ss.getSprite(4, 0), 0, 0);
		lightningWeaponButton.setEntType(EData.BUILDING_LIGHTNING_WEAPON);
		lightningWeaponButton.addEnableCombination(new int[] { EData.BUILDING_DEV_CENTER });
		lightningWeaponButton.setLimitAtOne(true);
		panel.addButton(lightningWeaponButton);

		// Missile Silo
		GuiButton missileSiloButton = new GuiButton(engine, ss.getSprite(5, 0), 0, 0);
		missileSiloButton.setEntType(EData.BUILDING_MISSILE_SILO);
		missileSiloButton.addEnableCombination(new int[] { EData.BUILDING_DEV_CENTER });
		missileSiloButton.setLimitAtOne(true);
		panel.addButton(missileSiloButton);

		return panel;
	}

	private static GuiPanel getEarthPanel(Engine engine, GuiMenu menu, int id) {
		GuiPanel panel = new GuiPanel(menu, id);
		panel.setLocation(engine.getContainer().getWidth() - 190, 280);

		SpriteSheet ss = ResourceManager.getSpriteSheet("earthentbuttons");

		// Soldier
		GuiButton soldierButton = new GuiButton(engine, ss.getSprite(0, 0), 0, 0);
		soldierButton.setEntType(EData.MOVER_SOLDIER);
		soldierButton.addEnableCombination(new int[] { EData.BUILDING_BARRACK });
		panel.addButton(soldierButton);

		// Scout
		GuiButton scoutButton = new GuiButton(engine, ss.getSprite(1, 0), 0, 0);
		scoutButton.setEntType(EData.MOVER_SCOUT);
		scoutButton.addEnableCombination(new int[] { EData.BUILDING_BARRACK });
		panel.addButton(scoutButton);

		// Soldier
		GuiButton jeepButton = new GuiButton(engine, ss.getSprite(2, 0), 0, 0);
		jeepButton.setEntType(EData.MOVER_JEEP);
		jeepButton.addEnableCombination(new int[] { EData.BUILDING_BARRACK });
		panel.addButton(jeepButton);

		// Collector
		GuiButton collectorButton = new GuiButton(engine, ss.getSprite(0, 1), 0, 0);
		collectorButton.setEntType(EData.MOVER_COLLECTOR);
		collectorButton.addEnableCombination(new int[] { EData.BUILDING_REFINERY, EData.BUILDING_CONSTRUCTOR });
		collectorButton.addEnableCombination(new int[] { EData.BUILDING_REFINERY, EData.BUILDING_BIG_CONSTRUCTOR });
		panel.addButton(collectorButton);

		// Transport
		GuiButton transportButton = new GuiButton(engine, ss.getSprite(1, 1), 0, 0);
		transportButton.setEntType(EData.MOVER_TRANSPORT);
		transportButton.addEnableCombination(new int[] { EData.BUILDING_CONSTRUCTOR });
		transportButton.addEnableCombination(new int[] { EData.BUILDING_BIG_CONSTRUCTOR });
		panel.addButton(transportButton);

		// Tank
		GuiButton tankButton = new GuiButton(engine, ss.getSprite(2, 2), 0, 0);
		tankButton.setEntType(EData.MOVER_TANK);
		tankButton.addEnableCombination(new int[] { EData.BUILDING_CONSTRUCTOR });
		tankButton.addEnableCombination(new int[] { EData.BUILDING_BIG_CONSTRUCTOR });
		panel.addButton(tankButton);

		// Anti building
		GuiButton antiBuildingButton = new GuiButton(engine, ss.getSprite(2, 1), 0, 0);
		antiBuildingButton.setEntType(EData.MOVER_ANTI_BUILDING);
		antiBuildingButton.addEnableCombination(new int[] { EData.BUILDING_DEV_CENTER, EData.BUILDING_CONSTRUCTOR });
		antiBuildingButton.addEnableCombination(new int[] { EData.BUILDING_DEV_CENTER, EData.BUILDING_BIG_CONSTRUCTOR });
		panel.addButton(antiBuildingButton);

		// Ligthning
		GuiButton ligthningButton = new GuiButton(engine, ss.getSprite(0, 2), 0, 0);
		ligthningButton.setEntType(EData.MOVER_LIGHTING);
		ligthningButton.addEnableCombination(new int[] { EData.BUILDING_RADAR, EData.BUILDING_CONSTRUCTOR });
		ligthningButton.addEnableCombination(new int[] { EData.BUILDING_RADAR, EData.BUILDING_BIG_CONSTRUCTOR });
		ligthningButton.addEnableCombination(new int[] { EData.BUILDING_DEV_CENTER, EData.BUILDING_CONSTRUCTOR });
		ligthningButton.addEnableCombination(new int[] { EData.BUILDING_DEV_CENTER, EData.BUILDING_BIG_CONSTRUCTOR });
		panel.addButton(ligthningButton);

		// Anti aerial
		GuiButton antiAerialButton = new GuiButton(engine, ss.getSprite(1, 2), 0, 0);
		antiAerialButton.setEntType(EData.MOVER_ANTI_AERIAL);
		antiAerialButton.addEnableCombination(new int[] { EData.BUILDING_STARPORT, EData.BUILDING_CONSTRUCTOR });
		antiAerialButton.addEnableCombination(new int[] { EData.BUILDING_STARPORT, EData.BUILDING_BIG_CONSTRUCTOR });
		antiAerialButton.addEnableCombination(new int[] { EData.BUILDING_STARPORT_2, EData.BUILDING_CONSTRUCTOR });
		antiAerialButton.addEnableCombination(new int[] { EData.BUILDING_STARPORT_2, EData.BUILDING_BIG_CONSTRUCTOR });
		panel.addButton(antiAerialButton);

		// Flame launcher
		GuiButton flameLauncherButton = new GuiButton(engine, ss.getSprite(0, 3), 0, 0);
		flameLauncherButton.setEntType(EData.MOVER_FLAME_LAUNCHER);
		flameLauncherButton.addEnableCombination(new int[] { EData.BUILDING_CONSTRUCTOR });
		flameLauncherButton.addEnableCombination(new int[] { EData.BUILDING_BIG_CONSTRUCTOR });
		panel.addButton(flameLauncherButton);

		// Hacker
		/*GuiButton hackerButton = new GuiButton(engine, ss.getSprite(2, 3), 0, 0);
		hackerButton.setEntType(EData.MOVER_HACKER);
		hackerButton.addEnableCombination(new int[] { EData.BUILDING_RADAR, EData.BUILDING_CONSTRUCTOR });
		hackerButton.addEnableCombination(new int[] { EData.BUILDING_RADAR, EData.BUILDING_BIG_CONSTRUCTOR });
		hackerButton.addEnableCombination(new int[] { EData.BUILDING_DEV_CENTER, EData.BUILDING_CONSTRUCTOR });
		hackerButton.addEnableCombination(new int[] { EData.BUILDING_DEV_CENTER, EData.BUILDING_BIG_CONSTRUCTOR });
		panel.addButton(hackerButton);
		 */
		// Artillery
		GuiButton artilleryButton = new GuiButton(engine, ss.getSprite(0, 4), 0, 0);
		artilleryButton.setEntType(EData.MOVER_ARTILLERY);
		artilleryButton.addEnableCombination(new int[] { EData.BUILDING_RADAR, EData.BUILDING_CONSTRUCTOR });
		artilleryButton.addEnableCombination(new int[] { EData.BUILDING_RADAR, EData.BUILDING_BIG_CONSTRUCTOR });
		artilleryButton.addEnableCombination(new int[] { EData.BUILDING_DEV_CENTER, EData.BUILDING_CONSTRUCTOR });
		artilleryButton.addEnableCombination(new int[] { EData.BUILDING_DEV_CENTER, EData.BUILDING_BIG_CONSTRUCTOR });
		panel.addButton(artilleryButton);

		// Builder
		GuiButton builderButton = new GuiButton(engine, ss.getSprite(1, 3), 0, 0);
		builderButton.setEntType(EData.MOVER_BUILDER);
		builderButton.addEnableCombination(new int[] { EData.BUILDING_DEV_CENTER, EData.BUILDING_BIG_CONSTRUCTOR });
		panel.addButton(builderButton);

		return panel;
	}

	private static GuiPanel getAirPanel(Engine engine, GuiMenu menu, int id) {
		GuiPanel panel = new GuiPanel(menu, id);
		panel.setLocation(engine.getContainer().getWidth() - 190, 280);

		SpriteSheet ss = ResourceManager.getSpriteSheet("airentbuttons");

		// Hunter
		GuiButton hunterButton = new GuiButton(engine, ss.getSprite(0, 0), 0, 0);
		hunterButton.setEntType(EData.MOVER_HUNTER_1);
		hunterButton.addEnableCombination(new int[] { EData.BUILDING_STARPORT });
		panel.addButton(hunterButton);

		// S scout
		GuiButton scoutButton = new GuiButton(engine, ss.getSprite(1, 0), 0, 0);
		scoutButton.setEntType(EData.MOVER_SKY_SCOUT);
		scoutButton.addEnableCombination(new int[] { EData.BUILDING_STARPORT });
		panel.addButton(scoutButton);

		// AirShip
		GuiButton airshipButton = new GuiButton(engine, ss.getSprite(2, 0), 0, 0);
		airshipButton.setEntType(EData.MOVER_AIRSHIP);
		airshipButton.addEnableCombination(new int[] { EData.BUILDING_DEV_CENTER, EData.BUILDING_STARPORT_2 });
		panel.addButton(airshipButton);

		return panel;
	}

	private static GuiPanel getMarinePanel(Engine engine, GuiMenu menu, int id) {
		GuiPanel panel = new GuiPanel(menu, id);
		panel.setLocation(engine.getContainer().getWidth() - 190, 280);

		SpriteSheet ss = ResourceManager.getSpriteSheet("marineentbuttons");

		// Destroyer
		GuiButton destroyerButton = new GuiButton(engine, ss.getSprite(0, 0), 0, 0);
		destroyerButton.setEntType(EData.MOVER_DESTROYER);
		destroyerButton.addEnableCombination(new int[] { EData.BUILDING_PORT });
		panel.addButton(destroyerButton);

		// Transport
		GuiButton transportButton = new GuiButton(engine, ss.getSprite(1, 0), 0, 0);
		transportButton.setEntType(EData.MOVER_MARINE_TRANSPORT);
		transportButton.addEnableCombination(new int[] { EData.BUILDING_PORT, EData.BUILDING_RADAR });
		transportButton.addEnableCombination(new int[] { EData.BUILDING_PORT, EData.BUILDING_DEV_CENTER });
		panel.addButton(transportButton);

		// Scout
		GuiButton scoutButton = new GuiButton(engine, ss.getSprite(2, 0), 0, 0);
		scoutButton.setEntType(EData.MOVER_MARINE_SCOUT);
		scoutButton.addEnableCombination(new int[] { EData.BUILDING_PORT });
		panel.addButton(scoutButton);

		// Missile launcher
		GuiButton missileLauncherButton = new GuiButton(engine, ss.getSprite(3, 0), 0, 0);
		missileLauncherButton.setEntType(EData.MOVER_MISSILE_LAUNCHER);
		missileLauncherButton.addEnableCombination(new int[] { EData.BUILDING_PORT, EData.BUILDING_DEV_CENTER });
		panel.addButton(missileLauncherButton);

		return panel;
	}

}
