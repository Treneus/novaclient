package com.nova.util;

import com.nova.modules.client.ClickGuiModule;

public final class Theme {
    public static ClickGuiModule cfg;

    private Theme() {}

    /** Accent colour; {@code offset} shifts the hue so lists can fade through a gradient. */
    public static int accent(float offset) {
        float h = 0.72f;
        if (cfg != null) {
            h = cfg.hue.getFloat() / 360f;
            if (cfg.rainbow.get()) {
                h = (System.currentTimeMillis() % 100000L) / 10000f * cfg.rainbowSpeed.getFloat();
            }
        }
        return 0xFF000000 | hsv(h + offset, 0.55f, 1f);
    }

    public static int hsv(float h, float s, float v) {
        h = h - (float) Math.floor(h);
        float c = v * s;
        float x = c * (1f - Math.abs((h * 6f) % 2f - 1f));
        float m = v - c;
        float r, g, b;
        switch ((int) (h * 6f) % 6) {
            case 0 -> { r = c; g = x; b = 0; }
            case 1 -> { r = x; g = c; b = 0; }
            case 2 -> { r = 0; g = c; b = x; }
            case 3 -> { r = 0; g = x; b = c; }
            case 4 -> { r = x; g = 0; b = c; }
            default -> { r = c; g = 0; b = x; }
        }
        return ((int) ((r + m) * 255f) << 16) | ((int) ((g + m) * 255f) << 8) | (int) ((b + m) * 255f);
    }
}
