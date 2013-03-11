package rts.core.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import rts.utils.Timer;

/**
 * This class represent a goal to finish the current game.
 * 
 * When the method isComplete return true, the goal is complete.
 * 
 * @author Vince
 * 
 */
public class GameGoal {

	public static final int BATTLE = 0;

	private int goalType;
	private Timer timer;

	public GameGoal(int type) {
		goalType = type;
		// start the process after 10 seconds
		timer = new Timer(10000);
	}

	public boolean isComplete(Engine engine, int delta) {
		if (timer.isTimeComplete()) {
			if (engine.getEntsCount()[engine.getPlayer().getId()] == 0)
				return true;
			else {
				switch (goalType) {
				case BATTLE:
					int nbAlive = 0;
					HashMap<Integer, ArrayList<Player>> players = engine.getPlayers();
					Iterator<Integer> i = players.keySet().iterator();
					while (i.hasNext()) {
						int id = i.next();
						ArrayList<Player> a = players.get(id);
						int nbEnt = 0;
						for (int j = 0; j < a.size(); j++) {
							nbEnt += engine.getEntsCount()[a.get(j).getId()];
						}
						if (nbEnt != 0)
							nbAlive++;
					}
					return (nbAlive == 1 && players.size() != 1);
				default:
					return false;
				}
			}
		} else
			timer.update(delta);
		return false;
	}

	public static String getType(String gameType) {
		if (Integer.parseInt(gameType) == BATTLE)
			return "Battle";

		return "Unknow";
	}

}
