package com.nova.modules.movement;

import com.nova.module.Category;
import com.nova.module.Module;
import com.nova.setting.NumberSetting;
import net.minecraft.world.phys.Vec3;

public class Glide extends Module {
    private final NumberSetting fall = add(new NumberSetting("Fall Speed", 0.05, 0.01, 0.3, 0.01));

    public Glide() {
        super("Glide", "Fall slowly (pair with NoFall)", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        var p = mc.player;
        if (p.onGround() || p.isInWater() || p.isInLava() || p.getAbilities().flying) return;
        Vec3 v = p.getDeltaMovement();
        if (v.y < -fall.get()) p.setDeltaMovement(v.x, -fall.get(), v.z);
    }
}
