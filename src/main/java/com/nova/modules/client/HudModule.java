package com.nova.modules.client;

import com.nova.module.Category;
import com.nova.module.Module;
import com.nova.module.ModuleManager;
import com.nova.setting.BoolSetting;
import com.nova.setting.ModeSetting;
import com.nova.util.Gfx;
import com.nova.util.Theme;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class HudModule extends Module {
    private final BoolSetting watermark = add(new BoolSetting("Watermark", true));
    private final BoolSetting list = add(new BoolSetting("Module List", true));
    private final ModeSetting side = add(new ModeSetting("List Side", "Right", "Right", "Left").showIf(list::get));
    private final BoolSetting background = add(new BoolSetting("List Background", true).showIf(list::get));
    private final BoolSetting info = add(new BoolSetting("Module Info", true).showIf(list::get));
    private final BoolSetting coords = add(new BoolSetting("Coordinates", true));
    private final BoolSetting shadow = add(new BoolSetting("Text Shadow", true));

    public HudModule() {
        super("HUD", "On-screen overlay", Category.CLIENT);
        hudHidden = true;
        loadEnabled(true);
    }

    @Override
    public void onHudRender(GuiGraphics g) {
        Font f = mc.font;
        int sw = g.guiWidth(), sh = g.guiHeight();
        boolean sh2 = shadow.get();

        if (watermark.get()) {
            String name = "Nova";
            String rest = " | " + mc.getFps() + " fps";
            int w = f.width(name) + f.width(rest) + 14;
            Gfx.round(g, 4, 4, w, 14, 3, 0xB00E0F13);
            Gfx.round(g, 4, 6, 2, 10, 1, Theme.accent(0f));
            Gfx.text(g, name, 10, 8, Theme.accent(0f), sh2);
            Gfx.text(g, rest, 10 + f.width(name), 8, 0xFFB4B5C0, sh2);
        }

        if (list.get()) {
            boolean right = side.is("Right");
            List<Module> mods = new ArrayList<>();
            for (Module m : ModuleManager.all()) {
                if (m.isEnabled() && m.toggleable && !m.hudHidden) mods.add(m);
            }
            mods.sort(Comparator.comparingInt((Module m) -> -f.width(m.name + " " + (info.get() ? m.info() : ""))));
            int rowH = f.lineHeight + 3;
            int y = 3 + (!right && watermark.get() ? 20 : 0);
            int i = 0;
            for (Module m : mods) {
                String inf = info.get() ? m.info() : "";
                String suffix = inf.isEmpty() ? "" : " " + inf;
                int w = f.width(m.name) + f.width(suffix) + 9;
                int x = right ? sw - w - 3 : 3;
                int col = Theme.accent(i * 0.04f);
                if (background.get()) Gfx.rect(g, x, y, w, rowH, 0x900E0F13);
                Gfx.rect(g, right ? x + w - 2 : x, y, 2, rowH, col);
                int tx = right ? x + 3 : x + 5;
                Gfx.text(g, m.name, tx, y + 2, col, sh2);
                if (!suffix.isEmpty()) Gfx.text(g, suffix, tx + f.width(m.name), y + 2, 0xFFA0A1AC, sh2);
                y += rowH;
                i++;
            }
        }

        if (coords.get()) {
            String s = String.format(Locale.ROOT, "XYZ  %.1f  %.1f  %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
            Gfx.text(g, s, 4, sh - 12, 0xFFD0D1DA, sh2);
        }
    }
}
