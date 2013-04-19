package rts.views;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


import org.luawars.Log;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.loading.DeferredResource;

import rts.core.Game;
import rts.core.engine.GameGoal;
import rts.core.engine.GameMusic;
import rts.core.engine.GameRound;
import rts.core.engine.Player;
import rts.core.engine.map.Map;
import rts.core.network.INetworkMenuListener;
import rts.core.network.NetworkManager;
import rts.core.network.menu_tcp_containers.ClientState;
import rts.core.network.menu_tcp_containers.MessageState;
import rts.core.network.menu_tcp_containers.ServerState;
import rts.utils.Colors;
import rts.utils.Configuration;
import rts.utils.ResourceManager;
import rts.utils.Timer;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.CallbackWithReason;
import de.matthiasmann.twl.ComboBox;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ListBox;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.Table;
import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.ThemeInfo;
import de.matthiasmann.twl.ToggleButton;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.ListBox.CallbackReason;
import de.matthiasmann.twl.TableBase.CellWidgetCreator;
import de.matthiasmann.twl.model.AbstractTableModel;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;
import de.matthiasmann.twl.model.ListModel;
import de.matthiasmann.twl.model.SimpleChangableListModel;
import de.matthiasmann.twl.model.SimpleIntegerModel;



public class CreateView extends View {

        class ChatArea extends HTMLTextAreaModel {
        String html;

        ChatArea(String s) { super(s); }

        public String getHtml() {
            return html;
        }

        public void setHtml(String html) {
            super.setHtml(html);
            this.html = html;
        }
    }

    private static final String TILESET_LOCATION = "resources/maps";

    private boolean initComplete;
    private int playerPosition;
    private Timer switchTimer;
    private ArrayList<Map> maps;
    private int totalMaps;
    private NetworkManager netManager;
    private Image background;
    private Image title;

    // GUI

    // Server Panel
    private Widget serverPanel;
    private ListBox<Map> mapList;
    private SimpleChangableListModel<Map> mapListModel;
    private ComboBox<String> comboTecLevel;
    private SimpleChangableListModel<String> comboTecLevelModel;
    private ComboBox<String> comboMoney;
    private SimpleChangableListModel<String> comboMoneyModel;

    // Map Panel
    private Widget mapPanel;

    // Client Panel
    private Table clientTable;
    private ClientTableModel tableModel;


    // Buttons
    private Button exitButton;
    private Button launchButton;

    public CreateView() {
        playerPosition = -1;
    }

    @Override
    public void initResources() {
        background = ResourceManager.getImage("create_view_background");
        title = ResourceManager.getSpriteSheet("menutitles").getSprite(0, 1);
        maps = new ArrayList<Map>(ResourceManager.getAllMaps().values());
        Collections.sort(maps);
        totalMaps = maps.size();

        //remove the maps not yet unlocked by the current profile
        int delete = maps.size() - Configuration.getProgress(Configuration.getProfile1());
        if (delete >= 0)
        {
            while(delete != 0)
            {
                maps.remove(maps.size() - 1);
                delete--;
            }
        }

        netManager = game.getNetworkManager();
        netManager.setMenuListener(new NetworkMenuListener());
        switchTimer = new Timer(1000);
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        super.enter(container, game);
        if (netManager.isServer()) {
            serverPanel.setEnabled(true);
            mapList.setSelected(0);
            // Init server state for listener
            ServerState ss = netManager.getServerState();
            ss.mapName = mapListModel.getEntry(mapList.getSelected()).toString();
            ss.tecLevel = comboTecLevel.getSelected();
            ss.startMoney = comboMoney.getSelected();
            ss.gameType = 0;
            ss.nbMaxPlayer = mapListModel.getEntry(mapList.getSelected()).getNumberOfSpawns();
            launchButton.setVisible(true);
        } else {
            serverPanel.setEnabled(false);
            launchButton.setVisible(false);
        }
        clientTable.setEnabled(true);
        exitButton.setEnabled(true);
        switchTimer.resetTime();
        container.getInput().clearKeyPressedRecord();
        container.getInput().clearMousePressedRecord();
        container.getInput().consumeEvent();

    }

