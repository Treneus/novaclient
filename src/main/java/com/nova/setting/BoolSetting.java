package com.nova.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.function.BooleanSupplier;

public class BoolSetting extends Setting {
    private boolean value;

    public BoolSetting(String name, boolean def) {
        super(name);
        this.value = def;
        this.anim = def ? 1f : 0f;
    }

    public boolean get() { return value; }
    public void set(boolean v) { value = v; }
    public void toggle() { value = !value; }

    @Override
    public BoolSetting showIf(BooleanSupplier c) {
        super.showIf(c);
        return this;
    }

    @Override
    public JsonElement save() { return new JsonPrimitive(value); }

    @Override
    public void load(JsonElement e) {
        if (e != null && e.isJsonPrimitive()) {
            value = e.getAsBoolean();
            anim = value ? 1f : 0f;
        }
    }
}
