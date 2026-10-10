package com.nova.modules.combat;

import com.nova.module.Category;
import com.nova.module.Module;
import com.nova.setting.BoolSetting;
import com.nova.setting.ModeSetting;
import com.nova.setting.NumberSetting;
import com.nova.util.Targets;
import java.util.Comparator;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class KillAura extends Module {
    /** Current target (read by TargetHUD). */
    public static LivingEntity target;

    private final NumberSetting range = add(new NumberSetting("Range", 4.2, 2.0, 6.0, 0.1));
    private final ModeSetting timing = add(new ModeSetting("Timing", "Cooldown", "Cooldown", "CPS"));
    private final NumberSetting cps = add(new NumberSetting("CPS", 12, 1, 20, 1).showIf(() -> timing.is("CPS")));
    private final ModeSetting rotation = add(new ModeSetting("Rotation", "Smooth", "None", "Snap", "Smooth"));
    private final NumberSetting smooth = add(new NumberSetting("Smoothness", 0.35, 0.05, 1.0, 0.05).showIf(() -> rotation.is("Smooth")));
    private final ModeSetting priority = add(new ModeSetting("Priority", "Distance", "Distance", "Health"));
    private final BoolSetting players = add(new BoolSetting("Players", true));
    private final BoolSetting hostile = add(new BoolSetting("Hostile", true));
    private final BoolSetting passive = add(new BoolSetting("Passive", false));
    private final BoolSetting swing = add(new BoolSetting("Swing", true));

    private long next;

    public KillAura() {
        super("KillAura", "Automatically attacks nearby targets", Category.COMBAT);
    }

    @Override
    public void onDisable() {
        target = null;
    }

    @Override
    public String info() {
        return range.display();
    }

    @Override
    public void onTick() {
        if (mc.gameMode == null) return;
        List<LivingEntity> list = Targets.find(range.get(), players.get(), hostile.get(), passive.get());
        if (list.isEmpty()) {
            target = null;
            return;
        }
        if (priority.is("Health")) list.sort(Comparator.comparingDouble(LivingEntity::getHealth));
        target = list.get(0);

        if (!rotation.is("None")) {
            Vec3 aim = target.getBoundingBox().getCenter();
            float[] r = Targets.rotationsTo(aim);
            var p = mc.player;
            if (rotation.is("Snap")) {
                p.setYRot(r[0]);
                p.setXRot(r[1]);
            } else {
                float f = smooth.getFloat();
                p.setYRot(p.getYRot() + Mth.wrapDegrees(r[0] - p.getYRot()) * f);
                p.setXRot(Mth.clamp(p.getXRot() + (r[1] - p.getXRot()) * f, -90f, 90f));
            }
        }

        if (!ready()) return;
        mc.gameMode.attack(mc.player, target);
        if (swing.get()) mc.player.swing(InteractionHand.MAIN_HAND);
    }

    private boolean ready() {
        if (timing.is("Cooldown")) return mc.player.getAttackStrengthScale(0.5f) >= 1.0f;
        long now = System.currentTimeMillis();
        if (now < next) return false;
        next = now + (long) (1000.0 / cps.get() * (0.85 + Math.random() * 0.3));
        return true;
    }
}
