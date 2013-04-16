package org.luawars.LuaJScripting;

import rts.core.Game;
import rts.core.engine.ingamegui.GuiInGame;

import java.util.HashMap;

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
//    public static Game game;
//    public static int baseX;
//    public static int baseY;
    public static HashMap<String, Integer> luaJGlobal = new HashMap<String, Integer>();
    /*
        List of globals:
        "baseX" - gives x location of last constructor made
        "baseY" - gives y location of last constructor made
     */

    /**
     * Adds globals from the game to this class which will then be able to interface with our LuaJ scripts
     * @param key
     * @param value
     */
    public static void addNewLuaJGlobal(String key, Integer value) {
        luaJGlobal.put(key, value);
    }
}
