package com.nova.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.function.BooleanSupplier;

public class ModeSetting extends Setting {
    public final String[] modes;
    private int index;

    public ModeSetting(String name, String def, String... modes) {
        super(name);
        this.modes = modes;
        for (int i = 0; i < modes.length; i++) if (modes[i].equals(def)) index = i;
    }

    public String get() { return modes[index]; }
    public boolean is(String mode) { return modes[index].equals(mode); }
    public void next() { index = (index + 1) % modes.length; }
    public void prev() { index = (index - 1 + modes.length) % modes.length; }

    @Override
    public ModeSetting showIf(BooleanSupplier c) {
        super.showIf(c);
        return this;
    }

    @Override
    public JsonElement save() { return new JsonPrimitive(modes[index]); }

    @Override
    public void load(JsonElement e) {
        if (e == null || !e.isJsonPrimitive()) return;
        String s = e.getAsString();
        for (int i = 0; i < modes.length; i++) if (modes[i].equals(s)) index = i;
    }
}
