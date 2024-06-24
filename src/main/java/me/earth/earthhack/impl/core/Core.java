package me.earth.earthhack.impl.core;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.plugin.PluginConfig;
import me.earth.earthhack.impl.managers.client.FileManager;
import me.earth.earthhack.impl.managers.client.PluginManager;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.fabricmc.loader.impl.lib.accesswidener.AccessWidenerReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 3arthh4ck's Core
 */
public final class Core implements PreLaunchEntrypoint {
    /** Logger for the Core. */
    public static final Logger LOGGER = LogManager.getLogger("3arthh4ck-Core");

    /** Load the core */
    @Override
    public void onPreLaunch() {
        LOGGER.info("Found Environment: " + FabricLoader.getInstance().getEnvironmentType());
        Bus.EVENT_BUS.subscribe(Scheduler.getInstance());
        ClassLoader classLoader = FabricLauncherBase.getLauncher().getTargetClassLoader();

        File util = new File(FileManager.EARTHHACK_ROOT + "/util");
        if (!util.exists())
            util.mkdir();

        File plugins = new File(FileManager.EARTHHACK_ROOT + "/plugins");
        if (!plugins.exists())
            plugins.mkdir();

        PluginManager.getInstance().createPluginConfigs(classLoader);

        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.DEFAULT)
                .setSide(MixinEnvironment.Side.CLIENT);
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.PREINIT)
                .setSide(MixinEnvironment.Side.CLIENT);
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.INIT)
                .setSide(MixinEnvironment.Side.CLIENT);
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.DEFAULT)
                .setSide(MixinEnvironment.Side.CLIENT);

        AccessWidenerReader accessWidenerReader = new AccessWidenerReader(FabricLoaderImpl.INSTANCE.getAccessWidener());

        for (PluginConfig config : PluginManager.getInstance().getPluginConfigs()) {

            if (config.getAccessWidener() != null) {
                LOGGER.info("Adding " + config.getName() + "'s AccessWidener: " + config.getAccessWidener());

                try  {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream(config.getAccessWidener()), StandardCharsets.UTF_8));
                    accessWidenerReader.read(reader, FabricLauncherBase.getLauncher().getTargetNamespace());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to read AccessWidener file from plugin " + config.getName(), e);
                }
            }

            if (config.getMixinConfig() != null) {
                LOGGER.info("Adding "
                        + config.getName()
                        + "'s MixinConfig: "
                        + config.getMixinConfig());

                Mixins.addConfiguration(config.getMixinConfig());
            }
        }
    }
}
