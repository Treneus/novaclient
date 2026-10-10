package com.nova.setting;

import com.google.gson.JsonElement;
import java.util.function.BooleanSupplier;

public abstract class Setting {
    public final String name;
    /** GUI animation state. */
    public float anim;
    private BooleanSupplier visible = () -> true;

    protected Setting(String name) {
        this.name = name;
    }

    public Setting showIf(BooleanSupplier condition) {
        this.visible = condition;
        return this;
    }

    public boolean isVisible() {
        return visible.getAsBoolean();
    }

    public abstract JsonElement save();

    public abstract void load(JsonElement element);
}
