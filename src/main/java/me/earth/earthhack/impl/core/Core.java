package me.earth.earthhack.impl.core;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.plugin.PluginConfig;
import me.earth.earthhack.impl.managers.client.FileManager;
import me.earth.earthhack.impl.managers.client.PluginManager;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.fabricmc.loader.impl.lib.accesswidener.AccessWidenerReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 3arthh4ck's Core
 */
public final class Core implements PreLaunchEntrypoint {
    /** Logger for the Core. */
    public static final Logger LOGGER = LogManager.getLogger("3arthh4ck-Core");
    public static final ClassLoader CLASS_LOADER = FabricLauncherBase.getLauncher().getTargetClassLoader();

    /** Load the core */
    @Override
    public void onPreLaunch() {
        Bus.EVENT_BUS.subscribe(Scheduler.getInstance());
        new FileManager();

        PluginManager.getInstance().createPluginConfigs();

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
                    BufferedReader reader = new BufferedReader(new InputStreamReader(CLASS_LOADER.getResourceAsStream(config.getAccessWidener()), StandardCharsets.UTF_8));
                    accessWidenerReader.read(reader, FabricLauncherBase.getLauncher().getTargetNamespace());
                } catch (Exception e) {
                    LOGGER.error("Failed to read AccessWidener from plugin: " + config.getName(), e);
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
