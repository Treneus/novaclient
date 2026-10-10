package com.nova.modules.combat;

import com.nova.module.Category;
import com.nova.module.Module;
import com.nova.setting.NumberSetting;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Items;

public class AutoTotem extends Module {
    private final NumberSetting health = add(new NumberSetting("Health", 20, 1, 20, 1));
    private final NumberSetting delay = add(new NumberSetting("Delay (ticks)", 1, 0, 10, 1));

    private int timer;

    public AutoTotem() {
        super("AutoTotem", "Keeps a Totem of Undying in your offhand", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (timer > 0) {
            timer--;
            return;
        }
        var p = mc.player;
        if (mc.gameMode == null || p.getHealth() > health.getFloat()) return;
        if (p.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) return;
        for (int i = 0; i < 36; i++) {
            if (!p.getInventory().getItem(i).is(Items.TOTEM_OF_UNDYING)) continue;
            int slot = i < 9 ? i + 36 : i; // hotbar slots live at 36..44 in the player container
            mc.gameMode.handleInventoryMouseClick(p.inventoryMenu.containerId, slot, 40, ClickType.SWAP, p);
            timer = delay.getInt();
            return;
        }
    }
}
