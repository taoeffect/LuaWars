package rts.core.engine;

import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.Widget;

import rts.core.Game;
import rts.core.engine.ingamegui.GuiInGame;
import rts.core.engine.layers.Layer;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.IEntity;
import rts.core.engine.layers.entities.MoveableEntity;
import rts.core.engine.layers.entities.buildings.Building;
import rts.core.engine.layers.entities.effects.EffectManager;
import rts.core.engine.layers.entities.others.CountDown;
import rts.core.engine.layers.entities.others.Mineral;
import rts.core.engine.map.Map;
import rts.core.network.NetworkManager;
import rts.core.network.ig_udp_containers.EntityState;
import rts.utils.Configuration;
import rts.utils.ResourceManager;
import rts.utils.Timer;
import rts.views.View;

/**
 * The entry point to all game mechanics.
 * 
 * This class regroup Map, collision, rendering, network, etc and link every
 * module to make the game working.
 * 
 * The method initGame must be called to initialize the engine to play.
 * 
 * @author Vincent PIRAULT
 * 
 */
public class Engine extends View {

	// Configs and others
	private static float DEFAULT_MOUSE_SCROLL_SPEED = 0.2f;
	private static final int LIMIT_BEFORE_SCROLL = 20;
	private static final int PATHFINDING_MAX_SEARCH_DISTANCE = 100;
	private static final int TIME_BEFORE_SEE_GAME = 5000;

	private Font inGameFont;
	private NetworkManager netManager;
	private EffectManager effectManager;
	private Widget igmWidget;
	private ArrayList<GameRound> rounds;
	private ArrayList<Layer> layers;
	private ArrayList<CountDown> countDownList;
	private GuiInGame gui;
	private AStarPathFinder pathFinder;
	private PlayerInput input;
	private Timer loadGameTimer;
	private Timer exitTimer;
	private Image gameOverImage;
	private Image gameWinImage;
	private int currentRound;
	private int xScrollDecal;
	private int yScrollDecal;
	private float mouseScrollSpeed;
	private boolean gameOver;
	private boolean gameWin;
	private boolean mouseRightPressed;
	private boolean mouseLeftPressed;
	private boolean isNetwork;

	// To count the ents
	private int[] entsCount;

	public Engine() {
		rounds = new ArrayList<GameRound>();
		gui = new GuiInGame(this);
		layers = new ArrayList<Layer>();
		for (int i = 0; i < 5; i++) {
			layers.add(new Layer(this, i));
		}
		input = new PlayerInput(this);
		mouseScrollSpeed = DEFAULT_MOUSE_SCROLL_SPEED;
		entsCount = new int[9];
		countDownList = new ArrayList<CountDown>();
		loadGameTimer = new Timer(TIME_BEFORE_SEE_GAME);
		exitTimer = new Timer(4000);
		exitTimer.setTimeComplete();
		effectManager = new EffectManager(this);
	}

	@Override
	public void initResources() {
		input.init();
		gui.init();
		inGameFont = ResourceManager.getFont("courrier16b");
		gameOverImage = ResourceManager.getImage("youlost");
		gameWinImage = ResourceManager.getImage("youwin");
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		super.enter(container, game);
		container.setMouseGrabbed(true);
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		super.leave(container, game);
		igmWidget.setVisible(false);
		container.setMouseGrabbed(false);
	}

