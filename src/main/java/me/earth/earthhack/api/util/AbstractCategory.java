package me.earth.earthhack.api.util;

import me.earth.earthhack.api.util.interfaces.Nameable;

public abstract class AbstractCategory implements Nameable {

    private final String name;
    private final int ordinal;

    public AbstractCategory(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public int ordinal() {
        return this.ordinal;
    }

}
