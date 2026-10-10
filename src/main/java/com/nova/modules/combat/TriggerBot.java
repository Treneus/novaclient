package com.nova.modules.combat;

import com.nova.module.Category;
import com.nova.module.Module;
import com.nova.setting.BoolSetting;
import com.nova.setting.NumberSetting;
import com.nova.util.Targets;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;

public class TriggerBot extends Module {
    private final NumberSetting charge = add(new NumberSetting("Min Charge", 0.95, 0.5, 1.0, 0.05));
    private final BoolSetting players = add(new BoolSetting("Players", true));
    private final BoolSetting hostile = add(new BoolSetting("Hostile", true));
    private final BoolSetting passive = add(new BoolSetting("Passive", false));

    public TriggerBot() {
        super("TriggerBot", "Attacks whatever your crosshair is on", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc.screen != null || mc.gameMode == null) return;
        if (!(mc.hitResult instanceof EntityHitResult hit)) return;
        if (!(hit.getEntity() instanceof LivingEntity le)) return;
        if (!Targets.matches(le, players.get(), hostile.get(), passive.get())) return;
        if (mc.player.getAttackStrengthScale(0.5f) < charge.getFloat()) return;
        mc.gameMode.attack(mc.player, le);
        mc.player.swing(InteractionHand.MAIN_HAND);
    }
}
