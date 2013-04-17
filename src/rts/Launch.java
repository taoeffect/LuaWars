package rts;

import java.io.IOException;

import org.luawars.LuaJScripting.CallLua;
import org.luawars.Log;
import org.newdawn.slick.SlickException;

import rts.core.Game;

import javax.swing.*;

/**
 * Entry point to launch the game.
 * 
 * @author Vincent PIRAULT
 * 
 */
public class Launch {
    public static Game g;
	public static void main(String[] args) {
		try {
            if (System.getenv("DEBUG") != null)
                Log.currentLevel = Log.LEVEL.DEBUG;
			g = new Game("lib/resources.jar", "config/config.properties");

            // if you don't understand this line, read this: http://www.lua.org/pil/8.1.html
            CallLua.initLuaPath("?.lua;?/?.lua;?/?/?.lua;resources/Lua Scripts/?.lua");

            g.launch();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
