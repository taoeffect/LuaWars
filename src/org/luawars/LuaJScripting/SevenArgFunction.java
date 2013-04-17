package org.luawars.LuaJScripting;

/**
 * Created with IntelliJ IDEA.
 * User: studman69
 * Date: 4/13/13
 * Time: 8:32 PM
 * To change this template use File | Settings | File Templates.
 */
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

abstract public class SevenArgFunction extends LibFunction {

    abstract public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3, LuaValue arg4, LuaValue arg5, LuaValue arg6, LuaValue arg7);

    public SevenArgFunction() {
    }

//    public SevenArgFunction(LuaValue env) {
//        this.env = env;
//    }

    @Override
    public final LuaValue call() {
        return call(NIL, NIL, NIL, NIL, NIL, NIL, NIL);
    }

    @Override
    public final LuaValue call(LuaValue arg) {
        return call(arg, NIL, NIL, NIL, NIL, NIL, NIL);
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2) {
        return call(arg1, arg2, NIL, NIL, NIL, NIL, NIL);
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        return call(arg1, arg2, arg3, NIL, NIL, NIL, NIL);
    }

    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3, LuaValue arg4) {
        return call(arg1, arg2, arg3, arg4, NIL, NIL, NIL);
    }

    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3, LuaValue arg4, LuaValue arg5) {
        return call(arg1, arg2, arg3, arg4, arg5, NIL, NIL);
    }

    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3, LuaValue arg4, LuaValue arg5, LuaValue arg6) {
        return call(arg1, arg2, arg3, arg4, arg5, arg6, NIL);
    }

    @Override
    public Varargs invoke(Varargs varargs) {
        return call(varargs.arg1(), varargs.arg(2), varargs.arg(3), varargs.arg(4), varargs.arg(5), varargs.arg(6), varargs.arg(7));
    }

}

