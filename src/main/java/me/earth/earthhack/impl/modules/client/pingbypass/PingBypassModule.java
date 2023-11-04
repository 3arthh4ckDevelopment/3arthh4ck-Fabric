package me.earth.earthhack.impl.modules.client.pingbypass;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.modules.Caches;

public class PingBypassModule extends Module {
    public static final ModuleCache<PingBypassModule> CACHE =
            Caches.getModule(PingBypassModule.class);
    public final Setting<PbProtocol> protocol =
            register(new EnumSetting<>("Protocol", PbProtocol.New));

    public PingBypassModule() {
        super("PingBypass", Category.Client);
    }

    public boolean isOld()
    {
        return protocol.getValue() == PbProtocol.Legacy;
    }
}
