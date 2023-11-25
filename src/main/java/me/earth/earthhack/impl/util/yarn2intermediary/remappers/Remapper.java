package me.earth.earthhack.impl.util.yarn2intermediary.remappers;

import me.earth.earthhack.impl.util.yarn2intermediary.Mapping;
import org.objectweb.asm.tree.ClassNode;

public interface Remapper {
    void remap(ClassNode cn, Mapping mapping);
}
