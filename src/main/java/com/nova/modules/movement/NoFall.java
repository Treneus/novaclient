package com.nova.modules.movement;

import com.nova.module.Category;
import com.nova.module.Module;
import com.nova.setting.NumberSetting;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public class NoFall extends Module {
    private final NumberSetting distance = add(new NumberSetting("Min Fall Distance", 2.5, 1.0, 10.0, 0.5));

    public NoFall() {
        super("NoFall", "Cancels fall damage", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        var p = mc.player;
        if (mc.getConnection() == null || p.isFallFlying()) return;
        if (p.fallDistance > distance.get()) {
            mc.getConnection().send(new ServerboundMovePlayerPacket.StatusOnly(true, p.horizontalCollision));
        }
    }
}
