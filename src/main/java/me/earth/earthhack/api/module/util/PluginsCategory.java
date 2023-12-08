package me.earth.earthhack.api.module.util;

import me.earth.earthhack.api.module.Module;

import java.util.HashMap;
import java.util.Map;

public class PluginsCategory {

    private final Category pluginCategory = new Category("Plugins", 6);

    public Category getCategory() {
        return pluginCategory;
    }

    public Category[] getCategories() {
        Category[] c = new Category[Category.values().length + 1];
        System.arraycopy(Category.values(), 0, c, 0, Category.values().length);
        c[Category.values().length] = getCategory();
        return c;
    }

    private final Map<Module, Category> pluginModulesMap = new HashMap<>();

    public void addPluginModule(Module module) {
        pluginModulesMap.put(module, module.getCategory());
    }

    public Map<Module, Category> getPluginsModuleList() {
        return pluginModulesMap;
    }

    private final static PluginsCategory INSTANCE = new PluginsCategory();

    public static PluginsCategory getInstance() {
        return INSTANCE;
    }
}
