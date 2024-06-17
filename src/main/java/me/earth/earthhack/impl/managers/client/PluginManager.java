package me.earth.earthhack.impl.managers.client;

import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.api.plugin.PluginConfig;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.Core;
import me.earth.earthhack.impl.managers.client.exception.BadPluginException;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;

import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

    private final Map<PluginConfig, Plugin> plugins = new HashMap<>();
    private final Map<String, PluginConfig> configs = new HashMap<>();
    private ClassLoader classLoader;

    /** Private Ctr since this is a Singleton. */
    private PluginManager() { }

    /** @return the Singleton Instance for this Manager. */
    public static PluginManager getInstance()
    {
        return INSTANCE;
    }

    /**
     * Used by {@link Core#Core()}.
     * Scans the "earthhack/plugins" folders for Plugins.
     * If it can find jarFiles whose Manifest contain a
     * "3arthh4ckConfig" the jar will be added to the classPath
     * and a {@link PluginConfig} will be created. If the PluginConfig
     * contains a "mixinConfig" entry that MixinConfig will be added by
     * the CoreMod.
     *
     * @param pluginClassLoader the classLoader to load Plugins with.
     */
    public void createPluginConfigs(ClassLoader pluginClassLoader) {

        this.classLoader = pluginClassLoader;
        Core.LOGGER.info("PluginManager: Scanning for PluginConfigs.");

        loadPlugins(new File(PATH).listFiles());
    }

    private void loadPlugins(File[] files) {
        try {
            for (File file : Objects.requireNonNull(files)) {
                if (file.getName().endsWith(".jar")) {
                    Core.LOGGER.info("PluginManager: Scanning " + file.getName());
                    try {
                        scanJarFile(file);
                    }
                    catch (Exception e)
                    {
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
        for (PluginConfig config : configs.values()) {
            if (plugins.containsKey(config)) {
                Earthhack.getLogger().error("Can't register Plugin "
                        + config.getName()
                        + ", a plugin with that name is already registered.");
                continue;
            }

            Earthhack.getLogger().info("Instantiating: "
                    + config.getName()
                    + ", MainClass: "
                    + config.getMainClass());
            try {
                Class<?> loadedClass = classLoader.loadClass(config.getMainClass());
                Constructor<?> constructor = loadedClass.getConstructor();
                constructor.setAccessible(true);
                Plugin plugin = (Plugin) constructor.newInstance();
                plugins.put(config, plugin);
            }
            catch (Throwable e) {
                Earthhack.getLogger().error("Error instantiating : "
                        + config.getName() + ", caused by:");

                e.printStackTrace();
            }
        }
    }

    private void scanJarFile(File file) throws Exception {
        try (JarFile jarFile = new JarFile(file)) {

            Manifest manifest = jarFile.getManifest();
            Attributes attributes = manifest.getMainAttributes();
            String configName = attributes.getValue("3arthh4ckConfig");

            if (configName == null) {
                throw new BadPluginException(jarFile.getName() + ": Manifest doesn't provide a 3arthh4ckConfig!");
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

            configs.put(configName, config);
        }
    }

    /**
     * @return a map of all found PluginConfigs.
     */
    public Map<String, PluginConfig> getConfigs()
    {
        return configs;
    }

    /**
     * @return a Map of all found Plugins with their names as keys.
     */
    public Map<PluginConfig, Plugin> getPlugins()
    {
        return plugins;
    }

}
