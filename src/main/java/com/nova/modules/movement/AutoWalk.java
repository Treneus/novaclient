package com.nova.modules.movement;

import com.nova.module.Category;
import com.nova.module.Module;

public class AutoWalk extends Module {
    public AutoWalk() {
        super("AutoWalk", "Holds the forward key for you", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        mc.options.keyUp.setDown(true);
    }

    @Override
    public void onDisable() {
        mc.options.keyUp.setDown(false);
    }
}
