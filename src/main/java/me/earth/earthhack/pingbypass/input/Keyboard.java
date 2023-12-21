package me.earth.earthhack.pingbypass.input;

import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static me.earth.earthhack.api.util.interfaces.Globals.mc;

//TODO: only use InputUtil

public class Keyboard {
    private static final Map<Integer, Boolean> STATES = new ConcurrentHashMap<>();

    public static int getKeyboardSize() {
        return GLFW.GLFW_KEY_LAST;
    }

    public static String getKeyName(int key) {

        return GLFW.glfwGetKeyName(key, 0);
        // return InputUtil.fromKeyCode(key, 0).getTranslationKey().substring(13).replace('.', ' ').toUpperCase();
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
        try {
            return InputUtil.fromTranslationKey("key.keyboard." + string.toLowerCase()).getCode();
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    public static void enableRepeatEvents(boolean enable) {
        // org.lwjgl.input.Keyboard.enableRepeatEvents(enable);
    }

    public static boolean getEventKeyState() {
        // return org.lwjgl.input.Keyboard.getEventKeyState();
        return false;
    }

    public static int getEventKey() {
        // return org.lwjgl.input.Keyboard.getEventKey();
        return -1;
    }

    public static char getEventCharacter() {
        // return org.lwjgl.input.Keyboard.getEventCharacter();
        return 'a';
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
