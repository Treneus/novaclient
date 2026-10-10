package com.nova.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/** Small immediate-mode drawing helpers (all colours are ARGB). */
public final class Gfx {
    private Gfx() {}

    public static int a(int rgb, float alpha) {
        int al = (int) (Mth.clamp(alpha, 0f, 1f) * 255f);
        return (al << 24) | (rgb & 0xFFFFFF);
    }

    /** Multiplies the alpha channel of an ARGB colour. */
    public static int mulA(int argb, float alpha) {
        int al = (int) (((argb >>> 24) & 0xFF) * Mth.clamp(alpha, 0f, 1f));
        return (al << 24) | (argb & 0xFFFFFF);
    }

    public static int lerp(int c1, int c2, float t) {
        t = Mth.clamp(t, 0f, 1f);
        int a = ch(c1, c2, 24, t), r = ch(c1, c2, 16, t), g = ch(c1, c2, 8, t), b = ch(c1, c2, 0, t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int ch(int c1, int c2, int shift, float t) {
        int x = (c1 >>> shift) & 0xFF, y = (c2 >>> shift) & 0xFF;
        return (int) (x + (y - x) * t);
    }

    public static float smooth(float cur, float target, float rate) {
        if (Math.abs(target - cur) < 0.003f) return target;
        return cur + (target - cur) * Math.min(1f, rate);
    }

    public static float ease(float t) {
        float f = 1f - Mth.clamp(t, 0f, 1f);
        return 1f - f * f * f;
    }

    public static boolean inside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    public static void rect(GuiGraphics g, int x, int y, int w, int h, int color) {
        g.fill(x, y, x + w, y + h, color);
    }

    /** Filled rounded rectangle (small radii only). */
    public static void round(GuiGraphics g, int x, int y, int w, int h, int radius, int color) {
        int r = Math.min(radius, Math.min(w, h) / 2);
        if (r <= 0) {
            g.fill(x, y, x + w, y + h, color);
            return;
        }
        g.fill(x + r, y, x + w - r, y + h, color);
        g.fill(x, y + r, x + r, y + h - r, color);
        g.fill(x + w - r, y + r, x + w, y + h - r, color);
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int inset = (int) Math.round(r - Math.sqrt(r * r - dy * dy));
            g.fill(x + inset, y + i, x + r, y + i + 1, color);
            g.fill(x + w - r, y + i, x + w - inset, y + i + 1, color);
            g.fill(x + inset, y + h - 1 - i, x + r, y + h - i, color);
            g.fill(x + w - r, y + h - 1 - i, x + w - inset, y + h - i, color);
        }
    }

    public static void text(GuiGraphics g, String s, int x, int y, int color) {
        g.drawString(Minecraft.getInstance().font, s, x, y, color, false);
    }

    public static void text(GuiGraphics g, String s, int x, int y, int color, boolean shadow) {
        g.drawString(Minecraft.getInstance().font, s, x, y, color, shadow);
    }

    public static void bold(GuiGraphics g, String s, int x, int y, int color) {
        g.drawString(Minecraft.getInstance().font, Component.literal(s).withStyle(ChatFormatting.BOLD), x, y, color, false);
    }

    public static int width(String s) {
        return Minecraft.getInstance().font.width(s);
    }
}
