package me.earth.earthhack.impl.util.network;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IClientPlayerEntity;
import me.earth.earthhack.impl.core.ducks.entity.ILivingEntity;

// TODO: fix this
public class PhysicsUtil implements Globals
{
    public static void runPhysicsTick()
    {
        int lastSwing = ((ILivingEntity) mc.player)
                .earthhack$getTicksSinceLastSwing();
        int useCount  = ((ILivingEntity) mc.player)
                .earthhack$getActiveItemStackUseCount();

        int hurtTime              = mc.player.hurtTime;
        float prevSwingProgress   = mc.player.lastHandSwingProgress;
        float swingProgress       = mc.player.handSwingProgress;
        int swingProgressInt      = mc.player.handSwingTicks;
        boolean isSwingInProgress = mc.player.handSwinging;
        float rotationYaw         = mc.player.yaw;
        float prevRotationYaw     = mc.player.prevYaw;
        float renderYawOffset     = mc.player.renderYaw;
        float prevRenderYawOffset = mc.player.lastRenderYaw;
        float rotationYawHead     = mc.player.headYaw;
        float prevRotationYawHead = mc.player.prevHeadYaw;
        float cameraYaw           = mc.player.renderYaw;
        float prevCameraYaw       = mc.player.lastRenderYaw;
        // float renderArmYaw        = mc.player.renderArmYaw;
        // float prevRenderArmYaw    = mc.player.prevRenderArmYaw;
        // float renderArmPitch      = mc.player.renderArmPitch;
        // float prevRenderArmPitch  = mc.player.prevRenderArmPitch;
        float walk                = mc.player.distanceTraveled;
        // float prevWalk            = mc.player.prevDistanceWalkedModified;
        // double chasingPosX        = mc.player.chasingPosX;
        // double prevChasingPosX    = mc.player.prevChasingPosX;
        // double chasingPosY        = mc.player.chasingPosY;
        // double prevChasingPosY    = mc.player.prevChasingPosY;
        // double chasingPosZ        = mc.player.chasingPosZ;
        // double prevChasingPosZ    = mc.player.prevChasingPosZ;
        // float limbSwingAmount     = mc.player.limbAnimator.swin;
        // float prevLimbSwingAmount = mc.player.prevLimbSwingAmount;
        float limbSwing           = mc.player.limbAnimator.getSpeed();

        ((IClientPlayerEntity) mc.player).earthhack$superUpdate();

        ((ILivingEntity) mc.player)
                .earthhack$setTicksSinceLastSwing(lastSwing);
        ((ILivingEntity) mc.player)
                .earthhack$setActiveItemStackUseCount(useCount);

        mc.player.hurtTime                      = hurtTime;
        mc.player.lastHandSwingProgress         = prevSwingProgress;
        mc.player.handSwingProgress             = swingProgress;
        mc.player.handSwingTicks                = swingProgressInt;
        mc.player.handSwinging                  = isSwingInProgress;
        mc.player.yaw                           = rotationYaw;
        mc.player.prevYaw                       = prevRotationYaw;
        // mc.player.renderYaw                  = renderYawOffset;
        // mc.player.lastRenderYaw              = prevRenderYawOffset;
        mc.player.headYaw                       = rotationYawHead;
        mc.player.prevHeadYaw                   = prevRotationYawHead;
        mc.player.renderYaw                     = cameraYaw;
        mc.player.lastRenderYaw                 = prevCameraYaw;
        // mc.player.renderArmYaw               = renderArmYaw;
        // mc.player.prevRenderArmYaw           = prevRenderArmYaw;
        // mc.player.renderArmPitch             = renderArmPitch;
        // mc.player.prevRenderArmPitch         = prevRenderArmPitch;
        mc.player.distanceTraveled              = walk;
        // mc.player.prevDistanceWalkedModified = prevWalk;
        // mc.player.chasingPosX                = chasingPosX;
        // mc.player.prevChasingPosX            = prevChasingPosX;
        // mc.player.chasingPosY                = chasingPosY;
        // mc.player.prevChasingPosY            = prevChasingPosY;
        // mc.player.chasingPosZ                = chasingPosZ;
        // mc.player.prevChasingPosZ            = prevChasingPosZ;
        // mc.player.limbSwingAmount            = limbSwingAmount;
        // mc.player.prevLimbSwingAmount        = prevLimbSwingAmount;
        // mc.player.limbSwing                  = limbSwing;

        ((IClientPlayerEntity) mc.player).earthhack$invokeSendMovementPackets();
    }

}
