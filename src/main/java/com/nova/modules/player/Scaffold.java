package com.nova.modules.player;

import com.nova.module.Category;
import com.nova.module.Module;
import com.nova.setting.BoolSetting;
import com.nova.setting.NumberSetting;
import com.nova.util.Move;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class Scaffold extends Module {
    private final NumberSetting delay = add(new NumberSetting("Delay (ticks)", 0, 0, 5, 1));
    private final NumberSetting expand = add(new NumberSetting("Expand", 0, 0, 4, 1));
    private final BoolSetting swing = add(new BoolSetting("Swing", true));

    private int cooldown;

    public Scaffold() {
        super("Scaffold", "Places the block in your hand under you", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (cooldown > 0) {
            cooldown--;
            return;
        }
        var p = mc.player;
        if (mc.gameMode == null || !(p.getMainHandItem().getItem() instanceof BlockItem)) return;

        double[] d = Move.direction();
        int ex = expand.getInt();
        Vec3 base = p.position().add(d[0] * ex, -1.0, d[1] * ex);
        place(BlockPos.containing(base));
    }

    private void place(BlockPos target) {
        if (!mc.level.getBlockState(target).canBeReplaced()) return;
        for (Direction dir : Direction.values()) {
            BlockPos neighbour = target.relative(dir);
            if (mc.level.getBlockState(neighbour).canBeReplaced()) continue;
            Direction face = dir.getOpposite();
            Vec3 hit = Vec3.atCenterOf(neighbour).add(face.getStepX() * 0.5, face.getStepY() * 0.5, face.getStepZ() * 0.5);
            mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, new BlockHitResult(hit, face, neighbour, false));
            if (swing.get()) mc.player.swing(InteractionHand.MAIN_HAND);
            cooldown = delay.getInt();
            return;
        }
    }
}
