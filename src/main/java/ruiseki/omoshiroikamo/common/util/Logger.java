package ruiseki.omoshiroikamo.common.util;

import java.util.function.Supplier;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.ParameterizedMessage;

import cpw.mods.fml.common.FMLLog;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.config.GeneralConfig;

public class Logger {

    public static String currentPhase = "UNKNOWN";

    private Logger() {}

    public static void log(Level level, String message) {
        FMLLog.log(LibMisc.MOD_NAME, level, message);
    }

    public static void log(Level level, String message, Throwable t) {
        FMLLog.log(LibMisc.MOD_NAME, level, message, t);
    }

    public static void log(Level level, String message, Object... args) {
        if (!isEnabled(level)) return;
        log(level, formatMessage(message, args));
    }

    public static void log(Level level, Supplier<String> supplier) {
        if (isEnabled(level)) {
            log(level, supplier.get());
        }
    }

    private static boolean isEnabled(Level level) {
        return level != Level.DEBUG || GeneralConfig.enableDebug;
    }

    private static String formatMessage(String msg, Object... args) {
        if (args == null || args.length == 0) {
            return msg;
        }

        if (msg.contains("{}")) {
            return new ParameterizedMessage(msg, args).getFormattedMessage();
        }

        return String.format(msg, args);
    }

    public static void info(String msg) {
        log(Level.INFO, msg);
    }

    public static void info(String msg, Object... args) {
        log(Level.INFO, msg, args);
    }

    public static void warn(String msg) {
        log(Level.WARN, msg);
    }

    public static void warn(String msg, Object... args) {
        log(Level.WARN, msg, args);
    }

    public static void error(String msg) {
        log(Level.ERROR, msg);
    }

    public static void error(String msg, Throwable t) {
        log(Level.ERROR, msg, t);
    }

    public static void error(String msg, Object... args) {
        log(Level.ERROR, msg, args);
    }

    public static void debug(String msg) {
        if (GeneralConfig.enableDebug) {
            log(Level.DEBUG, msg);
        }
    }

    public static void debug(String msg, Object... args) {
        if (GeneralConfig.enableDebug) {
            log(Level.DEBUG, msg, args);
        }
    }

    public static void debug(Supplier<String> supplier) {
        log(Level.DEBUG, supplier);
    }

    public static void setPhase(String phase) {
        currentPhase = phase;
    }

    public static String getPhase() {
        return currentPhase;
    }
}
