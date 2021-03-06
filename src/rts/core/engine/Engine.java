package rts.core.engine;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Iterator;
import java.util.Collections;

import org.luawars.LuaJScripting.CallLua;
import org.luawars.LuaJScripting.LuaJGlobal;
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
import rts.core.engine.layers.entities.*;
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

import static java.lang.Math.abs;

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

    //TRUNG NGUYEN
    public PlayerInput getInput() {
        return input;
    }

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

    private boolean escapeMenu; // we don't want the screen to scroll when the escape menu is up
    CallLua callLua;

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

        callLua = new CallLua(gui.getMenuGui(), input);

        escapeMenu = false;
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
        container.setMouseGrabbed(false);
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
        try {
            game.getNetworkManager().createServer();
            game.getNetworkManager().joinServer("localhost");
            game.enterState(Game.CREATE_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mainMenu() {
        if (isNetwork) {
            netManager.stopClient();
            if (netManager.isServer()) {
                netManager.stopServer();
            }
        }
        GameMusic.stopMusic();
        GameMusic.loopMainTheme();
        game.enterState(Game.MAIN_MENU_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
    }

    public void tutorialMenu() {
        if (isNetwork) {
            netManager.stopClient();
            if (netManager.isServer()) {
                netManager.stopServer();
            }
        }
        GameMusic.stopMusic();
        GameMusic.loopMainTheme();
        try {
            game.getNetworkManager().createServer();
            game.getNetworkManager().joinServer("localhost");
            game.enterState(Game.TUTORIAL_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initTwlComponent() {
        igmWidget = new Widget();
        igmWidget.setSize(200, 220);
        igmWidget.setPosition(container.getWidth() / 2 - 110, container.getHeight() / 2 - 35);

        Label label = new Label("Exit");
        label.setPosition(85, 20);
        igmWidget.add(label);

        Button mainButton = new Button("Main Menu");
        mainButton.setSize(100, 20);
        mainButton.setPosition(30, 45);
        mainButton.addCallback(new Runnable() {
            @Override
            public void run() {
                mainMenu();
            }
        });
        igmWidget.add(mainButton);

        Button yesButton = new Button("Campaign Menu");
        yesButton.setSize(100, 20);
        yesButton.setPosition(30, 85);
        yesButton.addCallback(new Runnable() {
            @Override
            public void run() {
                exit();
            }
        });
        igmWidget.add(yesButton);

        Button tutorialButton = new Button("Tutorial Menu");
        tutorialButton.setSize(100, 20);
        tutorialButton.setPosition(30, 125);
        tutorialButton.addCallback(new Runnable() {
            @Override
            public void run() {
                tutorialMenu();
            }
        });
        igmWidget.add(tutorialButton);

        Button noButton = new Button("Cancel");
        noButton.setSize(100, 20);
        noButton.setPosition(30, 165);
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
    // THIS OPENS UP THE ESCAPE BUTTON
    public void keyPressed(int key, char c) {
        super.keyPressed(key, c);

        if (loadGameTimer.isTimeComplete() && !gameOver && !gameWin) {
            switch (key) {
                case Input.KEY_ESCAPE:
                    igmWidget.setVisible(!igmWidget.isVisible());
                    escapeMenu = !escapeMenu;
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
        if (!escapeMenu && !input.isPressedLeft() && !container.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) && getMap().isNeededScroll()) {
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

        // player can put stuff they want to update every frame in here
        callLua.runScript("resources/Lua Scripts/update.lua");


        // Mettre � 0 le nombres d'entit�s
        resetEntsCount();

        // Mettre � jour toutes les couches
        for (int i = 0; i < layers.size(); i++) {
            layers.get(i).updateAll(container, delta);
        }

        // UPDATE CURRENT ROUND
        rounds.get(currentRound).update(this, delta);

        // Si la partie est en r�seau
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
        callLua.reset(null);//getPlayer());

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
                    if (checkUnlock())                             //if it's true that the next map should be unlock, add 1 to number of unlocked maps for current profile
                    {
                        int k =  Configuration.getProgress(Configuration.getProfile1());
                        k++;
                        Configuration.setProgress(Configuration.getProfile1(), k);
                        game.getStateByIndex(6).initResources();                         //update CreateView
                        game.getStateByIndex(6).initTwlComponent();
                    }
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

    public boolean checkUnlock()             //check if the next map should be unlocked upon gameWin
    {
        boolean result = false;

        ArrayList<Map> maps = new ArrayList<Map>(ResourceManager.getAllMaps().values());     //maps = all maps in resources
        Collections.sort(maps);                                                              //order of maps in CreateView (campaign menu)
        int correctMap = 0;
        for (int i = 0; i < maps.size(); i++)
        {
            if (maps.get(i).getName() == getMap().getName())
            {
                correctMap = i;                                                 //number of the current map as listed in CreateView
            }
        }
        if(correctMap == Configuration.getProgress(Configuration.getProfile1()) - 1)   //if current map is the last map that was last unlocked, result is true
        {
            result = true;
        }

        return result;
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

    private int getTypeFromName(String unitName) {
        int type = -1;
        for(int i = 0; i < EData.NAMES.length; i++) {
            if(EData.NAMES[i].equals(unitName)) {
                type = i;
                break;
            }
        }
        return type;
    }

    /**
     * Creates a priority queue of all the player's active entities based on shortest distance
     * @param tileX - point to select close to
     * @param tileY
     */
    /* To determine all enemies in the area */
    public ArrayList<ActiveEntity> selectAllEnemies(int tileX,int tileY)
    {
        ArrayList<ActiveEntity> allEnemies =
                new ArrayList<ActiveEntity>();
        for (int i = 0; i < layers.size(); i++) {
            for(int j = 0; j < layers.get(i).getArray().size(); j++){
                if(layers.get(i).getArray().get(j) instanceof ActiveEntity){
                    ActiveEntity currentUnit = (ActiveEntity) layers.get(i).getArray().get(j);
                    if(currentUnit.getPlayerId() != this.getPlayer().getId()) {
                        allEnemies.add(currentUnit);
                    }
                }
            }
        }
        return allEnemies;
    }

    /* Determine's distance of unit A and B */
    public boolean DistanceOfUnit(ActiveEntity unitA, ActiveEntity unitB)
    {
        float currentTileX1 = unitA.getX()/getTileW();
        float currentTileY1 = unitA.getX()/getTileW();
        float currentTileX2 = unitB.getX()/getTileW();
        float currentTileY2 = unitB.getX()/getTileW();
        // get units less than radius number of tiles away
        double distanceFromPoint = Math.pow(currentTileX1 - currentTileX2, 2) + Math.pow(currentTileY1 - currentTileY2, 2);

        return Math.pow(EData.VIEW[unitA.getType()],2) < distanceFromPoint;
    }

    /* Selects all units */
    public ArrayList<ActiveEntity> selectAllUnits(int x, int y)
    {
        ArrayList<ActiveEntity> allUnits = new ArrayList<ActiveEntity>();

        for(int i=0;i< layers.size(); i++)
        {
            for(int j=0; i<layers.get(i).getArray().size(); j++)
            {
                if (layers.get(i).getArray().get(j) instanceof ActiveEntity)
                {
                    ActiveEntity currentUnit = (ActiveEntity) layers.get(i).getArray().get(j);
                    if (currentUnit.getPlayerId() == this.getPlayer().getId())
                    {
                        allUnits.add(currentUnit);
                    }
                }
            }
        }
        return allUnits;
    }

    /* Trung's Function */
    public ArrayList<ActiveEntity> selectClosestEntities(int tileX, int tileY, float radius, int numUnits, String unitType){
        // this part finds all of the player's units on the map and adds them into allEnts
        PriorityQueue<ActiveEntity> allEnts =
                new PriorityQueue<ActiveEntity>(10, new ActiveEntityComparator(new Point(tileX, tileY)));
        int wantedUnitType = unitType == null ? -1 : getTypeFromName(unitType);
        for (int i = 0; i < layers.size(); i++) {
            for(int j = 0; j < layers.get(i).getArray().size(); j++){
                if(layers.get(i).getArray().get(j) instanceof ActiveEntity){
                    ActiveEntity currentUnit = (ActiveEntity) layers.get(i).getArray().get(j);
                    if(currentUnit.getPlayerId() == this.getPlayer().getId() && (unitType == null || wantedUnitType == currentUnit.getType())) {
                        allEnts.add(currentUnit);
                    }
                }
            }
        }

        // now that we have all the entities, find the entities that are closest to the radius and put them in selectedUnits
        ArrayList<ActiveEntity> selectedUnits = new ArrayList<ActiveEntity>();
        Iterator<ActiveEntity> iter = allEnts.iterator();
        for(int i = 0; i < numUnits; i++){
            if(iter.hasNext()){
                ActiveEntity currentUnit = iter.next();
                float currentTileX = currentUnit.getX()/getTileW();
                float currentTileY = currentUnit.getX()/getTileW();
                // get units less than radius number of tiles away
                double distanceFromPoint = Math.pow(currentTileX - tileX, 2) + Math.pow(currentTileY - tileY, 2);
                if(distanceFromPoint > Math.pow(radius, 2))
                {
                    // note that once we find one that's not in the distance, we can break
                    // because all the other ones have a greater radius
                    break;
                }
                selectedUnits.add(currentUnit);
                currentUnit.selected();
            }
        }
        return selectedUnits;
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

    public CallLua getPlayerLua() {
        return callLua;
    }

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
