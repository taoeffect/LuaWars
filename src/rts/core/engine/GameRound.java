package rts.core.engine;

import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.SlickException;

import rts.core.engine.map.Map;

/**
 * This class provide engine inputs parameters.
 * 
 * @author Vincent PIRAULT
 * 
 */
public class GameRound {

	private Map map;
	private GameGoal goal;
	private ArrayList<Player> players;
	private HashMap<Integer, ArrayList<Player>> playersByTeam;
	private AI ai;

	public GameRound(Map map, GameGoal goal) {
		this.map = map;
		this.goal = goal;
		this.players = new ArrayList<Player>();
		this.playersByTeam = new HashMap<Integer, ArrayList<Player>>();
		this.ai = new AI();
	}

	public void update(Engine engine, int delta) throws SlickException {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).isAI()) {
				ai.update(engine, players.get(i), delta);
			}
		}

		if (goal.isComplete(engine, delta)) {
			engine.nextGameRound();
		}
	}

	public void updatePlayer(Player player) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getId() == player.getId()) {
				if (!players.get(i).isPlayer()) {
					players.get(i).update(player);
				}
			}
		}
	}

	public void addPlayer(Player player) {
		players.add(player);
		if (playersByTeam.containsKey(new Integer(player.getTeamId()))) {
			playersByTeam.get(new Integer(player.getTeamId())).add(player);
		} else {
			ArrayList<Player> a = new ArrayList<Player>();
			a.add(player);
			playersByTeam.put(player.getTeamId(), a);
		}
	}

	public void removePlayer(int playerId) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getId() == playerId) {
				players.remove(i);
			}
		}
	}

	public Player getPlayer(int id) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getId() == id) {
				return players.get(i);
			}
		}
		return null;
	}

	public Player getPlayer() {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).isPlayer()) {
				return players.get(i);
			}
		}
		return null;
	}

	public Map getMap() {
		return map;
	}

	public HashMap<Integer, ArrayList<Player>> getPlayers() {
		return playersByTeam;
	}

}
