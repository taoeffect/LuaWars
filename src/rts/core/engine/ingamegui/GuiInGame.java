package rts.core.engine.ingamegui;

import java.util.ArrayList;

import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luawars.Log;
import org.luawars.LuaJScripting.CallLua;
import org.luawars.LuaJScripting.LuaJGlobal;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;

import rts.Launch;
import rts.core.engine.Engine;
import rts.core.engine.Player;
import rts.core.network.menu_tcp_containers.MessageState;
import rts.utils.Colors;
import rts.utils.ResourceManager;
import rts.utils.Timer;

public class GuiInGame {

    private static final int TCHAT_MESSAGE_TIME = 10000;

    private Engine engine;
    private Image guiBackground;
    private Image patch;
    private GuiMenu menuGui;
    private String message;
    private Timer timer;
    private ArrayList<Message> messagesArray;
    private boolean speakMod;
    private boolean onGui;
    private boolean visible;
    private int width;
    private int counter;

    public GuiInGame(Engine engine) {
        this.menuGui = new GuiMenu(engine);
        this.engine = engine;
        this.visible = true;
        this.width = 200;
        this.timer = new Timer(TCHAT_MESSAGE_TIME);
        this.messagesArray = new ArrayList<Message>(5);
    }

    public void init() {
        this.guiBackground = ResourceManager.getImage("ihm_ingame");
        this.patch = ResourceManager.getImage("ihmpatch");
        this.menuGui.init();
        // TRUNG NGUYEN
        //CallLua.runScript("myScript.lua", null);
        //LuaJGlobal.initializeLuaJGlobal();
    }

    // NOTE: THERE'S A WEIRD BUG WHERE IF YOU TYPE IN AN UPPERCASE LETTER IT WILL PUT A SPACE BEFORE THE UPPERCASE LETTER
    // OR AT LEAST WHEN I TRY TO PRESS SHIFT + LETTER (to make it uppercase)
    public void keyPressed(int key, char c) {
        if (speakMod) {
            switch (key) {
                case Input.KEY_ENTER:
                    if (!message.isEmpty()) {
                        addMessage(engine.getNetworkManager().sendMessage(message));
                    }
                    speakMod = false;
                    if(message.startsWith("call ")) {
                        String fileName = message.substring(5, message.length());
                        Log.trace("attempting to call script: " + fileName + ".lua");
                        CallLua.runScript("resources/Lua Scripts/" + fileName);
                    }
                    message = "";
                    break;
                case Input.KEY_ESCAPE:
                    speakMod = false;
                    message = "";
                    break;
                case Input.KEY_BACK:
                    if (!message.isEmpty())
                        message = message.substring(0, message.length() - 1);
                    break;
                default:
                    message += c;
                    break;
            }
        } else {
            switch (key) {
                case Input.KEY_Y:
                    if (engine.isNetwork())
                        speakMod = true;
                    break;
            }
        }
    }

    public void mousePressed(int button, int x, int y) {
        menuGui.mousePressed(button, x, y);
    }

    public void render(GameContainer container, Graphics g) {
        if (visible) {
            // TRUNG NGUYEN
            // draw tile locations so player can see the tiles (so they can do whatever functions they want with them)
            for(int x = 0; x < Launch.g.getEngine().getMap().getWidthInPixel(); x += 200) {
                for(int y = 0; y < Launch.g.getEngine().getMap().getHeightInPixel(); y += 200) {
                    g.setColor(Color.yellow);
                    g.drawString(((x - Launch.g.getEngine().getXScrollDecal()) / Launch.g.getEngine().getTileW()) + ", " + ((y - Launch.g.getEngine().getYScrollDecal()) / Launch.g.getEngine().getTileH()), x, y);
                }
            }
            // player can put stuff they want to update every frame in here
            CallLua.runScript("resources/Lua Scripts/update.lua");
            //
            g.drawImage(guiBackground, container.getWidth() - width, 0);
            if (container.getHeight() > guiBackground.getHeight()) {
                g.drawImage(patch, container.getWidth() - width, guiBackground.getHeight());
            }
            // Render minimap
            if (menuGui.containRadarOrDevCenter()) {
                engine.getMap().renderMiniMap(g, container.getWidth() - width + 25, 25, 150, 150, false);
            } else {
                g.setColor(Color.black);
                g.drawString("Unavailable", container.getWidth() - width + 50, 80);
            }

            // Render player infos
            Player p = engine.getPlayer();
            g.setColor(Colors.getColor(p.getColor()));
            g.drawString("[Lv " + (p.getTecLevel() + 1) + "][" + p.getPseudo() + "]", container.getWidth() - width + 20, 3);
            if (p.getMoney() >= p.getMaxMoney()) {
                g.setColor(Color.red);
            } else
                g.setColor(Colors.GOLD);
            if (p.getMoney() > 0) {
                g.drawString("" + p.getMoney() + "$", container.getWidth() - width + 80, 178);
            } else
                g.drawString("0$", container.getWidth() - width + 80, 178);

            // Render menu
            menuGui.render(container, g);
        }

        drawTchat(container, g);
    }

