package com.nova.modules.player;

import com.nova.module.Category;
import com.nova.module.Module;
import com.nova.setting.NumberSetting;

public class AutoRespawn extends Module {
    private final NumberSetting delay = add(new NumberSetting("Delay (ticks)", 5, 0, 40, 1));

    private int timer;

    public AutoRespawn() {
        super("AutoRespawn", "Respawns automatically after dying", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (!mc.player.isDeadOrDying()) {
            timer = 0;
            return;
        }
        if (++timer >= delay.getInt()) {
            mc.player.respawn();
            timer = 0;
        }
    }
}