	public void exit() {
		if (isNetwork) {
			netManager.stopClient();
			if (netManager.isServer()) {
				netManager.stopServer();
			}
		}
		GameMusic.stopMusic();
		GameMusic.loopMainTheme();
		game.enterState(Game.NETWORK_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
	}

	@Override
	public void initTwlComponent() {
		igmWidget = new Widget();
		igmWidget.setSize(220, 70);
		igmWidget.setPosition(container.getWidth() / 2 - 110, container.getHeight() / 2 - 35);

		Label label = new Label("Do you really want to exit ?");
		label.setPosition(20, 20);
		igmWidget.add(label);

		Button yesButton = new Button("Yes");
		yesButton.setSize(30, 20);
		yesButton.setPosition(20, 35);
		yesButton.addCallback(new Runnable() {
			@Override
			public void run() {
				exit();
			}
		});
		igmWidget.add(yesButton);

		Button noButton = new Button("No");
		noButton.setSize(30, 20);
		noButton.setPosition(120, 35);
		noButton.addCallback(new Runnable() {
			@Override
			public void run() {
				igmWidget.setVisible(false);
			}
		});
		igmWidget.add(noButton);
		igmWidget.setVisible(false);

		root.add(igmWidget);

		gui.resize();
	}

	@Override
	public void keyPressed(int key, char c) {
		super.keyPressed(key, c);

		if (loadGameTimer.isTimeComplete() && !gameOver && !gameWin) {
			switch (key) {
			case Input.KEY_ESCAPE:
				igmWidget.setVisible(!igmWidget.isVisible());
				break;
			default:
				gui.keyPressed(key, c);
				break;
			}
		}
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		super.mousePressed(button, x, y);
		if (loadGameTimer.isTimeComplete() && !gameOver && !gameWin)
			gui.mousePressed(button, x, y);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.setFont(inGameFont);
		if (!loadGameTimer.isTimeComplete()) {
			g.setColor(Color.red);
			g.drawString("Load game, please wait...", container.getWidth() / 2 - 120, container.getHeight() / 2 - 30);
		} else {
			getMap().render(g, container, -xScrollDecal, -yScrollDecal);

			g.translate(xScrollDecal, yScrollDecal);

			for (int i = 0; i < layers.size(); i++) {
				layers.get(i).render(container, g);
			}

			for (int i = 0; i < layers.size(); i++) {
				layers.get(i).renderInfos(g);
			}

			// Fog of War
			getMap().renderFow(g, container, -xScrollDecal, -yScrollDecal);

			g.translate(-xScrollDecal, -yScrollDecal);

			// RENDER inputs
			input.render(container, g);

			// RENDER Gui
			gui.render(container, g);

			// RENDER CountDown
			for (int i = 0; i < countDownList.size(); i++) {
				countDownList.get(i).render(g, 50 + i * 15);
			}

			// DEBUG
			if (Configuration.isDebug()) {
				g.setColor(Color.white);
				g.drawString((container.getInput().getMouseX() + (-xScrollDecal)) / 20 + "  " + (container.getInput().getMouseY() + (-yScrollDecal)) / 20, 10,
						30);
			}

			// Twl gui
			super.render(container, game, g);

			if (gameOver) {
				g.drawImage(gameOverImage, container.getWidth() / 2 - 240, container.getHeight() / 2 - 27);
			} else {
				if (gameWin) {
					g.drawImage(gameWinImage, container.getWidth() / 2 - 200, container.getHeight() / 2 - 27);
				}
			}

			// Cursors
			input.renderCursor(container, g, gui.isOnGui());
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		super.update(container, game, delta);
		GameSound.update(delta);
		effectManager.update(delta);

		int mx = container.getInput().getMouseX();
		int my = container.getInput().getMouseY();

		mouseLeftPressed = container.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON);
		mouseRightPressed = container.getInput().isMousePressed(Input.MOUSE_RIGHT_BUTTON);

		// UPDATE SCROLL
		if (!input.isPressedLeft() && !container.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) && getMap().isNeededScroll()) {
			float s = (container.getInput().isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) ? mouseScrollSpeed * delta * 2 : mouseScrollSpeed * delta;

			xScrollDecal += (mx < LIMIT_BEFORE_SCROLL && xScrollDecal + s < 0) ? s : 0;
			xScrollDecal -= (mx > container.getWidth() - LIMIT_BEFORE_SCROLL && xScrollDecal + s > container.getWidth() - getMap().getWidthInPixel()
					- gui.getWidth()) ? s : 0;

			yScrollDecal += (my < LIMIT_BEFORE_SCROLL && yScrollDecal + s < 0) ? s : 0;
			yScrollDecal -= (my > container.getHeight() - LIMIT_BEFORE_SCROLL && yScrollDecal + s > container.getHeight() - getMap().getHeightInPixel()) ? s
					: 0;

			if (yScrollDecal + s < container.getHeight() - getMap().getHeightInPixel()) {
				yScrollDecal = container.getHeight() - getMap().getHeightInPixel();
			}

			if (xScrollDecal + s < container.getWidth() - getMap().getWidthInPixel() - gui.getWidth()) {
				xScrollDecal = container.getWidth() - getMap().getWidthInPixel() - gui.getWidth();
			}
		}

		gui.updateMouseEvent(container, delta);

		// UPDATE MOUSE MOVE AND CLICK
		input.update(container, gui.isMouseOnGui(container, mx, my), mx, my, -xScrollDecal, -yScrollDecal);

		// Mettre à 0 le nombres d'entités
		resetEntsCount();

		// Mettre à jour toutes les couches
		for (int i = 0; i < layers.size(); i++) {
			layers.get(i).updateAll(container, delta);
		}

		// UPDATE CURRENT ROUND
		rounds.get(currentRound).update(this, delta);

		// Si la partie est en réseau
		if (isNetwork) {
			netManager.update();
		}

		if (!loadGameTimer.isTimeComplete())
			loadGameTimer.update(delta);

		if (gameOver || gameWin) {
			exitTimer.update(delta);
			if (exitTimer.isTimeComplete()) {
				exit();
			}
		}
	}

