package org.luawars.LuaJScripting;

import org.luaj.vm2.*;
import org.luaj.vm2.ast.Chunk;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.parser.LuaParser;
import org.luaj.vm2.parser.ParseException;
import org.luawars.Log;
import rts.Launch;
import rts.core.engine.ingamegui.GuiButton;
import rts.core.engine.ingamegui.GuiPanel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Trung
 * Date: 3/23/13
 * Time: 8:43 PM
 * To change this template use File | Settings | File Templates.
 *
 * This class is an interface between Lua and Java. The code in this class mainly interacts
 * with static global variables in the LuaJGlobal.java file.
 *
 * @TODO
 * attack/target
 * being attacked
 * setting up base
 * set priorities
 * timing
 * selecting specific units
 *
 * IMPORTANT STUFF TO NOTE:
 * - If you want to access pretty much anything in the game, I made a static variable, g, in Launch.java.
 * With this you can access pretty much anything in the game, e.g. the engine, gui stuff, anything else.
 * Just do something like:
 * Launch.g.getEngine().whatever else
 *
 * - Also, if you want to create a new function, create a static class (you'll see plenty of examples in here)
 * And add it to the CallLua.call() (just copy previous examples in the function).
 * Then you can call the function in your lua script.
 *
 * - I've been testing everything in GuiInGame.java in the keyPressed() function. You can turn it on by pressing 'y'
 * and submit the command by pressing enter.
 */
public class CallLua extends TwoArgFunction {
    public static Globals G = JsePlatform.standardGlobals();

    public CallLua() {}

    /**
     * When the code:
     * require 'org.luawars.LuaJScripting.CallLua'
     * is called in Lua in myScript.lua, this function is called.
     * This function in turn sets up all the functions that are interfaced between Lua and Java.
     * @param modName - module name
     * @param env
     * @return
     */
    public LuaValue call(LuaValue modName, LuaValue env) {

        Log.trace("calling {}", modName);
        LuaValue library = tableOf();

        // if you want to add functions, add them to the library
        library.set("createUnit", new createUnit());
        library.set("selectUnits", new selectUnits());
        library.set("deselectUnits", new deselectUnits());
        library.set("moveOrSpecialAction", new moveOrSpecialAction());
        library.set("getLuaJGlobal", new getLuaJGlobal());
        library.set("placeBuilding", new placeBuilding());
        library.set("setUpBase", new setUpBase());
        library.set("drawText", new drawText());
        library.set("addPriority", new addPriority());
        library.set("getTopPriority", new getTopPriority());
        library.set("removeTopPriority", new removeTopPriority());
        library.set("clearPriorities", new clearPriorities());


        env.set("org.luawars.LuaJScripting.CallLua", library);

        return library;
    }

    /**
     * initializes the lua global variable LUA_PATH which determines where Lua should look to open up Lua scripts
     * @param path - path to set LUA_PATH to
     */
    public static void initLuaPath(String path) {
        CallLua.G.package_.setLuaPath(path);
        Log.debug("LUA_PATH set to {}", G.package_.path);
    }

    public static LuaValue runScript(String fileNameWithPath) {
        return runScript(fileNameWithPath, "");
    }
        /**
         * General method to run a Lua script.
         * Note: run script must be called before you can call callFunction.
         * I'm not exactly sure why. I think it is because Lua puts all the functions' addresses on a table by
         * calling runScript, and once the functions are on a table, calling a function will make Lua
         * look at the table to see what address to go to
         *
         * @param scriptFileName - script to run
         * @return
         */
    public static LuaValue runScript(String scriptFileName, String folderPath) {
        Log.trace("running script {}", scriptFileName);
        try {
            if(folderPath == null) {
                //folderPath = "resources/Lua Scripts/";
                folderPath = "C:/Users/Trung/Documents/GitHub/LuaWars/resources/Lua Scripts/";
            }

            String tempScriptFileName = scriptFileName;
            // if it ends with .lua, then get rid of the .lua part
//            if(scriptFileName.endsWith(".lua")) {
//                tempScriptFileName = scriptFileName.substring(0, scriptFileName.length() - 4);
//            }
            // to see how to use lua parser look at this
            //https://github.com/headius/luaj/blob/master/README.html
            // scroll down to parser section
            //System.out.println("Calling " + folderPath + scriptFileName);
            LuaParser parser = new LuaParser(new FileInputStream(folderPath + scriptFileName));
            Chunk chunk = parser.Chunk();
            return G.loadFile(folderPath + scriptFileName).call();
            // if we want our game to put anything, then put error message displays here
        } catch(FileNotFoundException e) {
            System.out.println("FILE NOT FOUND");
        } catch(ParseException e) {
            System.out.println("PARSE FAILED: " + e);
        } catch(LuaError e) {
            System.out.println("LUA ERROR: " + e);
        }
        return NIL;
    }

    // create more call functions to support more arg nums?

    /**
     * After the script has been run, you can call functions using this method.
     * Note that runScript must be called before you can use this.
     * IF YOU'RE READING THIS SOURCE CODE, YOU DON'T REALLY NEED THESE TO WRITE YOUR LUA SCRIPTS.
     * THESE FUNCTIONS ARE ONLY IF YOU WANT TO CALL FUNCTIONS FROM LUA CODE IN YOUR JAVA CODE.
     * @param functionName
     * @param arg
     * @return
     */
    public static LuaValue callFunction(String functionName, LuaValue arg) {
        Log.trace("Calling function {} with argument {}", functionName, arg);
        return G.get(functionName).call(arg);
    }

    public static LuaValue callFunction(String functionName, LuaValue arg1, LuaValue arg2) {
        LuaValue[] args = {arg1, arg2};
        Log.trace("Calling function {} with arguments {}", functionName, args);
        return G.get(functionName).call(arg1, arg2);
    }

    public static LuaValue callFunction(String functionName, LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        LuaValue[] args = {arg1, arg2, arg3};
        Log.trace("Calling function {} with arguments {}", functionName, args);
        return G.get(functionName).call(arg1, arg2, arg3);
    }

    /**
     * Allows you to call a Lua function in a script without having to call runScript before (although internally,
     * it is just calling the script before you call the Lua function).
     * IF YOU'RE READING THIS SOURCE CODE, YOU DON'T REALLY NEED THESE TO WRITE YOUR LUA SCRIPTS.
     * THESE FUNCTIONS ARE ONLY IF YOU WANT TO CALL FUNCTIONS FROM LUA CODE IN YOUR JAVA CODE.
     *
     * @param scriptFileName
     * @param functionName
     * @param arg
     * @return
     */
    public static LuaValue callFunctionFromScript(String scriptFileName, String functionName, LuaValue arg) {
        String[] function = {scriptFileName, functionName};
        Log.trace("Calling script function {} with argument {}", function, arg);
        G.loadFile(scriptFileName).call();
        return G.get(functionName).call(arg);
    }

    public static LuaValue callFunctionFromScript(String scriptFileName, String functionName, LuaValue arg1, LuaValue arg2) {
        String[] function = {scriptFileName, functionName};
        LuaValue[] args = {arg1, arg2};
        Log.trace("Calling script function {} with arguments {}", function, args);
        G.loadFile(scriptFileName).call();
        return G.get(functionName).call(arg1, arg2);
    }

    public static LuaValue callFunctionFromScript(String scriptFileName, String functionName, LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        String[] function = {scriptFileName, functionName};
        LuaValue[] args = {arg1, arg2, arg3};
        Log.trace("Calling script function {} with arguments {}", function, args);
        G.loadFile(scriptFileName).call();
        return G.get(functionName).call(arg1, arg2, arg3);
    }

    /**
     * To create a method for Lua to call, we create a static class that extends a LuaFunction (e.g. OneArgFunction,
     * TwoArgFunction, etc). Then we implement the call function.
     *
     * NOTE: I ALSO MADE A SevenArgFunction class. You can use this if you need to create a function with more than
     * 3 arguments instead of using VarArgFunction.
     */
    public static class createUnit extends TwoArgFunction {
        public LuaValue call(LuaValue panelId, LuaValue buttonNum) {
            Log.trace("calling createUnit function with panelId {}, buttonNum {}", panelId, buttonNum);

            ArrayList<GuiPanel> panels = Launch.g.getEngine().getGui().getMenuGui().getPanels();
            if(panelId.toint() >= 0 && panelId.toint() < panels.size()){

                ArrayList<GuiButton> buttons = panels.get(panelId.toint()).getButtons();
                if(buttonNum.toint() >= 0 && buttonNum.toint() < buttons.size()) {
                    Log.trace("creating unit {}", buttons.get(buttonNum.toint()));
                    buttons.get(buttonNum.toint()).launchCreateEntityProcess();
                }
                else {
                    Log.error("Attempted to use button outside of button size range.");
                }
            }
            else {
                Log.error("Attempted to use panel outside of panel size range.");
            }
            return NIL;
        }
    }

    public static class selectUnits extends SevenArgFunction {
        // NOTE: THIS COULD MESS UP BECAUSE THERE ARE MULTIPLE UNITS THAT HAVE THE NAME BUILDER,
        // LIKEWISE THERE ARE MULTIPLE UNITS WITH THE NAME SCOUT
        public LuaValue call(LuaValue tileX, LuaValue tileY, LuaValue radius, LuaValue numUnits, LuaValue unitType, LuaValue NIL1, LuaValue NIL2) {
            // make it return a list of the selected units
            // right now selectUnitsAt returns an arraylist of active entities
            // might need to convert them into a lua list
            // if unit type is NIL, then make tempUnitType null,
            // or if the unit name is provided then select that unit
            String tempUnitType = unitType == NIL ? null : unitType.tojstring();
            System.out.println(Launch.g.getEngine().getInput().selectUnitsAt(tileX.toint(), tileY.toint(), radius.tofloat(), numUnits.toint(), tempUnitType));
            return NIL;
        }
    }

    public static class deselectUnits extends SevenArgFunction {
        public LuaValue call(LuaValue NIL0, LuaValue NIL1, LuaValue NIL2, LuaValue NIL3, LuaValue NIL4, LuaValue NIL5, LuaValue NIL6) {
            Launch.g.getEngine().deselectAllEntities();
            return NIL;
        }
    }

    public static class moveOrSpecialAction extends TwoArgFunction {
        // the lua version takes in tile coordinates
        // however moveOrSpecialAction takes in x, y coordinates (i.e. pixel coordinates)
        // so we need to convert the tiles to pixel coordinates
        public LuaValue call(LuaValue tileX, LuaValue tileY) {
            Launch.g.getEngine().getInput().moveOrSpecialAction(tileX.toint() * Launch.g.getEngine().getTileW(), tileY.toint() * Launch.g.getEngine().getTileH());
            return NIL;
        }
    }

    /**
     * Returns a global variable for the player to use
     * Also updates all the global variables when called
     */
    public static class getLuaJGlobal extends OneArgFunction {
        public LuaValue call(LuaValue globalVarName) {
            if(LuaJGlobal.luaJGlobal.get(globalVarName.tojstring()) != null)
            {
                // updates which stuff is building
                // NOTE: THIS ASSUMES THAT ONLY ONE THING IS BUILDING FOR EACH PANEL
                // AND THERE AREN'T ANY BUILDINGS/UNITS IN THE WAITING LIST
                ArrayList<GuiPanel> panels = Launch.g.getEngine().getGui().getMenuGui().getPanels();
                // i only want to check if a building is being built when this is called
                for(int i = 0; i < panels.size(); i++) {
                    ArrayList<GuiButton> buttons = Launch.g.getEngine().getGui().getMenuGui().getPanels().get(i).getButtons();
                    boolean panelCurrentlyBuilding = false;
                    for(int j = 0; j < buttons.size(); j++) {
                        if(buttons.get(j).hasProcessReady()) {
                            panelCurrentlyBuilding = true;
                            LuaJGlobal.addNewLuaJGlobal("buildingPanel" + i, LuaInteger.valueOf(j));
                            LuaJGlobal.addNewLuaJGlobal("buildingPanelReady" + i, LuaInteger.valueOf(j));
                        }
                        else if(!buttons.get(j).getProcessList().isEmpty()) {
                            panelCurrentlyBuilding = true;
                            LuaJGlobal.addNewLuaJGlobal("buildingPanel" + i, LuaInteger.valueOf(j));
                            LuaJGlobal.addNewLuaJGlobal("buildingPanelReady" + i, LuaInteger.valueOf(-1));

                        }
                    }
                    if(panelCurrentlyBuilding == false) {
                        LuaJGlobal.addNewLuaJGlobal("buildingPanel" + i, LuaInteger.valueOf(-1));
                        LuaJGlobal.addNewLuaJGlobal("buildingPanelReady" + i, LuaInteger.valueOf(-1));

                    }
                }
                return LuaJGlobal.getLuaJGlobal(globalVarName.tojstring());
            }
            return NIL;
        }
    }

    // right now it places the first building it finds (from the panels)
    // even if you have both a building (from panel 0) and a wall (from panel 2) to place.
    // I might need to extend it to use 4 arguments, panel and building, but for now, I'll leave it
    public static class placeBuilding extends TwoArgFunction {
        public LuaValue call(LuaValue xLoc, LuaValue yLoc) {
            ArrayList<GuiPanel> panels = Launch.g.getEngine().getGui().getMenuGui().getPanels();
            OUTERLOOP:
            for(GuiPanel panel : panels) {
                ArrayList<GuiButton> buttons = panel.getButtons();
                for(GuiButton button : buttons) {
                    if(button.placeBuilding(xLoc.toint(), yLoc.toint()))
                    {
                        break OUTERLOOP;
                    }
                }
            }
            return NIL;
        }
    }

    public static class setUpBase extends ZeroArgFunction {
        public LuaValue call() {
            Launch.g.getEngine().getInput().setUpBase();
            return NIL;
        }
    }

    public static class drawText extends SevenArgFunction {
        public LuaValue call(LuaValue xCoordinate, LuaValue yCoordinate, LuaValue text, LuaValue NIL0, LuaValue NIL1, LuaValue NIL2, LuaValue NIL3) {
            Launch.g.getEngine().getContainer().getGraphics().drawString(text.tojstring(), xCoordinate.toint(), yCoordinate.toint());
            return NIL;
        }
    }

    public static class addPriority extends SevenArgFunction {
        public LuaValue call(LuaValue functionCall, LuaValue parameters, LuaValue priority, LuaValue NIL0, LuaValue NIL1, LuaValue NIL2, LuaValue NIL3) {
            LuaJGlobal.AIpriorityQueue.add(new AIGamePriorities(functionCall, parameters, priority));
            return NIL;
        }
    }

    public static class removeTopPriority extends SevenArgFunction {
        public LuaValue call(LuaValue functionCall, LuaValue parameters, LuaValue priority, LuaValue NIL0, LuaValue NIL1, LuaValue NIL2, LuaValue NIL3) {
            LuaTable topPriority = new LuaTable();
            System.out.println("java.removeTopPriority");
            if(LuaJGlobal.AIpriorityQueue.size() > 0) {
                System.out.println("XXX" + LuaJGlobal.AIpriorityQueue.peek());
                topPriority.set(0, LuaJGlobal.AIpriorityQueue.peek().myFunction);
                topPriority.set(1, LuaJGlobal.AIpriorityQueue.peek().parameters);
                LuaJGlobal.AIpriorityQueue.poll();
                return topPriority;
            }
            return NIL;
        }
    }

    public static class getTopPriority extends SevenArgFunction {
        public LuaValue call(LuaValue NIL0, LuaValue NIL1, LuaValue NIL2, LuaValue NIL3, LuaValue NIL4, LuaValue NIL5, LuaValue NIL6) {
            LuaTable topPriority = new LuaTable();
            if(LuaJGlobal.AIpriorityQueue.peek() != null) {
                topPriority.set(0, LuaJGlobal.AIpriorityQueue.peek().myFunction);
                topPriority.set(1, LuaJGlobal.AIpriorityQueue.peek().parameters);
                return topPriority;
            }
            else {
                return NIL;
            }

        }
    }

//    public static class getAllPriorities extends SevenArgFunction {
//        public LuaValue call(LuaValue NIL0, LuaValue NIL1, LuaValue NIL2, LuaValue NIL3, LuaValue NIL4, LuaValue NIL5, LuaValue NIL6) {
//            LuaTable allPriorities = new LuaTable();
//            int i = 0;
//            for(AIGamePriorities p : LuaJGlobal.AIpriorityQueue) {
//                LuaTable topPriority = new LuaTable();
//                topPriority.set(0, LuaJGlobal.AIpriorityQueue.peek().myFunction);
//                topPriority.set(1, LuaJGlobal.AIpriorityQueue.peek().parameters);
//                i++;
//                allPriorities.set(i, topPriority);
//            }
//            return allPriorities;
//        }
//    }

    public static class clearPriorities extends SevenArgFunction {
        public LuaValue call(LuaValue functionCall, LuaValue yCoordinate, LuaValue text, LuaValue NIL0, LuaValue NIL1, LuaValue NIL2, LuaValue NIL3) {
            LuaJGlobal.AIpriorityQueue.clear();
            return NIL;
        }
    }

    public static class repairBuilding extends TwoArgFunction
    {
        public LuaValue call(LuaValue tileX, LuaValue tileY)
        {
            Launch.g.getEngine().getInput().repairBuilding(tileX.toint()*Launch.g.getEngine().getTileW(), tileY.toint()*Launch.g.getEngine().getTileH());
            return NIL;
        }
    }

    public static class sellBuilding extends TwoArgFunction
    {
        public LuaValue call(LuaValue tileX, LuaValue tileY)
        {
            Launch.g.getEngine().getInput().sellBuilding(tileX.toint()*Launch.g.getEngine().getTileW(), tileY.toint()*Launch.g.getEngine().getTileH());
            return NIL;
        }
    }

    public static class cancelProcess extends TwoArgFunction {
        public LuaValue call(LuaValue panelId, LuaValue buttonNum) {
            Log.trace("calling cancel function with panelId {}, buttonNum {}", panelId, buttonNum);

            ArrayList<GuiPanel> panels = Launch.g.getEngine().getGui().getMenuGui().getPanels();
            if(panelId.toint() >= 0 && panelId.toint() < panels.size()){

                ArrayList<GuiButton> buttons = panels.get(panelId.toint()).getButtons();
                if(buttonNum.toint() >= 0 && buttonNum.toint() < buttons.size()) {
                    Log.trace("creating unit or building {}", buttons.get(buttonNum.toint()));
                    buttons.get(buttonNum.toint()).checkCancelProcess();
                }
                else {
                    Log.error("Attempted to use button outside of button size range.");
                }
            }
            else {
                Log.error("Attempted to use panel outside of panel size range.");
            }
            return NIL;
        }
    }
}
