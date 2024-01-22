package me.earth.earthhack.impl.modules.movement.packetfly;

import me.earth.earthhack.api.module.data.DefaultData;

final class PacketFlyData extends DefaultData<PacketFly>
{
    public PacketFlyData(PacketFly module)
    {
        super(module);
        register(module.mode, """
                - Setback : Slow PacketFly, doesn't predict LagBacks.
                - Fast : Standard PacketFly mode.
                - Factor : Like Fast (when Factor is 1.0) but you can use the Factor Setting to make it go even faster (desyncs).
                - Slow : Similar to SetBack, but still predicts LagBacks, also uses Factor (desync).
                - Compatibility : Does nothing. Is meant to be used together with the PacketFly's of other clients to signal 3arthh4ck that a PacketFly is in use.
                """);
        register(module.factor, "The Speed multiplier when using Mode " +
                "Factor or Slow.");
        register(module.phase, """
                - Off : Don't phase
                - Semi : Phase
                - Full : Phase fully into blocks.""");
        register(module.type, """
                - Down : Sends an invalid packet going down
                - Up : Sends an invalid packet going up
                - Preserve : Sends invalid packets with random offsets.""");
        register(module.antiKick, "Makes you glide down slowly to " +
                "prevent you from getting kicked.");
        register(module.conceal, "Multiplier for when you PacketFly inside" +
                " the void. With low values (like 0.4) and Mode Preserve" +
                " certain AntiCheats that prevent flying in the void " +
                "(2b2tpvp) can be bypassed.");
        register(module.answer, "Answers LagBacks legitimately.");
        register(module.bbOffset,
                "Makes your hitbox smaller for detecting phase.");
        register(module.invalidY, "Offset for the OutOfBounds packets.");
        register(module.sendTeleport, "If LagBacks should be predicted.");
        register(module.concealY, "If the player is under this Y-Coordinate" +
                " the C-Multiplier will be applied. This is important" +
                " for certain AntiCheats (2b2tpvp - NoVoidPacketFly)");
        // double register? // register(module.conceal, "Lower -> Slower, will make you slower.");
        register(module.ySpeed, "Multiplier for your vertical Speed.");
        register(module.xzSpeed, "Multiplier for your horizontal Speed.");
    }

    @Override
    public String getDescription()
    {
        return "Uses a special exploit to Fly and Phase.";
    }

}