	// Scroll

	public void changeScrollView(int x, int y) {
		xScrollDecal = x;
		yScrollDecal = y;
	}

	public void centerScrollOn(int x, int y) {
		if (x < (container.getWidth() - gui.getWidth()) / 2) {
			x = (container.getWidth() - gui.getWidth()) / 2;
		} else {
			if (x > getMap().getWidthInPixel() - (container.getWidth() - gui.getWidth()) / 2) {
				x = getMap().getWidthInPixel() - (container.getWidth() - gui.getWidth()) / 2;
			}
		}

		if (y < container.getHeight() / 2) {
			y = container.getHeight() / 2;
		} else {
			if (y > getMap().getHeightInPixel() - (container.getHeight() / 2)) {
				y = getMap().getHeightInPixel() - (container.getHeight() / 2);
			}
		}

		xScrollDecal = -(x - ((container.getWidth() - gui.getWidth()) / 2));
		yScrollDecal = -(y - (container.getHeight() / 2));
	}

	public void initGame(ArrayList<GameRound> rounds, boolean isNetwork) {
		// reset the engine
		GameSound.init();
		resetEntsCount();
		this.loadGameTimer.resetTime();
		this.gameOver = false;
		this.gameWin = false;
		this.exitTimer.setTimeComplete();
		this.countDownList.clear();
		this.mouseLeftPressed = false;
		this.mouseRightPressed = false;
		this.container.getInput().clearMousePressedRecord();
		this.container.getInput().clearKeyPressedRecord();
		this.container.getInput().consumeEvent();
		this.rounds.clear();
		this.isNetwork = isNetwork;
		this.currentRound = 0;
		for (int i = 0; i < layers.size(); i++) {
			layers.get(i).clear();
		}
		gui.clear();

		// add the new rounds
		this.rounds.addAll(rounds);
		this.pathFinder = new AStarPathFinder(getMap(), getMap().getWidthInPixel(), true);

		// INIT ALL ENTITES USING MAP
		getMap().init(this);
	}

	public void nextGameRound() {
		if (currentRound == rounds.size() - 1) {
			// GAME IS OVER
			if (entsCount[getPlayer().getId()] == 0) {
				if (!gameOver) {
					exitTimer.resetTime();
					gameOver = true;
				}
			} else {
				if (!gameWin) {
					exitTimer.resetTime();
					gameWin = true;
				}
			}
		} else {
			currentRound++;
			for (int i = 0; i < layers.size(); i++) {
				layers.get(i).clear();
			}
			this.pathFinder = new AStarPathFinder(getMap(), PATHFINDING_MAX_SEARCH_DISTANCE, true);
			getMap().init(this);
		}
	}

	// Count Down
	public void removeCountDown(CountDown countDown) {
		countDownList.remove(countDown);
	}

	public void addCountDown(CountDown countDown) {
		countDownList.add(countDown);
	}

	// Network methods

	public void updatePlayer(Player player) {
		rounds.get(currentRound).updatePlayer(player);
	}

	public void updateEntitiesState(ArrayList<EntityState> states) {
		for (int i = 0; i < states.size(); i++) {
			layers.get(states.get(i).layer).updateEntityState(states.get(i));
		}
	}

	public void updateEntityState(EntityState entityState) {
		layers.get(entityState.layer).updateEntityState(entityState);
	}

	public ArrayList<EntityState> getAllPlayerEntities() {
		ArrayList<EntityState> array = new ArrayList<EntityState>();
		int id = getPlayer().getId();
		for (int i = 0; i < layers.size(); i++) {
			array.addAll(layers.get(i).getPlayerEntitiesState(id));
		}
		return array;
	}

	public void removeEntity(int networkId, int layer) {
		layers.get(layer).removeEntity(networkId);
	}

	public void removeAllEntity(int playerId) {
		for (int i = 0; i < layers.size(); i++) {
			layers.get(i).removeAllEntity(playerId);
		}
		rounds.get(currentRound).removePlayer(playerId);
	}

	public void serverClose() {
		exit();
	}

	// Entities methods

	public ArrayList<Building> getPlayerBuilding() {
		return layers.get(Layer.FIRST_EFFECT).getPlayerBuilding();
	}

	public boolean isPlayerEntity(int playerId) {
		return getPlayer().getId() == playerId;
	}

	public void addEntity(IEntity e) {
		if (e instanceof Building && isPlayerEntity(((Building) e).getPlayerId())) {
			GameSound.constructionEffect();
		}
		layers.get(e.getLayer()).addEntity(e);
	}

