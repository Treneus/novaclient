package com.nova.modules.combat;

import com.nova.module.Category;
import com.nova.module.Module;
import com.nova.setting.BoolSetting;
import com.nova.setting.NumberSetting;
import com.nova.util.Targets;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class AimAssist extends Module {
    private final NumberSetting range = add(new NumberSetting("Range", 5.5, 1.0, 8.0, 0.1));
    private final NumberSetting fov = add(new NumberSetting("FOV", 90, 10, 360, 5));
    private final NumberSetting speed = add(new NumberSetting("Speed", 0.25, 0.05, 1.0, 0.05));
    private final BoolSetting vertical = add(new BoolSetting("Vertical", true));
    private final BoolSetting onlyAttacking = add(new BoolSetting("Only While Attacking", true));
    private final BoolSetting players = add(new BoolSetting("Players", true));
    private final BoolSetting hostile = add(new BoolSetting("Hostile", true));
    private final BoolSetting passive = add(new BoolSetting("Passive", false));

    public AimAssist() {
        super("AimAssist", "Smoothly nudges your aim toward targets", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc.screen != null) return;
        if (onlyAttacking.get() && !mc.options.keyAttack.isDown()) return;
        var p = mc.player;

        LivingEntity best = null;
        float bestDiff = fov.getFloat() / 2f;
        for (LivingEntity e : Targets.find(range.get(), players.get(), hostile.get(), passive.get())) {
            float[] r = Targets.rotationsTo(e.getBoundingBox().getCenter());
            float diff = Math.abs(Mth.wrapDegrees(r[0] - p.getYRot()));
            if (diff <= bestDiff) {
                best = e;
                bestDiff = diff;
            }
        }
        if (best == null) return;

        float[] r = Targets.rotationsTo(best.getBoundingBox().getCenter());
        float s = speed.getFloat();
        p.setYRot(p.getYRot() + Mth.clamp(Mth.wrapDegrees(r[0] - p.getYRot()) * s, -12f, 12f));
        if (vertical.get()) {
            p.setXRot(Mth.clamp(p.getXRot() + Mth.clamp((r[1] - p.getXRot()) * s, -8f, 8f), -90f, 90f));
        }
    }
}
