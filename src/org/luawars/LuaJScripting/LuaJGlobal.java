package org.luawars.LuaJScripting;

import org.luaj.vm2.LuaValue;
import rts.Launch;
import rts.core.Game;
import rts.core.engine.ingamegui.GuiInGame;

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
 */
public class LuaJGlobal {

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
