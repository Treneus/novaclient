package com.nova.modules.misc;

import com.nova.module.Category;
import com.nova.module.Module;
import com.nova.setting.BoolSetting;
import com.nova.setting.NumberSetting;
import net.minecraft.world.InteractionHand;

public class AntiAfk extends Module {
    private final NumberSetting interval = add(new NumberSetting("Interval (s)", 20, 5, 120, 1));
    private final BoolSetting rotate = add(new BoolSetting("Look Around", true));

    private long next;

    public AntiAfk() {
        super("AntiAFK", "Stops you being kicked for idling", Category.MISC);
    }

    @Override
    public void onTick() {
        long now = System.currentTimeMillis();
        if (now < next) return;
        next = now + (long) (interval.get() * 1000.0 * (0.8 + Math.random() * 0.4));
        mc.player.swing(InteractionHand.MAIN_HAND);
        if (rotate.get()) mc.player.setYRot(mc.player.getYRot() + (float) ((Math.random() - 0.5) * 24.0));
    }
}
