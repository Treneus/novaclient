package com.nova.modules.movement;

import com.nova.module.Category;
import com.nova.module.Module;
import com.nova.setting.NumberSetting;
import com.nova.util.Move;
import net.minecraft.world.phys.Vec3;

public class Spider extends Module {
    private final NumberSetting speed = add(new NumberSetting("Climb Speed", 0.2, 0.05, 0.6, 0.05));

    public Spider() {
        super("Spider", "Climb walls like a spider", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        var p = mc.player;
        if (p.horizontalCollision && Move.isMoving()) {
            Vec3 v = p.getDeltaMovement();
            p.setDeltaMovement(v.x, speed.get(), v.z);
            p.resetFallDistance();
        }
    }
}
