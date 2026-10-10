package com.nova.modules.movement;

import com.nova.module.Category;
import com.nova.module.Module;
import com.nova.setting.NumberSetting;
import com.nova.util.Attr;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Step extends Module {
    private final NumberSetting height = add(new NumberSetting("Height", 1.0, 0.6, 2.5, 0.1));

    public Step() {
        super("Step", "Walk up blocks without jumping", Category.MOVEMENT);
    }

    @Override
    public String info() {
        return height.display();
    }

    @Override
    public void onTick() {
        Attr.set(Attributes.STEP_HEIGHT, height.get());
    }

    @Override
    public void onDisable() {
        Attr.set(Attributes.STEP_HEIGHT, 0.6);
    }
}
