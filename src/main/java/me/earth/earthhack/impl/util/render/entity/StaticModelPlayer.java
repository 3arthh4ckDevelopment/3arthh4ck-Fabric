package me.earth.earthhack.impl.util.render.entity;

import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class StaticModelPlayer<T extends PlayerEntity> extends PlayerEntityModel<T> implements Globals {
    private final T player;
    private float limbSwing;
    private float limbSwingAmount;
    private float yaw;
    private float yawHead;
    private float pitch;

    public StaticModelPlayer(T playerIn, boolean smallArms, ModelPart modelSize) {
        super(modelSize, smallArms);
        this.player = playerIn;
        this.limbSwing = player.limbAnimator.getPos();
        this.limbSwingAmount = player.limbAnimator.getSpeed();
        this.yaw = player.getBodyYaw();
        this.yawHead = player.getHeadYaw();
        this.pitch = player.getPitch();
        this.sneaking = player.isSneaking();
        this.rightArmPose = getArmPose(player, player.getActiveHand() == Hand.MAIN_HAND ? player.getMainHandStack() : player.getOffHandStack());
        this.leftArmPose = getArmPose(player, player.getActiveHand() == Hand.MAIN_HAND ? player.getOffHandStack() : player.getMainHandStack());
        this.handSwingProgress = player.handSwingProgress;
        this.animateModel(player, limbSwing, limbSwingAmount, mc.getTickDelta());
    }

    // public void render(float scale) {
    //     this.render(null, null, player, limbSwing, limbSwingAmount, player.age, yawHead, pitch, scale);
    // }

    public void disableArmorLayers() {
        this.jacket.visible = false;
        this.leftPants.visible = false;
        this.rightPants.visible = false;
        this.leftSleeve.visible = false;
        this.rightSleeve.visible = false;
        this.hat.visible = true;
        this.head.visible = false;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public float getLimbSwing() {
        return limbSwing;
    }

    public void setLimbSwing(float limbSwing) {
        this.limbSwing = limbSwing;
    }

    public float getLimbSwingAmount() {
        return limbSwingAmount;
    }

    public void setLimbSwingAmount(float limbSwingAmount) {
        this.limbSwingAmount = limbSwingAmount;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getYawHead() {
        return yawHead;
    }

    public void setYawHead(float yawHead) {
        this.yawHead = yawHead;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    private static BipedEntityModel.ArmPose getArmPose(PlayerEntity player, ItemStack stack) {
        if (stack.isEmpty()) {
            return ArmPose.EMPTY;
        }
        if (stack.getItem() instanceof BowItem && player.getItemUseTime() > 0) {
            return ArmPose.BOW_AND_ARROW;
        }
        return ArmPose.ITEM;
    }

}
