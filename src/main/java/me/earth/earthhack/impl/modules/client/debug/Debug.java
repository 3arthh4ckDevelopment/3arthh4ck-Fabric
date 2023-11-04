package me.earth.earthhack.impl.modules.client.debug;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Debug extends Module {
    private final Setting<Boolean> debugPlace =
            register(new BooleanSetting("DebugPlacePing", false));
    private final Setting<Boolean> debugPlaceDistance =
            register(new BooleanSetting("DebugPlaceDistance", false));
    private final Setting<Boolean> debugBreak =
            register(new BooleanSetting("DebugBreakPing", false));
    private final Setting<Boolean> glGrid =
            register(new BooleanSetting("GlGrid", false));
    private final Setting<Boolean> nbt =
            register(new BooleanSetting("NBT-Reader", false));

    private final Map<BlockPos, Long> times  = new ConcurrentHashMap<>();
    private final Map<BlockPos, Long> attack = new ConcurrentHashMap<>();
    private final Map<Integer, BlockPos> ids = new ConcurrentHashMap<>();

    public Debug(){
        super("Debug", Category.Client);
    }
}
