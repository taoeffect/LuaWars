package org.luawars.LuaJScripting;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.*;
import org.luawars.Log;
import org.newdawn.slick.KeyListener;
import rts.Launch;
import rts.core.engine.ingamegui.GuiButton;
import rts.core.engine.ingamegui.GuiPanel;
import rts.core.engine.ingamegui.GuiPanelFactory;

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
 */
public class CallLua extends TwoArgFunction {
    public static Globals G = JsePlatform.standardGlobals();

    public CallLua() {}

    /**
     * When the code:
     * require 'org.luawars.LuaJScripting.CallLua'
     * is called in Lua, this function is called.
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
        library.set("moveOrSpecialAction", new moveOrSpecialAction());
        library.set("getLuaJGlobal", new getLuaJGlobal());
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
    public static LuaValue runScript(String scriptFileName) {
        Log.debug("running script {}", scriptFileName);
        return G.loadFile(scriptFileName).call();
    }

    // create more call functions to support more arg nums?

    /**
     * After the script has been run, you can call functions using this method.
     * Note that runScript must be called before you can use this.
     * @param functionName
     * @param arg
     * @return
     */
    public static LuaValue callFunction(String functionName, LuaValue arg) {
        Log.trace("Calling function {} with argument {}" , functionName, arg);
        return G.get(functionName).call(arg);
    }

    public static LuaValue callFunction(String functionName, LuaValue arg1, LuaValue arg2) {
        LuaValue[] args = {arg1, arg2};
        Log.trace("Calling function {} with arguments {}" , functionName, args);
        return G.get(functionName).call(arg1, arg2);
    }

    public static LuaValue callFunction(String functionName, LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        LuaValue[] args = {arg1, arg2, arg3};
        Log.trace("Calling function {} with arguments {}" , functionName, args);
        return G.get(functionName).call(arg1, arg2, arg3);
    }

    /**
     * Allows you to call a Lua function in a script without having to call runScript before (although internally,
     * it is just calling the script before you call the Lua function).
     *
     * @param scriptFileName
     * @param functionName
     * @param arg
     * @return
     */
    public static LuaValue callFunctionFromScript(String scriptFileName, String functionName, LuaValue arg) {
        String[] function = {scriptFileName, functionName};
        Log.trace("Calling script function {} with argument {}" , function, arg);
        G.loadFile(scriptFileName).call();
        return G.get(functionName).call(arg);
    }

    public static LuaValue callFunctionFromScript(String scriptFileName, String functionName, LuaValue arg1, LuaValue arg2) {
        String[] function = {scriptFileName, functionName};
        LuaValue[] args = {arg1, arg2};
        Log.trace("Calling script function {} with arguments {}" , function, args);
        G.loadFile(scriptFileName).call();
        return G.get(functionName).call(arg1, arg2);
    }

    public static LuaValue callFunctionFromScript(String scriptFileName, String functionName, LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        String[] function = {scriptFileName, functionName};
        LuaValue[] args = {arg1, arg2, arg3};
        Log.trace("Calling script function {} with arguments {}" , function, args);
        G.loadFile(scriptFileName).call();
        return G.get(functionName).call(arg1, arg2, arg3);
    }

    /**
     * To create a method for Lua to call, we create a static class that extends a LuaFunction (e.g. OneArgFunction,
     * TwoArgFunction, etc). Then we implement the call function.
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

    public static class selectUnits extends ThreeArgFunction {
        public LuaValue call(LuaValue xLoc, LuaValue yLoc, LuaValue numUnits) {
            Launch.g.getEngine().getInput().selectUnitsAt(xLoc.toint(), yLoc.toint(), numUnits.toint());
            return NIL;
        }
    }

    // make sure calls aren't out of bounds
    public static class moveOrSpecialAction extends TwoArgFunction {
        public LuaValue call(LuaValue xLoc, LuaValue yLoc) {
            Launch.g.getEngine().getInput().moveOrSpecialAction(xLoc.toint(), yLoc.toint());
            return NIL;
        }
    }

    public static class getLuaJGlobal extends OneArgFunction {
        public LuaValue call(LuaValue globalVarName) {
            if(LuaJGlobal.luaJGlobal.get(globalVarName.tojstring()) != null)
            {
                return LuaValue.valueOf(LuaJGlobal.luaJGlobal.get(globalVarName.tojstring()).intValue());
            }
            return NIL;
        }
    }
}
