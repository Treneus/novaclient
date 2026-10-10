package com.nova.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.Locale;
import java.util.function.BooleanSupplier;

public class NumberSetting extends Setting {
    public final double min, max, step;
    private double value;

    public NumberSetting(String name, double def, double min, double max, double step) {
        super(name);
        this.min = min;
        this.max = max;
        this.step = step;
        set(def);
        this.anim = (float) fraction();
    }

    public double get() { return value; }
    public float getFloat() { return (float) value; }
    public int getInt() { return (int) Math.round(value); }
    public double fraction() { return (value - min) / (max - min); }

    public void set(double v) {
        v = Math.max(min, Math.min(max, v));
        v = Math.round((v - min) / step) * step + min;
        value = Math.max(min, Math.min(max, v));
    }

    public String display() {
        if (step >= 1) return String.valueOf(Math.round(value));
        if (step >= 0.1) return String.format(Locale.ROOT, "%.1f", value);
        return String.format(Locale.ROOT, "%.2f", value);
    }

    @Override
    public NumberSetting showIf(BooleanSupplier c) {
        super.showIf(c);
        return this;
    }

    @Override
    public JsonElement save() { return new JsonPrimitive(value); }

    @Override
    public void load(JsonElement e) {
        if (e != null && e.isJsonPrimitive()) {
            set(e.getAsDouble());
            anim = (float) fraction();
        }
    }
}
