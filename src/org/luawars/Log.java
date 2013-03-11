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

    private static final long START_TIME = System.currentTimeMillis();

    public static LEVEL   currentLevel = LEVEL.INFO;
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


    public static void trace(String msg) {
        log(LEVEL.TRACE, msg, null);
    }
    public static void trace(String format, Object param1) {
        formatAndLog(LEVEL.TRACE, format, param1, null);
    }
    public static void trace(String format, Object param1, Object param2) {
        formatAndLog(LEVEL.TRACE, format, param1, param2);
    }
    public static void trace(String format, Object... argArray) {
        formatAndLog(LEVEL.TRACE, format, argArray);
    }
    public static void trace(String msg, Throwable t) {
        log(LEVEL.TRACE, msg, t);
    }
    public static void debug(String msg) {
        log(LEVEL.DEBUG, msg, null);
    }
    public static void debug(String format, Object param1) {
        formatAndLog(LEVEL.DEBUG, format, param1, null);
    }
    public static void debug(String format, Object param1, Object param2) {
        formatAndLog(LEVEL.DEBUG, format, param1, param2);
    }
    public static void debug(String format, Object... argArray) {
        formatAndLog(LEVEL.DEBUG, format, argArray);
    }
    public static void debug(String msg, Throwable t) {
        log(LEVEL.DEBUG, msg, t);
    }
    public static void info(String msg) {
        log(LEVEL.INFO, msg, null);
    }
    public static void info(String format, Object arg) {
        formatAndLog(LEVEL.INFO, format, arg, null);
    }
    public static void info(String format, Object arg1, Object arg2) {
        formatAndLog(LEVEL.INFO, format, arg1, arg2);
    }
    public static void info(String format, Object... argArray) {
        formatAndLog(LEVEL.INFO, format, argArray);
    }
    public static void info(String msg, Throwable t) {
        log(LEVEL.INFO, msg, t);
    }
    public static void warn(String msg) {
        log(LEVEL.WARN, msg, null);
    }
    public static void warn(String format, Object arg) {
        formatAndLog(LEVEL.WARN, format, arg, null);
    }
    public static void warn(String format, Object arg1, Object arg2) {
        formatAndLog(LEVEL.WARN, format, arg1, arg2);
    }
    public static void warn(String format, Object... argArray) {
        formatAndLog(LEVEL.WARN, format, argArray);
    }
    public static void warn(String msg, Throwable t) {
        log(LEVEL.WARN, msg, t);
    }
    public static void error(String msg) {
        log(LEVEL.ERROR, msg, null);
    }
    public static void error(String format, Object arg) {
        formatAndLog(LEVEL.ERROR, format, arg, null);
    }
    public static void error(String format, Object arg1, Object arg2) {
        formatAndLog(LEVEL.ERROR, format, arg1, arg2);
    }
    public static void error(String format, Object... argArray) {
        formatAndLog(LEVEL.ERROR, format, argArray);
    }
    public static void error(String msg, Throwable t) {
        log(LEVEL.ERROR, msg, t);
    }


    private static void log(LEVEL level, String message, Throwable t) {
        if (currentLevel.compareTo(level) > 0)
            return;

        StringBuilder buf = new StringBuilder(32);

        // Append date-time if so configured
        if (showTime) {
            buf.append(new Date());
            buf.append(' ');
        }

        // Append current thread name if so configured
        if (showThread) {
            buf.append('[');
            buf.append(Thread.currentThread().getName());
            buf.append("] ");
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
        PrintStream out = level.compareTo(LEVEL.WARN) >= 0 ? System.err : System.out;
        out.println(buf.toString());
        out.flush();
        if (t != null) t.printStackTrace(System.err);
    }

}
