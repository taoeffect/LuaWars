package org.luawars.LuaJScripting;

import org.luaj.vm2.LuaValue;
import rts.Launch;
import rts.core.Game;
import rts.core.engine.Player;
import rts.core.engine.PlayerInput;
import rts.core.engine.ingamegui.GuiButton;
import rts.core.engine.ingamegui.GuiInGame;
import rts.core.engine.ingamegui.GuiMenu;
import rts.core.engine.ingamegui.GuiPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Trung
 * Date: 3/25/13
 * Time: 1:15 PM
 * To change this template use File | Settings | File Templates.
 *
 * Allows LuaJ to interact with our java program. Since LuaJ requires a bunch of static classes
 * to create new functions, we have to create static global variables to allow LuaJ to interact with our Java code.
 *
 * This can't be static because there will be two AIs, the player's AI and the enemy's AI. Each of these AI's needs access
 * to a different set of globals.
 */
public class LuaJGlobal {
	private GuiMenu menu;
    private PlayerInput input;
    private Player player;

      // These two are a little confusing because Lua starts it's indices at 1 instead of 0
	  // To accomodate this, I just increase the size of these two arrays by 1
                                 // and the valid indices will be (1 to panel.size() + 1)
                                 // but they will refer to the original/previous panel
                                 // i.e. panelReady[1] refers to GuiPanel[0], panelReady[2] refers to GuiPanel[1], etc
	public ArrayList<Point> enemies;         // need to update
	public String lastPlacedBuilding;        // need to update
	public Point lastPlacedBuildingLocation; // need to update
	public ArrayList<Point> beingAttacked;   // need to update

	public LuaJGlobal(GuiMenu menu, PlayerInput input, Player player) {
        this.menu = menu;
        this.input = input;
        this.player = player;
        init(player);
    }

    /**
	 * Tells whether a tile coordinate has fog of war on it or not
	 * @param tileCoordinate
	 * @return true if location is visible, else return false
	 */
	public boolean isTileVisible(Point tileCoordinate) {
		return !(Launch.g.getEngine().getMap().isEnableFow() &&
				Launch.g.getEngine().getMap().fogOn(tileCoordinate.x, tileCoordinate.y));
	}

    public int getMoney() {
        return player.getMoney();
    }

    public int getPanelBuilding(int panelId) {
        // actualPanelId is because Lua starts it's index at 1 instead of 0
        int actualPanelId = panelId - 1;
        GuiPanel panel = menu.getPanels().get(actualPanelId);
        ArrayList<GuiButton> buttons = panel.getButtons();
        for(int j = 0; j < buttons.size(); j++) {
            if(!buttons.get(j).getProcessList().isEmpty()) {
                return j;
            }
        }
        return -1;
    }

    public int getPanelReady(int panelId) {
        // actualPanelId is because Lua starts it's index at 1 instead of 0
        int actualPanelId = panelId - 1;
        GuiPanel panel = menu.getPanels().get(actualPanelId);
        ArrayList<GuiButton> buttons = panel.getButtons();
        for(int j = 0; j < buttons.size(); j++) {
            if(!buttons.get(j).hasProcessReady()) {
                return j;
            }
        }
        return -1;
    }

	/**
	 * This will initialize all the globals to some value. This is the same as the other initialize function, but I'm
	 * using LuaJava with this so I'm trying to test that.
	 */
	public void init(Player player) {
        this.player = player;

        // these 4 things aren't yet implemented
		//enemies = new ArrayList<Point>();
		//lastPlacedBuilding = "";
		//lastPlacedBuildingLocation = new Point(-1, -1);
		//beingAttacked = new ArrayList<Point>();
	}


	// HashMap that contains all of the globals
	public static HashMap<String, LuaValue> luaJGlobal = new HashMap<String, LuaValue>();
	/*
		List of globals:
		"baseX" - gives x location of last constructor made
		"baseY" - gives y location of last constructor made
		"buildingPanel0" through "buildingPanel3" - tells whether a specific panel is currently building something
		"buildingPanelReady0" through "buildingPanelReady3" - tells whether a specific panel is currently building something
		XXX get enemy locations (if we can see them)
		XXX get units/buildings that are being attacked
		XXX get whether a certain area is visible or not (has Fog of War)
	 */


	public static PriorityQueue<AIGamePriorities> AIpriorityQueue = new PriorityQueue<AIGamePriorities>();

	public static void initializeLuaJGlobal() {
		luaJGlobal.put("baseX", LuaValue.NIL);
		luaJGlobal.put("baseY", LuaValue.NIL);
		for(int i = 0 ; i < Launch.g.getEngine().getGui().getMenuGui().getPanels().size(); i++) {
			luaJGlobal.put("buildingPanel" + i, LuaValue.valueOf(-1));
			luaJGlobal.put("buildingPanelReady" + i, LuaValue.valueOf(-1));
		}
		luaJGlobal.put("money", LuaValue.valueOf(Launch.g.getEngine().getPlayer().getMoney()));
	}
	/**
	 * Adds (or renews) globals from the game to this class which will then be able to interface with our LuaJ scripts
	 * @param key
	 * @param value
	 */
	public static void addNewLuaJGlobal(String key, LuaValue value) {
		removeLuaJGlobal(key);
		luaJGlobal.put(key, value);
	}

	public static void removeLuaJGlobal(String key) {
		luaJGlobal.remove(key);
	}

	public static LuaValue getLuaJGlobal(String key) {
		if(luaJGlobal.get(key) == null)
			return LuaValue.NIL;
		else
			return luaJGlobal.get(key);
	}
}
