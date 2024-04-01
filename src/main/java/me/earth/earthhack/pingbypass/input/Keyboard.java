package me.earth.earthhack.pingbypass.input;

import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static me.earth.earthhack.api.util.interfaces.Globals.mc;

public class Keyboard {
    private static final Map<Integer, Boolean> STATES = new ConcurrentHashMap<>();

    public static int getKeyboardSize() {
        return GLFW.GLFW_KEY_LAST;
    }

    public static String getKeyName(int key) {
        return InputUtil.fromKeyCode(key, 0).getTranslationKey().substring(13).replace('.', ' ').toUpperCase();
    }

    public static int getRControl() {
        return InputUtil.GLFW_KEY_RIGHT_CONTROL;
    }

    public static int getLControl() {
        return InputUtil.GLFW_KEY_LEFT_CONTROL;
    }

    public static int getLMenu() {
        return GLFW.GLFW_KEY_MENU;
    }

    public static int getEscape() {
        return InputUtil.GLFW_KEY_ESCAPE;
    }

    public static int getSpace() {
        return InputUtil.GLFW_KEY_SPACE;
    }

    public static int getDelete() {
        return InputUtil.GLFW_KEY_DELETE;
    }

    public static int getNone() {
        return InputUtil.UNKNOWN_KEY.getCode();
    }

    public static int getKeyV() {
        return InputUtil.GLFW_KEY_V;
    }

    public static int getKeyM() {
        return InputUtil.GLFW_KEY_M;
    }

    public static int getKeyIndex(String string) {
        string = string.replace(" ", ".").toLowerCase();
        switch (string) {
            case "+":
                return 334;
            case ".":
                return 46;
            case "=":
                return 61;
            case "*":
                return 332;
            case "-":
                return 47;
            case "'":
                return 39;
            case ",":
                return 44;
            case "rshift":
                return 344;
            case "shift":
                return 340;
            case "rctrl":
                return 345;
            case "ctrl":
                return 341;
            case "alt":
                return 342;
            case "ralt":
                return 346;
        }
        try {
            System.out.println("key.keyboard." + string + " Code: " + InputUtil.fromTranslationKey("key.keyboard." + string).getCode());
            return InputUtil.fromTranslationKey("key.keyboard." + string).getCode();
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    public static void enableRepeatEvents(boolean enable) {
        // org.lwjgl.input.Keyboard.enableRepeatEvents(enable);
    }

    public static boolean isKeyDown(int code) {
        /*
        if (PingBypass.isConnected()) {
            return STATES.getOrDefault(code, false);
        }
         */

        return InputUtil.isKeyPressed(mc.getWindow().getHandle(), code);
    }

}
