package me.earth.earthhack.pingbypass.input;

import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO: finish this
public class Keyboard {
    private static final Map<Integer, Boolean> STATES = new ConcurrentHashMap<>();

    public static int getKeyboardSize() {
        return GLFW.GLFW_KEY_LAST;
    }

    public static String getKeyName(int key) {
        // TODO: -1 check?
        return GLFW.glfwGetKeyName(key, org.lwjgl.glfw.GLFW.glfwGetKeyScancode(key));
    }

    public static int getRControl() {
        return GLFW.GLFW_KEY_RIGHT_CONTROL;
    }

    public static int getLControl() {
        return GLFW.GLFW_KEY_LEFT_CONTROL;
    }

    public static int getLMenu() {
        return GLFW.GLFW_KEY_MENU;
    }

    public static int getEscape() {
        return GLFW.GLFW_KEY_ESCAPE;
    }

    public static int getSpace() {
        return GLFW.GLFW_KEY_SPACE;
    }

    public static int getDelete() {
        return GLFW.GLFW_KEY_DELETE;
    }

    public static int getNone() {
        return GLFW.GLFW_KEY_UNKNOWN;
    }

    public static int getKeyV() {
        return GLFW.GLFW_KEY_V;
    }

    public static int getKeyM() {
        return GLFW.GLFW_KEY_M;
    }

    @Deprecated
    public static void enableRepeatEvents(boolean enable) {
        // org.lwjgl.input.Keyboard.enableRepeatEvents(enable);
    }

    @Deprecated
    public static boolean getEventKeyState() {
        // return org.lwjgl.input.Keyboard.getEventKeyState();
        return false;
    }

    @Deprecated
    public static int getEventKey() {
        // return org.lwjgl.input.Keyboard.getEventKey();
        return -1;
    }

    @Deprecated
    public static char getEventCharacter() {
        // return org.lwjgl.input.Keyboard.getEventCharacter();
        return 'a';
    }

    @Deprecated
    public static int getKeyIndex(String string) {
        // return org.lwjgl.input.Keyboard.getKeyIndex(string);
        return -1;
    }

    @Deprecated
    public static boolean isKeyDown(int code) {
        /*
        if (PingBypass.isConnected()) {
            return STATES.getOrDefault(code, false);
        }
        */

        // return org.lwjgl.input.Keyboard.isKeyDown(code);
        return GLFW.glfwGetKeyScancode(code) != -1; // won't work.
    }
}