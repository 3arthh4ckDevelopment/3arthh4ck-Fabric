package me.earth.earthhack.impl.core;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.plugin.PluginConfig;
import me.earth.earthhack.impl.managers.client.PluginManager;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.util.misc.FileUtil;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 3arthh4ck's Core.
 * I will port the transformers if they are found
 * to be necessary, as it's a pain with Fabric.
 */
public class Core {
    /** Logger for the Core. */
    public static final Logger LOGGER = LogManager.getLogger("3arthh4ck-Core");

    /**
     * Initialize the Core.
     * @param pluginClassLoader PluginClassLoader for loading Plugins.
     */
    public void init(ClassLoader pluginClassLoader)
    {
        LOGGER.info("Found Environment: " + FabricLoader.getInstance().getEnvironmentType());
        Bus.EVENT_BUS.subscribe(Scheduler.getInstance());

        Path path = Paths.get("earthhack");
        FileUtil.createDirectory(path);
        FileUtil.getDirectory(path, "util");
        FileUtil.getDirectory(path, "plugins");

        PluginManager.getInstance().createPluginConfigs(pluginClassLoader);

        MixinBootstrap.init();
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.DEFAULT)
                .setSide(MixinEnvironment.Side.CLIENT);
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.PREINIT)
                .setSide(MixinEnvironment.Side.CLIENT);
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.INIT)
                .setSide(MixinEnvironment.Side.CLIENT);
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.DEFAULT)
                .setSide(MixinEnvironment.Side.CLIENT);

        // Mixins.addConfiguration("mixins.forge.json"); // No mixins to be applied...

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

        Mixins.addConfiguration("mixins.earth.json");
        String obfuscationContext = "yarn";

        MixinEnvironment.getDefaultEnvironment()
                .setObfuscationContext(obfuscationContext);
    }
}
