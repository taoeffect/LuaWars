package rts.core.engine.ingamegui;

import java.awt.Point;
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import rts.core.engine.Engine;
import rts.core.engine.GameSound;
import rts.core.engine.Player;
import rts.core.engine.Utils;
import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.EData;
import rts.core.engine.layers.entities.EntityGenerator;
import rts.core.engine.layers.entities.MoveableEntity;
import rts.core.engine.layers.entities.buildings.Building;
import rts.core.engine.layers.entities.buildings.BuildingECreator;
import rts.core.engine.layers.entities.others.Wall;
import rts.utils.Colors;
import rts.utils.Timer;

public class GuiButton {

    private static final Color TRANS_CYAN = new Color(51, 51, 102, 200);
    private static final Color TRANS = new Color(255, 255, 255, 100);
    private static final int BLINK_TIME = 300;

    private static final int TIME_BEFORE_MOUSEOVER = 1000;

    private GuiPanel panel;
    private Engine engine;
    private Building building;
    private ArrayList<int[]> enableCombination;
    private ArrayList<CreateEntityProcess> processList;
    private Timer blinkTimer;
    private Timer mouseOverTimer;
    private String name;
    private String price;
    private Image image;
    private int x;
    private int y;
    private int width;
    private int height;
    private boolean enable;
    private boolean visible;    // not sure what visible is
    private boolean tabButton;
    private boolean limitAtOne;
    private boolean blink;
    private boolean alwaysEnable;
    private int entType;
    private int diX;
    private int diY;

