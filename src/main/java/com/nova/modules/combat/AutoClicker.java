package com.nova.modules.combat;

import com.nova.module.Category;
import com.nova.module.Module;
import com.nova.setting.BoolSetting;
import com.nova.setting.NumberSetting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.EntityHitResult;

public class AutoClicker extends Module {
    private final NumberSetting minCps = add(new NumberSetting("Min CPS", 8, 1, 20, 1));
    private final NumberSetting maxCps = add(new NumberSetting("Max CPS", 12, 1, 20, 1));
    private final BoolSetting holdOnly = add(new BoolSetting("Hold To Click", true));

    private long next;

    public AutoClicker() {
        super("AutoClicker", "Clicks for you while you hold attack", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc.screen != null || mc.gameMode == null) return;
        if (holdOnly.get() && !mc.options.keyAttack.isDown()) return;
        long now = System.currentTimeMillis();
        if (now < next) return;
        double lo = Math.min(minCps.get(), maxCps.get()), hi = Math.max(minCps.get(), maxCps.get());
        next = now + (long) (1000.0 / (lo + Math.random() * (hi - lo)));
        if (mc.hitResult instanceof EntityHitResult hit) mc.gameMode.attack(mc.player, hit.getEntity());
        mc.player.swing(InteractionHand.MAIN_HAND);
    }
}
