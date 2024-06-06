package me.earth.earthhack.impl.core.ducks.entity;

import me.earth.earthhack.impl.commands.packet.util.Dummy;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.entity.EntityType;

/**
 * Duck interface for {@link net.minecraft.entity.Entity}.
 */
public interface IEntity extends Dummy
{
    /**
     * @return the isInWeb field.
     */
    boolean earthhack$inWeb();

    /**
     * @return the EntityType of this Entity.
     */
    EntityType earthhack$getType();

    /**
     * @return time since this Entity has been set dead.
     */
    long earthhack$getDeathTime();

    /**
     * Alternative to !{@link net.minecraft.entity.Entity#isAlive()}.
     *
     * @return <tt>true</tt> if this Entity is Pseudo Dead.
     */
    boolean earthhack$isPseudoDead();

    /**
     * Makes {@link IEntity#earthhack$isPseudoDead()} return the given value.
     *
     * @param pseudoDead the pseudoDeadState
     */
    void earthhack$setPseudoDead(boolean pseudoDead);

    /**
     * @return the StopWatch used to Un-PseudoDead
     *         an Entity if it hasn't died after a time.
     */
    StopWatch earthhack$getPseudoTime();

    /**
     * @return the {@link System#currentTimeMillis()}
     *         this Entity has been created on.
     */
    long earthhack$getTimeStamp();

    @Override
    default boolean earthhack$isDummy()
    {
        return false;
    }

    void earthhack$setDummy(boolean dummy);

    long earthhack$getOldServerPosX();

    long earthhack$getOldServerPosY();

    long earthhack$getOldServerPosZ();

    void earthhack$setOldServerPos(long x, long y, long z);

}
