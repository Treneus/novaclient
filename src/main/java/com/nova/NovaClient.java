package com.nova;

import com.nova.config.ConfigManager;
import com.nova.gui.ClickGuiScreen;
import com.nova.gui.Hud;
import com.nova.module.ModuleManager;
import com.nova.util.Keys;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NovaClient implements ClientModInitializer {
    public static final String MOD_ID = "novaclient";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        ModuleManager.init();
        ConfigManager.load();
        ClientTickEvents.END_CLIENT_TICK.register(NovaClient::tick);
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigManager.save());
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(MOD_ID, "hud"), (graphics, delta) -> Hud.render(graphics));
        LOGGER.info("Nova client loaded - press Right Shift in a world to open the menu.");
    }

    private static void tick(Minecraft mc) {
        Keys.update();

        if (Keys.justPressed(GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            if (mc.screen == null && mc.player != null) {
                mc.setScreen(new ClickGuiScreen());
            } else if (mc.screen instanceof ClickGuiScreen gui && !gui.isListening()) {
                mc.setScreen(null);
            }
        }
        if (mc.screen instanceof ClickGuiScreen gui) gui.handleKeys();

        ModuleManager.tick(mc);
    }
}
