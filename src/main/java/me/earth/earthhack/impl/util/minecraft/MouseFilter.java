package me.earth.earthhack.impl.util.minecraft;

public class MouseFilter {
    private float targetValue;
    private float remainingValue;
    private float lastAmount;

    public float smooth(float value, float factor) { // ion know what they supposed to be, maybe it's this =)
        this.targetValue += value;
        value = (this.targetValue - this.remainingValue) * factor;
        this.lastAmount += (value - this.lastAmount) * 0.5F;
        if (value > 0.0F && value > this.lastAmount || value < 0.0F && value < this.lastAmount) {
            value = this.lastAmount;
        }

        this.remainingValue += value;
        return value;
    }

    public void reset() {
        this.targetValue = 0.0F;
        this.remainingValue = 0.0F;
        this.lastAmount = 0.0F;
    }
}
