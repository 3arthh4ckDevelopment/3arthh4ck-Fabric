package me.earth.earthhack.impl.util.yarn2notch.remappers;

import me.earth.earthhack.impl.util.yarn2notch.Mapping;
import org.objectweb.asm.tree.ClassNode;

public interface Remapper {
    void remap(ClassNode cn, Mapping mapping);
}
