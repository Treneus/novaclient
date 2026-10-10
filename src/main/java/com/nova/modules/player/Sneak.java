package com.nova.modules.player;

import com.nova.module.Category;
import com.nova.module.Module;

public class Sneak extends Module {
    public Sneak() {
        super("Sneak", "Always sneak", Category.PLAYER);
    }

    @Override
    public void onTick() {
        mc.options.keyShift.setDown(true);
    }

    @Override
    public void onDisable() {
        mc.options.keyShift.setDown(false);
    }
}
