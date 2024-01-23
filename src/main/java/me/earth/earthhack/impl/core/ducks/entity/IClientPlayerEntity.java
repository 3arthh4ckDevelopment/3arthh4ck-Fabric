package me.earth.earthhack.impl.core.ducks.entity;

/**
 * Duck interface for {@link net.minecraft.client.network.ClientPlayerEntity}.
 */
public interface IClientPlayerEntity
{
    double earthhack$getLastReportedX();

    double earthhack$getLastReportedY();

    double earthhack$getLastReportedZ();

    float earthhack$getLastReportedYaw();

    float earthhack$getLastReportedPitch();

    boolean earthhack$getLastOnGround();

    void earthhack$setLastReportedX(double x);

    void earthhack$setLastReportedY(double y);

    void earthhack$setLastReportedZ(double z);

    void earthhack$setLastReportedYaw(float yaw);

    void earthhack$setLastReportedPitch(float pitch);

    int earthhack$getPositionUpdateTicks();

    void earthhack$superUpdate();

    void earthhack$invokeSendMovementPackets();

    void earthhack$setHorseJumpPower(float jumpPower);
}