    public void resize() {
        menuGui.resizeMenu();
    }

    private void drawTchat(GameContainer container, Graphics g) {
        // Render messages infos
        g.resetTransform();
        counter = 20;
        synchronized (messagesArray) {
            for (int i = messagesArray.size() - 1; i >= 0; i--) {
                Color c = messagesArray.get(i).getColor();
                g.setColor(c);
                if (i == 0) {
                    c.a = 1 - timer.getPercentage();
                    g.drawString(messagesArray.get(i).getMessage(), 10, container.getHeight() - counter);
                } else {
                    g.drawString(messagesArray.get(i).getMessage(), 10, container.getHeight() - counter);
                }
                counter += 15;
            }

            if (timer.isTimeComplete()) {
                if (!messagesArray.isEmpty())
                    messagesArray.remove(0);
                timer.resetTime();
            }
        }

        if (speakMod) {
            g.setColor(Colors.getColor(engine.getPlayer().getColor()));
            g.drawString("Say: " + message, 10, container.getHeight() - 100);
        }
    }

    public void addEntityToBuildingList(int type) {
        menuGui.addEntityToBuildingList(type);
    }

    public void removeEntityFromBuildingList(int type) {
        menuGui.removeEntityFromBuildingList(type);
    }

    public void updateMouseEvent(GameContainer container, int delta) {
        if (visible && menuGui.containRadarOrDevCenter()) {
            int mx = container.getInput().getMouseX();
            int my = container.getInput().getMouseY();

            // Click on minimap ?
            if ((mx > container.getWidth() - width + 25 && mx < container.getWidth() - 25) && (my > 25 && my < 175)) {
                int px = ((mx - (container.getWidth() - width + 25)) * engine.getMap().getWidthInTiles()) / 150;
                int py = ((my - 25) * engine.getMap().getHeightInTiles()) / 150;

                if (engine.isMouseLeftPressed()) {
                    // Put mouse in center
                    px -= (container.getWidth() - width) / (engine.getTileW() * 2);
                    py -= (container.getHeight()) / (engine.getTileH() * 2);

                    if (px < 0)
                        px = 0;
                    if (py < 0)
                        py = 0;

                    if ((px * engine.getTileW()) + container.getWidth() - width > engine.getMap().getWidthInPixel()) {
                        px = engine.getMap().getWidthInTiles() - ((container.getWidth() - width) / engine.getTileW());
                    }

                    if ((py * engine.getTileH()) + container.getHeight() > engine.getMap().getHeightInPixel()) {
                        py = engine.getMap().getHeightInTiles() - (container.getHeight() / engine.getTileH());
                    }
                    engine.changeScrollView(-(px * 20), -(py * 20));
                } else {
                    if (engine.isMouseRightPressed()) {
                        engine.moveEntitiesTo(px * engine.getTileW(), py * engine.getTileH());
                    }
                }
            }
        }

        menuGui.update(container, delta);
        timer.update(delta);
    }

    public void increaseBuildLimit(int panelId, int increase) {
        menuGui.increaseBuildLimit(panelId, increase);
    }

    public void decreaseBuildLimit(int panelId, int decrease) {
        menuGui.decreaseBuildLimit(panelId, decrease);
    }

    public int hashCode() {
        return menuGui.hashCode();
    }

    public void clear() {
        menuGui.clear();
        message = "";
        speakMod = false;
        onGui = false;
        visible = true;
        timer.resetTime();
        messagesArray.clear();
    }

    public void addMessage(MessageState messageState) {
        synchronized (messagesArray) {
            if (messagesArray.size() == 5) {
                messagesArray.remove(0);
            }
            messagesArray.add(new Message(messageState.getCmpMessage(), messageState.color));
            timer.resetTime();
        }
    }

    public void hideOrShow() {
        visible = !visible;
    }

    public boolean isMouseOnGui(GameContainer container, int mx, int my) {
        onGui = (visible && mx > container.getWidth() - width && my < container.getHeight());//
        return onGui;
    }

    public boolean isOnGui() {
        return onGui;
    }

    public int getWidth() {
        if (visible)
            return width;
        else
            return 0;
    }

    // TRUNG NGUYEN
    public GuiMenu getMenuGui() {
        return menuGui;
    }

    public boolean isRepairMod() {
        return menuGui.isRepairMod();
    }

    public boolean isSellMod() {
        return menuGui.isSellMod();
    }

    private class Message {

        private String message;
        private Color color;

        public Message(String message, String colorId) {
            this.message = message;
            this.color = Colors.getNewColorInstance(colorId);
        }

        public String getMessage() {
            return message;
        }

        public Color getColor() {
            return color;
        }
    }

}
