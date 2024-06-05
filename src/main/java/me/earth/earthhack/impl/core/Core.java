package me.earth.earthhack.impl.core;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.plugin.PluginConfig;
import me.earth.earthhack.impl.managers.client.PluginManager;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;

/**
 * 3arthh4ck's Core.
 */
public class Core {
    /** Logger for the Core. */
    public static final Logger LOGGER = LogManager.getLogger("3arthh4ck-Core");

    /** Load the core */
    public Core() { //TODO: initialize this earlier!
        if (PluginManager.getInstance().getConfigs().values().isEmpty()) {
            init();
        } else {
            throw new RuntimeException("3arthh4ck core is already initialized!");
        }
    }

    /**
     * Initialize the Core.
     */
    private void init() {
        LOGGER.info("Found Environment: " + FabricLoader.getInstance().getEnvironmentType());
        Bus.EVENT_BUS.subscribe(Scheduler.getInstance());

        File util = new File(FabricLoader.getInstance().getConfigDir() + "/earthhack/util");
        if (!util.exists())
            util.mkdir();

        File plugins = new File(FabricLoader.getInstance().getConfigDir() + "/earthhack/plugins");
        if (!plugins.exists())
            plugins.mkdir();

        PluginManager.getInstance().createPluginConfigs(getClass().getClassLoader());

        MixinBootstrap.init();
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.DEFAULT)
                .setSide(MixinEnvironment.Side.CLIENT);
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.PREINIT)
                .setSide(MixinEnvironment.Side.CLIENT);
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.INIT)
                .setSide(MixinEnvironment.Side.CLIENT);
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.DEFAULT)
                .setSide(MixinEnvironment.Side.CLIENT);

        for (PluginConfig config : PluginManager.getInstance()
                .getConfigs()
                .values())
        {
            if (config.getMixinConfig() != null)
            {
                LOGGER.info("Adding "
                        + config.getName()
                        + "'s MixinConfig: "
                        + config.getMixinConfig());

                Mixins.addConfiguration(config.getMixinConfig());
            }
        }
    }
}
