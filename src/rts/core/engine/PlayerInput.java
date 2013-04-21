package rts.core.engine;

import java.awt.Point;
import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import rts.core.engine.layers.entities.ActiveEntity;
import rts.core.engine.layers.entities.MoveableEntity;
import rts.core.engine.layers.entities.buildings.Building;
import rts.core.engine.layers.entities.buildings.BuildingECreator;
import rts.utils.ResourceManager;

public class PlayerInput {

    public static final int CURSOR_NO_ACTION = 0;
    public static final int CURSOR_MOVE = 1;
    public static final int CURSOR_ATTACK = 2;
    public static final int CURSOR_SPECIAL_ACTION = 3;
    public static final int CURSOR_SELL = 4;
    public static final int CURSOR_REPAIR = 5;

    private static final int SINGLE_SELECTION_LIMIT = 20;
    private static final int TIME_BETWEEN_CURSOR_FRAME = 200;

    private int pressedX;
    private int pressedY;
    private boolean pressedLeft;

    private ArrayList<MoveableEntity> movers;
    private ArrayList<ActiveEntity> selected;
    private Engine engine;

    // Cursors
    private Image cursor;
    private Animation[] cursors;

    public PlayerInput(Engine engine) {
        this.engine = engine;
        this.movers = new ArrayList<MoveableEntity>();
        this.selected = new ArrayList<ActiveEntity>();
    }

