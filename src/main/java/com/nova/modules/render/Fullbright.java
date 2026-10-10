package com.nova.modules.render;

import com.nova.module.Category;
import com.nova.module.Module;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class Fullbright extends Module {
    public Fullbright() {
        super("Fullbright", "See clearly in the dark", Category.RENDER);
    }

    @Override
    public void onTick() {
        var p = mc.player;
        MobEffectInstance cur = p.getEffect(MobEffects.NIGHT_VISION);
        if (cur == null || cur.getDuration() < 20000) {
            p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 100000, 0, false, false));
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null) mc.player.removeEffect(MobEffects.NIGHT_VISION);
    }
}
