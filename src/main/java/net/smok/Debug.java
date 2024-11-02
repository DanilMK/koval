package net.smok;

import net.smok.koval.Koval;

public class Debug {

    public static void log(String info) {
        Koval.LOGGER.info(info);
    }
    public static void err(String info) {
        Koval.LOGGER.error(info);
    }
    public static void warn(String info) {
        Koval.LOGGER.warn(info);
    }
}
