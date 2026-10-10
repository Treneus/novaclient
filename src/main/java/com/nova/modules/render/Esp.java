package com.nova.modules.render;

import com.nova.module.Category;
import com.nova.module.Module;
import com.nova.setting.BoolSetting;
import com.nova.setting.NumberSetting;
import com.nova.util.Targets;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/** Entity outlines through walls (uses the vanilla glowing outline, client side only). */
public class Esp extends Module {
    private final NumberSetting range = add(new NumberSetting("Range", 64, 8, 128, 4));
    private final BoolSetting players = add(new BoolSetting("Players", true));
    private final BoolSetting hostile = add(new BoolSetting("Hostile", true));
    private final BoolSetting passive = add(new BoolSetting("Passive", false));

    private final Set<Entity> glowing = Collections.newSetFromMap(new IdentityHashMap<>());

    public Esp() {
        super("ESP", "Outlines entities through walls", Category.RENDER);
    }

    @Override
    public void onTick() {
        Set<Entity> now = Collections.newSetFromMap(new IdentityHashMap<>());
        for (LivingEntity e : Targets.find(range.get(), players.get(), hostile.get(), passive.get())) {
            now.add(e);
            if (glowing.add(e)) e.setGlowingTag(true);
        }
        glowing.removeIf(e -> {
            if (now.contains(e)) return false;
            e.setGlowingTag(false);
            return true;
        });
    }

    @Override
    public void onDisable() {
        for (Entity e : glowing) e.setGlowingTag(false);
        glowing.clear();
    }
}
