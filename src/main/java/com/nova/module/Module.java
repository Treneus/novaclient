package com.nova.module;

import com.nova.NovaClient;
import com.nova.setting.Setting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public abstract class Module {
    protected final Minecraft mc = Minecraft.getInstance();

    public final String name;
    public final String description;
    public final Category category;
    public final List<Setting> settings = new ArrayList<>();

    public int key = -1;
    /** false = settings-only entry (e.g. ClickGUI) with no on/off switch. */
    public boolean toggleable = true;
    /** Hide from the HUD module list. */
    public boolean hudHidden = false;

    // GUI animation state
    public boolean expanded;
    public float expandAnim, hoverAnim, enabledAnim;

    private boolean enabled;

    protected Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    protected <T extends Setting> T add(T setting) {
        settings.add(setting);
        return setting;
    }

    public boolean isEnabled() { return !toggleable || enabled; }
    public boolean hasSettings() { return !settings.isEmpty() || toggleable; }
    public void toggle() { setEnabled(!enabled); }

    /** Sets state without running onEnable/onDisable (config load / defaults). */
    public void loadEnabled(boolean value) {
        this.enabled = value;
        this.enabledAnim = value ? 1f : 0f;
    }

    public void setEnabled(boolean value) {
        if (!toggleable || value == enabled) return;
        enabled = value;
        try {
            if (value) onEnable(); else onDisable();
        } catch (Throwable t) {
            NovaClient.LOGGER.error("Module {} failed during {}", name, value ? "enable" : "disable", t);
        }
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}
    public void onHudRender(GuiGraphics graphics) {}

    /** Short suffix shown next to the name in the HUD list. */
    public String info() { return ""; }
}
