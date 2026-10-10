package com.nova.modules.combat;

import com.nova.module.Category;
import com.nova.module.Module;
import com.nova.setting.NumberSetting;
import com.nova.util.Attr;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Reach extends Module {
    private final NumberSetting entity = add(new NumberSetting("Entity Reach", 4.0, 3.0, 6.0, 0.1));
    private final NumberSetting block = add(new NumberSetting("Block Reach", 5.0, 4.5, 8.0, 0.1));

    public Reach() {
        super("Reach", "Extends how far you can hit and build", Category.COMBAT);
    }

    @Override
    public String info() {
        return entity.display();
    }

    @Override
    public void onTick() {
        Attr.set(Attributes.ENTITY_INTERACTION_RANGE, entity.get());
        Attr.set(Attributes.BLOCK_INTERACTION_RANGE, block.get());
    }

    @Override
    public void onDisable() {
        Attr.set(Attributes.ENTITY_INTERACTION_RANGE, 3.0);
        Attr.set(Attributes.BLOCK_INTERACTION_RANGE, 4.5);
    }
}
