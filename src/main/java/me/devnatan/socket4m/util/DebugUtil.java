package me.devnatan.socket4m.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DebugUtil {

    public static void debug(String str, Object... params) {
        System.out.println("[" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "] " + String.format(str, params));
    }

    public static void err(String str, Object... params) {
        System.err.println("[" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "] " + String.format(str, params));
    }

}
