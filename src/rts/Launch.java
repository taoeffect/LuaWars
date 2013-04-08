package rts;

import java.io.IOException;

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

	public static void main(String[] args) {
        if (System.getenv("DEBUG") != null)
            Log.currentLevel = Log.DEBUG;
        try {
            new Game("lib/resources.jar", "config/config.properties").launch();
        } catch (SlickException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
