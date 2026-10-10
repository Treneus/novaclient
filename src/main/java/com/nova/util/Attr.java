package com.nova.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public final class Attr {
    private Attr() {}

    public static void set(Holder<Attribute> attribute, double value) {
        var p = Minecraft.getInstance().player;
        if (p == null) return;
        AttributeInstance inst = p.getAttribute(attribute);
        if (inst != null && inst.getBaseValue() != value) inst.setBaseValue(value);
    }
}
