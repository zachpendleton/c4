package c4.util;

import c4.util.exception.UncheckedProcessException;

import java.io.IOException;
import java.lang.reflect.Field;

public class ProcessUtils {
    public static int getPid(Process process) throws UncheckedProcessException {
        try {
            Field field = process.getClass().getDeclaredField("pid");
            field.setAccessible(true);

            return field.getInt(process);
        } catch (IllegalAccessException|NoSuchFieldException e) {
            throw new UncheckedProcessException();
        }
    }

    public static void kill(Process process) {
        try {
            if (!process.isAlive()) return;
            Runtime.getRuntime().exec("kill -9 " + getPid(process));
        } catch (IOException e) {
            // ignore
        }
    }
}
