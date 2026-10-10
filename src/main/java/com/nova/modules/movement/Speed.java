package com.nova.modules.movement;

import com.nova.module.Category;
import com.nova.module.Module;
import com.nova.setting.ModeSetting;
import com.nova.setting.NumberSetting;
import com.nova.util.Move;
import net.minecraft.world.phys.Vec3;

public class Speed extends Module {
    private final ModeSetting mode = add(new ModeSetting("Mode", "Strafe", "Strafe", "Bhop"));
    private final NumberSetting speed = add(new NumberSetting("Speed", 1.3, 1.0, 4.0, 0.05));

    public Speed() {
        super("Speed", "Move faster", Category.MOVEMENT);
    }

    @Override
    public String info() {
        return mode.get();
    }

    @Override
    public void onTick() {
        var p = mc.player;
        if (!Move.isMoving() || p.isShiftKeyDown() || p.isInWater() || p.isInLava()) return;
        double[] d = Move.direction();
        double s = 0.2806 * speed.get();
        Vec3 v = p.getDeltaMovement();
        double y = v.y;
        if (mode.is("Bhop") && p.onGround()) y = 0.42;
        p.setDeltaMovement(d[0] * s, y, d[1] * s);
    }
}
