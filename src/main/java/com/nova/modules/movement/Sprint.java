package com.nova.modules.movement;

import com.nova.module.Category;
import com.nova.module.Module;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", "Always sprint while moving forward", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc.options.keyUp.isDown() && !mc.player.isShiftKeyDown()) mc.options.keySprint.setDown(true);
    }

    @Override
    public void onDisable() {
        mc.options.keySprint.setDown(false);
    }
}
