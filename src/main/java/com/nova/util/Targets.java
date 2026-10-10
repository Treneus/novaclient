package com.nova.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class Targets {
    private Targets() {}

    public static boolean matches(LivingEntity e, boolean players, boolean hostile, boolean passive) {
        if (e instanceof ArmorStand) return false;
        if (e instanceof Player p) return players && !p.isSpectator();
        if (e instanceof Enemy) return hostile;
        if (e instanceof Mob) return passive;
        return false;
    }

    /** Valid targets in range, nearest first. */
    public static List<LivingEntity> find(double range, boolean players, boolean hostile, boolean passive) {
        Minecraft mc = Minecraft.getInstance();
        List<LivingEntity> out = new ArrayList<>();
        if (mc.player == null || mc.level == null) return out;
        for (Entity e : mc.level.entitiesForRendering()) {
            if (!(e instanceof LivingEntity le) || e == mc.player) continue;
            if (!le.isAlive() || mc.player.distanceTo(le) > range) continue;
            if (!matches(le, players, hostile, passive)) continue;
            out.add(le);
        }
        out.sort(Comparator.comparingDouble(en -> mc.player.distanceTo(en)));
        return out;
    }

    /** {yaw, pitch} needed to look at {@code target} from the player's eyes. */
    public static float[] rotationsTo(Vec3 target) {
        Vec3 eye = Minecraft.getInstance().player.getEyePosition();
        double dx = target.x - eye.x, dy = target.y - eye.y, dz = target.z - eye.z;
        double flat = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, flat));
        return new float[] {yaw, Mth.clamp(pitch, -90f, 90f)};
    }
}