    @Override
    public void leave(GameContainer container, StateBasedGame game) throws SlickException {
        super.leave(container, game);
        tableModel.clear();
        playerPosition = -1;
        launchButton.setVisible(false);
    }

    @Override
    public void initTwlComponent() {

        int x = container.getWidth() / 2 - 370;// 25
        int y = container.getHeight() / 2 - 250;// 50

        // Server panel
        serverPanel = new Widget();
        serverPanel.setPosition(x, y);
        serverPanel.setSize(400, 430);

        Label l = new Label("Select a map:              " + maps.size() + "/" + totalMaps + " Maps Unlocked");
        l.setPosition(20, 24);
        serverPanel.add(l);

        mapListModel = new SimpleChangableListModel<Map>(maps);
        mapList = new ListBox<Map>(mapListModel);
        mapList.setPosition(20, 40);
        mapList.setSize(360, 350);
        mapList.setSelected(0);
        mapList.addCallback(new CallbackWithReason<CallbackReason>() {
            @Override
            public void callback(CallbackReason arg0) {
                sendServerInfos();
            }
        });
        serverPanel.add(mapList);

        l = new Label("Tec level:");
        l.setPosition(20, 400);
        serverPanel.add(l);

        comboTecLevelModel = new SimpleChangableListModel<String>("Level 1", "Level 2", "Level 3", "Level 4");
        comboTecLevel = new ComboBox<String>(comboTecLevelModel);
        comboTecLevel.setPosition(100, 390);
        comboTecLevel.setSize(80, 20);
        comboTecLevel.setSelected(0);
        comboTecLevel.addCallback(new Runnable() {
            @Override
            public void run() {
                sendServerInfos();
            }
        });
        serverPanel.add(comboTecLevel);

        l = new Label("Start Money:");
        l.setPosition(200, 400);
        serverPanel.add(l);

        comboMoneyModel = new SimpleChangableListModel<String>();
        for (int i = 5000; i <= 50000; i += 2500) {
            comboMoneyModel.addElement(i + "$");
        }
        comboMoney = new ComboBox<String>(comboMoneyModel);
        comboMoney.addCallback(new Runnable() {
            @Override
            public void run() {
                sendServerInfos();
            }
        });
        comboMoney.setPosition(300, 390);
        comboMoney.setSize(80, 20);
        comboMoney.setSelected(1);
        serverPanel.add(comboMoney);


        root.add(serverPanel);

        // Map panel

        mapPanel = new Widget();
        mapPanel.setPosition(x + 475, y);
        mapPanel.setSize(250, 270);

        root.add(mapPanel);

        // Client Panel

        clientTable = new Table();
        tableModel = new ClientTableModel();
        tableModel.registerTableCellRender(clientTable);
        clientTable.setModel(tableModel);
        clientTable.setSize(410, 200);
        clientTable.setPosition(x - 3, 450 + y);
        clientTable.setDefaultSelectionManager();
        root.add(clientTable);


        // Buttons

        exitButton = new Button("Back");
        exitButton.setSize(70, 30);
        exitButton.setPosition(x - 5, y + 500);
        exitButton.addCallback(new Runnable() {
            @Override
            public void run() {
                if (netManager.isServer()) {
                    netManager.stopServer();
                }
                netManager.stopClient();
                CreateView.this.game.enterState(Game.PROFILE_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
            }
        });
        root.add(exitButton);

        launchButton = new Button("Launch");
        launchButton.setSize(70, 30);
        launchButton.setPosition(540 + x, y + 350);
        launchButton.setVisible(false);
        launchButton.addCallback(new Runnable() {
            @Override
            public void run() {
                if (mapList.getSelected() == Configuration.getProgress(Configuration.getProfile1()) - 1)
                {
                    comboTecLevel.setSelected(0);
                    comboMoney.setSelected(1);
                }
                netManager.launchGame();
            }
        });
        root.add(launchButton);

        initComplete = true;
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.drawImage(background, 0, 0);
        super.render(container, game, g);
        Map m = maps.get(mapList.getSelected());
        int x = container.getWidth() / 2 - 370;
        int y = container.getHeight() / 2 - 250;
        g.drawImage(title, x + 170, y - 40);
        g.setColor(Color.white);
        g.drawString("(" + m.getWidthInPixel() + "*" + m.getHeightInPixel() + ")", x + 545, y + 22);
        m.renderMiniMap(g, 500 + x, y + 45, 200, 200, true);
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        super.update(container, game, delta);
    }

    @Override
    public int getID() {
        return Game.CREATE_VIEW_ID;
    }

    // Networks methods

    private void sendClientInfo() {
        if (playerPosition != -1) {
            synchronized (tableModel) {
                ClientState cs = netManager.getClientState();
                cs.team = tableModel.getSelectedTeam();
                cs.spawn = tableModel.getSelectedSpawn();
                cs.color = tableModel.getSelectedColor();
                cs.isReady = tableModel.isReady();
            }
            netManager.updateClientState();
        }
    }

    private void updateServerInfos(ServerState ss) {
        if (!netManager.isServer()) {
            boolean changeMap = false;
            for (int i = 0; i < mapListModel.getNumEntries(); i++) {
                if (mapListModel.getEntry(i).toString().equals(ss.mapName)) {
                    if (mapList.getSelected() != i) {
                        changeMap = true;
                        mapList.setSelected(i);
                    }
                    break;
                }
            }

            if (changeMap && playerPosition >= ss.nbMaxPlayer) {
                // Player is ejected because the new map didn't have enough row
                netManager.stopClient();
                CreateView.this.game.enterState(Game.NETWORK_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
            } else {
                comboTecLevel.setSelected(ss.tecLevel);
                comboMoney.setSelected(ss.startMoney);
            }
        }
        // Send client infos
        sendClientInfo();
    }

    private void sendServerInfos() {
        ServerState ss = netManager.getServerState();
        if (initComplete && ss != null) {
            ss.mapName = mapListModel.getEntry(mapList.getSelected()).toString();
            ss.tecLevel = comboTecLevel.getSelected();
            ss.startMoney = comboMoney.getSelected();
            ss.gameType = 0;
            ss.nbMaxPlayer = mapListModel.getEntry(mapList.getSelected()).getNumberOfSpawns();

            // Send infos
            netManager.updateServerState();
        }
    }

    private class NetworkMenuListener implements INetworkMenuListener {

        @Override
        public void clientsInfosChange(ArrayList<ClientState> clientStates) {
            String[] spawns = new String[clientStates.size()];
            String[] colors = new String[clientStates.size()];

            for (int i = 0; i < clientStates.size(); i++) {
                spawns[i] = clientStates.get(i).spawn;
                colors[i] = clientStates.get(i).color;
            }

            synchronized (tableModel) {
                for (int i = 0; i < 8; i++) {
                    if (i < clientStates.size()) {
                        // Real players
                        tableModel.updateRow(i, spawns, colors, clientStates.get(i));
                    } else {
                        // Non player
                        tableModel.resetRow(i);
                    }
                }
            }
        }

        @Override
        public void connectionSuccess() {
        }

        @Override
        public void disconnected() {
            game.enterState(Game.MAIN_MENU_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
        }

        @Override
        public void loadGame(ServerState serverState) {
            updateServerInfos(serverState);
            serverPanel.setEnabled(false);
            clientTable.setEnabled(false);
            exitButton.setEnabled(false);

            // Only one game round
            ArrayList<GameRound> rounds = new ArrayList<GameRound>();
            GameRound round = new GameRound(mapListModel.getEntry(mapList.getSelected()), new GameGoal(0));

            tableModel.initPlayersWithTable(round);

            // Add the round and initialize the engine
            rounds.add(round);
            game.getEngine().setNetworkManager(netManager);
            game.getEngine().initGame(rounds, true);

            // Load complete send it to server
            netManager.getClientState().isLoad = true;
            netManager.updateClientState();
        }

        //chat panel method, not needed
        @Override
        public void receiveMessage(MessageState message) {
        }

        @Override
        public void serverInfosChange(ServerState serverState) {
            updateServerInfos(serverState);
        }

        @Override
        public void switchToGame() {
            GameMusic.playMusic();
            game.enterState(Game.ENGINE_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
        }
    }

    private class ClientTableModel extends AbstractTableModel {

        private boolean callBackSecure;

        private ComboBoxValue[] comboPlayer;
        private ComboBoxValue[] comboTeam;
        private ComboBoxValue[] comboSpawns;
        private ComboBoxValue[] comboColor;
        private CheckBoxValue[] checkBoxReady;

        private ArrayList<SimpleChangableListModel<String>> comboPlayerModel;
        private ArrayList<SimpleChangableListModel<String>> comboSpawnModel;
        private ArrayList<SimpleChangableListModel<String>> comboColorModel;

        public ClientTableModel() {
            callBackSecure = true;
            comboPlayer = new ComboBoxValue[8];
            comboTeam = new ComboBoxValue[8];
            comboSpawns = new ComboBoxValue[8];
            comboColor = new ComboBoxValue[8];
            checkBoxReady = new CheckBoxValue[8];
            comboPlayerModel = new ArrayList<SimpleChangableListModel<String>>();
            comboSpawnModel = new ArrayList<SimpleChangableListModel<String>>();
            comboColorModel = new ArrayList<SimpleChangableListModel<String>>();

            for (int i = 0; i < 8; i++) {
                comboPlayerModel.add(new SimpleChangableListModel<String>("--", "AI"));
                comboSpawnModel.add(new SimpleChangableListModel<String>("1", "2", "3", "4", "5", "6", "7", "8"));
                comboColorModel.add(new SimpleChangableListModel<String>("Yellow", "Red", "Green", "Blue", "Purple", "Pink", "Orange", "Cyan"));

                comboSpawns[i] = new ComboBoxValue(0, comboSpawnModel.get(i));
                comboPlayer[i] = new ComboBoxValue(0, comboPlayerModel.get(i));
                comboTeam[i] = new ComboBoxValue(0, new SimpleChangableListModel<String>("1", "2", "3", "4", "5", "6", "7", "8"));
                comboColor[i] = new ComboBoxValue(0, comboColorModel.get(i));
                checkBoxReady[i] = new CheckBoxValue();
            }
        }

        public void clear() {
            for (int i = 0; i < 8; i++) {
                resetRow(i);
            }
        }

        public void initPlayersWithTable(GameRound round) {
            // Get remaining colors
            ArrayList<String> remainingColors = new ArrayList<String>();
            for (int i = 0; i < comboColorModel.get(playerPosition).getNumEntries(); i++) {
                remainingColors.add(comboColorModel.get(playerPosition).getEntry(i));
            }

            // Get remaining spawns
            ArrayList<String> remainingSpawns = new ArrayList<String>();
            for (int i = 0; i < comboSpawnModel.get(playerPosition).getNumEntries(); i++) {
                remainingSpawns.add(comboSpawnModel.get(playerPosition).getEntry(i));
            }

            // Now players

            for (int i = 0; i < 8; i++) {
                String pseudo = comboPlayerModel.get(i).getEntry(comboPlayer[i].getValue());
                if (!(pseudo.equals("--") || pseudo.equals("AI"))) {
                    String money = comboMoneyModel.getEntry(comboMoney.getSelected());
                    int teamId = comboTeam[i].getValue();

                    Player player = new Player();
                    player.setPseudo(pseudo);
                    player.setMoney(Integer.parseInt(money.substring(0, money.length() - 1)));
                    player.setId(i);
                    player.setTeamId(teamId);
                    player.setPlayer((i == playerPosition));
                    player.setTecLevel(comboTecLevel.getSelected());

                    // Colors and Spawns
                    player.setColor(Colors.getColorId(comboColorModel.get(i).getEntry(comboColor[i].getValue())));
                    player.setSpawn(Integer.parseInt(comboSpawnModel.get(i).getEntry(comboSpawns[i].getValue())) - 1);

                    round.addPlayer(player);
                }
            }
        }

        public boolean isReady() {
            return true;
        }

        public String getSelectedColor() {
            return comboColorModel.get(playerPosition).getEntry(comboColor[playerPosition].getValue());
        }

        public String getSelectedSpawn() {
            return "1";
        }

        public int getSelectedTeam() {
            return 1;
        }

        public void updateRow(int row, String[] spawns, String[] colors, ClientState clientState) {
            callBackSecure = false;

            // Set name
            comboPlayerModel.get(row).clear();
            comboPlayerModel.get(row).addElement(clientState.name);
            comboPlayer[row].setValue(0);
            comboPlayer[row].setEnable(false);

            boolean playerRow = clientState.connectionId == netManager.getClientState().connectionId;
            int nbSpawn = mapListModel.getEntry(mapList.getSelected()).getNumberOfSpawns() + 1;

            if (playerRow) {

                if (playerPosition == -1) {
                    playerPosition = clientState.position;
                }

                comboTeam[row].setEnable(true);
                comboSpawns[row].setEnable(true);
                comboColor[row].setEnable(true);
                checkBoxReady[row].setEnable(true);

                comboTeam[row].addCallBack();
                comboSpawns[row].addCallBack();
                comboColor[row].addCallBack();
                checkBoxReady[row].addCallBack();

                // Update model
                // Update spawns model
                comboSpawnModel.get(row).clear();
                for (int i = 1; i < nbSpawn; i++) {
                    boolean contain = false;
                    for (int j = 0; j < spawns.length; j++) {
                        if (spawns[j].equals(i + "")) {
                            contain = true;
                            break;
                        }
                    }
                    if (!contain || (i + "").equals(clientState.spawn)) {
                        comboSpawnModel.get(row).addElement(i + "");
                    }
                }

                // Update colors model
                comboColorModel.get(row).clear();
                for (int i = 0; i < Colors.COLORS.length; i++) {
                    boolean contain = false;
                    for (int j = 0; j < colors.length; j++) {
                        if (colors[j].equals(Colors.COLORS[i])) {
                            contain = true;
                            break;
                        }
                    }
                    if (!contain || Colors.COLORS[i].equals(clientState.color)) {
                        comboColorModel.get(row).addElement(Colors.COLORS[i]);
                    }
                }

            } else {
                comboTeam[row].setEnable(false);
                comboSpawns[row].setEnable(false);
                comboColor[row].setEnable(false);
                checkBoxReady[row].setEnable(false);

                comboTeam[row].removeCallBack();
                comboSpawns[row].removeCallBack();
                comboColor[row].removeCallBack();
                checkBoxReady[row].removeCallBack();

                comboTeam[row].setValue(clientState.team);
                checkBoxReady[row].setValue((clientState.isReady) ? 1 : 0);
            }

            comboSpawns[row].setValue(0);

            boolean find = false;
            for (int i = 0; i < comboSpawnModel.get(row).getNumEntries(); i++) {
                if (comboSpawnModel.get(row).getEntry(i).equals(clientState.spawn)) {
                    find = true;
                    comboSpawns[row].setValue(i);
                    break;
                }
            }

            if (!find)// To refresh players when spawn change because of map
                sendClientInfo();

            comboColor[row].setValue(0);

            for (int i = 0; i < comboColorModel.get(row).getNumEntries(); i++) {
                if (comboColorModel.get(row).getEntry(i).equals(clientState.color)) {
                    comboColor[row].setValue(i);
                    break;
                }
            }

            callBackSecure = true;
        }

        public void resetRow(int row) {
            callBackSecure = false;

            // Set player combo

            comboPlayerModel.get(row).clear();
            comboPlayerModel.get(row).addElement("--");
            comboPlayerModel.get(row).addElement("AI");
            comboPlayer[row].setValue(0);

            comboTeam[row].setValue(0);

            comboSpawnModel.get(row).clear();
            for (int i = 0; i < 8; i++) {
                comboSpawnModel.get(row).addElement("" + (i + 1));
            }
            comboSpawns[row].setValue(0);

            comboColorModel.get(row).clear();
            comboColorModel.get(row).addElement("Yellow");
            comboColorModel.get(row).addElement("Red");
            comboColorModel.get(row).addElement("Green");
            comboColorModel.get(row).addElement("Blue");
            comboColorModel.get(row).addElement("Purple");
            comboColorModel.get(row).addElement("Pink");
            comboColorModel.get(row).addElement("Orange");
            comboColorModel.get(row).addElement("Cyan");
            comboColor[row].setValue(0);

            checkBoxReady[row].setValue(0);

            comboTeam[row].removeCallBack();
            comboSpawns[row].removeCallBack();
            comboColor[row].removeCallBack();
            checkBoxReady[row].removeCallBack();

            comboPlayer[row].setEnable(false);
            comboTeam[row].setEnable(false);
            comboSpawns[row].setEnable(false);
            comboColor[row].setEnable(false);
            checkBoxReady[row].setEnable(false);
            callBackSecure = true;
        }

        public void registerTableCellRender(Table table) {
            table.registerCellRenderer(ComboBoxValue.class, new ComboBoxCellWidgetCreator());
            table.registerCellRenderer(CheckBoxValue.class, new CheckBoxCellWidgetCreator());
        }

        @Override
        public int getNumRows() {
            return 1;
        }

        @Override
        public int getNumColumns() {
            return 1;
        }

        @Override
        public String getColumnHeaderText(int column) {
            switch (column) {
                case 0:
                    return "Color";
                case 1:
                    return "Team";
                case 2:
                    return "Spawns";
                case 3:
                    return "Player";
                case 4:
                    return "Ready";
                default:
                    return "";
            }
        }

        @Override
        public Object getCell(int row, int column) {
            switch (column) {
                case 0:
                    return comboColor[row];

                case 1:
                    return comboTeam[row];
                case 2:
                    return comboSpawns[row];
                case 3:
                    return comboPlayer[row];
                case 4:
                    return checkBoxReady[row];
                default:
                    return "";
            }
        }

        public class ComboBoxValue extends SimpleIntegerModel {
            private Runnable runnable;
            private MyComboBox combo;
            private final ListModel<String> model;

            public ComboBoxValue(int value, ListModel<String> model) {
                super(0, model.getNumEntries() - 1, value);
                this.model = model;

            }

            public ListModel<String> getModel() {
                return model;
            }

            public void addCallBack() {
                if (runnable == null && combo != null) {
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (callBackSecure)
                                sendClientInfo();
                        }
                    };
                    combo.addCallback(runnable);
                }
            }

            public void removeCallBack() {
                if (runnable != null && combo != null) {
                    combo.removeCallback(runnable);
                    runnable = null;
                }
            }

            @Override
            public void setValue(int value) {
                super.setValue(value);
                if (combo != null) {
                    combo.setSelectedFromData(value);
                }
            }

            public void setEnable(boolean enable) {
                if (combo != null)
                    combo.setEnableFromData(enable);
            }

            public void setComboBox(MyComboBox cb) {
                combo = cb;
            }
        }

        private class ComboBoxCellWidgetCreator implements CellWidgetCreator {
            private int comboBoxHeight;
            private ComboBoxValue data;

            public void applyTheme(ThemeInfo themeInfo) {
                comboBoxHeight = themeInfo.getParameter("comboBoxHeight", 0);
            }

            public String getTheme() {
                return "ComboBoxCellRenderer";
            }

            public Widget updateWidget(Widget existingWidget) {
                MyComboBox cb = (MyComboBox) existingWidget;
                if (cb == null) {
                    cb = new MyComboBox();
                }
                // in this example there should be no update to cells
                // but the code pattern here can also be used when updates are
                // generated. Care should be taken that the above type cast
                // does not fail.
                data.setComboBox(cb);
                cb.setData(data);
                data.setValue(0);
                cb.setSelectedFromData(0);
                return cb;
            }

            public void positionWidget(Widget widget, int x, int y, int w, int h) {
                // this method will size and position the ComboBox
                // If the widget should be centered (like a check box) then this
                // would be done here
                widget.setPosition(x, y);
                widget.setSize(w, h);
            }

            public void setCellData(int row, int column, Object data) {
                // we have to remember the cell data for the next call of
                // updateWidget
                this.data = (ComboBoxValue) data;
            }

            public Widget getCellRenderWidget(int x, int y, int width, int height, boolean isSelected) {
                // this cell does not render anything itself
                return null;
            }

            public int getColumnSpan() {
                // no column spanning
                return 1;
            }

            public int getPreferredHeight() {
                // we have to inform the table about the required cell height
                // before
                // we can create the widget - so we need to get the required
                // height
                // from the theme - see applyTheme/getTheme
                return comboBoxHeight;
            }

        }

        private class MyComboBox extends ComboBox<String> implements Runnable {
            ComboBoxValue data;

            public MyComboBox() {
                setTheme("combobox"); // keep default theme name
                addCallback(this);
            }

            public void setSelectedFromData(int value) {
                setSelected(value);
            }

            public void setEnableFromData(boolean enable) {
                setEnabled(enable);
            }

            void setData(ComboBoxValue data) {
                this.data = data;
                setModel(data.getModel());
            }

            public void run() {
                if (data != null) {
                    if (getSelected() > -1)
                        data.setValue(getSelected());
                }
            }
        }

        // CheckBox

        private class CheckBoxValue extends SimpleIntegerModel {
            private ToggleButton box;
            private Runnable runnable;

            public CheckBoxValue() {
                super(0, 1, 0);
            }

            public void setCheckBox(ToggleButton cb) {
                box = cb;
            }

            @Override
            public void setValue(int value) {
                super.setValue(value);
                if (box != null) {
                    box.setActive(value == 1);
                }
            }

            public void addCallBack() {
                if (runnable == null && box != null) {
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (callBackSecure)
                                sendClientInfo();
                        }
                    };
                    box.addCallback(runnable);
                }
            }

            public void removeCallBack() {
                if (runnable != null && box != null) {
                    box.removeCallback(runnable);
                    runnable = null;
                }
            }

            public boolean isCheck() {
                if (box != null)
                    return box.isActive();
                else
                    return false;
            }

            public void setEnable(boolean enable) {
                if (box != null) {
                    box.setEnabled(enable);
                }
            }

        }

        private class CheckBoxCellWidgetCreator implements CellWidgetCreator {
            private CheckBoxValue data;

            public void applyTheme(ThemeInfo themeInfo) {

            }

            public String getTheme() {
                return "checkbox";
            }

            public Widget updateWidget(Widget existingWidget) {
                ToggleButton cb = (ToggleButton) existingWidget;
                if (cb == null) {
                    cb = new ToggleButton();
                    cb.setTheme("checkbox");
                }
                // in this example there should be no update to cells
                // but the code pattern here can also be used when updates are
                // generated. Care should be taken that the above type cast
                // does not fail.
                data.setCheckBox(cb);
                return cb;
            }

            public void positionWidget(Widget widget, int x, int y, int w, int h) {
                widget.setPosition(x, y);
                widget.setSize(w, h);
            }

            public void setCellData(int row, int column, Object data) {
                this.data = (CheckBoxValue) data;
            }

            public Widget getCellRenderWidget(int x, int y, int width, int height, boolean isSelected) {
                // this cell does not render anything itself
                return null;
            }

            public int getColumnSpan() {
                return 1;
            }

            public int getPreferredHeight() {
                return 10;
            }

        }

    }

}



