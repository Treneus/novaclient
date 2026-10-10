package com.nova.modules.render;

import com.nova.module.Category;
import com.nova.module.Module;
import com.nova.modules.combat.KillAura;
import com.nova.util.Gfx;
import com.nova.util.Theme;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;

public class TargetHud extends Module {
    private float shown = -1f;

    public TargetHud() {
        super("TargetHUD", "Shows the health of your target", Category.RENDER);
        hudHidden = true;
    }

    @Override
    public void onHudRender(GuiGraphics g) {
        LivingEntity t = null;
        if (KillAura.target != null && KillAura.target.isAlive()) t = KillAura.target;
        else if (mc.hitResult instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity le) t = le;
        if (t == null) {
            shown = -1f;
            return;
        }

        float hp = t.getHealth(), max = Math.max(1f, t.getMaxHealth());
        shown = shown < 0 ? hp : shown + (hp - shown) * 0.15f;
        float ratio = Math.max(0f, Math.min(1f, shown / max));

        int w = 130, h = 34;
        int x = g.guiWidth() / 2 + 24, y = g.guiHeight() / 2 + 24;
        int accent = Theme.accent(0f);
        Gfx.round(g, x, y, w, h, 4, 0xC00E0F13);
        Gfx.round(g, x, y + 5, 2, h - 10, 1, accent);
        Gfx.text(g, t.getName().getString(), x + 9, y + 6, 0xFFFFFFFF, true);
        String hpText = String.format(Locale.ROOT, "%.1f", hp);
        Gfx.text(g, hpText, x + w - 8 - Gfx.width(hpText), y + 6, 0xFFB4B5C0, true);
        int barX = x + 9, barY = y + 21, barW = w - 18;
        Gfx.round(g, barX, barY, barW, 5, 2, 0xFF2A2B34);
        int fill = Math.max(2, Math.round(barW * ratio));
        Gfx.round(g, barX, barY, fill, 5, 2, Gfx.lerp(0xFFE5484D, 0xFF46D17A, ratio));
    }
}
