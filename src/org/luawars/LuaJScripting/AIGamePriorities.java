package org.luawars.LuaJScripting;

import org.luaj.vm2.LuaValue;

/**
 * Created with IntelliJ IDEA.
 * User: studman69
 * Date: 4/18/13
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class AIGamePriorities implements Comparable<AIGamePriorities> {
    public LuaValue myFunction;
    public LuaValue parameters;
    public int priority;

    public AIGamePriorities(LuaValue myFunction, LuaValue parameters, int priority) {
        this.myFunction = myFunction;
        this.parameters = parameters;
        this.priority = priority;
    }

    @Override
    public int compareTo(AIGamePriorities o) {
        if(this.priority > o.priority)
            return -1;
        else if(this.priority < o.priority)
            return 1;
        return 0;
    }

    @Override
    public String toString() {
        return myFunction.tojstring() + "(" + parameters + ") " + priority;
    }
}