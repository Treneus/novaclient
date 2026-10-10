package com.nova.modules.movement;

import com.nova.module.Category;
import com.nova.module.Module;
import com.nova.setting.BoolSetting;
import com.nova.setting.ModeSetting;
import com.nova.setting.NumberSetting;
import com.nova.util.Move;
import net.minecraft.world.entity.player.Abilities;

public class Fly extends Module {
    private final ModeSetting mode = add(new ModeSetting("Mode", "Velocity", "Velocity", "Creative"));
    private final NumberSetting speed = add(new NumberSetting("Speed", 1.0, 0.1, 5.0, 0.1));
    private final NumberSetting vertical = add(new NumberSetting("Vertical Speed", 1.0, 0.1, 3.0, 0.1).showIf(() -> mode.is("Velocity")));
    private final BoolSetting antiKick = add(new BoolSetting("Anti-Kick", true).showIf(() -> mode.is("Velocity")));

    private int ticks;

    public Fly() {
        super("Fly", "Lets you fly", Category.MOVEMENT);
    }

    @Override
    public String info() {
        return mode.get();
    }

    @Override
    public void onTick() {
        var p = mc.player;
        if (mode.is("Creative")) {
            Abilities ab = p.getAbilities();
            ab.mayfly = true;
            ab.flying = true;
            ab.setFlyingSpeed(0.05f * speed.getFloat());
            return;
        }
        double[] d = Move.direction();
        double h = speed.get() * 0.4;
        double y = 0;
        if (mc.options.keyJump.isDown()) y += vertical.get() * 0.3;
        if (mc.options.keyShift.isDown()) y -= vertical.get() * 0.3;
        if (antiKick.get() && y == 0 && ++ticks % 20 == 0) y = -0.04;
        p.setDeltaMovement(d[0] * h, y, d[1] * h);
        p.resetFallDistance();
    }

    @Override
    public void onDisable() {
        var p = mc.player;
        if (p == null) return;
        Abilities ab = p.getAbilities();
        if (mode.is("Creative")) {
            ab.flying = false;
            ab.mayfly = p.isCreative() || p.isSpectator();
            ab.setFlyingSpeed(0.05f);
        }
    }
}
