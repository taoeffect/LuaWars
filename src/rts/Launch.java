package rts;

import java.io.IOException;

import org.luawars.Log;
import org.newdawn.slick.SlickException;

import rts.core.Game;

/**
 * Entry point to launch the game.
 * 
 * @author Vincent PIRAULT
 * 
 */
public class Launch {

	public static void main(String[] args) {
		try {
            if (System.getenv("DEBUG") != null)
                Log.currentLevel = Log.LEVEL.DEBUG;

			Game g = new Game("lib/resources.jar", "config/config.properties");
			g.launch();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

}
