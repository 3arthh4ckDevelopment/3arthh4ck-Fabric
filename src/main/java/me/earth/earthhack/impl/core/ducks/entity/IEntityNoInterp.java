package me.earth.earthhack.impl.core.ducks.entity;

public interface IEntityNoInterp {
    double earthhack$getNoInterpX();

    double earthhack$getNoInterpY();

    double earthhack$getNoInterpZ();

    void earthhack$setNoInterpX(double x);

    void earthhack$setNoInterpY(double y);

    void earthhack$setNoInterpZ(double z);

    int earthhack$getPosIncrements();

    void earthhack$setPosIncrements(int posIncrements);

    float earthhack$getNoInterpSwingAmount();

    float earthhack$getNoInterpSwing();

    float earthhack$getNoInterpPrevSwing();

    void earthhack$setNoInterpSwingAmount(float noInterpSwingAmount);

    void earthhack$setNoInterpSwing(float noInterpSwing);

    void earthhack$setNoInterpPrevSwing(float noInterpPrevSwing);

    /**
     * @return <tt>true</tt> unless this Entity is a ClientPlayerEntity.
     */
    boolean earthhack$isNoInterping();

    void earthhack$setNoInterping(boolean noInterping);
}
