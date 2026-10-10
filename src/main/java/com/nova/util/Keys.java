package com.nova.util;

import java.util.Locale;
import org.lwjgl.glfw.GLFW;

/** Polls raw GLFW input so we never depend on Minecraft's (frequently changing) input classes. */
public final class Keys {
    private static final boolean[] CUR = new boolean[GLFW.GLFW_KEY_LAST + 1];
    private static final boolean[] PREV = new boolean[GLFW.GLFW_KEY_LAST + 1];

    private Keys() {}

    /** Call once per client tick. */
    public static void update() {
        System.arraycopy(CUR, 0, PREV, 0, CUR.length);
        long window = GLFW.glfwGetCurrentContext();
        for (int k = GLFW.GLFW_KEY_SPACE; k <= GLFW.GLFW_KEY_LAST; k++) {
            CUR[k] = window != 0L && GLFW.glfwGetKey(window, k) == GLFW.GLFW_PRESS;
        }
    }

    public static boolean isDown(int key) {
        return key >= 0 && key < CUR.length && CUR[key];
    }

    public static boolean justPressed(int key) {
        return key >= 0 && key < CUR.length && CUR[key] && !PREV[key];
    }

    public static boolean mouseDown(int button) {
        long w = GLFW.glfwGetCurrentContext();
        return w != 0L && GLFW.glfwGetMouseButton(w, button) == GLFW.GLFW_PRESS;
    }

    public static String name(int key) {
        if (key < 0) return "None";
        if (key >= GLFW.GLFW_KEY_F1 && key <= GLFW.GLFW_KEY_F25) return "F" + (key - GLFW.GLFW_KEY_F1 + 1);
        String n = GLFW.glfwGetKeyName(key, 0);
        if (n != null) return n.toUpperCase(Locale.ROOT);
        return switch (key) {
            case GLFW.GLFW_KEY_SPACE -> "SPACE";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "LSHIFT";
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "RSHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "LCTRL";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "RCTRL";
            case GLFW.GLFW_KEY_LEFT_ALT -> "LALT";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "RALT";
            case GLFW.GLFW_KEY_TAB -> "TAB";
            case GLFW.GLFW_KEY_ENTER -> "ENTER";
            case GLFW.GLFW_KEY_BACKSPACE -> "BACKSPACE";
            case GLFW.GLFW_KEY_INSERT -> "INSERT";
            case GLFW.GLFW_KEY_DELETE -> "DELETE";
            case GLFW.GLFW_KEY_HOME -> "HOME";
            case GLFW.GLFW_KEY_END -> "END";
            case GLFW.GLFW_KEY_PAGE_UP -> "PGUP";
            case GLFW.GLFW_KEY_PAGE_DOWN -> "PGDN";
            case GLFW.GLFW_KEY_UP -> "UP";
            case GLFW.GLFW_KEY_DOWN -> "DOWN";
            case GLFW.GLFW_KEY_LEFT -> "LEFT";
            case GLFW.GLFW_KEY_RIGHT -> "RIGHT";
            case GLFW.GLFW_KEY_CAPS_LOCK -> "CAPS";
            default -> "KEY " + key;
        };
    }
}
