package me.earth.earthhack.impl.managers.client;

import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.api.plugin.PluginConfig;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.Core;
import me.earth.earthhack.impl.managers.client.exception.BadPluginException;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Manages {@link Plugin}s for 3arthh4ck.
 */
public class PluginManager
{
    private static final PluginManager INSTANCE = new PluginManager();
    private static final String PATH = "earthhack/plugins";

    private final Map<PluginConfig, Plugin> pluginMap = new HashMap<>();
    private ClassLoader classLoader = Core.CLASS_LOADER;

    /** Private Ctr since this is a Singleton. */
    private PluginManager() { }

    /** @return the Singleton Instance for this Manager. */
    public static PluginManager getInstance()
    {
        return INSTANCE;
    }

    /**
     * Used by {@link Core}.
     * Scans the "earthhack/plugins" folders for Plugins.
     * If it can find jarFiles whose Manifest contain a
     * "3arthh4ckConfig" the jar will be added to the classPath
     * and a {@link PluginConfig} will be created. If the PluginJson
     * contains a "mixinConfig" entry that MixinConfig will be added by
     * the CoreMod.
     */
    public void createPluginConfigs() {
        Core.LOGGER.info("PluginManager: Scanning for PluginConfigs.");
        
        File[] folder = new File(PATH).listFiles();
        if (folder != null) {
            loadPlugins(folder);
        }
    }

    public void loadPlugins(File[] files) {
        try {
            for (File file : Objects.requireNonNull(files)) {
                if (file.getName().endsWith(".jar")) {
                    Core.LOGGER.info("PluginManager: Scanning " + file.getName());
                    try {
                        loadJarFile(file);
                    }
                    catch (Exception e) {
                        Core.LOGGER.error("Error loading Plugin: " + file.getName() + ", caused by:");
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called by {@link 3arthh4ck}.
     * Instantiates all found Plugins.
     */
    public void instantiatePlugins() {
        for (PluginConfig pluginConfig : pluginMap.keySet()) {
            if (pluginMap.get(pluginConfig) != null) {
                Earthhack.getLogger().error("Can't register Plugin "
                        + pluginConfig.getName()
                        + ", a plugin with that name is already registered.");
                continue;
            }

            Earthhack.getLogger().info("Instantiating: "
                    + pluginConfig.getName()
                    + ", MainClass: "
                    + pluginConfig.getMainClass());
            try {
                Class<?> loadedClass = classLoader.loadClass(pluginConfig.getMainClass());
                Constructor<?> constructor = loadedClass.getConstructor();
                constructor.setAccessible(true);
                pluginMap.put(pluginConfig, (Plugin) constructor.newInstance());
            } catch (Throwable e) {
                Earthhack.getLogger().error("Error instantiating: "
                        + pluginConfig.getName() + ", caused by:");
                e.printStackTrace();
            }
        }
    }

    private void loadJarFile(File file) throws Exception {
        try (JarFile jarFile = new JarFile(file)) {

            Manifest manifest = jarFile.getManifest();
            Attributes attributes = manifest.getMainAttributes();
            String configName = attributes.getValue("3arthh4ckConfig");

            if (configName == null) {
                throw new BadPluginException(jarFile.getName() + ": Manifest doesn't provide a 3arthh4ckConfig!");
            }

            String pluginVersion = attributes.getValue("Fabric-Minecraft-Version");
            if (pluginVersion == null) {
                throw new BadPluginException(jarFile.getName() + ": Manifest doesn't provide a Fabric-Minecraft-Version!");
            }

            // >:D
            FabricLauncherBase.getLauncher().addToClassPath(file.toPath());

            PluginConfig config = Jsonable.GSON.fromJson(
                    new InputStreamReader(
                            Objects.requireNonNull(
                                    this.classLoader.getResourceAsStream(configName))),
                    PluginConfig.class);

            if (config == null) {
                throw new BadPluginException(jarFile.getName()
                        + ": Found a PluginConfig, but couldn't instantiate it.");
            }

            Core.LOGGER.info("Found PluginConfig: "
                    + config.getName()
                    + ", MainClass: "
                    + config.getMainClass()
                    + ", Mixins: "
                    + config.getMixinConfig());

            String gameVersion = FabricLoaderImpl.INSTANCE.getGameProvider().getNormalizedGameVersion();
            if (!pluginVersion.equals(gameVersion)) {
                Core.LOGGER.warn("Plugin "
                        + config.getName()
                        + " is for Minecraft version "
                        + pluginVersion
                        + " but you're using "
                        + gameVersion);
            }

            pluginMap.put(config, null);
        }
    }

    /**
     * @return a Set of all found {@link PluginConfig}
     */
    public Set<PluginConfig> getPluginConfigs() {
        return pluginMap.keySet();
    }

    /**
     * @return a Set of all the instances of the loaded {@link Plugin}
     */
    public Set<Plugin> getPlugins() {
        return new HashSet<>(pluginMap.values());
    }

}