    public GuiButton(Engine engine, Image image, int x, int y) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.engine = engine;
        this.processList = new ArrayList<CreateEntityProcess>();
        this.enableCombination = new ArrayList<int[]>();
        this.entType = -1;
        this.blinkTimer = new Timer(BLINK_TIME);
        this.mouseOverTimer = new Timer(TIME_BEFORE_MOUSEOVER);
    }

    public void clear() {
        processList.clear();
        blinkTimer.resetTime();
        mouseOverTimer.resetTime();
        if (!alwaysEnable)
            enable = false;
        visible = false;
        blink = false;
    }

    public void addEnableCombination(int[] combination) {
        enableCombination.add(combination);
    }

    public void launchCreateEntityProcess() {
        if (!processList.isEmpty()) {
            if (processList.get(0).ready) {
                building = (Building) EntityGenerator.createActiveEntityNoNetwork(engine, entType, 0, 0);
            } else {
                if (processList.get(0).pause) {
                    processList.get(0).pause = false;
                } else {
                    panel.addButtonToWait(this);
                    if (!limitAtOne)
                        processList.add(new CreateEntityProcess());
                }
            }
        } else if(enable) {
            GameSound.construction();
            panel.addButtonToWait(this);
            processList.add(new CreateEntityProcess());
        }
    }

    public boolean isMouseOver(int mx, int my) {
        if (tabButton || (!tabButton && visible))
            return ((mx > x && mx < x + width) && (my > y && my < y + height));
        else
            return false;
    }

    private boolean isMouseOver() {
        return isMouseOver(engine.getContainer().getInput().getMouseX(), engine.getContainer().getInput().getMouseY());
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void checkCancelProcess() {
        if (engine.isMouseRightPressed() && isMouseOver()) {
            if (enable) {
                if (!processList.isEmpty()) {
                    if (processList.get(0).pause || processList.get(0).ready) {
                        if (processList.size() > 1) {
                            processList.get(1).advancement = processList.get(0).advancement;
                            if (processList.get(0).pause)
                                processList.get(1).pause = true;
                        } else {
                            engine.getPlayer().addMoney(processList.get(0).advancement);
                        }
                        processList.remove(0);
                        building = null;
                    } else {
                        if (!processList.get(0).pause) {
                            processList.get(0).pause = true;
                        } else {
                            processList.remove(0);
                            building = null;
                        }
                    }
                }
            }
        }
    }

    public void blink(int delta) {
        blinkTimer.update(delta);
        if (blinkTimer.isTimeComplete()) {
            blink = !blink;
            blinkTimer.resetTime();
        }
    }

    public void resetBlink() {
        if (blink) {
            blinkTimer.resetTime();
            blink = false;
        }
    }

    public void render(GameContainer container, Graphics g) {
        if (enable) {
            if (blink) {
                g.drawImage(image, x, y, TRANS);
            } else {
                g.drawImage(image, x, y);
            }
        } else {
            g.drawImage(image, x, y, TRANS);
        }

        if (!processList.isEmpty()) {
            g.setColor(Color.red);
            CreateEntityProcess process = processList.get(0);
            g.fillRect(x + 2, y + 51, (56 * process.advancement) / process.price, 7);
            if (process.pause) {
                g.drawString("Pause", x + 10, y + 15);
            } else {
                if (process.ready) {
                    g.drawString("Ready", x + 10, y + 15);
                }
            }
            g.drawString("" + processList.size(), x + 3, y + 1);
        }

        if (building != null) {
            building.renderLocationOnMap(container, g);
        }
    }

    public void renderInfo(GameContainer container, Graphics g) {
        if (isMouseOver() && mouseOverTimer.isTimeComplete()) {
            g.setColor(TRANS_CYAN);
            if (diX == -1 && diY == -1) {
                diX = container.getInput().getMouseX();
                diY = container.getInput().getMouseY();
            }
            if (tabButton) {
                if (diX + name.length() > container.getWidth()) {
                    diX -= name.length();
                }
                g.fillRect(diX, diY, name.length() * 10, 20);
                g.setColor(Color.black);
                g.drawRect(diX, diY, name.length() * 10, 20);
                g.setColor(Color.white);
                g.drawString(name, diX + 2, diY);
            } else {
                int w = (name.length() > price.length() ? name.length() : price.length()) * 10;
                if (diX + w > container.getWidth()) {
                    diX -= w;
                }
                g.fillRect(diX, diY, w, 60);
                g.setColor(Color.black);
                g.drawRect(diX, diY, w, 60);
                g.setColor(Color.white);
                g.drawString(name, diX + 2, diY);
                g.setColor(Colors.GOLD);
                g.drawString(price, diX + 2, diY + 20);
                g.setColor((engine.getPlayer().getTecLevel() >= EData.TEC_LEVEL[entType] ? Color.cyan : Color.red));
                g.drawString("Level: " + (EData.TEC_LEVEL[entType] + 1), diX + 2, diY + 40);
            }
        }
    }

    public boolean hasProcessReady() {
        if (!processList.isEmpty()) {
            return processList.get(0).ready;
        }
        return false;
    }

    public boolean update(int delta) {
        if (visible && engine.isMouseLeftPressed()) {
            int x = engine.getMouseX() / engine.getTileW();
            int y = engine.getMouseY() / engine.getTileH();
            System.out.println("buildIng at coor" + x + ", " + y);
            if (building != null && building.isValidLocation(x, y)) {
                Player player = engine.getPlayer();
                if (engine.isNetwork()) {
                    if (building instanceof Wall) {
                        ArrayList<Point> a = ((Wall) building).getOthersValidLocation();
                        for (int i = 0; i < a.size(); i++) {
                            engine.getNetworkManager().sendCreateEntity(entType, player.getId(), player.getTeamId(), a.get(i).x * engine.getTileW(),
                                    a.get(i).y * engine.getTileH());
                        }
                    }
                    engine.getNetworkManager().sendCreateEntity(entType, player.getId(), player.getTeamId(), x * engine.getTileW(), y * engine.getTileH());
                } else {
                    ActiveEntity ae = EntityGenerator.createActiveEntityNoNetwork(engine, entType, player.getId(), player.getTeamId());
                    ae.setLocation(x * engine.getTileW(), y * engine.getTileH());
                    if (ae instanceof BuildingECreator) {
                        ((BuildingECreator) ae).checkPrimary();
                    } else {
                        if (ae instanceof Wall) {
                            ArrayList<Point> a = ((Wall) ae).getOthersValidLocation();
                            for (int i = 0; i < a.size(); i++) {
                                ActiveEntity w = EntityGenerator.createActiveEntityNoNetwork(engine, entType, player.getId(), player.getTeamId());
                                w.setLocation(a.get(i).x * engine.getTileW(), a.get(i).y * engine.getTileH());
                                engine.addEntity(w);
                            }
                        }
                    }
                    engine.addEntity(ae);

                    // Special case type = refinery = + 1 collector
                    if (entType == EData.BUILDING_REFINERY) {
                        Point p2 = Utils.getCloserPoint(engine.getMap(), x, y);
                        if (p2 != null) {
                            ae = EntityGenerator.createActiveEntityNoNetwork(engine, EData.MOVER_COLLECTOR, player.getId(), player.getTeamId());
                            ae.setLocation(p2.x * engine.getTileW(), p2.y * engine.getTileH());
                            engine.addEntity(ae);
                        }
                    }
                }
                engine.getGui().addEntityToBuildingList(entType);
                processList.remove(0);
                building = null;
            }
        } else {
            if (engine.isMouseRightPressed()) {
                building = null;
            }
        }
        return checkProcess(delta);
    }

    // Modeled after update(delta) function above
    public boolean placeBuilding(int x, int y) {
        // first check to see if we can build a building
        if (processList.size() > 0 && processList.get(0).ready) {
            building = (Building) EntityGenerator.createActiveEntityNoNetwork(engine, entType, 0, 0);
        }
        // visible checked to see if you were on the correct panel,
        // but i don't really care about what panel i'm on anymore,
        // i just need to build the building.
        // this is why i didn't include boolean visible in this check
        if (building != null && building.checkValidPlacement(x, y)) {
            Player player = engine.getPlayer();
            if (engine.isNetwork()) {
                if (building instanceof Wall) {
                    ArrayList<Point> a = ((Wall) building).getOthersValidLocation();
                    for (int i = 0; i < a.size(); i++) {
                        engine.getNetworkManager().sendCreateEntity(entType, player.getId(), player.getTeamId(), a.get(i).x * engine.getTileW(),
                                a.get(i).y * engine.getTileH());
                    }
                }
                engine.getNetworkManager().sendCreateEntity(entType, player.getId(), player.getTeamId(), x * engine.getTileW(), y * engine.getTileH());
            } else {
                ActiveEntity ae = EntityGenerator.createActiveEntityNoNetwork(engine, entType, player.getId(), player.getTeamId());
                ae.setLocation(x * engine.getTileW(), y * engine.getTileH());
                if (ae instanceof BuildingECreator) {
                    ((BuildingECreator) ae).checkPrimary();
                } else {
                    if (ae instanceof Wall) {
                        ArrayList<Point> a = ((Wall) ae).getOthersValidLocation();
                        for (int i = 0; i < a.size(); i++) {
                            ActiveEntity w = EntityGenerator.createActiveEntityNoNetwork(engine, entType, player.getId(), player.getTeamId());
                            w.setLocation(a.get(i).x * engine.getTileW(), a.get(i).y * engine.getTileH());
                            engine.addEntity(w);
                        }
                    }
                }
                engine.addEntity(ae);

                // Special case type = refinery = + 1 collector
                if (entType == EData.BUILDING_REFINERY) {
                    Point p2 = Utils.getCloserPoint(engine.getMap(), x, y);
                    if (p2 != null) {
                        ae = EntityGenerator.createActiveEntityNoNetwork(engine, EData.MOVER_COLLECTOR, player.getId(), player.getTeamId());
                        ae.setLocation(p2.x * engine.getTileW(), p2.y * engine.getTileH());
                        engine.addEntity(ae);
                    }
                }
            }
            engine.getGui().addEntityToBuildingList(entType);
            processList.remove(0);
            building = null;
            return true;
        }
        // if we tried to place a building but failed, then deselect the building we are trying to place
        building = null;
        return false;
    }

    private boolean checkProcess(int delta) {
        if (!processList.isEmpty()) {
            processList.get(0).update(delta);
            if (processList.get(0).complete()) {
                // CREATE ENTITY
                if (EData.isBuilding(entType)) {
                    // Building
                    if (!processList.get(0).ready) {
                        processList.get(0).ready = true;
                        GameSound.buildingReady();
                    }
                } else {
                    // Mover
                    ArrayList<Building> buildings = engine.getPlayerBuilding();
                    int[] possibilities = EData.BUILDING_PLACE[entType];
                    boolean find = false;
                    for (int i = 0; i < possibilities.length; i++) {
                        for (int j = 0; j < buildings.size(); j++) {
                            if (buildings.get(j).getType() == possibilities[i] && ((BuildingECreator) buildings.get(j)).isPrimary()) {
                                Building b = buildings.get(j);
                                Point p = Utils.getCloserPoint(engine.getMap(), (int) b.getX() / engine.getTileW(), (int) b.getY() / engine.getTileH());
                                Point rp = null;
                                if (b instanceof BuildingECreator) {
                                    rp = ((BuildingECreator) b).getRallyingPoint();
                                }
                                Player player = engine.getPlayer();
                                if (engine.isNetwork()) {
                                    if (rp != null) {
                                        engine.getNetworkManager().sendCreateEntity(entType, player.getId(), player.getTeamId(), rp.x, rp.y,
                                                p.x * engine.getTileW(), p.y * engine.getTileH());
                                    } else {
                                        engine.getNetworkManager().sendCreateEntity(entType, player.getId(), player.getTeamId(), p.x * engine.getTileW(),
                                                p.y * engine.getTileH());
                                    }
                                } else {
                                    ActiveEntity ae = EntityGenerator.createActiveEntityNoNetwork(engine, entType, player.getId(), player.getTeamId());
                                    ae.setLocation(p.x * engine.getTileW(), p.y * engine.getTileH());
                                    engine.addEntity(ae);
                                    if (ae instanceof MoveableEntity && rp != null) {
                                        Point pt = Utils.getCloserPoint(engine.getMap(), rp.x / engine.getTileW(), rp.y / engine.getTileH());
                                        ((MoveableEntity) ae).move(pt.x * engine.getTileW(), pt.y * engine.getTileH());
                                    }
                                }
                                GameSound.unitReady();
                                find = true;
                                break;
                            }
                        }
                        if (find)
                            break;
                    }
                    processList.remove(0);
                }
            }
            return false;
        } else
            return true;
    }

    public void checkEnable(ArrayList<Integer> buildingList, int delta, boolean visible) {
        this.visible = visible;
        if (!alwaysEnable) {
            boolean hasBuildingNeeded = true;
            for (int i = 0; i < enableCombination.size(); i++) {
                int[] comb = enableCombination.get(i);
                hasBuildingNeeded = true;
                for (int j = 0; j < comb.length; j++) {
                    if (!buildingList.contains(new Integer(comb[j]))) {
                        hasBuildingNeeded = false;
                        break;
                    }
                }
                if (hasBuildingNeeded)
                    break;
            }

            if (enable) {
                if (limitAtOne) {
                    if (!hasBuildingNeeded || buildingList.contains(new Integer(entType))) {
                        setEnable(false);
                        processList.clear();
                    }
                } else {
                    if (!hasBuildingNeeded) {
                        setEnable(false);
                        processList.clear();
                    }
                }
            } else {
                // No enable
                if (limitAtOne) {
                    if (hasBuildingNeeded && !buildingList.contains(new Integer(entType)) && engine.getPlayer().getTecLevel() >= EData.TEC_LEVEL[entType]) {
                        setEnable(true);
                    }
                } else {
                    if (hasBuildingNeeded) {
                        if (tabButton) {
                            setEnable(true);
                        } else {
                            if (entType != -1 && engine.getPlayer().getTecLevel() >= EData.TEC_LEVEL[entType]) {
                                setEnable(true);
                            }
                        }
                    }
                }
            }
        }

        // Show info timer
        if (isMouseOver()) {
            mouseOverTimer.update(delta);
        } else {
            diX = -1;
            diY = -1;
            mouseOverTimer.resetTime();
        }
    }

    public ArrayList<CreateEntityProcess> getProcessList() {
        return processList;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setEntType(int entType) {
        this.entType = entType;
        this.name = EData.NAMES[entType];
        this.price = "Price: " + EData.PRICE[entType] + "$";
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnable() {
        return enable;
    }

    private void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setPanel(GuiPanel panel) {
        this.panel = panel;
    }

    public void setTabButton(boolean tabButton) {
        this.tabButton = tabButton;
    }

    public void setLimitAtOne(boolean limitAtOne) {
        this.limitAtOne = limitAtOne;
    }

    public void setAlwaysEnable(boolean alwaysEnable) {
        this.alwaysEnable = alwaysEnable;
        this.enable = alwaysEnable;
    }

    private class CreateEntityProcess {

        private Timer timer;
        private int price;
        private int advancement;
        private boolean pause;
        private boolean ready;

        public CreateEntityProcess() {
            timer = new Timer(80);
            price = EData.PRICE[entType];
        }

        public void update(int delta) {
            if (!pause && !complete()) {
                timer.update(delta);
                if (timer.isTimeComplete()) {
                    if (engine.getPlayer().removeMoney(5)) {
                        advancement += 5;
                        GameSound.build();
                    } else {
                        GameSound.insiffucientFunds();
                    }
                    timer.resetTime();
                }
            }
        }

        public boolean complete() {
            return advancement == price;
        }
    }

}