    public void init() {
        cursor = ResourceManager.getImage("cursor");
        SpriteSheet cursorSheet = ResourceManager.getSpriteSheet("cursors");
        cursors = new Animation[6];

        for (int i = 0; i < 6; i++)
            cursors[i] = new Animation();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                cursors[i].addFrame(cursorSheet.getSprite(j, i), TIME_BETWEEN_CURSOR_FRAME);
            }
        }
    }

    // mx and my determine where the mouse is on the current screen
    // decX and decY determine where the screen is relative to the game map
    public void update(GameContainer container, boolean onGui, int mx, int my, int decX, int decY) throws SlickException {
        // Get Selected
        selected.clear();
        selected.addAll(engine.getPlayerSelectedEntities());

        // Get movers
        movers.clear();
        movers.addAll(engine.getSelectedMoveableEntities((mx + decX) / engine.getTileW(), (my + decY) / engine.getTileH()));

        updateKey(container);
        updateMouse(container, onGui, mx, my, decX, decY);
    }

    // here might allow us to select items or move them
    /*New function to attack or move all units*/
    public void moveOrAttackAction(int x, int y)
    {
        ArrayList<ActiveEntity> selectedAllUnits = engine.selectAllUnits(x,y);
        ArrayList<ActiveEntity> selectedAllEnemies = engine.selectAllEnemies(x,y);
        moveOrSpecialAction(x, y);
        if (selectedAllUnits != null && selectedAllEnemies != null && !(engine.getMap().isEnableFow() && engine.getMap().fogOn(x / engine.getTileW(), y / engine.getTileH())))
        {
            for(ActiveEntity ae1 : selectedAllUnits)
            {
                for(ActiveEntity ae2 : selectedAllEnemies)
                {
                    if(engine.distanceOfUnits(ae1,ae2))
                    {
                        ae1.target((ActiveEntity) ae2, (int)ae2.getX(), (int)ae2.getY());
                    }
                }
            }
        }
        else
        {
            moveOrSpecialAction(x,y);
        }

    }

    private void updateMouse(GameContainer container, boolean onGui, int mx, int my, int decX, int decY) {
        if (engine.isMouseRightPressed() && !onGui) {
            if (engine.getGui().isSellMod()) {
                sellBuilding(mx + decX, my + decY);
                // note i changed the structure of these if/else statements
                // to be less confusing
            } else if (engine.getGui().isRepairMod()) {
                repairBuilding(mx + decX, my + decY);
            } else {
                moveOrSpecialAction(mx + decX, my + decY);
            }
        } else {
            selectAction(container, onGui, mx, my, decX, decY);
        }
    }

    private void repairBuilding(int x, int y) {
        ActiveEntity ae = engine.getEntityAt(null, x, y);
        if (ae != null && ae instanceof Building && engine.isPlayerEntity(ae.getPlayerId())) {
            ((Building) ae).repair();
        }
    }

    private void sellBuilding(int x, int y) {
        ActiveEntity ae = engine.getEntityAt(null, x, y);
        if (ae != null && ae instanceof Building && engine.isPlayerEntity(ae.getPlayerId())) {
            ((Building) ae).sell();
        }
    }

    public void moveOrSpecialAction(int mx, int my) {
        ActiveEntity e = engine.getEntityAt(null, mx, my); // this is our enemy unit
        if (e != null && !(engine.getMap().isEnableFow() && engine.getMap().fogOn(mx / engine.getTileW(), my / engine.getTileH()))) {
            if (!selected.isEmpty() && e instanceof ActiveEntity) {
                // attack enemy
                // target() is the attack function
                if (selected.size() == 1) {
                    selected.get(0).target((ActiveEntity) e, mx / engine.getTileW(), my / engine.getTileH());
                } else {
                    // All entity target !
                    ArrayList<Point> points = Utils.getCloserPoints(engine.getMap(), movers, mx / engine.getTileW(), my / engine.getTileH());

                    int counter = 0;

                    for (int i = 0; i < selected.size(); i++) {
                        if (selected.get(i) instanceof Building) {
                            selected.get(i).target((ActiveEntity) e, 0, 0);
                        } else {
                            if (counter < points.size()) {
                                selected.get(i).target((ActiveEntity) e, points.get(counter).x, points.get(counter).y);
                                counter++;
                            }
                        }
                    }
                }
            }
        } else {
            if (!movers.isEmpty()) {
                if (movers.size() == 1) {
                    if (engine.getMap().blocked(movers.get(0), mx / engine.getTileW(), my / engine.getTileH())) {
                        Point p = Utils.getCloserPoint(engine.getMap(), movers.get(0), mx / engine.getTileW(), my / engine.getTileH());
                        movers.get(0).moveFromPlayerAction(p.x * engine.getTileW(), p.y * engine.getTileH());
                    } else
                        movers.get(0).moveFromPlayerAction(mx, my);
                } else {
                    // Plusieurs entit�s, il faut regrouper des points �
                    // proximit�
                    //this is how it determines how to move unit
                    ArrayList<Point> points = Utils.getCloserPoints(engine.getMap(), movers, mx / engine.getTileW(), my / engine.getTileH());

                    for (int i = 0; i < movers.size(); i++) {
                        movers.get(i).moveFromPlayerAction(points.get(i).x * engine.getTileW(), points.get(i).y * engine.getTileH());
                    }
                }
            }
        }

        if (!selected.isEmpty() && selected.get(0) instanceof BuildingECreator) {
            ((BuildingECreator) selected.get(0)).changeRallyingPoint(mx, my);
        }
    }

    // used to select units
    private void selectAction(GameContainer container, boolean onGui, int mx, int my, int decX, int decY) {
        // first mouse click, after this, player can drag to select units with select box
        if (container.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
            if (!pressedLeft) {
                pressedX = mx;
                pressedY = my;
                pressedLeft = true;
            }
            // when player releases the mouse, it selects the units inside select box
            // changed the if statement, but it's pretty much the same
        } else if (pressedLeft) {
            if (Math.abs(pressedX - mx) < SINGLE_SELECTION_LIMIT && Math.abs(pressedY - my) < SINGLE_SELECTION_LIMIT && !onGui) {
                ActiveEntity e = engine.getEntityAt(null, mx + decX, my + decY);
                if (e != null) {
                    if (!e.isSelected())
                        engine.deselectAllEntities();
                    e.selected();
                } else {
                    engine.deselectAllEntities();
                }
            } else {
                engine.deselectAllEntities();
                // Select severals entities
                engine.selectEntitiesBetween(pressedX, pressedY, mx, my);
            }
            pressedLeft = false;
        }
    }

    // TRUNG NGUYEN'S SELECT ACTION FOR LUA
    public ArrayList<ActiveEntity> selectUnitsAt(int tileX, int tileY, float radius, int numUnits, String unitType) {
        // NOTE I DON'T WANT TO DESELECT UNITS. THIS WAY I CAN ADD DIFFERENT TYPES OF UNITS TO THE ONES SELECTED
        //engine.deselectAllEntities();
        // Select several entities
        ArrayList<ActiveEntity> selectedUnits = engine.selectClosestEntities(tileX, tileY, radius, numUnits, unitType); // add a way to select specific type of unit
        selected.addAll(selectedUnits);
        for(ActiveEntity e : selectedUnits) {
            if(e instanceof MoveableEntity) {
                movers.add((MoveableEntity) e);
            }
        }
        return selectedUnits;
    }

    public void setUpBase() {
        ArrayList<ActiveEntity> selectedUnits = selectUnitsAt(0, 0, 10000, 1, "Builder"); // add a way to select specific type of unit
        if(selectedUnits.size() > 0) {
            moveOrSpecialAction((int)selectedUnits.get(0).getX(), (int)selectedUnits.get(0).getY());
        }
    }

    private void updateKey(GameContainer container) {
        if (container.getInput().isKeyPressed(Input.KEY_TAB))
            engine.getGui().hideOrShow();
    }

    public void render(GameContainer container, Graphics g) throws SlickException {
        int mx = container.getInput().getMouseX();
        int my = container.getInput().getMouseY();
        if (pressedLeft) {
            g.setColor(Color.white);
            g.drawRect((pressedX <= mx) ? pressedX : mx, (pressedY <= my) ? pressedY : my, Math.abs(pressedX - mx), Math.abs(pressedY - my));
        }
    }

    // looks important too
    public void renderCursor(GameContainer container, Graphics g, boolean onGui) {
        int mx = container.getInput().getMouseX();
        int my = container.getInput().getMouseY();
        if (engine.getGui().isSellMod() || engine.getGui().isRepairMod()) {
            ActiveEntity ae = engine.getEntityAt(null, engine.getMouseX(), engine.getMouseY());
            if (ae != null && ae instanceof Building && engine.isPlayerEntity(ae.getPlayerId())) {
                g.drawAnimation(cursors[engine.getGui().isSellMod() ? CURSOR_SELL : CURSOR_REPAIR], mx - 10, my - 10);
            } else {
                g.drawAnimation(cursors[CURSOR_NO_ACTION], mx - 10, my - 10);
            }
        } else {
            if (!selected.isEmpty() && !onGui) {
                int cursor = selectCursor(selected.get(0), engine.getMouseX(), engine.getMouseY());
                g.drawAnimation(cursors[cursor], mx - 10, my - 10);
            } else {
                g.drawImage(cursor, mx, my);
            }
        }

    }

    // looks pretty important
    private int selectCursor(ActiveEntity firstSelected, int mx, int my) {
        for (int i = 0; i < selected.size(); i++) {
            int c = selected.get(i).getTargetCursor(engine.getEntityAt(null, mx, my), mx, my);
            if (c != CURSOR_NO_ACTION) {
                return c;
            }
        }
        return CURSOR_NO_ACTION;
    }

    public boolean isPressedLeft() {
        return pressedLeft;
    }
}
