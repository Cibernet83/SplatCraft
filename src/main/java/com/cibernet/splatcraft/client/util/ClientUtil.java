package com.cibernet.splatcraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientUtil {
    /*public static boolean canPerformRoll(PlayerEntity entity) {
        Input input = ((ClientPlayerEntity)entity).input;
        return !PlayerDataComponent.Cooldown.hasCooldown(entity) && input.jumping && (input.movementSideways != 0 || input.movementForward != 0);
    }

    public static Vec3d getDodgeRollVector(PlayerEntity entity) {
        Input input = ((ClientPlayerEntity)entity).input;
        return new Vec3d(input.movementSideways, -0.4f, input.movementForward);
    }*/
}