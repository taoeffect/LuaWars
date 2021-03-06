package org.luawars;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintStream;
import java.util.Date;


/**
 * Created with IntelliJ IDEA.
 * User: gslepak
 * Date: 3/10/13
 * Time: 3:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class Log {
    public enum LEVEL {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

    public static final LEVEL TRACE    = LEVEL.TRACE;
    public static final LEVEL DEBUG    = LEVEL.DEBUG;
    public static final LEVEL INFO     = LEVEL.INFO;
    public static final LEVEL WARN     = LEVEL.WARN;
    public static final LEVEL ERROR    = LEVEL.ERROR;
    public static LEVEL   currentLevel = INFO;
    public static boolean showTime     = true;
    public static boolean showThread   = true;
    

    private static void formatAndLog(LEVEL level, String format, Object arg1, Object arg2) {
        if (currentLevel.compareTo(level) <= 0) {
            FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
            log(level, tp.getMessage(), tp.getThrowable());
        }
    }
    private static void formatAndLog(LEVEL level, String format, Object... arguments) {
        if (currentLevel.compareTo(level) <= 0) {
            FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
            log(level, tp.getMessage(), tp.getThrowable());
        }
    }

    public static String me() {
        StackTraceElement t = Thread.currentThread().getStackTrace()[2];
        return t.getClassName()+"."+t.getMethodName()+":"+t.getLineNumber();
    }
    public static String me(int idx) {
        StackTraceElement t = Thread.currentThread().getStackTrace()[2+idx];
        return t.getClassName()+"."+t.getMethodName()+":"+t.getLineNumber();
    }
    public static void logEnterMethod(LEVEL l) {
        logEnterMethod(l, 1);
    }
    public static void logExitMethod(LEVEL l) {
        logExitMethod(l, 1);
    }
    public static void logEnterMethod(LEVEL l, int idx) {
        log(l, me(1+idx) + "...", null);
    }
    public static void logExitMethod(LEVEL l, int idx) {
        log(l, me(1+idx) + " done!", null);
    }


    public static void trace(String msg) {
        log(TRACE, msg, null);
    }
    public static void trace(String format, Object param1) {
        formatAndLog(TRACE, format, param1, null);
    }
    public static void trace(String format, Object param1, Object param2) {
        formatAndLog(TRACE, format, param1, param2);
    }
    public static void trace(String format, Object... argArray) {
        formatAndLog(TRACE, format, argArray);
    }
    public static void trace(String msg, Throwable t) {
        log(TRACE, msg, t);
    }
    public static void debug(String msg) {
        log(DEBUG, msg, null);
    }
    public static void debug(String format, Object param1) {
        formatAndLog(DEBUG, format, param1, null);
    }
    public static void debug(String format, Object param1, Object param2) {
        formatAndLog(DEBUG, format, param1, param2);
    }
    public static void debug(String format, Object... argArray) {
        formatAndLog(DEBUG, format, argArray);
    }
    public static void debug(String msg, Throwable t) {
        log(DEBUG, msg, t);
    }
    public static void info(String msg) {
        log(INFO, msg, null);
    }
    public static void info(String format, Object arg) {
        formatAndLog(INFO, format, arg, null);
    }
    public static void info(String format, Object arg1, Object arg2) {
        formatAndLog(INFO, format, arg1, arg2);
    }
    public static void info(String format, Object... argArray) {
        formatAndLog(INFO, format, argArray);
    }
    public static void info(String msg, Throwable t) {
        log(INFO, msg, t);
    }
    public static void warn(String msg) {
        log(WARN, msg, null);
    }
    public static void warn(String format, Object arg) {
        formatAndLog(WARN, format, arg, null);
    }
    public static void warn(String format, Object arg1, Object arg2) {
        formatAndLog(WARN, format, arg1, arg2);
    }
    public static void warn(String format, Object... argArray) {
        formatAndLog(WARN, format, argArray);
    }
    public static void warn(String msg, Throwable t) {
        log(WARN, msg, t);
    }
    public static void error(String msg) {
        log(ERROR, msg, null);
    }
    public static void error(String format, Object arg) {
        formatAndLog(ERROR, format, arg, null);
    }
    public static void error(String format, Object arg1, Object arg2) {
        formatAndLog(ERROR, format, arg1, arg2);
    }
    public static void error(String format, Object... argArray) {
        formatAndLog(ERROR, format, argArray);
    }
    public static void error(String msg, Throwable t) {
        log(ERROR, msg, t);
    }


    private static void log(LEVEL level, String message, Throwable t) {
        if (currentLevel.compareTo(level) > 0)
            return;

        StringBuilder buf = new StringBuilder(32);

        // Append date-time if so configured
        if (showTime) {
            buf.append(new Date()+" ");
        }

        // Append current thread name if so configured
        if (showThread) {
            buf.append('['+Thread.currentThread().getName()+"] ");
        }

        switch (level) {
            case TRACE:
                buf.append("TRACE: ");
                break;
            case DEBUG:
                buf.append("DEBUG: ");
                break;
            case INFO:
                buf.append("INFO: ");
                break;
            case WARN:
                buf.append("WARN: ");
                break;
            case ERROR:
                buf.append("ERROR: ");
                break;
        }

        // Append the message
        buf.append(message);
        PrintStream out = level.compareTo(WARN) >= 0 ? System.err : System.out;
        out.println(buf.toString());
        out.flush();
        if (t != null) t.printStackTrace(System.err);
    }

}
