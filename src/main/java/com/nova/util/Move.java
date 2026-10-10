package com.nova.util;

import net.minecraft.client.Minecraft;

public final class Move {
    private Move() {}

    public static boolean isMoving() {
        var o = Minecraft.getInstance().options;
        return o.keyUp.isDown() || o.keyDown.isDown() || o.keyLeft.isDown() || o.keyRight.isDown();
    }

    /** Normalised world-space {x, z} direction of the current WASD input, or {0, 0}. */
    public static double[] direction() {
        Minecraft mc = Minecraft.getInstance();
        var o = mc.options;
        double f = (o.keyUp.isDown() ? 1 : 0) - (o.keyDown.isDown() ? 1 : 0);
        double s = (o.keyLeft.isDown() ? 1 : 0) - (o.keyRight.isDown() ? 1 : 0);
        if (f == 0 && s == 0) return new double[] {0, 0};
        double len = Math.sqrt(f * f + s * s);
        f /= len;
        s /= len;
        double yaw = Math.toRadians(mc.player.getYRot());
        double sin = Math.sin(yaw), cos = Math.cos(yaw);
        return new double[] {-sin * f + cos * s, cos * f + sin * s};
    }
}
