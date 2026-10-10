package com.nova.module;

import com.nova.NovaClient;
import com.nova.modules.client.ClickGuiModule;
import com.nova.modules.client.HudModule;
import com.nova.modules.combat.*;
import com.nova.modules.misc.*;
import com.nova.modules.movement.*;
import com.nova.modules.player.*;
import com.nova.modules.render.*;
import com.nova.util.Keys;
import com.nova.util.Theme;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;

public final class ModuleManager {
    private static final List<Module> ALL = new ArrayList<>();
    private static final Map<Category, List<Module>> BY_CATEGORY = new EnumMap<>(Category.class);

    private ModuleManager() {}

    public static void init() {
        Theme.cfg = new ClickGuiModule();
        register(
            // Combat
            new KillAura(), new AimAssist(), new TriggerBot(), new AutoClicker(), new Reach(), new AutoTotem(),
            // Movement
            new Sprint(), new Fly(), new Speed(), new Step(), new NoFall(), new Spider(), new Glide(), new AutoWalk(),
            // Player
            new Scaffold(), new AutoRespawn(), new Sneak(),
            // Render
            new Fullbright(), new Esp(), new TargetHud(),
            // Misc
            new AntiAfk(), new Panic(),
            // Client
            Theme.cfg, new HudModule());
    }

    private static void register(Module... modules) {
        for (Module m : modules) {
            ALL.add(m);
            BY_CATEGORY.computeIfAbsent(m.category, k -> new ArrayList<>()).add(m);
        }
    }

    public static List<Module> all() { return ALL; }

    public static List<Module> byCategory(Category c) { return BY_CATEGORY.getOrDefault(c, List.of()); }

    public static void tick(Minecraft mc) {
        if (mc.screen == null) {
            for (Module m : ALL) {
                if (m.toggleable && m.key > 0 && Keys.justPressed(m.key)) m.toggle();
            }
        }
        if (mc.player == null || mc.level == null) return;
        for (Module m : ALL) {
            if (!m.isEnabled()) continue;
            try {
                m.onTick();
            } catch (Throwable t) {
                NovaClient.LOGGER.error("Module {} crashed and was disabled", m.name, t);
                m.setEnabled(false);
            }
        }
    }
}
