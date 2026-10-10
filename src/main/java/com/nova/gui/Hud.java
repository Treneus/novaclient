package com.nova.gui;

import com.nova.module.Module;
import com.nova.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** Dispatches HUD rendering to every enabled module. */
public final class Hud {
    private Hud() {}

    public static void render(GuiGraphics g) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        for (Module m : ModuleManager.all()) {
            if (m.isEnabled()) m.onHudRender(g);
        }
    }
}
