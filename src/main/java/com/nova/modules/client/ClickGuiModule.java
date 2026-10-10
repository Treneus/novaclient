package com.nova.modules.client;

import com.nova.module.Category;
import com.nova.module.Module;
import com.nova.setting.BoolSetting;
import com.nova.setting.NumberSetting;

/** Settings-only entry that controls how the menu looks. */
public class ClickGuiModule extends Module {
    public final NumberSetting hue = add(new NumberSetting("Accent Hue", 258, 0, 360, 1));
    public final BoolSetting rainbow = add(new BoolSetting("Rainbow", false));
    public final NumberSetting rainbowSpeed = add(new NumberSetting("Rainbow Speed", 0.6, 0.1, 3.0, 0.1).showIf(rainbow::get));
    public final NumberSetting opacity = add(new NumberSetting("Opacity", 0.94, 0.5, 1.0, 0.02));
    public final BoolSetting dim = add(new BoolSetting("Dim Background", true));
    public final BoolSetting tooltips = add(new BoolSetting("Descriptions", true));

    public ClickGuiModule() {
        super("ClickGUI", "Appearance of this menu", Category.CLIENT);
        toggleable = false;
    }
}
