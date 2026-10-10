package com.nova.modules.misc;

import com.nova.module.Category;
import com.nova.module.Module;
import com.nova.module.ModuleManager;

public class Panic extends Module {
    public Panic() {
        super("Panic", "Instantly disables every module", Category.MISC);
        hudHidden = true;
    }

    @Override
    public void onEnable() {
        for (Module m : ModuleManager.all()) {
            if (m != this && m.toggleable) m.setEnabled(false);
        }
        setEnabled(false);
        mc.setScreen(null);
    }
}