	public void removeEntity(IEntity e) {
		layers.get(e.getLayer()).removeEntity(e);
		if (isNetwork && e instanceof ActiveEntity) {
			netManager.sendDeleteEntity(((ActiveEntity) e).getNetworkID(), ((ActiveEntity) e).getPlayerId(), e.getLayer());
		}
	}

	public ActiveEntity getEntityAt(ActiveEntity entity, float x, float y) {
		return getMap().getEntityAt(entity, (int) x / getTileW(), (int) y / getTileH());
	}

	public void deselectAllEntities() {
		for (int i = 0; i < layers.size(); i++)
			layers.get(i).deselectAllEntities();
	}

	public void selectEntitiesBetween(int sx, int sy, int mx, int my) {
		sx += -xScrollDecal;
		sy += -yScrollDecal;
		mx += -xScrollDecal;
		my += -yScrollDecal;
		for (int i = 0; i < layers.size(); i++) {
			layers.get(i).selectEntitiesBetween((sx <= mx) ? sx : mx, (sy <= my) ? sy : my, (sx > mx) ? sx : mx, (sy > my) ? sy : my);
		}
	}

	public ArrayList<ActiveEntity> getPlayerSelectedEntities() {
		ArrayList<ActiveEntity> a = new ArrayList<ActiveEntity>();
		for (int i = 0; i < layers.size(); i++) {
			a.addAll(layers.get(i).getPlayerSelectedEntities(getPlayer().getId()));
		}
		return a;
	}

	public ArrayList<MoveableEntity> getSelectedMoveableEntities(int x, int y) {
		ArrayList<MoveableEntity> a = new ArrayList<MoveableEntity>();
		for (int i = 0; i < layers.size(); i++) {
			a.addAll(layers.get(i).getSelectedMoveableEntities(x, y));
		}
		return a;
	}

	public Mineral getCloserMineral(ActiveEntity entity) {
		return layers.get(Layer.FIRST_EFFECT).getCloserMineral(entity);
	}

	public ActiveEntity getFirstEnemyEntity(ActiveEntity entity, int view) {
		int sx = (int) (entity.getX() - ((view / 2) * 20));
		int sy = (int) (entity.getY() - ((view / 2) * 20));
		for (int i = 0; i < view; i++) {
			for (int j = 0; j < view; j++) {
				ActiveEntity ae = getEntityAt(entity, sx + 20 * i, sy + 20 * j);
				if (ae != null && ae.isAlive() && ae.getTeamId() != -1 && ae.getTeamId() != entity.getTeamId())
					return ae;
			}
		}
		return null;
	}

	public void moveEntitiesTo(int mx, int my) {
		input.moveOrSpecialAction(mx, my);
	}

	// ents count

	public void resetEntsCount() {
		for (int i = 0; i < entsCount.length; i++)
			entsCount[i] = 0;
	}

	public int[] getEntsCount() {
		return entsCount;
	}

	public void addEntToCount(int playerId) {
		if (playerId == -1) {
			entsCount[8]++;
		} else {
			entsCount[playerId]++;
		}
	}

	// Others

	public Map getMap() {
		return rounds.get(currentRound).getMap();
	}

	public int getTileW() {
		return rounds.get(currentRound).getMap().getTileWidth();
	}

	public int getTileH() {
		return rounds.get(currentRound).getMap().getTileHeight();
	}

	public AStarPathFinder getPathFinder() {
		return pathFinder;
	}

	public void setNetworkManager(NetworkManager netManager) {
		this.netManager = netManager;
		this.netManager.setEngine(this);
	}

	public NetworkManager getNetworkManager() {
		return netManager;
	}

	public Player getPlayer() {
		return rounds.get(currentRound).getPlayer();
	}

	public Player getPlayer(int playerId) {
		return rounds.get(currentRound).getPlayer(playerId);
	}

	public HashMap<Integer, ArrayList<Player>> getPlayers() {
		return rounds.get(currentRound).getPlayers();
	}

	@Override
	public int getID() {
		return Game.ENGINE_VIEW_ID;
	}

	public int getMouseX() {
		return container.getInput().getMouseX() + (-xScrollDecal);
	}

	public int getMouseY() {
		return container.getInput().getMouseY() + (-yScrollDecal);
	}

	public int getXScrollDecal() {
		return xScrollDecal;
	}

	public int getYScrollDecal() {
		return yScrollDecal;
	}

	public GameContainer getContainer() {
		return container;
	}

	public boolean isNetwork() {
		return isNetwork;
	}

	public GuiInGame getGui() {
		return gui;
	}

	public boolean isMouseLeftPressed() {
		return mouseLeftPressed;
	}

	public boolean isMouseRightPressed() {
		return mouseRightPressed;
	}

}
